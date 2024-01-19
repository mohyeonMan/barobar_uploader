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

    public boolean createSchema(String schemaName){
        return dataMapper.createSchema(schemaName);
    }

    public void createGeomIndex(String schemaName){
        dataMapper.createGeomIndex(schemaName);
    }

    public void createSeq(String schemaName){
        dataMapper.createSeq(schemaName);
    }

    public void insertDrawing(HashMap<String,Object> data){
        dataMapper.insertDrawing(data);
    }

    public void insertFloors(HashMap<String,Object> data){
        dataMapper.insertFloors(data);
    }

    public void insertBuilding(HashMap<String,Object> data) {
        dataMapper.insertBuilding(data);
    }

    public void insertPart(HashMap<String,Object> data){
        dataMapper.insertPart(data);
    }

    public void insertProdType(HashMap<String,Object> data){
        dataMapper.insertProdType(data);
    }

    public void insertHeader(HashMap<String,Object> data){
        dataMapper.insertHeader(data);
    }
    
    public void insertSubDrawing(HashMap<String,Object> data){
        dataMapper.insertSubDrawing(data);
    }

    public void insertProdDetail(HashMap<String,Object> data){
        dataMapper.insertProdDetail(data);
    }

    public List<Integer> selectBuildingIdList(HashMap<String,Object> data){
        return dataMapper.selectBuildingIdList(data);
    }

    public List<Integer> selectFloorIdList(HashMap<String,Object> data){
        return dataMapper.selectFloorIdList(data);
    }
    public int selectProdDetail(HashMap<String,Object> data){
        return dataMapper.selectProdDetail(data);
    }

    public List<HashMap<String,Object>> selectTagList(HashMap<String,Object> data){
        return dataMapper.selectTagList(data);
    }

    public void insertGisPoints(HashMap<String,Object> data){
        dataMapper.insertGisPoints(data);
    }
    public void insertProd(HashMap<String,Object> data){
        dataMapper.insertProd(data);
    }

    public void updateBaseDrawingId(String data){
        dataMapper.updateBaseAreasDrawingId(data);
        dataMapper.updateBaseLinesDrawingId(data);
        dataMapper.updateBasePointsDrawingId(data);
    }

    public int selectHeaderId(HashMap<String,Object> data){
        return dataMapper.selectHeaderId(data);
    }
    


}