package com.scorelab.ioe.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Sensor.
 */
@Entity
@Table(name = "sensor")
public class Sensor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "sensor_id")
    private Long sensorId;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    private String type;

    @Column(name = "serial")
    private String serial;

    @Column(name = "store_type")
    private String storeType;

    @Column(name = "table_id")
    private String tableId;

    @ManyToOne
    private Device device;

    @OneToMany(mappedBy = "sensor")
    @JsonIgnore
    private Set<Subscription> subscriptions = new HashSet<>();

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "sensor")
    @JsonIgnore
    private Set<SensorData> sensorData = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSensorId() {
        return sensorId;
    }

    public void setSensorId(Long sensorId) {
        this.sensorId = sensorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Set<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<SensorData> getSensorData() {
        return sensorData;
    }

    public void setSensorData(Set<SensorData> sensorData) {
        this.sensorData = sensorData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sensor sensor = (Sensor) o;
        if(sensor.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, sensor.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Sensor{" +
            "id=" + id +
            ", sensorId='" + sensorId + "'" +
            ", name='" + name + "'" +
            ", description='" + description + "'" +
            ", type='" + type + "'" +
            ", serial='" + serial + "'" +
            ", storeType='" + storeType + "'" +
            ", tableId='" + tableId + "'" +
            '}';
    }
}
