package com.wave.profile_service.repositories;

import java.util.UUID;

import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.wave.profile_service.models.ProfileSearchModel;

@Repository
public interface ProfileSearchRepository extends ReactiveElasticsearchRepository<ProfileSearchModel, UUID> {

}
