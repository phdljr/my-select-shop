package com.sparta.myselectshop.dto;

import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.ProductFolder;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductResponseDto {

    private Long id;
    private String title;
    private String link;
    private String image;
    private int lprice;
    private int myprice;

    private final List<FolderResponseDto> productFolderList = new ArrayList<>();

    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.link = product.getLink();
        this.image = product.getImage();
        this.lprice = product.getLprice();
        this.myprice = product.getMyprice();

        // 지연 로딩을 사용하는 곳
        for (ProductFolder productFolder : product.getProductFolderList()) {
            productFolderList.add(new FolderResponseDto(productFolder.getFolder()));
        }
    }
}