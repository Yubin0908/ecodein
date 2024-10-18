package com.project.ecodein.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import com.project.ecodein.entity.Board;
import com.project.ecodein.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	
	public List<Comment> findByBoardNo(@Param("boardNo") Board boardNo);
}
