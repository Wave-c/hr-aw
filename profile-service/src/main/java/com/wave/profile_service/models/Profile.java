package com.wave.profile_service.models;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "public", value = "profiles_t")
public class Profile implements Persistable<UUID> {
    @Id
    private UUID id;
    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    @Column("patronymic")
    private String patronymic;
    @Column("phone")
    private String phone;
    private Boolean isActive;

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }
}
