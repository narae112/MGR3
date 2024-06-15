package com.MGR.repository;

import com.MGR.entity.AfterTypeCategory;
import com.MGR.entity.AttractionTypeCategory;
import com.MGR.entity.PersonalityCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalityCategoryRepository extends JpaRepository<PersonalityCategory, Long> {
//    List<PersonalityCategory> findByPersonalityCategoryId (Long personalityCategoryId);
    List<PersonalityCategory> findByCategoryId (Long categoryId);
}
