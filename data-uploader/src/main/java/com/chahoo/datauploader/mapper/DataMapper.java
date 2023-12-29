package com.chahoo.datauploader.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DataMapper {
    
    void insertFloors(HashMap<String,String> floor);

    List<HashMap<String,Object>> selectTest();

    void insertBuilding(HashMap<String, String> data);

    int isExistProdType(String prodTypeName);
}
