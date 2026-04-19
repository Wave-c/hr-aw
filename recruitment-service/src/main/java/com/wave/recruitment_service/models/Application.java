package com.wave.recruitment_service.models;

import java.util.UUID;

import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("application")
public class Application {
    private UUID id;
    private UUID vacancyId;
    private String resumeText;
    private String coverLetter;
    private Integer expectedSalary;
}
