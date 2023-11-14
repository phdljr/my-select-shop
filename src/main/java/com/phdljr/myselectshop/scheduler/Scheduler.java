package com.phdljr.myselectshop.scheduler;

import com.phdljr.myselectshop.entity.Product;
import com.phdljr.myselectshop.naver.dto.ItemDto;
import com.phdljr.myselectshop.naver.service.NaverApiService;
import com.phdljr.myselectshop.repository.ProductRepository;
import com.phdljr.myselectshop.service.ProductService;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j(topic = "Scheduler")
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final NaverApiService naverApiService;
    private final ProductService productService;
    private final ProductRepository productRepository;

    // cron: OS에서 반복 실행을 하기 위한 명령어
    // 초, 분, 시, 일, 월, 주 순서
    @Scheduled(cron = "0 0 1 * * *") // 매일 새벽 1시
    public void updatePrice() throws InterruptedException {
        log.info("가격 업데이트 실행");
        List<Product> productList = productRepository.findAll();
        for (Product product : productList) {
            // 1초에 한 상품 씩 조회합니다 (NAVER 제한)
            TimeUnit.SECONDS.sleep(1);

            // i 번째 관심 상품의 제목으로 검색을 실행합니다.
            String title = product.getTitle();
            List<ItemDto> itemDtoList = naverApiService.searchItems(title);

            if (itemDtoList.size() > 0) {
                // 검색어랑 가장 일치하는 상품이 최상단일 것으로 예측
                ItemDto itemDto = itemDtoList.get(0);
                // i 번째 관심 상품 정보를 업데이트합니다.
                Long id = product.getId();
                try {
                    productService.updateBySearch(id, itemDto);
                } catch (Exception e) {
                    log.error(id + " : " + e.getMessage());
                }
            }
        }
    }

}