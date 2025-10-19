package io.mmujcinovic.vaudoiseassurances.controllers;

import io.mmujcinovic.vaudoiseassurances.entities.Client;
import io.mmujcinovic.vaudoiseassurances.records.responses.ClientRespInterfaceRecord;
import io.mmujcinovic.vaudoiseassurances.records.requests.ClientReqInterfaceRecord;
import io.mmujcinovic.vaudoiseassurances.services.ClientService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/clients")
@Validated
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Creates a new client based on the provided request payload.
     * <p>
     * The request body must contain a valid implementation of
     * {@code ClientReqInterfaceRecord}. The creation is delegated to the
     * service layer. Upon success, this method returns an HTTP 201 Created
     * response with the resource location in the <i>Location</i> header and
     * the corresponding response record in the body.
     *
     * @param clientReq the request payload containing the client data
     * @return a {@code ResponseEntity} with HTTP status 201 and the created client record
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientRespInterfaceRecord> createClient(
            @RequestBody @Valid ClientReqInterfaceRecord clientReq) {
        Client createdClient = this.clientService.createClient(clientReq);
        return ResponseEntity.created(URI.create("/clients/" + createdClient.getId()))
                .body(ClientService.mapToClientRespRecord(createdClient));
    }

    /**
     * Retrieves an active client by its identifier.
     * <p>
     * If a client with the provided ID exists and is marked as active, this method
     * returns an HTTP 200 response containing the corresponding response record.
     * Otherwise, it returns an HTTP 404 Not Found with no content.
     *
     * @param id the identifier of the client to retrieve
     * @return a {@code ResponseEntity} with HTTP 200 and the client record if found,
     *         or HTTP 404 if no active client exists with the given ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientRespInterfaceRecord> findActiveClient(
            @PathVariable("id") @Positive Long id) {
        Optional<Client> clientOpt =  this.clientService.findActiveClient(id);
        return clientOpt.map(client ->
                ResponseEntity.ok(ClientService.mapToClientRespRecord(client)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Updates an active client using the provided identifier and request payload.
     * <p>
     * The client ID is supplied as a path variable, and the request body must
     * contain a valid implementation of {@code ClientReqInterfaceRecord}. The
     * update logic is handled by the service layer. If the operation succeeds,
     * this method returns an HTTP 200 response with the updated client record.
     *
     * @param id the identifier of the active client to update
     * @param clientReq the request payload containing the updated client data
     * @return a {@code ResponseEntity} with HTTP status 200 and the updated client record
     */
    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<ClientRespInterfaceRecord> updateActiveClient(
            @PathVariable("id") @Positive Long id,
            @RequestBody @Valid ClientReqInterfaceRecord clientReq) {
        Client client = this.clientService.updateActiveClient(id, clientReq);
        return ResponseEntity.ok(ClientService.mapToClientRespRecord(client));
    }

    /**
     * Deactivates a client by its identifier.
     * <p>
     * The client ID is provided as a path variable. The deactivation process
     * is delegated to the service layer, which marks the client as inactive
     * and performs any related operations. If the action completes
     * successfully, this method returns an HTTP 204 response with no content.
     *
     * @param id the identifier of the client to deactivate
     * @return a {@code ResponseEntity} with HTTP status 204 (no content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateClient(@PathVariable("id") @Positive Long id) {
        this.clientService.deactivateClient(id);
        return ResponseEntity.noContent().build();
    }
}
