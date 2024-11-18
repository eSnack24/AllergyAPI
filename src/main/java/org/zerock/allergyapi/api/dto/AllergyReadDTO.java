package org.zerock.allergyapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AllergyReadDTO {

    private Long ano;

    private String atitle_ko;
}
