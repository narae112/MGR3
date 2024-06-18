package com.MGR.service;

import com.MGR.entity.PersonalityCategory;
import com.MGR.repository.PersonalityCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalityCategoryService {
    private final PersonalityCategoryRepository personalityCategoryRepository;

    public void savePersonalityCategory(PersonalityCategory personalityCategory){
        personalityCategory.updatePersonalityCategory(personalityCategory.getName());
        personalityCategoryRepository.save(personalityCategory);
    }

    public void updatePersonalityCategory(Long id, PersonalityCategory personalityCategory){
        PersonalityCategory saveCategory = personalityCategoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(!StringUtils.isEmpty(saveCategory.getName())){
            personalityCategoryRepository.delete(personalityCategory);
        }
        saveCategory.updatePersonalityCategory(personalityCategory.getName());
    }
//    public void deletePersonalityCategory(Long personalityCategoryId) throws Exception{
//        List<PersonalityCategory> personalityCategories = personalityCategoryRepository.findByPersonalityCategoryId(personalityCategoryId);
//        for(PersonalityCategory personalityCategory:personalityCategories){
//            try{
//                personalityCategoryRepository.delete(personalityCategory);
//            }catch (Exception e){
//                throw new Exception("카테고리 삭제 중 오류가 발생하였습니다.");
//            }
//        }
//    }
    public void deletePersonalityByCategoryId(Long categoryId) throws Exception{
        List<PersonalityCategory> personalityCategories = personalityCategoryRepository.findByCategoryId(categoryId);
        for(PersonalityCategory personalityCategory : personalityCategories){
            try{
                personalityCategoryRepository.delete(personalityCategory);
            }catch (Exception e){
                System.err.println("이미지 삭제 실패: " + e.getMessage());
                throw new Exception("삭제 중 오류가 발생하였습니다.", e);
            }
        }
    }
}
