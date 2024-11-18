package org.zerock.allergyapi.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.allergyapi.api.domain.AProductEntity;
import org.zerock.allergyapi.api.dto.AllergyReadDTO;
import org.zerock.allergyapi.api.dto.ProductReadDTO;
import org.zerock.allergyapi.api.repository.AProductRepository;
import org.zerock.allergyapi.api.repository.AllergyRepository;
import org.zerock.allergyapi.api.repository.ProductRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class AllergyService {

    private final AllergyRepository allergyRepository;
    private final ProductRepository productRepository;
    private final AProductRepository aProductRepository;

    public void insertService(String tmp) throws IOException {

        List<ProductReadDTO> productList = productRepository.productList().orElseThrow();
        List<AllergyReadDTO> allergyList = allergyRepository.allergyList().orElseThrow();

        Map<Long, String> productMap = productList.stream()
                .collect(Collectors.toMap(
                        ProductReadDTO::getPno,
                        ProductReadDTO::getPtitle_ko
                ));

        Map<Long, String> allergyMap = allergyList.stream()
                .collect(Collectors.toMap(
                        AllergyReadDTO::getAno,
                        AllergyReadDTO::getAtitle_ko
                ));

        log.info(allergyMap);

        String encodedString = URLEncoder.encode(tmp, "UTF-8");

        String apiurl = String.format(
                "https://apis.data.go.kr/B553748/CertImgListServiceV3/getCertImgListServiceV3?ServiceKey=Dxhv%%2FFADXXMPmKxLHMxOkoyMrWL45dwTybbI8frUxCT1eyJKz0WstFSGR5f0XppdMp51F%%2FkluvX3%%2Bm4oTgJHJQ%%3D%%3D&manufacture=%s&returnType=json&pageNo=1&numOfRows=100",
                encodedString
        );

        URL url = new URL(apiurl);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            result.append(line);
        }

        if (result.toString().startsWith("{")) {
            // JSON 파싱
            JSONObject jsonObject = new JSONObject(result.toString());
            JSONObject body = jsonObject.getJSONObject("body");
            JSONArray items = body.getJSONArray("items");

            log.info("------------------------------------------------------2");
            log.info(items.toString());

            // 데이터를 Entity로 변환하여 저장
            for (int i = 0; i < items.length(); i++) {

                JSONObject itemWrapper = items.getJSONObject(i);
                JSONObject item = itemWrapper.getJSONObject("item");

                String prdlstNm = item.optString("prdlstNm");
                // allergy 정보 추출 (알레르기 정보)
                String allergyInfo = item.optString("allergy");

                // productMap에서 제품명 비교하여 해당 키 찾기
                Long productKey = productMap.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(prdlstNm)) // 제품명 비교
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse(null);

                if(productKey == null) continue;

                String[] allergyItems = allergyInfo.split("[, ()]+");

                List<Long> allergyKeys = Arrays.stream(allergyItems)
                        .map(String::trim)  // 각 알레르기 항목에 대한 공백 제거
                        .flatMap(allergy -> allergyMap.entrySet().stream()
                                .filter(entry -> entry.getValue().equals(allergy)) // 알레르기 정보 비교
                                .map(Map.Entry::getKey)) // 해당 키만 추출
                        .collect(Collectors.toList());

                for (Long key : allergyKeys) {

                    AProductEntity entity = AProductEntity.builder()
                            .pno(productKey)
                            .ano(key)
                            .build();

                    log.info(productMap.get(productKey));
                    log.info(productKey);
                    log.info(allergyMap.get(key));
                    log.info(key);

                    aProductRepository.save(entity);
                }
            }
        } else {
            log.error("Invalid response format. Expected JSON, but received: " + result.toString());
        }
    }
}
