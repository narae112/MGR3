package com.MGR.service;

import com.MGR.entity.AttractionTypeCategory;
import com.MGR.repository.AttractionTypeCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttractionTypeCategoryService {
    private final AttractionTypeCategoryRepository attractionTypeCategoryRepository;

    public void saveAttractionTypeCategory(AttractionTypeCategory attractionTypeCategory) throws  Exception{
        attractionTypeCategory.updateAttractionTypeCategory(attractionTypeCategory.getName());
        attractionTypeCategoryRepository.save(attractionTypeCategory);
    }
//    public void updateAttractionTypeCategory(Long id, AttractionTypeCategory attractionTypeCategory) throws Exception{
//        AttractionTypeCategory savedCategory = attractionTypeCategoryRepository.findById(id).
//                orElseThrow(EntityNotFoundException::new);
//        if(!StringUtils.isEmpty(savedCategory.getName())){
//            attractionTypeCategoryRepository.delete(attractionTypeCategory);
//        }
//        savedCategory.updateAttractionTypeCategory(name);
//
//    }
//    public void deleteAttractionTypeCategory(Long attractionTypeCategoryId) throws Exception{
//        List<AttractionTypeCategory> attractionTypeCategories = attractionTypeCategoryRepository.findByAttractionTypeCategoryId(attractionTypeCategoryId);
//        for(AttractionTypeCategory attractionTypeCategory : attractionTypeCategories){
//            try{
//                attractionTypeCategoryRepository.delete(attractionTypeCategory);
//            }catch (Exception e){
//                throw new Exception("카테고리 삭제 중 오류가 발생하였습니다.");
//            }
//        }
//
//    }
    public void deleteAttractionByCategoryId(Long categoryId) throws Exception{
        List<AttractionTypeCategory> attractionTypeCategories = attractionTypeCategoryRepository.findByCategoryId(categoryId);
        for(AttractionTypeCategory attractionTypeCategory : attractionTypeCategories){
            try{
                attractionTypeCategoryRepository.delete(attractionTypeCategory);
            }catch(Exception e){
                System.err.println("이미지 삭제 실패: " + e.getMessage());
                throw new Exception("삭제 중 오류가 발생하였습니다.", e);
            }
        }
    }
}
