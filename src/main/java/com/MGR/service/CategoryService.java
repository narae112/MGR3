package com.MGR.service;

import com.MGR.dto.CategoryFormDto;
import com.MGR.dto.ReviewBoardForm;
import com.MGR.entity.*;
import com.MGR.exception.DataNotFoundException;
import com.MGR.repository.AfterTypeCategoryRepository;
import com.MGR.repository.AttractionTypeCategoryRepository;
import com.MGR.repository.CategoryRepository;
import com.MGR.repository.PersonalityCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Attr;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final AttractionTypeCategoryService attractionTypeCategoryService;
    private final AfterTypeCategoryService afterTypeCategoryService;
    private final PersonalityCategoryService personalityCategoryService;
    private final AttractionTypeCategoryRepository attractionTypeCategoryRepository;
    private final AfterTypeCategoryRepository afterTypeCategoryRepository;
    private final PersonalityCategoryRepository personalityCategoryRepository;

    public void saveCategory(Category category){
        categoryRepository.save(category);
    }

    public Long createCategory(List<String> attractionTypeCategory, List<String> afterTypeCategory, List<String> personalityCategory) throws Exception {
        Category category = new Category();
        categoryRepository.save(category);

        for (String name : attractionTypeCategory) {
            AttractionTypeCategory attractionTypeCategories = new AttractionTypeCategory();
            attractionTypeCategories.setName(name);
            attractionTypeCategories.setCategory(category);
            attractionTypeCategoryService.saveAttractionTypeCategory(attractionTypeCategories);
        }

        for (String name : afterTypeCategory) {
            AfterTypeCategory afterTypeCategories = new AfterTypeCategory();
            afterTypeCategories.setName(name);
            afterTypeCategories.setCategory(category);
            afterTypeCategoryService.saveAfterTypeCategory(afterTypeCategories);
        }

        for (String name : personalityCategory) {
            PersonalityCategory personalityCategories = new PersonalityCategory();
            personalityCategories.setName(name);
            personalityCategories.setCategory(category);
            personalityCategoryService.savePersonalityCategory(personalityCategories);
        }

        return category.getId();
    }
   public void delete(Category category)throws Exception{
       try{
           attractionTypeCategoryService.deleteAttractionByCategoryId(category.getId());
           afterTypeCategoryService.deleteAfterByCategoryId(category.getId());
           personalityCategoryService.deletePersonalityByCategoryId(category.getId());
           this.categoryRepository.delete(category);
       }catch (Exception e){
           throw new Exception("삭제 중 오류가 발생하였습니다.", e);
       }
   }

   public void modify(Category category, List<String>attractionTypeCategory, List<String>afterTypeCategory, List<String>personalityCategory) throws Exception{
       if(attractionTypeCategory != null &&!attractionTypeCategory.isEmpty()){
           attractionTypeCategoryService.deleteAttractionByCategoryId(category.getId());

           for(String name : attractionTypeCategory){
               if(name != null && !name.isEmpty()){
                   AttractionTypeCategory attractionTypeCategories = new AttractionTypeCategory();
                   attractionTypeCategoryService.saveAttractionTypeCategory(attractionTypeCategories);
                   attractionTypeCategories.setName(name);
                   attractionTypeCategories.setCategory(category);
                   attractionTypeCategoryRepository.save(attractionTypeCategories);
               }

           }
           if(afterTypeCategory !=null && !afterTypeCategory.isEmpty()){
               afterTypeCategoryService.deleteAfterByCategoryId(category.getId());
           }
           for(String name : afterTypeCategory){
               if(name != null && !name.isEmpty()){
                   AfterTypeCategory afterTypeCategories = new AfterTypeCategory();
                   afterTypeCategoryService.saveAfterTypeCategory(afterTypeCategories);
                   afterTypeCategories.setName(name);
                   afterTypeCategories.setCategory(category);
                   afterTypeCategoryRepository.save(afterTypeCategories);
               }
           }
           if(personalityCategory != null && !personalityCategory.isEmpty()){
               personalityCategoryService.deletePersonalityByCategoryId(category.getId());
           }
           for(String name : personalityCategory){
               if(name != null && !name.isEmpty()){
                   PersonalityCategory personalityCategories = new PersonalityCategory();
                   personalityCategoryService.savePersonalityCategory(personalityCategories);
                   personalityCategories.setName(name);
                   personalityCategories.setCategory(category);
                   personalityCategoryRepository.save(personalityCategories);
               }
           }
       }
       categoryRepository.save(category);
   }
   public Category getCategory(Long id){
       Optional<Category> category = this.categoryRepository.findById(id);
       if(category.isPresent()){
           return category.get();
       }else {
           throw new DataNotFoundException("not found");
       }
   }
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    public CategoryFormDto mapCategoryToFormDto(Category category) {
        return CategoryFormDto.of(category);
    }

    public List<AttractionTypeCategory> getAllAttractionTypeCategories() {
        return attractionTypeCategoryRepository.findAll();
    }

    public List<AfterTypeCategory> getAllAfterTypeCategories() {
        return afterTypeCategoryRepository.findAll();
    }

    public List<PersonalityCategory> getAllPersonalityCategories() {
        return personalityCategoryRepository.findAll();
    }
}
