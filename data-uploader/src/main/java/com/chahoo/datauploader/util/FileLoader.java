package com.chahoo.datauploader.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class FileLoader {
    
    
    public static String[] loadCSVFile(String directory, String fileName){
        String filePath = directory+File.separator+fileName;
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

        String filePath = directory+File.separator+fileName;
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

    public static String convertPropertyUTF8(String data){
        return new String(data.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

}
