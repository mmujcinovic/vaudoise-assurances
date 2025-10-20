package io.mmujcinovic.vaudoiseassurances.repositories;

import io.mmujcinovic.vaudoiseassurances.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    /**
     * Checks whether there is an active company with the given identifier.
     * <p>
     * This query uses an {@code exists(...)} clause for efficiency:
     * it returns {@code true} as soon as a matching record is found,
     * without scanning more rows than necessary.
     *
     * @param companyIdentifier the unique identifier of the company to check
     * @return {@code true} if a company with this identifier exists and is active,
     *         {@code false} otherwise
     */
    @Query("""
            select exists (
                select  c
                from    Company c
                where   c.companyIdentifier = :companyIdentifier
                and     c.active = true)
           """)
    boolean existsByActiveCompanyIdentifier(@Param("companyIdentifier") String companyIdentifier);
}
