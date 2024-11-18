package org.zerock.allergyapi.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.allergyapi.api.service.AProductService;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product/allergyapi")
@Log4j2
public class AProductController {

    public final AProductService productService;

    @GetMapping("/save")
    public void save() throws IOException {

        productService.apiInsert();

    }

}
