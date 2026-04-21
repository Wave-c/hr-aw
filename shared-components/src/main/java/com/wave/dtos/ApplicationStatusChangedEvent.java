package com.wave.dtos;

import java.util.UUID;

import com.wave.components.ApplicationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationStatusChangedEvent {
    private UUID applicationId;
    private ApplicationStatus newStatus;
}
