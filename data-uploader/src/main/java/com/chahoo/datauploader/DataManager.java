package com.chahoo.datauploader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chahoo.datauploader.util.FileLoader;
import com.chahoo.datauploader.util.TextUtil;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * CSVManager
 */
@Component
@Setter
@RequiredArgsConstructor
@ToString
public class DataManager {
    private final DataRepository dataRepo;

    @Value("${schema_name}")
    private String schemaName;
    @Value("${directory}")
    private String directory;
    @Value("${floor_excel_name}")
    private String floorExcelName;
    @Value("${drawing_dwg_name}")
    private String drawingDwgName;
    @Value("${sub_drawing_dwg_name}")
    private String subDrawingDwgName;
    @Value("${tag_dwg_name}")
    private String tagDwgName;
    @Value("${material_file_name}")
    private String materialFileName;
    @Value("${wall_material_file_name}")
    private String wallMaterialFileName;
    @Value("${building_category}")
    private String category;
    @Value("${material_category}")
    private String materialCategory;
    @Value("${tag_category}")
    private String tagCategory;

    public void convertProperties() {
        setSchemaName(TextUtil.convertPropertyUTF8(schemaName));
        setDirectory(TextUtil.convertPropertyUTF8(directory));
        setFloorExcelName(TextUtil.convertPropertyUTF8(floorExcelName));
        setDrawingDwgName(TextUtil.convertPropertyUTF8(drawingDwgName));
        setSubDrawingDwgName(TextUtil.convertPropertyUTF8(subDrawingDwgName));
        setTagDwgName(TextUtil.convertPropertyUTF8(tagDwgName));
        setMaterialFileName(TextUtil.convertPropertyUTF8(materialFileName));
        setWallMaterialFileName(TextUtil.convertPropertyUTF8(wallMaterialFileName));
        setCategory(TextUtil.convertPropertyUTF8(category));
        setMaterialCategory(TextUtil.convertPropertyUTF8(materialCategory));
        setTagCategory(TextUtil.convertPropertyUTF8(tagCategory));
    }

    public void createSchema() {
        boolean success = dataRepo.createSchema(schemaName);
        if (success) {
            dataRepo.createGeomIndex(schemaName);
            dataRepo.createSeq(schemaName);
            System.out.println("<스키마 생성 성공>");
        } else {
            System.out.println("<스키마 생성 실패>");
        }

    }

    public void uploadFloors() {

        System.out.println("\n\n<" + category + " 업로드 시작>\n\n");

        floorFactory(category.trim(), floorExcelName.trim());

        System.out.println("\n\n<" + category + " 업로드 완료>\n\n");

    }

    @Transactional
    public void floorFactory(String category, String fileName) {


        Workbook workbook = FileLoader.loadXLSXFile(directory, fileName);
        String[] rows = FileLoader.getStringArrFromSheet(workbook.getSheetAt(0));
        this.floorGenerator(rows, category);

    }

    public void floorGenerator(String[] rows, String category) {

        String building = null;
        int buildingOrder = 0;
        int floorOrder = 0;
        String floorName = null;
        String originFloorName = null;

        HashMap<String, Object> data = new HashMap<>();
        data.put("schemaName", schemaName);

        for (int i = 0; i < rows.length; i++) {
            String row = rows[i];
            String[] rowArr;

            if (row.toUpperCase().startsWith("GG".trim()))
                continue;

            if (row.length() == 0) {
                building = null;
            } else if (building == null) {
                building = row.trim();

                // 동 추가
                data.put("buildingName", building);
                data.put("buildingCategory", category);
                data.put("buildingOrder", ++buildingOrder);

                dataRepo.insertBuilding(data);
                floorOrder = 0;

            } else {
                rowArr = row.split("\\|");

                originFloorName = TextUtil.replaceTargetText(rowArr[0].trim());

                data.put("x1", rowArr[1]);
                data.put("y1", rowArr[2]);
                data.put("x2", rowArr[3]);
                data.put("y2", rowArr[4]);
                data.put("drawingName", drawingDwgName);
                dataRepo.insertDrawing(data);

                data.put("buildingName", building);

                if (originFloorName.contains("~")) {

                    String[] fromTo = originFloorName.split("~");

                    // 다수의 층을 숫자 화
                    Integer from = TextUtil.floorToInteger(fromTo[0]);
                    Integer to = TextUtil.floorToInteger(fromTo[1]);

                    // 다수의 층 추가
                    for (int j = from; j <= to; j++) {
                        if (j == 0)
                            continue; // 0층은 없다.

                        floorName = TextUtil.IntegerToFloor(j);

                        data.put("floorName", TextUtil.replaceTargetText(floorName));
                        data.put("floorOrder", ++floorOrder);
                        dataRepo.insertFloors(data);

                        System.out.println(building + " / " + floorName + " / " + floorOrder);

                    }

                } else {

                    // 층 추가
                    data.put("floorName", originFloorName);
                    data.put("floorOrder", ++floorOrder);
                    dataRepo.insertFloors(data);

                    System.out.println(building + " / " + originFloorName + " / " + floorOrder);

                }
            }

        }

    }

