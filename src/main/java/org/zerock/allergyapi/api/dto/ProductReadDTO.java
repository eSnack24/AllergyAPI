package org.zerock.allergyapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductReadDTO {

    private Long pno;

    private String ptitle_ko;
}
