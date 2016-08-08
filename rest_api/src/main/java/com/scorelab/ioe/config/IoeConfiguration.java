package com.scorelab.ioe.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by tharidu on 6/14/16.
 */

@ConfigurationProperties(prefix = "ioe", ignoreUnknownFields = false)
public class IoeConfiguration {

    private final Cassandra cassandra = new Cassandra();
    private final Queue queue = new Queue();

    public Cassandra getCassandra() {
        return cassandra;
    }

    public Queue getQueue() {
        return queue;
    }

    public static class Cassandra {
        private String cassandraUrl;
        private String cassandraKeyspace = "ioe";

        public String getCassandraUrl() {
            return cassandraUrl;
        }

        public void setCassandraUrl(String cassandraUrl) {
            this.cassandraUrl = cassandraUrl;
        }

        public String getCassandraKeyspace() {
            return cassandraKeyspace;
        }

        public void setCassandraKeyspace(String cassandraKeyspace) {
            this.cassandraKeyspace = cassandraKeyspace;
        }
    }

    public static class Queue {
        private String queueName = "ioeQueue";
        private String queueUrl = "tcp://localhost:61616";
        private String mqttUrl = "tcp://localhost:61616";

        public String getQueueName() {
            return queueName;
        }

        public void setQueueName(String queueName) {
            this.queueName = queueName;
        }

        public String getQueueUrl() {
            return queueUrl;
        }

        public void setQueueUrl(String queueUrl) {
            this.queueUrl = queueUrl;
        }

        public String getMqttUrl() {
            return mqttUrl;
        }

        public void setMqttUrl(String mqttUrl) {
            this.mqttUrl = mqttUrl;
        }
    }
}
