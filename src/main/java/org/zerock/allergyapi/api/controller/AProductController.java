package org.zerock.allergyapi.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.allergyapi.api.service.AProductService;
import org.zerock.allergyapi.api.service.AllergyService;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product/allergyapi")
@Log4j2
public class AProductController {

    public final AProductService productService;

    private final AllergyService allergyService;

    private final String[] companyArr = {"해태", "오리온", "농심", "롯데제과주식회사", "크라운", "빙그레", "삼양식품"};

    @GetMapping("/save")
    public void save() throws IOException {

        for(String company : companyArr) {
            productService.apiInsert(company);
        }
    }

    @GetMapping("/allergysave")
    public void saveAllergy() throws IOException {

        for(String company : companyArr) {
            allergyService.insertService(company);
        }
    }

}
