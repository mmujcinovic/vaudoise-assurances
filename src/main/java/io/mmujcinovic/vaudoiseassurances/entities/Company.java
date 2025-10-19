package io.mmujcinovic.vaudoiseassurances.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "company", schema = "public")
@NoArgsConstructor
@Getter
@Setter
public class Company extends Client implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "company_identifier", nullable = false, updatable = false)
    private String companyIdentifier; // Company identifier

    @Override
    public String toString() {
        return "Company{" +
                "id=" + this.getId() +
                ", name='" + this.getName() + '\'' +
                ", phone='" + this.getPhone() + '\'' +
                ", email='" + this.getEmail() + '\'' +
                ", active=" + this.isActive() +
                ", companyIdentifier='" + this.companyIdentifier + '\'' +
                '}';
    }
}
