package org.zerock.allergyapi.api.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class ProductEntity {

    @Id
    private Long pno;

    private int price;

    private String Pfilename;

    private String ptitle_ko;

    private String pcontent_ko;

}
