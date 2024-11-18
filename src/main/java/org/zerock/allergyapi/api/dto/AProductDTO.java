package org.zerock.allergyapi.api.dto;

import lombok.Data;

@Data
public class AProductDTO {

    private Long pno;

    private int price;

    private String Pfilename;

    private String ptitle_ko;

    private String pcontent_ko;

}
