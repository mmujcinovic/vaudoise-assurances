package io.mmujcinovic.vaudoiseassurances.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contract", indexes = {
        @Index(name = "idx_contract_client_end", columnList = "client_id,end_date"),
        @Index(name = "idx_contract_client_end_update", columnList = "client_id,end_date,update_date")
})
@NoArgsConstructor
@Getter
@Setter
// Enables JPA auditing (e.g., automatic handling of creation and update timestamps) by registering the AuditingEntityListener for this entity
@EntityListeners(AuditingEntityListener.class)
public class Contract implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contract_seq")
    @SequenceGenerator(name = "contract_seq", sequenceName = "contract_seq", allocationSize = 1)
    private Long id; // Id

    @NotNull
    @Column(name = "client_id", nullable = false, updatable = false)
    private Long clientId; // Client

    @NotNull
    @Column(name = "start_date", nullable = false, updatable = false)
    private LocalDate startDate; // Start date

    @Column(name = "end_date")
    private LocalDate endDate; // End date

    @NotNull
    @Column(name = "cost_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal costAmount; // Cost amount

    @NotNull
    @JsonIgnore
    @LastModifiedDate // Automatically stores the date of the most recent update to this entity
    @Column(name = "update_date", nullable = false)
    private LocalDate updateDate; // Update date

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if ((null == object) || !this.getClass().equals(object.getClass())) return false;
        Contract contract = (Contract) object;
        return ((null != this.id) && this.id.equals(contract.getId()));
    }

    @Override
    public int hashCode() { return 31; }

    @Override
    public String toString() {
        return "Contract{" +
                "id=" + this.id +
                ", clientId=" + this.clientId +
                ", startDate=" + this.startDate +
                ", endDate=" + this.endDate +
                ", costAmount=" + this.costAmount +
                ", updateDate=" + this.updateDate +
                '}';
    }
}
