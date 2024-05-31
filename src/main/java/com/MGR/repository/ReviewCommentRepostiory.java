package com.MGR.repository;

import com.MGR.entity.QnaAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
}
