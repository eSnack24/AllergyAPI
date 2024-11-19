package org.zerock.allergyapi.api.service;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.allergyapi.api.domain.AProductEntity;
import org.zerock.allergyapi.api.domain.ProductEntity;
import org.zerock.allergyapi.api.repository.ProductRepository;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.UUID;


@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class AProductService {

    private final ProductRepository productRepository;

    public void apiInsert(String tmp) throws IOException {
        try {

            // 배열을 반복하면서 처리
            String encodedString = URLEncoder.encode(tmp, "UTF-8");
            String prdkind = URLEncoder.encode("과자", "UTF-8");

            String apiurl = String.format(
                    "http://apis.data.go.kr/B553748/CertImgListServiceV3/getCertImgListServiceV3?ServiceKey=Dxhv%%2FFADXXMPmKxLHMxOkoyMrWL45dwTybbI8frUxCT1eyJKz0WstFSGR5f0XppdMp51F%%2FkluvX3%%2Bm4oTgJHJQ%%3D%%3D&prdkind=%s&manufacture=%s&returnType=json&pageNo=1&numOfRows=100",
                    prdkind, encodedString
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
                    log.info(originalFileName);
                    int dotIndex = originalFileName.lastIndexOf('.');
                    String fileExtension = dotIndex != -1 ? originalFileName.substring(dotIndex) : ""; // 확장자

                    // UUID로 새 파일명 생성
                    String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                    try {
                        InputStream in = new URL("https" + imageUrl.substring(4)).openStream();
                        String savePath = "C:\\snack\\demo\\"; // 이미지 파일 저장 경로
                        String thumbnailPrefix = "s_"; // 썸네일 파일명 접두사

                        // 원본 이미지 저장
                        OutputStream outputStream = new FileOutputStream(savePath + uniqueFileName);
                        byte[] buffer = new byte[1024 * 8];

                        while (true) {
                            int count = in.read(buffer);
                            if (count == -1) {
                                break;
                            }
                            outputStream.write(buffer, 0, count);
                        }

                        // 스트림 종료
                        in.close();
                        outputStream.close();

                        log.info("파일이 성공적으로 저장되었습니다: " + uniqueFileName);

                        // 썸네일 생성
                        File originalFile = new File(savePath + uniqueFileName);
                        File thumbnailFile = new File(savePath + thumbnailPrefix + uniqueFileName);

                        try (InputStream thumbnailInputStream = new FileInputStream(originalFile);
                             OutputStream thumbnailOutputStream = new FileOutputStream(thumbnailFile)) {

                            Thumbnailator.createThumbnail(thumbnailInputStream, thumbnailOutputStream, 200, 200);
                            log.info("썸네일이 성공적으로 생성되었습니다: " + thumbnailPrefix + uniqueFileName);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        log.error("파일 저장 또는 썸네일 생성 중 오류 발생", e);
                    }



                    ProductEntity product = new ProductEntity();
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

        } catch(Exception e){
            e.printStackTrace();
        }
    }
}