    public void uploadMaterials() {

        System.out.println("\n\n<부재 업로드 시작>\n\n");

        materialFactory(materialFileName.trim());

        System.out.println("\n\n<부재 업로드 완료>\n\n");

    }

    @Transactional
    public void materialFactory(String fileName) {

        Workbook workbook = FileLoader.loadXLSXFile(directory, fileName);
        int sheetCount = workbook.getNumberOfSheets();
        String partName = null;

        HashMap<String, Object> params = new HashMap<>();
        for (int sheetNum = 0; sheetNum < sheetCount; sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);

            partName = sheet.getSheetName().trim();

            params.put("schemaName", schemaName);
            params.put("partName", partName);
            dataRepo.insertPart(params);

            String[] rows = FileLoader.getStringArrFromSheet(sheet);
            materialGenerator(rows, partName);
            
        }

    }

    public void materialGenerator(String[] rows, String partName) {

        System.out.println("\t-" + partName + " 업로드 시작-");

        String building = null;
        boolean isHeader = false;
        String[] headerArr = new String[0];
        for (int i = 0; i < rows.length; i++) {

            String row = rows[i];
            String[] rowArr;
            String prodTypeName;
            HashMap<String, Object> params = new HashMap<>();

            if (row.toUpperCase().startsWith("GG".trim()))
                continue;

            if (row.length() == 0) {
                building = null;
                headerArr = new String[0];
            } else if (building == null) {
                building = row.trim();
                isHeader = true;
            } else {
                // 헤더면 앞부분 자르고 prod_detail_header 업로드.
                if (isHeader) {
                    String header = row.replace("부호|x|y|x|y|층", "").replaceAll("^\\|", "");
                    if (header.length() != 0) {
                        headerArr = header.split("\\|", 0);
                    }
                    params.put("schemaName", schemaName);
                    params.put("header", headerArr);
                    dataRepo.insertHeader(params);
                    params.clear();

                    isHeader = false;
                } else {
                    rowArr = row.split("\\|");
                    prodTypeName = rowArr[0].trim();

                    //부재명 업로드
                    params.put("schemaName", schemaName);
                    params.put("prodTypeName", prodTypeName);
                    params.put("partName", partName);
                    dataRepo.insertProdType(params);
                    params.clear();

                    // data_arr 만들기.
                    // "부호|x|y|x|y|층" 을 제외한 데이터 arr
                    String[] dataArr = new String[0];
                    if (rowArr.length > 6) {
                        dataArr = Arrays.copyOfRange(rowArr, 6, rowArr.length);
                    }

                    // 건물 별로 층의 범위를 찾기 위함.
                    String floorName = rowArr[5];
                    String fromString;
                    String toString;
                    if (floorName.contains("~")) {
                        String[] floorNameFromTo = floorName.split("~");

                        fromString = TextUtil.handleNonNumericFloors(floorNameFromTo[0]);
                        toString = TextUtil.handleNonNumericFloors(floorNameFromTo[1]);
                    } else {
                        fromString = TextUtil.handleNonNumericFloors(floorName);
                        toString = TextUtil.handleNonNumericFloors(floorName);
                    }

                    params.put("schemaName", schemaName);
                    params.put("materialCategory", materialCategory.trim());
                    params.put("building", building.trim());

                    // 카테고리와 건물이름으로 목적 건물 찾기.
                    List<Integer> targetBuildings = dataRepo.selectBuildingIdList(params);
                    params.clear();

                    List<Integer> targetFloors = new ArrayList<>();

                    // 찾은 건물 들에서 fromString과 toString으로 목적 층 찾고 List에 담기.
                    for (int buildingId : targetBuildings) {
                        params.put("schemaName", schemaName);
                        params.put("buildingId", buildingId);
                        params.put("fromFloor", fromString);
                        params.put("toFloor", toString);
                        List<Integer> filteredFloors = dataRepo.selectFloorIdList(params);
                        for (int floorId : filteredFloors) {
                            targetFloors.add(floorId);
                        }
                        System.out.print(filteredFloors.size()+" / ");
                    }
                    params.clear();
                     System.out.println("\n");

                    Integer[] floorIdArr = targetFloors.toArray(new Integer[targetFloors.size()]);

                    params.put("schemaName", schemaName);
                    params.put("prodTypeName", prodTypeName);
                    params.put("floorId", floorIdArr);
                    params.put("header", headerArr);
                    params.put("dataArr", dataArr);
                    dataRepo.insertProdDetail(params);

                    // sub_drawing 넣고, 그 detail 아이디를 넣어줘야함
                    params.put("schemaName", schemaName);
                    params.put("x1", Double.valueOf(rowArr[1]));
                    params.put("y1", Double.valueOf(rowArr[2]));
                    params.put("x2", Double.valueOf(rowArr[3]));
                    params.put("y2", Double.valueOf(rowArr[4]));
                    params.put("subDrawingName", subDrawingDwgName);
                    dataRepo.insertSubDrawing(params);

                    params.clear();
                }
            }

        }
        System.out.println("\t-" + partName + " 업로드 완료-\n");
    }

    @Transactional
    public void matchTags() {

        System.out.println("\n\n<tags 안의 " + tagDwgName + " 데이터를 " + tagCategory + " 분류로 업로드합니다.>\n\n");

        HashMap<String, Object> params = new HashMap<>();
        params.put("schemaName", schemaName);
        params.put("tagDwgName", tagDwgName);
        params.put("category", tagCategory);
        dataRepo.insertGisPoints(params);
        dataRepo.insertProd(params);
        params.clear();

        System.out.println("\n\n<" + tagCategory + " 태그 업로드 완료>\n\n");

    }

    @Transactional
    public void matchDrawing(){
        
        System.out.print("\n\n<배경도면과 도면번호를 연결 합니다.>\n\n");

        dataRepo.updateBaseDrawingId(schemaName);

        System.out.print("\n\n<배경도면 연결이 완료되었습니다.>\n\n");
        

    }

    public void uploadWallMaterials(){
        System.out.println("\n\n<벽체 업로드 시작>\n\n");
        
        Workbook workbook = FileLoader.loadXLSXFile(directory, wallMaterialFileName);
        int sheetCount = workbook.getNumberOfSheets();
        String buildingName = null;

        HashMap<String, Object> params = new HashMap<>();
        params.put("schemaName", schemaName);
        params.put("partName", "벽체");
        dataRepo.insertPart(params);

        for (int sheetNum = 0; sheetNum < sheetCount; sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);

            buildingName = sheet.getSheetName().trim();

            String[] rows = FileLoader.getStringArrFromSheet(sheet);
            wallMaterialGenerator(rows, buildingName);
        }

        System.out.println("\n\n<벽체 업로드 완료>\n\n");
    }

    public void wallMaterialGenerator(String[] rows, String buildingName){
        
        String[] header = rows[1].split("\\|"); //헤더는 처음에 하나.
        header = Arrays.copyOfRange(header,1,header.length);
        
        HashMap<String,Object> params = new HashMap<>();
        params.put("schemaName", schemaName);
        params.put("header", header);
        dataRepo.insertHeader(params);
        
        params.clear();
        
        String row = null;
        String prodTypeName = null;
        String originalFloorName = null;

        for(int i = 2; i < rows.length ; i++){
            row = rows[i];

            String[] rowArr = row.split("\\|");

            if(row.length() == 0){
                prodTypeName = null;
                continue;
            } else if(prodTypeName == null){
                prodTypeName = rowArr[0];

                params.put("schemaName", schemaName);
                params.put("prodTypeName", prodTypeName);
                params.put("partName", "벽체");
                dataRepo.insertProdType(params);
                rowArr = Arrays.copyOfRange(rowArr, 1, rowArr.length);
            }

            originalFloorName = rowArr [0];

            String fromString=null;
            String toString=null;
            if (originalFloorName.contains("~")) {
                String[] floorNameFromTo = originalFloorName.split("~");

                fromString = TextUtil.handleNonNumericFloors(floorNameFromTo[0]);
                toString = TextUtil.handleNonNumericFloors(floorNameFromTo[1]);
            } else {
                fromString = TextUtil.handleNonNumericFloors(originalFloorName);
                toString = TextUtil.handleNonNumericFloors(originalFloorName);
            }


            params.put("schemaName", schemaName);
            params.put("building", buildingName);
            int buildingId = dataRepo.selectBuildingIdList(params).get(0);
            params.put("buildingId", buildingId);
            params.put("fromFloor", fromString);
            params.put("toFloor", toString);
            
            List<Integer> filteredFloors = dataRepo.selectFloorIdList(params);
            Integer[] floorIdArr = filteredFloors.toArray(new Integer[filteredFloors.size()]);

            params.put("schemaName", schemaName);
            params.put("prodTypeName", prodTypeName);
            params.put("floorId", floorIdArr);
            params.put("header", header);
            params.put("dataArr", rowArr);

            System.out.println(buildingId+" / " +prodTypeName + " / "+filteredFloors.size());
            dataRepo.insertProdDetail(params);

        }
    }

}