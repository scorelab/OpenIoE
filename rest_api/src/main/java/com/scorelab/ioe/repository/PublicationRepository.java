package com.scorelab.ioe.repository;

import com.scorelab.ioe.domain.Publication;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Publication entity.
 */
@SuppressWarnings("unused")
public interface PublicationRepository extends JpaRepository<Publication,Long> {

    @Query("select publication from Publication publication where publication.user.login = ?#{principal.username}")
    List<Publication> findByUserIsCurrentUser();

}
