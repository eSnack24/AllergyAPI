package org.zerock.allergyapi.api.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Table(name = "tbl_product")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductEntity {

    @Id
    private Long pno;

    private int price;

    private String Pfilename;

    private String ptitle_ko;

    private String pcontent_ko;

}
