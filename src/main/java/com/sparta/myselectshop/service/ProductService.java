package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.Folder;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.ProductFolder;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.entity.UserRoleEnum;
import com.sparta.myselectshop.naver.dto.ItemDto;
import com.sparta.myselectshop.repository.FolderRepository;
import com.sparta.myselectshop.repository.ProductFolderRepository;
import com.sparta.myselectshop.repository.ProductRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final FolderRepository folderRepository;
    private final ProductRepository productRepository;
    private final ProductFolderRepository productFolderRepository;

    public static final int MIN_MY_PRICE = 100;

    public ProductResponseDto createProduct(ProductRequestDto requestDto, final User user) {
        Product product = productRepository.save(new Product(requestDto, user));
        return new ProductResponseDto(product);
    }

    @Transactional
    public ProductResponseDto updateProduct(final Long id,
        final ProductMypriceRequestDto requestDto) {
        int myprice = requestDto.getMyprice();
        if (myprice < MIN_MY_PRICE) {
            throw new IllegalArgumentException(
                "유효하지 않은 관심 가격입니다. 최소 " + MIN_MY_PRICE + "원 이상으로 설정해 주세요.");
        }

        Product product = productRepository.findById(id).orElseThrow(() ->
            new NullPointerException("해당 상품을 찾을 수 없습니다.")
        );

        product.update(requestDto);

        return new ProductResponseDto(product);
    }

    // 지연 로딩을 사용하기 때문에 설정(조회 전용)
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProducts(final User user, final int page, final int size,
        final String sortBy, final boolean isAsc) {

        Direction direction = isAsc ? Direction.ASC : Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        UserRoleEnum userRoleEnum = user.getRole();

        Page<Product> products;
        if (userRoleEnum == UserRoleEnum.USER) {
            products = productRepository.findAllByUser(user, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }

        return products.map(ProductResponseDto::new);
    }

    @Transactional
    public void updateBySearch(final Long id, final ItemDto itemDto) {
        Product product = productRepository.findById(id).orElseThrow(() ->
            new NullPointerException("해당 상품은 존재하지 않습니다.")
        );

        product.updateByItemDto(itemDto);
    }

    public void addFolder(final Long productId, final Long folderId, final User user) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
            new NullPointerException("해당 상품은 존재하지 않습니다.")
        );

        Folder folder = folderRepository.findById(folderId).orElseThrow(() ->
            new NullPointerException("해당 폴더가 존재하지 않습니다.")
        );

        if (!product.getUser().getId().equals(user.getId())
            || !folder.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("회원님의 관심 상품이 아니거나, 회원님의 폴더가 아닙니다.");
        }

        Optional<ProductFolder> overlapFolder = productFolderRepository.findByProductAndFolder(
            product, folder);

        if(overlapFolder.isPresent()){
            throw new IllegalArgumentException("중복된 폴더입니다.");
        }

        productFolderRepository.save(new ProductFolder(product, folder));
    }
}
