package com.wave.recruitment_service.models;

import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("vacancy")
public class Vacancy implements Persistable<UUID> {
    @Id
    private UUID id;
    private String title;
    private String description;
    private List<String> formats;
    private List<String> tags;
    private Integer salaryFrom;
    private Integer salaryTo;
    private UUID createdBy;
    private List<UUID> availableFor;

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }
}
