package com.chahoo.datauploader;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * CSVManager
 */
@RequiredArgsConstructor
@Component
public class CSVManager {
    private final DataRepository dataRepo;

    @Value("${folder}")
    private String directory;
    @Value("${surface_file_name}")
    private String surface;
    @Value("${underground_file_name}")
    private String underground;
    @Value("${budae_file_name}")
    private String budae;
    /* @Value("${material_file_name}")
    private String material; */
    
    
    public void uploadFloors(){
        String category;
        
         category = "지상층";
        floorFactory(category, surface);

        category = "지하층";
        floorFactory(category, underground);

        category = "근린생활시설";
        floorFactory(category, budae);


        //WorkBook 테스트
        /* FileInputStream inputStream;
        try {
            File file = new File(directory+File.separator+surface);
            inputStream = new FileInputStream(file);
            System.out.println("file -> "+file.exists());

            Workbook workbook = new XSSFWorkbook(inputStream);
            

            for (Sheet sheet : workbook) {
               System.out.println(sheet.getRow(0));
                for (Row row : sheet) {
                    // 각 행의 셀 순회
                    for (Cell cell : row) {
                        String cellData = null;
                        switch (cell.getCellType()) {
                            case STRING:
                            cellData = cell.getStringCellValue();
                            // 문자열 값 처리
                            break;
                            case NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    java.util.Date dateValue = cell.getDateCellValue();
                                    cellData = dateValue.toString();
                                } else {
                                    double numericValue = cell.getNumericCellValue();
                                    cellData = String.valueOf(numericValue);
                                }
                                break;
                            case BOOLEAN:
                                boolean booleanValue = cell.getBooleanCellValue();
                                cellData = String.valueOf(booleanValue);
                                break;
                            default:
                        }
                        System.out.print(cellData+"\t");

                    }
                    System.out.println(); 
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("inputStream fucked up");
        } */
        




        System.out.println("\n\n프로그램을 종료합니다.\n\n");
        System.exit(0);

    }
    

    public void floorFactory(String category, String fileName){

        System.out.println("\n\n<"+category+" 업로드 시작>\n\n");
        
        System.out.println(directory + " 경로의 " + fileName + " 파일을 읽어들입니다.\n");
        String[] rows = FileLoader.loadCSVFile(directory, fileName);
        this.floorGenerator(rows,category);

    }




    @Transactional
    public void floorGenerator(String[] rows,String category){

        String building = null;
        int floorOrder = 0;
        
        
        for(int i=0 ; i < rows.length ; i++ ){
            String row = rows[i];
            String[] rowArr;

            if(row.toUpperCase().startsWith("GG".trim())) continue;
            
            if(building == null){
                building = row.trim();

                HashMap<String,String> data = new HashMap<>();

                // 동 추가
                data.put("building_name", building.trim());
                data.put("building_category", category);

                dataRepo.insertBuilding(data);
                floorOrder = 1;
            }else{
                if(row.length() == 1){
                    building = null;
                }else{
                    rowArr = rows[i].split("\\|");

                    if(rowArr[0].contains("~")){
                        String floor = rowArr[0].replace("층", "");
                        boolean under = floor.startsWith("지하");

                        String[] fromTo = floor.split("~");
                        System.out.println(fromTo[0]+"/"+fromTo[1]);
                        Integer from = Integer.parseInt(fromTo[0]);
                        Integer to = Integer.parseInt(fromTo[1]);
                        
                        for(int j = from; j <= to; j++){
                            
                            // 층 복수 추가
                            HashMap<String,String> data = new HashMap<>();
                            data.put("building_name", building);
                            data.put("floor_name", under? "지하"+j+"층":j+"층");
                            data.put("floor_order", String.valueOf(floorOrder));
                            data.put("x1", rowArr[1]);
                            data.put("y1", rowArr[2]);
                            data.put("x2", rowArr[3]);
                            data.put("y2", rowArr[4]);
                            
                            dataRepo.insertFloors(data);
                            
                            floorOrder++;

                        }



                    }else{
                        //층 추가
                        HashMap<String,String> data = new HashMap<>();
                        data.put("building_name", building);
                        data.put("floor_name", rowArr[0]);
                        data.put("floor_order", String.valueOf(floorOrder));
                        data.put("x1", rowArr[1]);
                        data.put("y1", rowArr[2]);
                        data.put("x2", rowArr[3]);
                        data.put("y2", rowArr[4]); 

                        
                        dataRepo.insertFloors(data);

                        floorOrder++;

                    }
                }
            }
        }
        System.out.println(category+" 데이터 업로드 완료.");
        
    }

    public void uploadMaterials(){
        System.out.println("\n\n<부재목록업로드 시작>\n\n");
        
        System.out.println(directory + " 경로의 " + " 파일을 읽어들입니다.\n");
    }
    
}