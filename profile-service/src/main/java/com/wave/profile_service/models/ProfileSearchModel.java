package com.wave.profile_service.models;

import java.util.UUID;

import org.springframework.data.elasticsearch.annotations.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "profiles")
public class ProfileSearchModel {
    private UUID id;
    private String firstName;
    private String lastName;
    private String patronymic;
}
