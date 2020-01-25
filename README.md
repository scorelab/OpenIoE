[![Gitter](https://img.shields.io/gitter/room/nwjs/nw.js.svg)](https://gitter.im/scorelab/scorelab)
[![Build Status](https://travis-ci.org/scorelab/OpenIoE.svg?branch=master)](https://travis-ci.org/scorelab/OpenIoE)
[![GitHub license](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://raw.githubusercontent.com/scorelab/OpenIoE/master/LICENSE)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/52edea34ff8943768a8c5a62728f73a7)](https://www.codacy.com/app/hcktheheaven/clocal-gcp?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=hcktheheaven/clocal-gcp&amp;utm_campaign=Badge_Grade)

# OpenIoE
OpenIoE is an Open-source middleware platform for building, managing, and integrating connected products with the Internet of Everything. It enables you to subscribe to data streams and get data from the sensors and store them. The application was generated using JHipster application generator. 

# Main features
 - A high-perfomance Java server side with Spring Boot
 - Message broker support with ActiveMQ Artemis
 - Multi Protocol support (MQTT, AMQP)
 - NoSQL data storage for sensor data (Cassandra)
 - Support for timeline series modelling
 - Publish-subscribe feature using MQTT

# Docker
 - All the services required to run the platform can be easily started via bundled docker-compose.yml located in rest_api/src/main/docker
 - Browse to rest_api/src/main/docker and run `docker-compose up -d`
 - Open 127.0.0.1 in the web browser where you can configure a user account and create devices and sensors
 - To stop the platform, run `docker-compose stop` in the same folder as above
 - Logs can be observed via `docker-compose logs [CONTAINER]` (ex: ioe-app)

# Installation
 - Clone the Github repository
 - Install and configure Cassandra (http://cassandra.apache.org/doc/latest/getting_started/installing.html)
 - Configure 'ioe' section in application.yml that can be found in rest_api/src/main/resources/config. Set cassandraUrl to localhost.

# Usage
 - Make sure Cassandra server is up. You can start Cassandra with 'sudo service cassandra start' and stop it with 'sudo service cassandra stop'. Verify that Cassandra is running by invoking nodetool status from the command line.
 - To run the artemis message broker (Compiled using ActiveMQ Artemis 1.5.x), 
     - In '/message_broker/etc/artemis.profile' file, change the ArtemisHome directory to match your Apache ActiveMQ Artemis installation directory and java.security.auth.login.config of JAVA_ARGS to match OpenIOE 'message_broker/etc/login.config'.
     - In '/message_broker/etc/bootstrap.xml' file, change the server configuration path to match OpenIOE 'message_broker/etc/broker.xml' file.
     - The executable for running the artemis message broker is found in '/message_broker/bin'. Run `./artemis run` in order to start the broker.
 - Spring Boot is run by 'gradlew' executable in /rest_api. Run `sudo ./gradlew` to start running the application.
 - Open 127.0.0.1 in the web browser where you can configure a user account and create devices, sensors, subscriptions and publications.
 
## Publish - subscribe behavior

The sensor data can be published using rest end point and message broker with MQTT support. Both methods accept the payload as JSON. An example for publishing data with rest endpoint using curl command is 
```json
curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' --header 'Authorization: Bearer <access_token>' -d '{
  "data": "24",
  "description": "test",
  "sensorId": 1,
  "timestamp": "2017-03-19T09:47:44.526Z",
  "topic" : "gps"
}' 'http://127.0.0.1:8080/api/sensors/{id}'
```
The user can also publish data using MQTT protocol with MQTT client applications by sending the data of the following form to the topic 'gps'.
```json
  "data": "24",
  "description": "test",
  "sensorId": 1,
  "timestamp": "2017-03-19T09:47:44.526Z"
```
The user can subscribe to topics defined for subscription entity using MQTT client applications by providing the topic name as gps. Whenever a new data is published, it will be stored in Cassandra and can be received by the user.

## Storing data
Data can be stored via the REST endpoint and the message broker. Both methods accept the payload as JSON.
An example is 
`{"sensorId":1,"data":"24","description":"test","timestamp":"2016-07-30T21:29:34+02:00"}`

## Retreiving data
Using the REST end point data can be retreived.
`http://127.0.0.1:8080/api/sensors/1/data` where 1 is the sensor id
`http://127.0.0.1:8080/api/sensors/1/data?dates=2016-07-30,2016-07-2` where multiple dates can be specified

*  The requests should pass the Authentication token

# Future work
 - Need to implement authentication for Publish Subscribe
