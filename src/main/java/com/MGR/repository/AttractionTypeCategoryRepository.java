package com.MGR.repository;

import com.MGR.entity.AfterTypeCategory;
import com.MGR.entity.AttractionTypeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttractionTypeCategoryRepository extends JpaRepository<AttractionTypeCategory, Long> {
    List<AttractionTypeCategory> findByCategoryId (Long categoryId);
//    List<AttractionTypeCategory> findByAttractionTypeCategoryId(Long attractionTypeCategoryId);
}
