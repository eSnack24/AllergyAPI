package org.zerock.allergyapi.api.domain;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "tbl_allergy")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AllergyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoIncrement로 자동 생성되는 pk
    private Long ano;

    private String atitle_ko;

    private String atitle_en;

    private String atitle_ja;

    private String atitle_zh;
}
