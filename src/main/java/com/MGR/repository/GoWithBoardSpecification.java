package com.MGR.repository;

import com.MGR.constant.AgeCategory;
import com.MGR.constant.LocationCategory;
import com.MGR.entity.GoWithBoard;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class GoWithBoardSpecification {

    public static Specification<GoWithBoard> withFilters(
            List<AgeCategory> ageCategories,
            List<LocationCategory> locationCategories,
            List<String> attractionTypes,
            List<String> afterTypes,
            List<String> personalities) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // AgeCategory에 해당하는 값이 하나라도 선택되었으면 해당 조건을 추가합니다.
            if (ageCategories != null && !ageCategories.isEmpty()) {
                predicates.add(root.get("ageCategory").in(ageCategories));
            }

            // LocationCategory에 해당하는 값이 하나라도 선택되었으면 해당 조건을 추가합니다.
            if (locationCategories != null && !locationCategories.isEmpty()) {
                predicates.add(root.get("locationCategory").in(locationCategories));
            }

            if (attractionTypes != null && !attractionTypes.isEmpty()) {
                Expression<String> attractionField = root.get("attractionTypes");
                predicates.add(buildPredicateForList(attractionField, attractionTypes, criteriaBuilder));
            }
            if (afterTypes != null && !afterTypes.isEmpty()) {
                Expression<String> afterField = root.get("afterTypes");
                predicates.add(buildPredicateForList(afterField, afterTypes, criteriaBuilder));
            }
            if (personalities != null && !personalities.isEmpty()) {
                Expression<String> personalityField = root.get("personalities");
                predicates.add(buildPredicateForList(personalityField, personalities, criteriaBuilder));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate buildPredicateForList(Expression<String> field, List<String> values, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        for (String value : values) {
            predicates.add(criteriaBuilder.like(field, "%" + value + "%"));
        }
        return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
    }
}
