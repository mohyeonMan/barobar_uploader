package com.chahoo.datauploader;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.chahoo.datauploader.mapper.DataMapper;

import lombok.RequiredArgsConstructor;

/**
 * DataRepository
 */
@Repository
@RequiredArgsConstructor
public class DataRepository {
    private final DataMapper dataMapper;

    public List<HashMap<String,Object>> selectTest(){
        return dataMapper.selectTest();
    }


    public void insertFloors(HashMap<String,String> data){
        dataMapper.insertFloors(data);
    }

    public void insertBuilding(HashMap<String,String> data) {
        dataMapper.insertBuilding(data);
    }
    

}