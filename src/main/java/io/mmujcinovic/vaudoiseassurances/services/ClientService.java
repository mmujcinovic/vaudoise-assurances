package io.mmujcinovic.vaudoiseassurances.services;

import io.mmujcinovic.vaudoiseassurances.entities.Client;
import io.mmujcinovic.vaudoiseassurances.entities.Company;
import io.mmujcinovic.vaudoiseassurances.entities.Person;
import io.mmujcinovic.vaudoiseassurances.exceptions.BadRequestException;
import io.mmujcinovic.vaudoiseassurances.records.requests.ClientReqInterfaceRecord;
import io.mmujcinovic.vaudoiseassurances.records.requests.ClientReqRecord;
import io.mmujcinovic.vaudoiseassurances.records.requests.CompanyReqRecord;
import io.mmujcinovic.vaudoiseassurances.records.requests.PersonReqRecord;
import io.mmujcinovic.vaudoiseassurances.records.responses.ClientRespInterfaceRecord;
import io.mmujcinovic.vaudoiseassurances.records.responses.ClientRespRecord;
import io.mmujcinovic.vaudoiseassurances.records.responses.CompanyRespRecord;
import io.mmujcinovic.vaudoiseassurances.records.responses.PersonRespRecord;
import io.mmujcinovic.vaudoiseassurances.repositories.ClientRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Transactional
@Validated
public class ClientService {

    private final ClientRepository clientRepository;

    private final ObjectProvider<ContractService> contractService;

    public ClientService(ClientRepository clientRepository,
                         ObjectProvider<ContractService> contractService) {
        this.clientRepository = clientRepository;
        this.contractService = contractService;
    }

    /**
     * Creates a new client based on the concrete subtype of the request record.
     * <p>
     * If the request is a {@code CompanyReqRecord}, this method first checks
     * whether an active company with the same identifier already exists.
     * If so, a {@code BadRequestException} is thrown. Otherwise, a new
     * {@code Company} entity is initialized with the common client fields
     * and the company-specific data, marked as active, then persisted.
     * <p>
     * If the request is a {@code PersonReqRecord}, a new {@code Person} entity
     * is created, populated with common fields and the person-specific data
     * (such as birthdate), marked as active, and then saved.
     * <p>
     * If the provided request does not match any supported subtype
     * (which should not happen if the sealed interface is enforced),
     * a {@code BadRequestException} is thrown.
     *
     * @param clientReq the request record containing the data needed to create the client
     * @return the newly created and saved {@code Client}
     * @throws BadRequestException if the client type is unsupported or if the company identifier already exists
     */    public Client createClient(@NotNull @Valid ClientReqInterfaceRecord clientReq) {
        // Case Company
        if (clientReq instanceof CompanyReqRecord companyRequestRecord) {
            if (this.clientRepository
                    .existsByActiveCompanyIdentifier(companyRequestRecord.companyIdentifier())) {
                throw new BadRequestException("A company with identifier '%s' already exists"
                        .formatted(companyRequestRecord.companyIdentifier()),
                        "Create client failed");
            }
            Company company = this.initCommonFieldClient(new Company(), companyRequestRecord.clientRequestRecord());
            company.setActive(true);
            company.setCompanyIdentifier(companyRequestRecord.companyIdentifier());
            return this.clientRepository.save(company);
        // Case Person
        } else if (clientReq instanceof PersonReqRecord personReq) {
            Person person = this.initCommonFieldClient(new Person(), personReq.clientRequestRecord());
            person.setActive(true);
            person.setBirthdate(personReq.birthdate());
            return this.clientRepository.save(person);
        }
        // /!\ Should never happen if the interface is sealed
        throw new BadRequestException(
                "Client type mismatch: entity=" + clientReq.getClass().getSimpleName()
                        + ". Expected matching type: CompanyReqRecord or PersonReqRecord.",
                "Create client failed"
        );
    }

    /**
     * Retrieves an active client by its identifier.
     * <p>
     * This method delegates the lookup to the repository and returns only
     * clients that are marked as active. The result is wrapped in an
     * {@code Optional} to indicate that the client may not exist or may
     * have been deactivated.
     *
     * @param id the unique identifier of the client to retrieve
     * @return an {@code Optional} containing the active {@code Client} if found,
     *         or empty otherwise
     */
    public Optional<Client> findActiveClient(@NotNull @Positive Long id) {
        return this.clientRepository.findByIdAndActiveTrue(id);
    }

