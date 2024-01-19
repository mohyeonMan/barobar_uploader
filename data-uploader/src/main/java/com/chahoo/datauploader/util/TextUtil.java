package com.chahoo.datauploader.util;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


@Component
public class TextUtil {

    private static String[] targetArr;
    private static String[] replaceArr;
    
    @Autowired
    public void init(ApplicationContext context) {
        
        targetArr = convertPropertyUTF8(context.getEnvironment().getProperty("target_floor_text")).split(",");
        replaceArr = convertPropertyUTF8(context.getEnvironment().getProperty("replace_floor_text")).split(",");

    }

    public static String convertPropertyUTF8(String data){
        return new String(data.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }
    
    public static String replaceTargetText(String text){
        for(int i = 0 ; i<targetArr.length ; i++){
            if(text.trim().equals(targetArr[i].trim())){
                text = replaceArr[i];
            }
        }
        
        return text;
    }
    
    public static Integer floorToInteger(String floorName){
        String floor = floorName.replace("지하", "-").replace("B","-").replace("층", "").replace("F", "");
        return Integer.parseInt(floor);
    }
    
    public static String IntegerToFloor(int floorNum){
        String floorName = "";
        if(floorNum<0){
            floorName += "지하";
        }
        floorName += Math.abs(floorNum)+"층";
        return floorName;
    }

    public static String handleNonNumericFloors(String floorName){
        try{
            int integerFloor = floorToInteger(floorName.trim());
            floorName = IntegerToFloor(integerFloor);
            return floorName; // 숫자 층이라면 replace가 불 필요. 
        }catch (NumberFormatException e){
            return replaceTargetText(floorName);
        }

    }
}
