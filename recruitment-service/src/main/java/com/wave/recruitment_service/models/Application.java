package com.wave.recruitment_service.models;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import com.wave.components.ApplicationStatus;
import com.wave.components.singly_linked_list.Node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("application")
public class Application implements Persistable<UUID> {
    @Id
    private UUID id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private UUID vacancyId;
    private String resumeText;
    private String coverLetter;
    private Integer expectedSalary;
    private ApplicationStatus status;

    @Transient
    private boolean isNew = true;
    @Transient
    private Node currentNode;
}
