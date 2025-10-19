package io.mmujcinovic.vaudoiseassurances.repositories;

import io.mmujcinovic.vaudoiseassurances.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Retrieves an active client by its identifier.
     * <p>
     * Only clients with {@code active = true} are considered. If no matching
     * active client is found, an empty {@code Optional} is returned.
     *
     * @param id the identifier of the client
     * @return an {@code Optional} containing the active client if found, or empty otherwise
     */
    Optional<Client> findByIdAndActiveTrue(@Param("id") Long id);
}
