package com.phdljr.myselectshop.repository;

import com.phdljr.myselectshop.entity.Folder;
import com.phdljr.myselectshop.entity.Product;
import com.phdljr.myselectshop.entity.ProductFolder;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductFolderRepository extends JpaRepository<ProductFolder, Long> {

    Optional<ProductFolder> findByProductAndFolder(Product product, Folder folder);
}
