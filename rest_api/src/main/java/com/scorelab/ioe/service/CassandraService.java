package com.scorelab.ioe.service;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.scorelab.ioe.config.IoeConfiguration;
import com.scorelab.ioe.nosql.StoreTypes;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.datastax.driver.core.querybuilder.QueryBuilder.in;
import static com.datastax.driver.core.querybuilder.QueryBuilder.ttl;

/**
 * Created by tharidu on 8/8/16.
 */

@Service
public class CassandraService implements SensorDataRepositoryService {
    private Cluster cluster;
    private Session session;

    @Inject
    private IoeConfiguration ioeConfiguration;

    @PostConstruct
    @Override
    public void connect() {
        cluster = Cluster.builder().addContactPoint(ioeConfiguration.getCassandra().getCassandraUrl()).withRetryPolicy(DefaultRetryPolicy.INSTANCE).build();

        session = cluster.connect();
        session.execute("CREATE KEYSPACE IF NOT EXISTS " + ioeConfiguration.getCassandra().getCassandraKeyspace() + " WITH replication = {"
            + " 'class': '" + ioeConfiguration.getCassandra().getStrategy() + "', "
            + " 'replication_factor': '" + ioeConfiguration.getCassandra().getReplicationFactor() + "' "
            + "};");
        session.execute("USE " + ioeConfiguration.getCassandra().getCassandraKeyspace());

//        session = cluster.connect(ioeConfiguration.getCassandra().getCassandraKeyspace());
    }

    @Override
    public void createSensorTable(Long sensorId, StoreTypes storeType) {
        String tableName = returnSensorTableName(sensorId);

        if (storeType.equals(StoreTypes.NORMAL)) {
            session.execute(
                "CREATE TABLE " + ioeConfiguration.getCassandra().getCassandraKeyspace() + "." + tableName + " (" +
                    "date text," +
                    "event_time timestamp," +
                    "value text," +
                    "description text," +
                    "topic text," +
                    "PRIMARY KEY (date,event_time)" +
                    ");");
        } else {
            session.execute(
                "CREATE TABLE " + ioeConfiguration.getCassandra().getCassandraKeyspace() + "." + tableName + " (" +
                    "event_time timestamp," +
                    "value text," +
                    "description text," +
                    "topic text," +
                    "PRIMARY KEY (event_time)" +
                    ") WITH CLUSTERING ORDER BY (event_time DESC);");
        }
    }

    @Override
    public void insertData(Long sensorId, String data, String description, ZonedDateTime timestamp, StoreTypes storeType, String topic, int ttlValue) {
        String tableName = returnSensorTableName(sensorId);
        String date = timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE);
        Date utcTimestamp = Date.from(timestamp.toInstant());

        if (storeType.equals(StoreTypes.NORMAL)) {
            Statement statement = QueryBuilder.insertInto(ioeConfiguration.getCassandra().getCassandraKeyspace(), tableName)
                .value("date", date)
                .value("event_time", utcTimestamp)
                .value("value", data)
                .value("description", description)
                .value("topic", topic);
            session.execute(statement);
        } else {
            Statement statement = QueryBuilder.insertInto(ioeConfiguration.getCassandra().getCassandraKeyspace(), tableName)
                .value("date", date)
                .value("event_time", utcTimestamp)
                .value("value", data)
                .value("description", description)
                .value("topic", topic)
                .using(ttl(ttlValue));
            session.execute(statement);
        }
    }

    @Override
    public List<String> readData(Long sensorId) {
        String tableName = returnSensorTableName(sensorId);
        List<String> results = new ArrayList<>();

        Statement statement = QueryBuilder
            .select()
            .raw("JSON *")
            .from(ioeConfiguration.getCassandra().getCassandraKeyspace(), tableName);
        ResultSet rs = session.execute(statement);


        for (Row row : rs) {
            results.add(row.getString(0));
        }

        return results;
    }

    @Override
    public List<String> readData(Long sensorId, String topic) {
        String tableName = returnSensorTableName(sensorId);
        List<String> results = new ArrayList<>();

        Statement statement = QueryBuilder
            .select()
            .raw("JSON *")
            .from(ioeConfiguration.getCassandra().getCassandraKeyspace(), tableName).allowFiltering()
            .where(in("topic", topic));
        ResultSet rs = session.execute(statement);
        
        for (Row row : rs) {
            results.add(row.getString(0));
        }

        return results;
    }

    @Override
    public List<String> readData(Long sensorId, List<LocalDate> dates) {
        String tableName = returnSensorTableName(sensorId);
        List<String> results = new ArrayList<>();

        Statement statement = QueryBuilder
            .select()
            .raw("JSON *")
            .from(ioeConfiguration.getCassandra().getCassandraKeyspace(), tableName)
            .where(in("date", Lists.transform(dates, Functions.toStringFunction())));
        ResultSet rs = session.execute(statement);

        for (Row row : rs) {
            results.add(row.getString(0));
        }

        return results;
    }

    private String returnSensorTableName(Long sensorId) {
        return "data_".concat(String.valueOf(sensorId));
    }
}
