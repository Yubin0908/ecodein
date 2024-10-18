package com.project.ecodein.repository;

import com.project.ecodein.entity.Buyer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Long> {
	// 검색 시 실행되는 코드
	Page<Buyer> findByBuyerStatusAndBuyerName (@Param ("status") boolean status,
                                                            @Param ("keyword") String buyerName,
                                                            Pageable pageable);

	// 기본 실행 코드(조회)
	Page<Buyer> findByBuyerStatus (Pageable pageable, @Param ("status") boolean buyerStatus);

	// 전체 검색(폐업 제외)
	@Query ("SELECT b FROM Buyer b WHERE b.buyerStatus = true ")
	public Page<Buyer> findAllAcitve (Pageable pageable);

	// 상호명으로 검색(폐업 제외)
	@Query ("SELECT b FROM Buyer b WHERE (b.buyerName LIKE CONCAT('%',:buyer_name,'%')) AND b.buyerStatus = true ")
	public Page<Buyer> findByBuyerNameActive (String buyer_name, Pageable pageable);


}
