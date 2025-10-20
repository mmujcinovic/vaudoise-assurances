package io.mmujcinovic.vaudoiseassurances.controllers;

import io.mmujcinovic.vaudoiseassurances.entities.Contract;
import io.mmujcinovic.vaudoiseassurances.records.requests.ContractCostReqRecord;
import io.mmujcinovic.vaudoiseassurances.records.requests.ContractReqRecord;
import io.mmujcinovic.vaudoiseassurances.records.responses.ContractRespRecord;
import io.mmujcinovic.vaudoiseassurances.records.responses.ContractSumCostRespRecord;
import io.mmujcinovic.vaudoiseassurances.services.ContractService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/contracts")
@Validated
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    /**
     * Creates a new contract for the specified client.
     * <p>
     * The client's identifier is provided as a path variable, and the request body
     * must contain a valid {@code ContractReqRecord}. The creation is delegated to
     * the service layer, which associates the new contract with the given client.
     * On success, this endpoint returns an HTTP {@code 201 Created} response with
     * the resource location in the <i>Location</i> header and the corresponding
     * response record in the body.
     *
     * @param clientId the identifier of the client for whom the contract is created
     * @param contract the request payload containing the contract data
     * @return a {@code ResponseEntity} with HTTP status 201 and the created contract record
     */
    @PostMapping(value = "/{clientId}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContractRespRecord> createContractForClient(
            @PathVariable("clientId") @Positive Long clientId,
            @RequestBody @Valid ContractReqRecord contract) {
        Contract createdContract = this.contractService.createContractForClient(clientId, contract);
        return ResponseEntity.created(URI.create("/contracts/" + createdContract.getId()))
                .body(ContractService.mapToContractRespRecord(createdContract));
    }

    /**
     * Retrieves the list of active contracts for a specific client,
     * optionally filtered by update date.
     * <p>
     * The client's identifier is provided as a path variable. Two optional query
     * parameters, {@code updatedAfter} and {@code updatedBefore}, can be used
     * to restrict the result set based on the contract's last update date.
     * The service ensures that the client exists and is active before fetching
     * contracts. The method returns an HTTP 200 response containing the list
     * of matching contract records.
     *
     * @param clientId the identifier of the client whose active contracts are requested
     * @param updatedAfter an optional lower bound for filtering by last update
     * @param updatedBefore an optional upper bound for filtering by last update
     * @return a {@code ResponseEntity} with HTTP status 200 and the list of contract records
     */
    @GetMapping("/{clientId}")
    public ResponseEntity<List<ContractRespRecord>> findActiveContractsForClient(
            @PathVariable("clientId") @Positive Long clientId,
            @RequestParam(required = false) LocalDate updatedAfter,
            @RequestParam(required = false) LocalDate updatedBefore) {
        List<Contract> activeContractList =
                this.contractService.findActiveContractsForClient(clientId, updatedAfter, updatedBefore);
        return ResponseEntity.ok(ContractService.mapToContractRespRecordList(activeContractList));
    }

    /**
     * Retrieves the total cost of all active contracts for a specific client.
     * <p>
     * The client's identifier is provided as a path variable. The method delegates
     * the computation to the service layer and returns an HTTP 200 response
     * containing the total cost wrapped in a {@code ContractSumCostRespRecord}.
     *
     * @param clientId the identifier of the client whose active contract cost total is requested
     * @return a {@code ResponseEntity} with HTTP status 200 and the summed cost of active contracts
     */
    @GetMapping("/{clientId}/sumCost")
    public ResponseEntity<ContractSumCostRespRecord> findSumActiveContractsCostForClient(
            @PathVariable("clientId") @Positive Long clientId) {
        BigDecimal sumActiveContractsCost = this.contractService.findSumActiveContractsCostForClient(clientId);
        return ResponseEntity.ok(new ContractSumCostRespRecord(clientId, sumActiveContractsCost));
    }

    /**
     * Updates the cost of an existing contract.
     * <p>
     * The contract identifier is provided as a path variable, and the new cost
     * information is supplied in the request body via {@code ContractCostReqRecord}.
     * The update logic is handled by the service layer. Upon success, this method
     * returns an HTTP 200 response containing the updated contract record.
     *
     * @param id the identifier of the contract to update
     * @param costAmount the request payload containing the new cost data
     * @return a {@code ResponseEntity} with HTTP status 200 and the updated contract record
     */
    @PutMapping(value = "/{id}/cost",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContractRespRecord> updateContractCost(
            @PathVariable @Positive Long id,
            @RequestBody ContractCostReqRecord costAmount) {
        Contract contract = this.contractService.updateContractCost(id, costAmount);
        return ResponseEntity.ok(ContractService.mapToContractRespRecord(contract));
    }
}
