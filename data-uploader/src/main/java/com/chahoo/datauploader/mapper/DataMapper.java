package com.chahoo.datauploader.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DataMapper {
    
    boolean createSchema(String schemaName);

    void createGeomIndex(String schemaName);

    void createSeq(String schemaName);

    void insertDrawing(HashMap<String,Object> data);

    void insertFloors(HashMap<String,Object> floor);

    List<HashMap<String,Object>> selectTest();

    void insertBuilding(HashMap<String, Object> data);

    int isExistProdType(String prodTypeName);

    void insertPart(HashMap<String,Object> data);

    void insertProdType(HashMap<String,Object> data);

    void insertHeader(HashMap<String,Object> data);

    void insertSubDrawing(HashMap<String,Object> data);
    
    void insertProdDetail(HashMap<String,Object> data);

    List<Integer> selectBuildingIdList(HashMap<String,Object> data);
    
    List<Integer> selectFloorIdList(HashMap<String,Object> data);
    
    int selectProdDetail(HashMap<String,Object> data);

    List<HashMap<String,Object>> selectTagList(HashMap<String,Object> data);

    void insertGisPoints(HashMap<String,Object> data);

    void insertProd(HashMap<String,Object> data);

    void updateBaseAreasDrawingId(String data);
    void updateBaseLinesDrawingId(String data);
    void updateBasePointsDrawingId(String data);

    int selectHeaderId(HashMap<String,Object> data);

}
