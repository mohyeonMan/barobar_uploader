package com.chahoo.datauploader.util;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.chahoo.datauploader.DataRepository;
import com.chahoo.datauploader.FileLoader;

import lombok.RequiredArgsConstructor;

/**
 * FloorUploader
 */
@RequiredArgsConstructor
@Component
public class FloorUploader {
    private final DataRepository dataRepo;

    @Value("${folder}")
    private String directory;
    @Value("${file_name}")
    private String fileName;

    public void uploadFloors(){

        System.out.println("\n\n< upload started >\n\n----------------------");

        Workbook workbook = FileLoader.loadXLSXFile(directory, fileName);
        Sheet sheet = workbook.getSheetAt(0);

        for(Row row: sheet){
            int rownum = row.getRowNum();

            
            System.out.println(row.getRowNum()+"/"+row.getCell(0).getStringCellValue().length());
        }
        
        System.out.println("\n\n프로그램을 종료합니다.\n\n");
        System.exit(0);


    }
    

}