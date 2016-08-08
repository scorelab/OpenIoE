package com.scorelab.ioe.service;

import com.scorelab.ioe.nosql.StoreTypes;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by tharidu on 8/8/16.
 */
public interface noSqlRepositoryService {

    public void connect();

    public void createSensorTable(Long sensorId, StoreTypes storeType);

    public void insertData(Long sensorId, String data, String description, ZonedDateTime timestamp, StoreTypes storeType, int ttlValue);

    public List<String> readData(Long sensorId);

    public List<String> readData(Long sensorId, List<LocalDate> dates);
}
