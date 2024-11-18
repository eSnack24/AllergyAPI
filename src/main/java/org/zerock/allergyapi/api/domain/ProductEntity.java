package org.zerock.allergyapi.api.domain;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pno;

    private int price;

    private String Pfilename;

    private String ptitle_ko;

    private String pcontent_ko;

}
