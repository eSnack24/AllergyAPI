package org.zerock.allergyapi.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.allergyapi.api.domain.ProductEntity;
import org.zerock.allergyapi.api.dto.ProductReadDTO;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {


    @Query("""
    select new org.zerock.allergyapi.api.dto.ProductReadDTO(p.pno, p.ptitle_ko)
    from ProductEntity p
    where p.pno > 0
    """)
    Optional<List<ProductReadDTO>> productList();
}
