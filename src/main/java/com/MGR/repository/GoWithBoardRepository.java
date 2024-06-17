package com.MGR.repository;

import com.MGR.entity.GoWithBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GoWithBoardRepository extends JpaRepository<GoWithBoard, Long>, JpaSpecificationExecutor<GoWithBoard> {

}
