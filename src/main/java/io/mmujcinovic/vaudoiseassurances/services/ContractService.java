package io.mmujcinovic.vaudoiseassurances.services;

import io.mmujcinovic.vaudoiseassurances.entities.Contract;
import io.mmujcinovic.vaudoiseassurances.exceptions.BadRequestException;
import io.mmujcinovic.vaudoiseassurances.records.requests.ContractCostReqRecord;
import io.mmujcinovic.vaudoiseassurances.records.requests.ContractReqRecord;
import io.mmujcinovic.vaudoiseassurances.records.responses.ContractRespRecord;
import io.mmujcinovic.vaudoiseassurances.repositories.ContractRepository;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Validated
public class ContractService {

    private final ContractRepository contractRepository;

    private final ObjectProvider<ClientService> clientService;

    public ContractService(ContractRepository contractRepository,
                           ObjectProvider<ClientService> clientService) {
        this.contractRepository = contractRepository;
        this.clientService = clientService;
    }

    /**
     * Creates a new contract for a specific client.
     * <p>
     * The method first checks whether the client with the given ID exists
     * and is active. If not, a {@code BadRequestException} is thrown. It also
     * validates the date range of the request, ensuring that the start date
     * is not after the end date. If both conditions are satisfied, a new
     * {@code Contract} entity is initialized with the provided data (or
     * default values) and persisted via the repository.
     *
     * @param clientId the identifier of the client for whom the contract is created
     * @param contractReq the request record containing the contract data
     * @return the newly created {@code Contract}
     * @throws BadRequestException if the client is not found, is inactive,
     *                             or the contract dates are invalid
     */
    public Contract createContractForClient(@NotNull @Positive Long clientId,
                                            @NotNull @Valid ContractReqRecord contractReq) {
        this.clientService.getObject().findActiveClient(clientId)
                .orElseThrow(() ->
                        new BadRequestException("Not found client by id: " + clientId,
                                "Create contract failed"));
        if ((null != contractReq.startDate()) && (null != contractReq.endDate())
                && contractReq.startDate().isAfter(contractReq.endDate())) {
            throw new BadRequestException("Invalid contract dates: startDate is after endDate",
                    "Create contract failed");
        }
        Contract contract = new Contract();
        contract.setClientId(clientId);
        contract.setStartDate(contractReq.startDate() == null ? LocalDate.now()
                : contractReq.startDate());
        contract.setEndDate(contractReq.endDate());
        contract.setCostAmount(contractReq.costAmount());
        return this.contractRepository.save(contract);
    }

    /**
     * Retrieves all active contracts for a given client, optionally filtered by an update date range.
     * <p>
     * A contract is considered active if its end date is {@code null} or strictly
     * after today's date. If {@code updatedAfter} and/or {@code updatedBefore}
     * are provided, the results are further filtered by the contract's updateDate.
     * If the client does not exist or is inactive, a BadRequestException is thrown.
     *
     * @param clientId the identifier of the client
     * @param updatedAfter the lower bound for the contract's updateDate (inclusive), or {@code null} to ignore
     * @param updatedBefore the upper bound for the contract's updateDate (inclusive), or {@code null} to ignore
     * @return a list of active contracts matching the criteria
     * @throws BadRequestException if the client does not exist or is inactive
     */
    public List<Contract> findActiveContractsForClient(
            @NotNull @Positive Long clientId,
            @Nullable LocalDate updatedAfter,
            @Nullable LocalDate updatedBefore) {
        this.clientService.getObject().findActiveClient(clientId)
                .orElseThrow(() ->
                        new BadRequestException("Not found client by id: " + clientId,
                                "Find active contracts failed"));
        return this.contractRepository
                .findActiveContractsForClientBetweenUpdatedDate(clientId,
                        LocalDate.now(), updatedAfter, updatedBefore);
    }

    /**
     * Calculates the total cost of all active contracts for a specific client.
     * <p>
     * The method first checks whether the client with the given ID exists and
     * is active. If not, a {@code BadRequestException} is thrown. If the
     * validation succeeds, the repository is queried to compute the sum of
     * active contracts at the current date.
     *
     * @param clientId the identifier of the client whose active contracts cost is requested
     * @return the total cost of the client's active contracts
     * @throws BadRequestException if the client does not exist or is inactive
     */
    public BigDecimal findSumActiveContractsCostForClient(
            @NotNull @Positive Long clientId) {
        this.clientService.getObject().findActiveClient(clientId)
                .orElseThrow(() ->
                        new BadRequestException("Not found client by id: " + clientId,
                                "Sum active contracts cost failed"));
        return this.contractRepository.findSumActiveContractsCostForClientAt(clientId, LocalDate.now());
    }

