package com.chahoo.datauploader;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.sf.jsqlparser.expression.TryCastExpression;

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

}
