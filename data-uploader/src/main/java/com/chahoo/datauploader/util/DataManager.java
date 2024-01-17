package com.chahoo.datauploader.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chahoo.datauploader.DataRepository;

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
    @Value("${building_category}")
    private String category;
    @Value("${material_category}")
    private String materialCategory;
    @Value("${tag_category}")
    private String tagCategory;
    @Value("${apart_base_level}")
    private String baseLevel;

    public void convertProperties(){
        setSchemaName(FileLoader.convertPropertyUTF8(schemaName));
        setDirectory(FileLoader.convertPropertyUTF8(directory));
        setFloorExcelName(FileLoader.convertPropertyUTF8(floorExcelName));
        setDrawingDwgName(FileLoader.convertPropertyUTF8(drawingDwgName));
        setSubDrawingDwgName(FileLoader.convertPropertyUTF8(subDrawingDwgName));
        setTagDwgName(FileLoader.convertPropertyUTF8(tagDwgName));
        setMaterialFileName(FileLoader.convertPropertyUTF8(materialFileName));
        setCategory(FileLoader.convertPropertyUTF8(category));
        setMaterialCategory(FileLoader.convertPropertyUTF8(materialCategory));
        setTagCategory(FileLoader.convertPropertyUTF8(tagCategory));
        setBaseLevel(FileLoader.convertPropertyUTF8(baseLevel));
    }


    public void createSchema(){
        boolean success = dataRepo.createSchema(schemaName);
        if(success){
            dataRepo.createGeomIndex(schemaName);
            dataRepo.createSeq(schemaName);
            System.out.println("<스키마 생성 성공>");
        }else{
            System.out.println("<스키마 생성 실패>");
        }
        
    }

    public void uploadFloors(){
        
        System.out.println("\n\n<"+category+" 업로드 시작>\n\n");

        floorFactory(category.trim(), floorExcelName.trim()); 

        System.out.println("\n\n<"+category+" 업로드 완료>\n\n");

    }
    @Transactional   
    public void floorFactory(String category, String fileName){

        System.out.println(directory + " 경로의 " + fileName + " 파일을 읽어들입니다.\n");

        Workbook workbook = FileLoader.loadXLSXFile(directory, fileName);
        String[] rows = getStringArrFromSheet(workbook.getSheetAt(0));
        for(String row:rows){
            System.out.println(row);
        }
        this.floorGenerator(rows,category);

    }


    public void floorGenerator(String[] rows,String category){

        int baseLevel = this.baseLevel.isEmpty()? 0:Integer.parseInt(this.baseLevel)-1;
        String building = null;
        int surfaceOrder = baseLevel;
        int underOrder = baseLevel;
        int floorOrder = 0;
        int floorNum = 0;
        String floorName = null;
        String originFloorName = null;

        for(int i=0 ; i < rows.length ; i++ ){
            String row = rows[i];
            String[] rowArr;

            if(row.toUpperCase().startsWith("GG".trim())) continue;
            
            if(building == null){
                building = row.trim();

                HashMap<String,Object> data = new HashMap<>();

                // 동 추가
                data.put("schemaName", schemaName);
                data.put("building_name", building.trim());
                data.put("building_category", category);

                dataRepo.insertBuilding(data);
                surfaceOrder = baseLevel;
                underOrder = baseLevel;

            }else{
                if(row.length() == 0){
                    building = null;
                }else{
                    rowArr = row.split("\\|");

                    boolean isUnderground;
                    originFloorName = rowArr[0];
                    
                    HashMap<String,Object> data = new HashMap<>();
                    
                    data.put("schemaName", schemaName);
                    data.put("x1", rowArr[1]);
                    data.put("y1", rowArr[2]);
                    data.put("x2", rowArr[3]);
                    data.put("y2", rowArr[4]); 
                    data.put("drawing_name", drawingDwgName);
                    data.put("building_name", building);

                    dataRepo.insertDrawing(data);

                    if(originFloorName.contains("~")){
                        
                        String[] fromTo = originFloorName.split("~");
                        Integer from = Integer.parseInt(fromTo[0].replace("지하", "-").replace("층", "").replace("F", ""));
                        Integer to = Integer.parseInt(fromTo[1].replace("지하", "-").replace("층", "").replace("F", ""));
                        

                        
                        for(int j = from; j <= to; j++){
                            if(j==0) continue;

                            floorNum = j;
                            isUnderground = floorNum < 0;
                            
                            if(isUnderground){
                                floorName = "지하"+Math.abs(floorNum)+"층"; // 절대값
                                floorOrder = --underOrder;
                            }else{
                                floorName = floorNum+"층";
                                floorOrder = ++surfaceOrder;
                            }

                            // 층 복수 추가
                            data.put("schemaName", schemaName);
                            data.put("floor_name", floorName.trim());
                            data.put("floor_order", floorOrder);

                            dataRepo.insertFloors(data);
                            
                            System.out.println(data.get("floor_name"));

                        }

                    }else{
                        isUnderground = originFloorName.startsWith("지하");

                        if(isUnderground){
                            floorOrder = --underOrder;
                        }else{
                            floorOrder = ++surfaceOrder;
                        }

                        //층 추가
                        data.put("schemaName", schemaName);
                        data.put("floor_name", originFloorName.trim());
                        data.put("floor_order", floorOrder);

                        dataRepo.insertFloors(data);
                        System.out.println(data.get("floor_name"));

                    }
                }
            }
        }
        
    }

    public String[] getStringArrFromSheet(Sheet sheet){
        String[] strArr = new String[sheet.getLastRowNum()+1];
        for(int i =0; i<= sheet.getLastRowNum();i++){
            try{
                Row row = sheet.getRow(i);
                String rowData = "";
                for(Cell cell : row){
                    String cellValue="";
                    switch (cell.getCellType()) {
                        case NUMERIC:
                            cellValue = String.valueOf((int)cell.getNumericCellValue());
                            break;
                        case STRING:
                            cellValue = cell.getStringCellValue();
                            break;
                        default:
                            break;
                    }
                    rowData = rowData + cellValue +"|";                
                }

                strArr[i] = rowData.replaceAll("\\|$", "");
            }catch(Exception e){
                strArr[i] = "";
            }
        }
        
        return strArr;
    }

    public String[] getStringArrFromSheetTest(Sheet sheet){
        String[] strArr = new String[sheet.getLastRowNum()+1];
        for(int i =0; i<= sheet.getLastRowNum();i++){
            try{
                Row row = sheet.getRow(i);
                String rowData = "";
                for(Cell cell : row){
                    String cellValue="";
                    switch (cell.getCellType()) {
                        case NUMERIC:
                            cellValue = String.valueOf(cell.getNumericCellValue());
                            break;
                        case STRING:
                            cellValue = cell.getStringCellValue();
                            break;
                        default:
                            break;
                    }
                    rowData = rowData + cellValue +"|";
                }

                strArr[i] = rowData.replaceAll("\\|$", "");
            }catch(Exception e){
                strArr[i] = "";
            }
        }
        
        return strArr;
    }
    
    public void uploadMaterials(){
        
        System.out.println("\n\n<부재 업로드 시작>\n\n");

        materialFactory(materialFileName.trim()); 

        System.out.println("\n\n<부재 업로드 완료>\n\n");


    }
    @Transactional
    public void materialFactory(String fileName){
        System.out.println(directory + " 경로의 " + fileName + " 파일을 읽어들입니다.\n\n");

        Workbook workbook = FileLoader.loadXLSXFile(directory, fileName);
        int sheetCount = workbook.getNumberOfSheets();
        String partName = null;
        
        HashMap<String,Object> params = new HashMap<>();
        for(int sheetNum = 0; sheetNum < sheetCount; sheetNum++){
            Sheet sheet = workbook.getSheetAt(sheetNum);

            partName = sheet.getSheetName().trim();
            
            params.put("schemaName", schemaName);
            params.put("partName", partName);
            dataRepo.insertPart(params);

            String[] rows = getStringArrFromSheet(sheet);
            materialGenerator(rows,partName);
        }

    }

    public void materialGenerator(String[] rows,String partName){

        String building = null;
        boolean isHeader = false;
        String[] headerArr = new String[0];
        System.out.println("\t-"+partName+" 업로드 시작-");
        for(int i=0 ; i < rows.length ; i++ ){
            
            String row = rows[i];
            String[] rowArr;
            String prodTypeName;
            HashMap<String,Object> params = new HashMap<>();


            if(row.toUpperCase().startsWith("GG".trim())) continue;

            if(row.length() == 0){
                building = null;
                headerArr = new String[0];
            }else if(building == null){
                building = row.trim();
                isHeader = true;
            }else{
                if(isHeader){
                    String header = row.replace("부호|x|y|x|y|층", "").replaceAll("^\\|", "");
                    if(header.length() !=0 ){
                        headerArr = header.split("\\|", 0);
                    }
                    params.put("schemaName", schemaName);
                    params.put("header", headerArr);
                    dataRepo.insertHeader(params);
                    params.clear();

                    isHeader = false;
                }else{
                    rowArr = row.split("\\|");
                    prodTypeName = rowArr[0];
                    
                    params.put("schemaName", schemaName);
                    params.put("prodTypeName",prodTypeName);
                    params.put("partName", partName);
                    dataRepo.insertProdType(params);
                    params.clear();
                
                    
                    //data_arr
                    //"부호|x|y|x|y|층" 을 제외한 데이터 arr
                    String[] dataArr = new String[0];
                    if(rowArr.length > 6){
                        dataArr =  Arrays.copyOfRange(rowArr,6, rowArr.length-1);
                    }
                    
                    //floor_id만 넣어주면 된다.
                    String floorName = rowArr[5].replace("F", "").replace("층", "").replace("B", "지하").replace("-", "지하");
                    floorName = floorName.replace("PHR", "옥탑지붕").replace("PH", "옥탑").replace("R", "지붕");
                    
                    
                    String fromString;
                    String toString;
                    if (floorName.contains("~")) {
                        String[] floorNamePart = floorName.split("~");
                        fromString = floorNamePart[0];
                        toString = floorNamePart[1];
                    }else{
                        fromString = floorName;
                        toString = floorName;
                    }
                    
                    fromString =  fromString+"층";
                    toString = toString+"층";

                    if(fromString.contains("옥탑")||fromString.contains("지붕")||fromString.contains("ALL")){
                        fromString = fromString.replace("층", "");
                    }
                    if(toString.contains("옥탑")||toString.contains("지붕")||fromString.contains("ALL")){
                        toString = toString.replace("층", "");
                    }
                    
                    // System.out.println(floorName);
                    // System.out.println(fromString+" / "+toString);
                    params.put("schemaName", schemaName);
                    params.put("materialCategory", materialCategory.trim());
                    params.put("building", building.trim());

                    List<Integer> targetBuildings = dataRepo.selectBuildingIdList(params);
                    params.clear();
                    
                    List<Integer> targetFloors = new ArrayList<>();

                    for(int buildingId : targetBuildings){
                        params.put("schemaName", schemaName);
                        params.put("buildingId", buildingId);
                        params.put("fromFloor", fromString);
                        params.put("toFloor", toString);
                        List<Integer> filteredFloors = dataRepo.selectFloorIdList(params);
                        for(int floorId : filteredFloors ){
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
                    

                    //System.out.println(headerArr +" / "+dataArr);

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
        System.out.println("\t-"+partName+" 업로드 완료-\n");
    }


    @Transactional
    public void matchTags(){

        System.out.println("\n\n<tags 안의 "+tagDwgName+" 데이터를 " +tagCategory+" 분류로 업로드합니다.>\n\n");

        HashMap<String,Object> params = new HashMap<>();
        params.put("schemaName", schemaName);
        params.put("tagDwgName", tagDwgName);
        params.put("category", tagCategory);
        dataRepo.insertGisPoints(params);
        dataRepo.insertProd(params);
        params.clear();

        System.out.println("\n\n<"+tagCategory+" 태그 업로드 완료>\n\n");
        

    }
    
    
    
}