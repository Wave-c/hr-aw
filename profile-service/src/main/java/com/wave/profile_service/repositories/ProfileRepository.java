package com.wave.profile_service.repositories;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.wave.profile_service.models.Profile;

@Repository
public interface ProfileRepository extends ReactiveCrudRepository<Profile, UUID> {

}
