package com.project.ecodein.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.project.ecodein.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Integer> {

	//검색어로 전체 검색
	@Query(value = "SELECT * "
		+ "FROM item "
		+ "WHERE (item_no like CONCAT('%', :search, '%') OR item_name like CONCAT('%', :search, '%'))", nativeQuery = true)
	Page<Item> searchItem (@Param(value = "search") String search, Pageable pageable);
	
	//검색어로 상품만 검색
	@Query(value = "SELECT * "
		+ "FROM item "
		+ "WHERE (item_no like CONCAT('%', :search, '%') OR item_name like CONCAT('%', :search, '%')) "
		+ "AND is_material = false", nativeQuery = true)
	Page<Item> searchItemOnly (@Param(value = "search") String search, Pageable pageable);
	
	//검색어로 재료만 검색
	@Query(value = "SELECT * "
		+ "FROM item "
		+ "WHERE (item_no like CONCAT('%', :search, '%') OR item_name like CONCAT('%', :search, '%')) "
		+ "AND is_material = true", nativeQuery = true)
	Page<Item> searchMaterialOnly (@Param(value = "search") String search, Pageable pageable);

    Item findByItemName(String name);
}
