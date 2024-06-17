package com.MGR.repository;

import com.MGR.entity.GoWithBoard;
import com.MGR.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GoWithBoardRepository extends JpaRepository<GoWithBoard, Long>, JpaSpecificationExecutor<GoWithBoard> {

    @Query("SELECT DISTINCT g.member FROM GoWithBoard g WHERE g.afterTypes = :afterType")
    List<Member> findMembersBySimilarAfterTypes(@Param("afterType") String afterType);

    @Query("SELECT DISTINCT g.member FROM GoWithBoard g WHERE g.attractionTypes = :attractionType")
    List<Member> findMembersBySimilarAttractions(@Param("attractionType") String attractionType);

    @Query("SELECT DISTINCT g.member FROM GoWithBoard g WHERE g.personalities = :personality")
    List<Member> findMembersBySimilarPersonalities(@Param("personality") String personality);
}
