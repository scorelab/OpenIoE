package com.scorelab.ioe.config;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.scorelab.ioe.nosql.StoreTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

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
        String tableName = "data_".concat(String.valueOf(sensorId));

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
    public void insertData(Long sensorId, String data, String description, Date timestamp, StoreTypes storeType, int ttl) {
        String tableName = "data_".concat(String.valueOf(sensorId));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        String date = df.format(timestamp);

        if (storeType.equals(StoreTypes.NORMAL)) {
            PreparedStatement statement = session.prepare(
                "INSERT INTO " + Constants.CASSANDRA_KEYSPACE + "." + tableName + " (date, event_time, value, description)"
                    + "VALUES (?,?,?,?);");

            BoundStatement boundStatement = new BoundStatement(statement);
            session.execute(boundStatement.bind(date, timestamp, data, description));

        } else {
            PreparedStatement statement = session.prepare(
                "INSERT INTO " + Constants.CASSANDRA_KEYSPACE + "." + tableName + " (event_time, value, description)"
                    + "VALUES (?,?,?,?) USING TTL ?;");

            BoundStatement boundStatement = new BoundStatement(statement);
            session.execute(boundStatement.bind(timestamp, data, description, ttl));
        }
    }
}
