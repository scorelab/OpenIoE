package com.scorelab.ioe.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by tharidu on 6/14/16.
 */

@ConfigurationProperties(prefix = "ioe", ignoreUnknownFields = false)
public class IoeConfiguration {

    private final Cassandra cassandra = new Cassandra();
    private final Topic topic = new Topic();

    public Cassandra getCassandra() {
        return cassandra;
    }

    public Topic getTopic() {
        return topic;
    }

    public static class Cassandra {
        private String cassandraUrl = "http://cassandra";
        private String cassandraKeyspace = "ioe";
        private String strategy = "SimpleStrategy";
        private String replicationFactor = "1";

        public String getStrategy() { return strategy; }

        public void setStrategy(String strategy) { this.strategy = strategy; }

        public String getReplicationFactor() { return replicationFactor; }

        public void setReplicationFactor(String replicationFactor) { this.replicationFactor = replicationFactor; }

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

    public static class Topic {
        private String topicName = "ioe";
        private String topicUrl = "tcp://localhost:61616";
        private String mqttUrl = "tcp://localhost:61616";
        private String username;
        private String password;

        public String getTopicName() {
            return topicName;
        }

        public void setTopicName(String topicName) {
            this.topicName = topicName;
        }

        public String getTopicUrl() {
            return topicUrl;
        }

        public void setTopicUrl(String topicUrl) {
            this.topicUrl = topicUrl;
        }

        public String getMqttUrl() {
            return mqttUrl;
        }

        public void setMqttUrl(String mqttUrl) {
            this.mqttUrl = mqttUrl;
        }

        public String getUsername() { return username; }

        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }

        public void setPassword(String password) { this.password = password; }
    }
}
