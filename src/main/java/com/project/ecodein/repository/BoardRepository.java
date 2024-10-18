package com.project.ecodein.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.project.ecodein.entity.Board;
import com.project.ecodein.entity.User;

public interface BoardRepository extends JpaRepository<Board, Integer> {

	// 검색
	@Query (value = "SELECT * "
		+ "FROM board "
		+ "WHERE (board_no LIKE CONCAT('%',:search,'%')"
		+ "OR board_title LIKE CONCAT('%',:search,'%'))", nativeQuery = true)
	public Page<Board> findBoardByKeyword (@Param (value = "search") String search, Pageable pageable);
	
	List<Board> findByUser (User user);

}
