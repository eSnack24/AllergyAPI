package org.zerock.allergyapi.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.allergyapi.api.domain.AProductEntity;
import org.zerock.allergyapi.api.domain.ProductEntity;
import org.zerock.allergyapi.api.repository.ProductRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;


@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class AProductService {

    private final ProductRepository productRepository;

    public void apiInsert() throws IOException {
        try {

            String originalString = "홈런볼";

            String encodedString = URLEncoder.encode(originalString, "UTF-8");

            String apiurl = String.format(
                    "https://apis.data.go.kr/B553748/CertImgListServiceV3/getCertImgListServiceV3?ServiceKey=Dxhv%%2FFADXXMPmKxLHMxOkoyMrWL45dwTybbI8frUxCT1eyJKz0WstFSGR5f0XppdMp51F%%2FkluvX3%%2Bm4oTgJHJQ%%3D%%3D&prdlstNm=%s&returnType=json&pageNo=1&numOfRows=100",
                    encodedString
            );

            URL url = new URL(apiurl);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            log.info("------------------------------------------------------1");

            log.info(result.toString());

            // 응답이 올바른 JSON 형식인지 확인
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

                    ProductEntity product = new ProductEntity();
                    product.setPno((long) (i + 1)); // 예시: 인덱스를 기본 키로 설정
                    product.setPtitle_ko(item.optString("prdlstNm", "No Title")); // 제품명
                    product.setPcontent_ko(item.optString("description", "No Description"));
                    product.setPfilename(item.optString("img", null));
                    product.setPrice(item.optInt("price", 0)); // 가격 필드 예시
                    product.setAllergy(item.optString("allergy", "No Allergy"));

                    productRepository.save(product);
                }
            } else {
                log.error("Invalid response format. Expected JSON, but received: " + result.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}