package com.MGR.controller;

import com.MGR.dto.CategoryFormDto;
import com.MGR.entity.*;
import com.MGR.service.AfterTypeCategoryService;
import com.MGR.service.AttractionTypeCategoryService;
import com.MGR.service.CategoryService;
import com.MGR.service.PersonalityCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final AttractionTypeCategoryService attractionTypeCategoryService;
    private final AfterTypeCategoryService afterTypeCategoryService;
    private final PersonalityCategoryService personalityCategoryService;

    @GetMapping(value = "/list")
    public String categoryList(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "categories/list";
    }

    @GetMapping("/create")
    public String categoryCreateForm(Model model) {
        CategoryFormDto categoryFormDto = new CategoryFormDto();
        model.addAttribute("categoryFormDto", categoryFormDto);
        return "categories/form";
    }

    @PostMapping("/create")
    public String categoryCreate(@ModelAttribute("categoryFormDto") @Valid CategoryFormDto categoryFormDto,
                                 BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "categories/form";
        }
        if (categoryFormDto.getAttractionTypeCategory().size() < 2 ||
                categoryFormDto.getAfterTypeCategory().size() < 2 ||
                categoryFormDto.getPersonalityCategory().size() < 2) {
            bindingResult.rejectValue("attractionTypeCategory", "size", "놀이기구 취향 키워드를 최소 두 개 이상 입력해주세요.");
            bindingResult.rejectValue("afterTypeCategory", "size", "동행 후 일정 키워드를 최소 두 개 이상 입력해주세요.");
            bindingResult.rejectValue("personalityCategory", "size", "성격 키워드를 최소 두 개 이상 입력해주세요.");
            return "categories/form";
        }
        try {
            Long categoryId = categoryService.createCategory(categoryFormDto.getAttractionTypeCategory(),
                    categoryFormDto.getAfterTypeCategory(),
                    categoryFormDto.getPersonalityCategory());
            model.addAttribute("categoryId", categoryId);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "카테고리 생성 중 오류가 발생했습니다.");
            return "categories/form";
        }

        return "redirect:/admin/category/list";
    }
    @GetMapping("/modify/{id}")
    public String categoryModify(Model model, @PathVariable("id") Long id){
        Category category = categoryService.getCategory(id);
        CategoryFormDto categoryFormDto = categoryService.mapCategoryToFormDto(category);
        model.addAttribute("categoryFormDto", categoryFormDto);
        return "categories/form";
    }

    @PostMapping("/modify/{id}")
    public String categoryModify(Model model, @PathVariable("id") Long id,
                                 @ModelAttribute("categoryFormDto") @Valid CategoryFormDto categoryFormDto,
                                 BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "categories/form";
        }

        try {
            Category category = categoryService.getCategory(id);
            categoryService.modify(category, categoryFormDto.getAttractionTypeCategory(),
                    categoryFormDto.getAfterTypeCategory(),
                    categoryFormDto.getPersonalityCategory());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "카테고리 수정 중 오류가 발생했습니다.");
            return "categories/form";
        }

        return "redirect:/admin/category/list";
    }

    @GetMapping("/delete/{id}")
    public String categoryDelete(@PathVariable("id") Long id){
        Category category = this.categoryService.getCategory(id);
        try{
            this.categoryService.delete(category);
        }catch (Exception e){
            e.printStackTrace();
            return "redirect:/error";
        }
        return "redirect:/admin/category/list";
    }


}
