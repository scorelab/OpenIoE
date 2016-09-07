# OpenIoE
Open-source middleware platform for building, managing, and integrating connected products with the Internet of Everything.

# Main features
 - A high-perfomance Java server side with Spring Boot
 - Message broker support with ActiveMQ Artemis
 - Multi Protocol support (MQTT, AMQP)
 - NoSQL data storage for sensor data (Cassandra)
 - Support for timeline series modelling

# Docker
 - All the services required to run the platform can be easily started via bundled docker-compose.yml located in rest_api/src/main/docker
 - Browse to rest_api/src/main/docker and run `docker-compose up -d`
 - Open 127.0.0.1 in the web browser where you can configure a user account and create devices and sensors
 - To stop the platform, run `docker-compose stop` in the same folder as above
 - Logs can be observed via `docker-compose logs [CONTAINER]` (ex: ioe-app)

# Installation
 - Clone the Github repository
 - Install and configure Cassandra (http://cassandra.apache.org/doc/latest/getting_started/installing.html)
 - Configure 'ioe' section in application.yml that can be found in rest_api/src/main/resources/config

# Usage
 - Make sure Cassandra server is up
 - Run Artemis message broker in a terminal using executable in /message_broker/bin
 - Run Spring Boot backend by 'gradlew' executable in /rest_api
 - Open 127.0.0.1 in the web browser where you can configure a user account and create devices and sensors

## Storing data
Data can be stored via the REST endpoint and the message broker. Both methods accept the payload as JSON.
An example is 
`{"sensorId":1,"data":"24","description":"test","timestamp":"2016-07-30T21:29:34+02:00"}`

## Retreiving data
Using the REST end point data can be retreived.
`http://127.0.0.1:8080/api/sensors/1/data` where 1 is the sensor id
`http://127.0.0.1:8080/api/sensors/1/data?dates=2016-07-30,2016-07-2` where multiple dates can be specified

*  The request should pass the Authentication token

# Future work
 - Docker container for easier dev and prod deployments (ongoing)
 - Authentication and access control for device and sensor end points
 - Detailed documentation
