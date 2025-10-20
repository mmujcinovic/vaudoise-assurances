package io.mmujcinovic.vaudoiseassurances.repositories;

import io.mmujcinovic.vaudoiseassurances.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    /**
     * Retrieves a contract by its identifier if it is active at the specified date.
     * <p>
     * A contract is considered active if its end date is {@code null} or strictly
     * after the provided reference date. The lookup is performed using the
     * contract's unique identifier. The result is returned as an {@code Optional}
     * to indicate that the contract may not exist or may no longer be active.
     *
     * @param id the identifier of the contract to retrieve
     * @param at the reference date used to determine if the contract is active
     * @return an {@code Optional} containing the contract if active, or empty otherwise
     */
    @Query("""
            select  c
            from    Contract c
            where   c.id = :id
            and     (c.endDate is null or :at < c.endDate)
            """)
    Optional<Contract> findActiveContractAt(@Param("id") Long id,
                                            @Param("at") LocalDate at);

    /**
     * Retrieves all active contracts for a specific client at the given date.
     * <p>
     * A contract is considered active if its end date is {@code null}
     * or strictly after the specified reference date. This query filters
     * contracts by the client's identifier and returns only those that
     * match the active criteria.
     *
     * @param clientId the identifier of the client whose contracts are requested
     * @param at the reference date used to determine if contracts are active
     * @return a list of active {@code Contract} entities for the given client
     */
    @Query("""
            select  c
            from    Contract c
            where   c.clientId = :clientId
            and     (c.endDate is null or :at < c.endDate)
            """)
    List<Contract> findActiveContractsForClient(@Param("clientId") Long clientId,
                                                @Param("at") LocalDate at);

    /**
     * Retrieves all active contracts for a given client, optionally filtered by an update date range.
     * <p>
     * A contract is deemed active if its end date is {@code null} or strictly later than
     * the provided reference date. If {@code updatedAfter} and/or {@code updatedBefore}
     * are provided, only contracts whose {@code updateDate} falls within the specified
     * bounds are returned. Contracts with a {@code null} updateDate are included only
     * if no update-date filters are specified.
     *
     * @param clientId the identifier of the client whose contracts are requested
     * @param at the reference date used to determine whether contracts are active
     * @param updatedAfter the inclusive lower bound for {@code updateDate}, or {@code null} to leave it unfiltered
     * @param updatedBefore the inclusive upper bound for {@code updateDate}, or {@code null} to leave it unfiltered
     * @return a list of active {@code Contract} entities matching the given criteria
     */
    @Query("""
            select  c
            from    Contract c
            where   c.clientId = :clientId
            and     (c.endDate is null or :at < c.endDate)
            and     (coalesce(:updatedAfter, null) is null or :updatedAfter <= c.updateDate)
            and     (coalesce(:updatedBefore, null) is null or :updatedBefore >= c.updateDate)
            """)
    List<Contract> findActiveContractsForClientBetweenUpdatedDate(@Param("clientId") Long clientId,
                                                                  @Param("at") LocalDate at,
                                                                  @Param("updatedAfter") LocalDate updatedAfter,
                                                                  @Param("updatedBefore") LocalDate updatedBefore);

    /**
     * Calculates the total cost of all active contracts for a specific client
     * at a given date.
     * <p>
     * A contract is considered active if its end date is {@code null} or
     * strictly after the provided reference date. The {@code COALESCE} function
     * ensures that zero is returned if no matching contracts exist.
     *
     * @param clientId the identifier of the client
     * @param at the reference date used to determine which contracts are active
     * @return a {@code BigDecimal} representing the total cost of active contracts,
     *         or zero if none are found
     */
    @Query("""
            select  coalesce(sum(c.costAmount), 0) as costAmountSum
            from    Contract c
            where   c.clientId = :clientId
            and     (c.endDate is null or :at < c.endDate)
            """)
    BigDecimal findSumActiveContractsCostForClientAt(@Param("clientId") Long clientId,
                                                     @Param("at") LocalDate at);
}
