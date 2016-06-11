package com.scorelab.ioe.repository;

import com.scorelab.ioe.domain.Sensor;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Sensor entity.
 */
@SuppressWarnings("unused")
public interface SensorRepository extends JpaRepository<Sensor,Long> {

    @Query("select sensor from Sensor sensor where sensor.user.login = ?#{principal.username}")
    List<Sensor> findByUserIsCurrentUser();

}
