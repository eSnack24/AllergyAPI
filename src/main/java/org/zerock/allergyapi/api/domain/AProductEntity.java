package org.zerock.allergyapi.api.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name ="tbl_product")
public class AProductEntity {

    @Id
    private Long pno;

    private int price;

    private String Pfilename;

    private String ptitle_ko;

    private String pcontent_ko;

    private String allergy;


}
