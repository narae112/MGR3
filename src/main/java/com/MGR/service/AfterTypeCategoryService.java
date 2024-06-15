package com.MGR.service;

import com.MGR.entity.AfterTypeCategory;
import com.MGR.repository.AfterTypeCategoryRepository;
import io.netty.util.internal.StringUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.junit.After;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AfterTypeCategoryService {
    public final AfterTypeCategoryRepository afterTypeCategoryRepository;

    public void saveAfterTypeCategory(AfterTypeCategory afterTypeCategory) throws Exception{
        afterTypeCategory.updateAfterTypeCategory(afterTypeCategory.getName());
        afterTypeCategoryRepository.save(afterTypeCategory);
    }
    public void updateAfterTypeCategory(Long id, AfterTypeCategory afterTypeCategory) throws Exception{
        AfterTypeCategory savedCategory = afterTypeCategoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(!StringUtils.isEmpty(savedCategory.getName())){
            afterTypeCategoryRepository.delete(afterTypeCategory);
        }
        savedCategory.updateAfterTypeCategory(afterTypeCategory.getName());
    }
//    public void deleteAfterTypeCategory(Long afterTypeCategoryId) throws Exception{
//        List<AfterTypeCategory> afterTypeCategories = afterTypeCategoryRepository.findByAfterTypeCategoryId(afterTypeCategoryId);
//        for(AfterTypeCategory afterTypeCategory : afterTypeCategories){
//            try{
//                afterTypeCategoryRepository.delete(afterTypeCategory);
//            }catch(Exception e){
//                throw new Exception("카테고리 삭제 중 오류가 발생하였습니다.");
//            }
//        }
//    }
    public void deleteAfterByCategoryId(Long categoryId)throws Exception{
        List<AfterTypeCategory> afterTypeCategories = afterTypeCategoryRepository.findByCategoryId(categoryId);
        for(AfterTypeCategory afterTypeCategory : afterTypeCategories){
            try{
                afterTypeCategoryRepository.delete(afterTypeCategory);
            }catch(Exception e){
                System.err.println("이미지 삭제 실패: " + e.getMessage());
                throw new Exception("삭제 중 오류가 발생하였습니다.", e);
            }
        }
    }
}
