package io.mmujcinovic.vaudoiseassurances.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "client", schema = "public", indexes = {
        @Index(name = "idx_client_active", columnList = "id,active"),
})
// Uses the JOINED strategy so each subclass has its own table linked to the parent by primary key
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@Getter
@Setter
public abstract class Client implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_seq")
    @SequenceGenerator(name = "client_seq", sequenceName = "client_seq", allocationSize = 1)
    private Long id; // Id

    @NotNull
    @Column(nullable = false)
    private String name; // Company name for Company or Last name for Person

    @NotNull
    @Column(nullable = false)
    private String phone; // Phone number

    @NotNull
    @Column(nullable = false)
    private String email; // Email address

    @NotNull
    @Column(nullable = false)
    private boolean active; // Is active (true if active)

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Client client)) return false;
        return ((null != this.id) && this.id.equals(client.getId()));
    }

    @Override
    public int hashCode() { return 31; }
}
