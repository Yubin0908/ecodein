package com.project.ecodein.repository;

import java.util.List;
import java.util.Optional;

import com.project.ecodein.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.project.ecodein.entity.Stock;
import com.project.ecodein.entity.Storage;
import jakarta.transaction.Transactional;

public interface StockRepository extends JpaRepository<Stock, Integer> {

	
	// 상품만 검색
	@Query(value = "SELECT s.* "
		+ "FROM stock s "
		+ "LEFT JOIN item i "
		+ "ON s.item_no = i.item_no "
		+ "WHERE (s.stock_no LIKE CONCAT('%',:search,'%') AND i.is_material = false) "
		+ "OR (i.item_name LIKE CONCAT('%',:search,'%') AND i.is_material = false) ",
		countQuery = "SELECT count(*) "
	        + "FROM stock s "
	        + "LEFT JOIN item i "
	        + "ON s.item_no = i.item_no "
	        + "WHERE (s.stock_no LIKE CONCAT('%',:search,'%') AND i.is_material = false) "
	        + "OR (i.item_name LIKE CONCAT('%',:search,'%') AND i.is_material = false)" 
		,nativeQuery = true)
	public Page<Stock> findStockByKeyword (@Param(value = "search") String search, Pageable pageable);

	// 전부 검색
	@Query(value = "SELECT s.* "
		+ "FROM stock s "
		+ "LEFT JOIN item i "
		+ "ON s.item_no = i.item_no "
		+ "WHERE s.stock_no LIKE CONCAT('%',:search,'%')  "
		+ "OR i.item_name LIKE CONCAT('%',:search,'%') ",
		countQuery = "SELECT count(*) "
	        + "FROM stock s "
	        + "LEFT JOIN item i "
	        + "ON s.item_no = i.item_no "
	        + "WHERE s.stock_no LIKE CONCAT('%',:search,'%')  "
	        + "OR i.item_name LIKE CONCAT('%',:search,'%') " 
		,nativeQuery = true)
	public Page<Stock> findAllStockByKeyword (String search, Pageable pageable);
	
	// 상품만 창고, 이름 검색
	@Query(value = "SELECT s.* "
		+ "FROM stock s "
		+ "LEFT JOIN item i "
		+ "ON s.item_no = i.item_no "
		+ "WHERE ((s.stock_no LIKE CONCAT('%',:search,'%') AND i.is_material = false) "
		+ "OR (i.item_name LIKE CONCAT('%',:search,'%') AND i.is_material =false)) "
		+ "AND s.storage_no = :storage_no " ,
		countQuery = "SELECT count(*) "
	        + "FROM stock s "
	        + "LEFT JOIN item i "
	        + "ON s.item_no = i.item_no "
	        + "WHERE ((s.stock_no LIKE CONCAT('%',:search,'%') AND i.is_material = false) "
	        + "OR (i.item_name LIKE CONCAT('%',:search,'%') AND i.is_material = false)) "
	        + "AND s.storage_no = :storage_no " ,
	        nativeQuery = true)
	public Page<Stock> findyStockByKeywordStorage (@Param(value = "search") String search, 
		@Param(value = "storage_no") Integer storage_no, Pageable pageable);

	// 창고, 이름 검색
	@Query(value = "SELECT s.* "
		+ "FROM stock s "
		+ "LEFT JOIN item i "
		+ "ON s.item_no = i.item_no "
		+ "WHERE (s.stock_no LIKE CONCAT('%',:search,'%')  "
		+ "OR i.item_name LIKE CONCAT('%',:search,'%')) "
		+ "AND s.storage_no = :storage_no " ,
		countQuery = "SELECT count(*) "
	        + "FROM stock s "
	        + "LEFT JOIN item i "
	        + "ON s.item_no = i.item_no "
	        + "WHERE (s.stock_no LIKE CONCAT('%',:search,'%')  "
	        + "OR i.item_name LIKE CONCAT('%',:search,'%')) "
	        + "AND s.storage_no = :storage_no " ,
	        nativeQuery = true)
	public Page<Stock> findAllStockByKeywordStorage (String search, Integer storage_no, Pageable pageable);

	
	// 재고 상세 정보
	@Query(value = "SELECT * FROM stock WHERE stock_no = :stock_no", nativeQuery = true)
	public Optional<Stock> findByStockNO (@Param(value = "stock_no") int stock_no);

	@Transactional
	@Modifying
	@Query(value = "UPDATE stock SET quantity = :quantity WHERE stock_no = :stock_no", nativeQuery = true)
	public void updateStock (@Param(value = "stock_no") int stock_no, @Param(value = "quantity") int quantity);

	
	// 재고 등록 전, 확인 용
	@Query(value = "SELECT * FROM stock WHERE item_no = :item_no AND storage_no = :storage_no", nativeQuery = true)
	public Optional<Stock> findStock(@Param(value = "item_no") int item_no,@Param(value = "storage_no") int storage_no);

	// 재고 추가
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO stock(item_no, storage_no, quantity) VALUES (:item_no, :storage_no, :quantity)", nativeQuery = true)
	public void addStock(@Param(value = "item_no") int item_no,@Param(value ="storage_no") int storage_no,@Param(value = "quantity") int quantity);

//    @Query(value = "select i.*, sum(s.quantity) as quantity, s.stock_no, s.storage_no from item i inner join (select * from stock) s on i.item_no = s.item_no " +
//        "where i.item_name like concat('%', :name, '%') group by s.item_no", nativeQuery = true)

//	List<Stock> orderFindAllStock(String name);

	// 재고 조회(제일 많은 재고 번호 조회)
	@Query(value = "select stock_no from stock where item_no = :item_no order by quantity desc limit 1", nativeQuery = true)
	int orderFindStock(int item_no);

	@Transactional
	@Modifying
	@Query(value = "UPDATE stock SET quantity = quantity - :quantity WHERE stock_no = :stock_no", nativeQuery = true)
	public void orderUpdateStock (@Param(value = "stock_no") int stock_no, @Param(value = "quantity") int quantity);


    List<Stock> findAllByItem_ItemNameContaining(String itemName);

//    List<Stock> orderFindAllStock(String name);

    // [241014] 장유빈 기능 추가
    // 기능 상세 : 창고별 재고조회 기능
    List<Stock> findByStorage(Storage storage);

    List<Stock> findByItem_itemNo (int itemNo);
}