    /**
     * Closes all active contracts for a specific client by setting their end date
     * to the current day.
     * <p>
     * The method first verifies that the client with the given ID exists and is
     * active. If not, a {@code BadRequestException} is thrown. Once validated,
     * all active contracts for the client are retrieved, their end date is updated
     * to {@code LocalDate.now()}, and the changes are persisted in bulk.
     *
     * @param clientId the identifier of the client whose active contracts must be closed
     * @throws BadRequestException if the client does not exist or is inactive
     */
    public void clotureActiveContractsForClient(@NotNull @Positive Long clientId) {
        this.clientService.getObject().findActiveClient(clientId)
                .orElseThrow(() ->
                        new BadRequestException("Not found client by id: " + clientId,
                                "Cloture active contracts failed"));
        List<Contract> activeContractList = this.contractRepository
                .findActiveContractsForClient(clientId, LocalDate.now());
        activeContractList.forEach(contract ->
                contract.setEndDate(LocalDate.now()));
        this.contractRepository.saveAll(activeContractList);
    }

    /**
     * Updates only the cost amount of an active contract.
     * <p>
     * The method first verifies that a contract with the given ID exists
     * and is currently active. If not, a {@code BadRequestException} is thrown.
     * When the contract is found, its cost amount is updated with the value
     * provided in the request record. In accordance with the requirements,
     * the update date is also set to the current date, and the entity is
     * persisted via the repository.
     *
     * @param id the identifier of the contract to update
     * @param costAmount the request record containing the new cost value
     * @return the updated {@code Contract}
     * @throws BadRequestException if the contract does not exist or is inactive
     */
    public Contract updateContractCost(@NotNull @Positive Long id,
                                       @NotNull @Valid ContractCostReqRecord costAmount) {
        Contract contract = this.findActiveContract(id)
                .orElseThrow(() ->
                        new BadRequestException("Not found contract by id: " + id,
                                "Update contract cost failed"));
        contract.setCostAmount(costAmount.costAmount());
        contract.setUpdateDate(LocalDate.now());
        return this.contractRepository.save(contract);
    }

    /**
     * Retrieves an active contract by its identifier at the current date.
     * <p>
     * This method delegates the lookup to the repository, which returns
     * only contracts that are still active as of {@code LocalDate.now()}.
     * The result is wrapped in an {@code Optional}, indicating that the
     * contract may not exist or may no longer be active.
     *
     * @param id the identifier of the contract to retrieve
     * @return an {@code Optional} containing the active {@code Contract} if found,
     *         or empty otherwise
     */
    private Optional<Contract> findActiveContract(Long id) {
        return this.contractRepository.findActiveContractAt(id, LocalDate.now());
    }

    /**
     * Converts a {@code Contract} entity into its corresponding response record.
     * <p>
     * This method extracts the relevant fields from the given contract
     * (identifier, client ID, start date, end date, and cost amount)
     * and returns a new {@code ContractRespRecord} containing that data.
     *
     * @param contract the {@code Contract} entity to convert
     * @return a {@code ContractRespRecord} representing the contract
     */
    public static ContractRespRecord mapToContractRespRecord(Contract contract) {
        return new ContractRespRecord(contract.getId(), contract.getClientId(),
                contract.getStartDate(), contract.getEndDate(), contract.getCostAmount());
    }

    /**
     * Converts a list of {@code Contract} entities into a list of response records.
     * <p>
     * Each contract in the provided list is individually converted using
     * {@link #mapToContractRespRecord(Contract)}, and the resulting records are
     * collected into a new list.
     *
     * @param contractList the list of {@code Contract} entities to convert
     * @return a list of {@code ContractRespRecord} instances
     */
    public static List<ContractRespRecord> mapToContractRespRecordList(List<Contract> contractList) {
        return contractList.stream().map(ContractService::mapToContractRespRecord).toList();
    }
}
