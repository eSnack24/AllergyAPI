package org.zerock.allergyapi.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.zerock.allergyapi.api.domain.ProductEntity;
import org.zerock.allergyapi.api.repository.ProductRepository;
import reactor.core.publisher.Mono;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;


@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class AProductService {

    private final ProductRepository productRepository;

    private final WebClient webClient;

    @Value("${nginx.server.url}")
    private String nginxServerUrl;

    public void apiInsert() throws IOException {
        try {

            String[] searchTerms = {"홈런볼", "포카칩"};

            // 배열을 반복하면서 처리
            for (String originalString : searchTerms) {
                String encodedString = URLEncoder.encode(originalString, "UTF-8");

                String apiurl = String.format(
                        "http://apis.data.go.kr/B553748/CertImgListServiceV3/getCertImgListServiceV3?ServiceKey=Dxhv%%2FFADXXMPmKxLHMxOkoyMrWL45dwTybbI8frUxCT1eyJKz0WstFSGR5f0XppdMp51F%%2FkluvX3%%2Bm4oTgJHJQ%%3D%%3D&prdlstNm=%s&returnType=json&pageNo=1&numOfRows=100",
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

                        log.info(item.optString("prdlstNm", "no title"));
                        String imageUrl = item.optString("imgurl1", null);
                        log.info(imageUrl);

                        // 파일명 추출 및 확장자 가져오기
                        int lastSlashIndex = imageUrl.lastIndexOf('/');
                        String originalFileName = imageUrl.substring(lastSlashIndex + 1);
                        log.info("------------------------------------------------------3");
                        log.info(originalFileName);
                        int dotIndex = originalFileName.lastIndexOf('.');
                        String fileExtension = dotIndex != -1 ? originalFileName.substring(dotIndex) : ""; // 확장자
                        log.info("------------------------------------------------------4");

                        // UUID로 새 파일명 생성
                        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                        try {
                            // 이미지 다운로드 (로컬 저장 없이 바로 WebClient로 업로드)
                            InputStream in = new URL("https" + imageUrl.substring(4)).openStream();

                            // MultipartFile로 변환
                            MockMultipartFile multipartFile = new MockMultipartFile(
                                    "file", uniqueFileName, "image/jpeg", in
                            );

                            // WebClient로 Nginx 서버에 업로드
                            uploadFileToNginx(multipartFile);

                            // 스트림 종료
                            in.close();

                            log.info("파일이 성공적으로 업로드되었습니다: " + uniqueFileName);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        ProductEntity product = new ProductEntity();
                        product.setPno((long) (i + 1)); // 예시: 인덱스를 기본 키로 설정
//                        log.info(item.optString("prdlstNm", "no title"));
                        product.setPtitle_ko(item.optString("prdlstNm", "no title"));
//                        log.info(item.optString("nutrient", "no nutrient"));
//                        product.setPcontent_ko(item.optString("nutrient", "no nutrient"));
                        product.setPfilename(uniqueFileName);
                        product.setPrice(4000); // 가격 필드 예시

                        productRepository.save(product);
                    }


                } else {
                    log.error("Invalid response format. Expected JSON, but received: " + result.toString());
                }
            }
            } catch(Exception e){
                e.printStackTrace();
            }
        }

    private void uploadFileToNginx(MockMultipartFile file) {
        try {
            // WebClient로 파일 업로드
            webClient.post()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                    .bodyValue(file)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnTerminate(() -> log.info("파일 업로드 완료"))
                    .subscribe(); // 비동기 처리

        } catch (Exception e) {
            e.printStackTrace();
            log.error("파일 업로드 실패: " + e.getMessage());
        }
    }
}
