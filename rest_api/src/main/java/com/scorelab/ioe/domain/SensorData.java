package com.scorelab.ioe.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A SensorData.
 */
@Entity
@Table(name = "sensor_data")
public class SensorData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "data")
    private String data;

    @Column(name = "description")
    private String description;

    @Column(name = "timestamp")
    private ZonedDateTime timestamp;

    @ManyToOne
    private Sensor sensor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SensorData sensorData = (SensorData) o;
        if(sensorData.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, sensorData.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SensorData{" +
            "id=" + id +
            ", data='" + data + "'" +
            ", description='" + description + "'" +
            ", timestamp='" + timestamp + "'" +
            '}';
    }
}
