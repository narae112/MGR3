package com.MGR.repository;
import com.MGR.entity.AfterTypeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AfterTypeCategoryRepository extends JpaRepository<AfterTypeCategory, Long> {
//    List<AfterTypeCategory> findByAfterTypeCategoryId (Long afterTypeCategoryId);

    List<AfterTypeCategory> findByCategoryId (Long categoryId);
}
