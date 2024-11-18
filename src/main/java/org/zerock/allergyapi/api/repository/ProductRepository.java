package org.zerock.allergyapi.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.allergyapi.api.domain.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

}
