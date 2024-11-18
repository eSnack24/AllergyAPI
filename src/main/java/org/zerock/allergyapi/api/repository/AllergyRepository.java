package org.zerock.allergyapi.api.repository;

import org.zerock.allergyapi.api.domain.AllergyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllergyRepository extends JpaRepository<AllergyEntity, Long> {
}
