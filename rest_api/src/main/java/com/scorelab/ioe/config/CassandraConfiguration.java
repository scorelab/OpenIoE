package com.scorelab.ioe.config;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.scorelab.ioe.domain.util.JSR310PersistenceConverters;
import com.scorelab.ioe.nosql.StoreTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;

/**
 * Created by tharidu on 6/14/16.
 */

@Service
public class CassandraConfiguration {
    private Cluster cluster;
    private Session session;

    public CassandraConfiguration() {
        connect();
    }

    //    @Autowired
    public void connect() {
        cluster = Cluster.builder().addContactPoint(Constants.CASSANDRA_URL).withRetryPolicy(DefaultRetryPolicy.INSTANCE).build();
        session = cluster.connect(Constants.CASSANDRA_KEYSPACE);
    }

    //    @Autowired
    public void createSensorTable(Long sensorId, StoreTypes storeType) {
        String tableName = returnSensorTableName(sensorId);

        if (storeType.equals(StoreTypes.NORMAL)) {
            session.execute(
                "CREATE TABLE " + Constants.CASSANDRA_KEYSPACE + "." + tableName + " (" +
                    "date text," +
                    "event_time timestamp," +
                    "value text," +
                    "description text," +
                    "PRIMARY KEY (date,event_time)" +
                    ");");
        } else {
            session.execute(
                "CREATE TABLE " + Constants.CASSANDRA_KEYSPACE + "." + tableName + " (" +
                    "event_time timestamp," +
                    "value text," +
                    "description text," +
                    "PRIMARY KEY (event_time)" +
                    ") WITH CLUSTERING ORDER BY (event_time DESC);");
        }
    }

    //    @Autowired
    public void insertData(Long sensorId, String data, String description, ZonedDateTime timestamp, StoreTypes storeType, int ttlValue) {
        String tableName = returnSensorTableName(sensorId);
        String date = timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE);
        Date utcTimestamp = Date.from(timestamp.toInstant());

        if (storeType.equals(StoreTypes.NORMAL)) {
            Statement statement = QueryBuilder.insertInto(Constants.CASSANDRA_KEYSPACE, tableName)
                .value("date", date)
                .value("event_time", utcTimestamp)
                .value("value", data)
                .value("description", description);
            session.execute(statement);
        } else {
            Statement statement = QueryBuilder.insertInto(Constants.CASSANDRA_KEYSPACE, tableName)
                .value("date", date)
                .value("event_time", utcTimestamp)
                .value("value", data)
                .value("description", description)
                .using(ttl(ttlValue));
            session.execute(statement);
        }
    }

    public List<String> readData(Long sensorId) {
        String tableName = returnSensorTableName(sensorId);
        List<String> results = new ArrayList<>();

        Statement statement = QueryBuilder
            .select()
            .raw("JSON *")
            .from(Constants.CASSANDRA_KEYSPACE, tableName);
        ResultSet rs = session.execute(statement);


        for (Row row : rs) {
            results.add(row.getString(0));
        }

        return results;
    }

    public List<String> readData(Long sensorId, List<LocalDate> dates) {
        String tableName = returnSensorTableName(sensorId);
        List<String> results = new ArrayList<>();

        Statement statement = QueryBuilder
            .select()
            .raw("JSON *")
            .from(Constants.CASSANDRA_KEYSPACE, tableName)
            .where(in("date", Lists.transform(dates, Functions.toStringFunction())));
        ResultSet rs = session.execute(statement);

        for (Row row : rs) {
            results.add(row.getString(0));
        }

        return results;
    }

    public String returnSensorTableName(Long sensorId) {
        return "data_".concat(String.valueOf(sensorId));
    }
}
