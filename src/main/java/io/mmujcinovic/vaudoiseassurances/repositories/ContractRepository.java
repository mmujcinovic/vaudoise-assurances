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
     * Retrieves all active contracts for a specific client at a given date,
     * optionally filtered by a range of update dates.
     * <p>
     * A contract is considered active if its end date is {@code null} or
     * strictly after the specified reference date. The query can be further
     * constrained using the optional {@code updatedBefore} and
     * {@code updatedAfter} parameters. If either is {@code null}, the
     * corresponding bound defaults to the contract's own update date
     * via {@code COALESCE}.
     *
     * @param clientId the identifier of the client whose contracts are requested
     * @param at the reference date used to determine if contracts are active
     * @param updatedBefore an optional upper bound for the update date filter
     * @param updatedAfter an optional lower bound for the update date filter
     * @return a list of matching active {@code Contract} entities
     */
    @Query("""
            select  c
            from    Contract c
            where   c.clientId = :clientId
            and     (c.endDate is null or :at < c.endDate)
            and     (c.updateDate between coalesce(:updatedBefore, c.updateDate) and coalesce(:updatedAfter, c.updateDate))
            """)
    List<Contract> findActiveContractsForClientBetweenUpdatedDate(@Param("clientId") Long clientId,
                                                                  @Param("at") LocalDate at,
                                                                  @Param("updatedBefore") LocalDate updatedBefore,
                                                                  @Param("updatedAfter") LocalDate updatedAfter);

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
