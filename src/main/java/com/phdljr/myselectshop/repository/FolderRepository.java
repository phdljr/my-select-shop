package com.phdljr.myselectshop.repository;

import com.phdljr.myselectshop.entity.User;
import com.phdljr.myselectshop.entity.Folder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    List<Folder> findAllByUserAndNameIn(User user, List<String> folderNames);
    // select * from folder where user_id = user.id and name in(folderNames);

    List<Folder> findALlByUser(User user);
}
