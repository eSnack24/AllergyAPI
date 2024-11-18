package org.zerock.allergyapi.api.repository;

import org.springframework.data.jpa.repository.Query;
import org.zerock.allergyapi.api.domain.AllergyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.allergyapi.api.dto.AllergyReadDTO;

import java.util.List;
import java.util.Optional;

public interface AllergyRepository extends JpaRepository<AllergyEntity, Long> {

    @Query("""
    select new org.zerock.allergyapi.api.dto.AllergyReadDTO(a.ano, a.atitle_ko)
    from AllergyEntity a
    where a.ano > 0
    """)
    Optional<List<AllergyReadDTO>> allergyList();
}
