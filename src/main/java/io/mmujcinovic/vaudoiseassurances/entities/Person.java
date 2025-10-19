package io.mmujcinovic.vaudoiseassurances.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "person", schema = "public")
@NoArgsConstructor
@Getter
@Setter
public class Person extends Client implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(nullable = false, updatable = false)
    private LocalDate birthdate; // Birthdate

    @Override
    public String toString() {
        return "Person{" +
                "id=" + this.getId() +
                ", name='" + this.getName() + '\'' +
                ", phone='" + this.getPhone() + '\'' +
                ", email='" + this.getEmail() + '\'' +
                ", active=" + this.isActive() +
                ", birthdate='" + this.birthdate + '\'' +
                '}';
    }
}
