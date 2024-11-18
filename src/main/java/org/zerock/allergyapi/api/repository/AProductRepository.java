package org.zerock.allergyapi.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.allergyapi.api.domain.AProductEntity;

public interface AProductRepository extends JpaRepository<AProductEntity, Long> {

}
