package org.zerock.allergyapi.api.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name ="tbl_product_allergy")
public class AProductEntity {

    @Id
    private Long apno;

    private Long ano;

    private Long pno;


}
