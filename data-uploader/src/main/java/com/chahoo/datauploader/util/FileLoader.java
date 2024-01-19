package com.chahoo.datauploader.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class FileLoader {
    
    
    public static String[] loadCSVFile(String directory, String fileName){
        String filePath = directory.replace("\\", "/")+File.separator+fileName;
        File file = new File(filePath);
        byte[] temp = null;

        if(file.exists()){
            try {
                temp = Files.readAllBytes(Paths.get(filePath));
                return new String(temp).split("\n");
            } catch (Exception e) {
                System.out.println("파일을 불러오는 중 문제가 발생하였습니다.");
                System.exit(0);
            }
        }


        System.out.println("파일을 찾을 수 없습니다.");
        System.exit(0);
        return null;
        
    }

    public static Workbook loadXLSXFile(String directory,String fileName){

        System.out.println(directory + " 경로의 " + fileName + " 파일을 읽어들입니다.\n\n");

        String filePath = directory.replace("\\", "/")+File.separator+fileName;
        File file = new File(filePath);
        if(file.exists()){
            try {
                return new XSSFWorkbook(file);
            } catch (Exception e) {
                System.out.println("파일을 불러오는 중 문제가 발생하였습니다.");
                System.exit(0);
            }
        }
        System.out.println("파일을 찾을 수 없습니다.");
        System.exit(0);
        return null;

    }

    public static void loadShpFile(String directory,String fileName){

        String filePath = directory+File.separator+fileName;
        File file = new File(filePath);
        if(file.exists()){
            try {
                System.out.println("exists");
            } catch (Exception e) {
                System.out.println("파일을 불러오는 중 문제가 발생하였습니다.");
                System.exit(0);
            }
        }
        System.out.println("파일을 찾을 수 없습니다.");
        System.exit(0);
    }

    public static String[] getStringArrFromSheet(Sheet sheet){
        String[] strArr = new String[sheet.getLastRowNum()+1];
        for(int i =0; i<= sheet.getLastRowNum();i++){
            try{
                Row row = sheet.getRow(i);
                String rowData = "";
                for(Cell cell : row){
                    String cellValue="";
                    switch (cell.getCellType()) {
                        case NUMERIC:
                            cellValue = String.valueOf((int)cell.getNumericCellValue())+"|";
                            break;
                        case STRING:
                            cellValue = cell.getStringCellValue()+"|";
                            break;
                        default:
                            break;
                    }
                    rowData = rowData + cellValue ;
                }

                strArr[i] = rowData.replaceAll("\\|$", "");
            }catch(Exception e){
                strArr[i] = "";
            }
        }
        return strArr;
    }

    

}