    /**
     * Updates an existing active client based on its identifier and the concrete subtype
     * of the provided request record.
     * <p>
     * First, the method attempts to retrieve the active client with the given ID.
     * If no active client is found, a {@code BadRequestException} is thrown. Once
     * the client is retrieved, its concrete type is matched with the corresponding
     * request record subtype (either {@code CompanyReqRecord} or {@code PersonReqRecord}).
     * The common fields and any subtype-specific attributes are updated accordingly,
     * and the modified entity is then persisted via the repository.
     * <p>
     * If the actual client type does not match the request record type (which
     * should not occur if the sealed interface is respected), a
     * {@code BadRequestException} is thrown.
     *
     * @param id the identifier of the active client to update
     * @param clientReq the request record containing the updated client data
     * @return the updated and saved {@code Client}
     * @throws BadRequestException if the client is not found or if the types do not match
     */
    public Client updateActiveClient(@NotNull @Positive Long id,
                                     @NotNull @Valid ClientReqInterfaceRecord clientReq) {
        Client client = this.findActiveClient(id).orElseThrow(() ->
                new BadRequestException("Not found client by id: " + id,
                        "Update client failed"));
        // Case Company
        if ((client instanceof Company company)
                && (clientReq instanceof CompanyReqRecord companyReq)) {
            this.initCommonFieldClient(company, companyReq.clientRequestRecord());
            return this.clientRepository.save(company);
        // Case Person
        } else if ((client instanceof Person person)
                && (clientReq instanceof PersonReqRecord personReq)) {
            this.initCommonFieldClient(person, personReq.clientRequestRecord());
            return this.clientRepository.save(person);
        }
        // /!\ Should never happen if the interface is sealed
        throw new BadRequestException(
                "Client type mismatch: entity=" + client.getClass().getSimpleName()
                        + ", reqRecord=" + clientReq.getClass().getSimpleName()
                        + ". Expected matching types: Company/CompanyReqRecord or Person/PersonReqRecord",
                "Update client failed"
        );
    }

    /**
     * Deactivates a client by its identifier and closes all its active contracts.
     * <p>
     * The method first attempts to retrieve the active client with the specified ID.
     * If no such client is found, a {@code BadRequestException} is thrown. Once
     * the client is retrieved, all of its active contracts are closed via the
     * contract service. The client is then marked as inactive and persisted
     * in the repository.
     *
     * @param id the identifier of the client to deactivate
     * @throws BadRequestException if no active client is found with the given ID
     */
    public void deactivateClient(@NotNull @Positive Long id) {
        Client client = this.findActiveClient(id).orElseThrow(() ->
                new BadRequestException("Not found client by id: " + id,
                        "Deactivate client failed"));
        this.contractService.getObject().clotureActiveContractsForClient(id);
        client.setActive(false);
        this.clientRepository.save(client);
    }

    /**
     * Converts a {@code Client} entity into its corresponding response record subtype.
     * <p>
     * A base {@code ClientRespRecord} is first created with the fields common to
     * all client types. Then, depending on whether the entity is a
     * {@code Company} or a {@code Person}, the method delegates to the
     * appropriate helper to produce the correct subtype of
     * {@code ClientRespInterfaceRecord}. If the entity type is not supported
     * (which should not happen if the sealed hierarchy is enforced),
     * a {@code BadRequestException} is thrown.
     *
     * @param client the {@code Client} entity to convert
     * @return a concrete implementation of {@code ClientRespInterfaceRecord}
     *         corresponding to the entity subtype
     * @throws BadRequestException if the entity type is not supported
     */
    public static ClientRespInterfaceRecord mapToClientRespRecord(
            @NotNull Client client) {
        ClientRespRecord clientRecord =
                new ClientRespRecord(client.getId(), client.getName(), client.getPhone(), client.getEmail(), client.isActive());
        // Case Company
        if (client instanceof Company company) {
            return mapToCompanyRespRecord(clientRecord, company);
        // Case Company
        } else if (client instanceof Person person) {
            return mapToPersonRespRecord(clientRecord, person);
        }
        // /!\ Should never happen if the interface is sealed
        throw new BadRequestException(
                "Client type mismatch: entity=" + client.getClass().getSimpleName()
                        + ". Expected matching type: Company or Person.",
                "Create client failed"
        );
    }

    /**
     * Builds a {@code CompanyRespRecord} using the common client data and
     * company-specific fields.
     *
     * @param clientRecord the base record containing the common client fields
     * @param company the {@code Company} entity providing additional data
     * @return a {@code CompanyRespRecord} combining both common and company-specific fields
     */    private static CompanyRespRecord mapToCompanyRespRecord(
            ClientRespRecord clientRecord,
            Company company) {
        return new CompanyRespRecord(clientRecord, company.getCompanyIdentifier());
    }

    /**
     * Builds a {@code PersonRespRecord} using the common client data and
     * person-specific fields.
     *
     * @param clientRecord the base record containing the common client fields
     * @param person the {@code Person} entity providing additional data
     * @return a {@code PersonRespRecord} combining both common and person-specific fields
     */
    private static PersonRespRecord mapToPersonRespRecord(
            ClientRespRecord clientRecord,
            Person person) {
        return new PersonRespRecord(clientRecord, person.getBirthdate());
    }

    /**
     * Initializes the common fields shared by all types of {@code Client} entities.
     * <p>
     * This method sets the name, phone, and email attributes of the given client
     * based on the data provided in the request record, then returns the same
     * client instance. It is intended to centralize the initialization logic
     * for fields that are common to both {@code Company} and {@code Person}.
     *
     * @param client the client entity to update (a subtype of {@code Client})
     * @param clientReq the request record containing the new values
     * @param <T> a concrete subtype of {@code Client}
     * @return the updated client instance
     */
    private <T extends Client> T initCommonFieldClient(T client,
                                                       ClientReqRecord clientReq) {
        client.setName(clientReq.name());
        client.setPhone(clientReq.phone());
        client.setEmail(clientReq.email());
        return client;
    }
}
