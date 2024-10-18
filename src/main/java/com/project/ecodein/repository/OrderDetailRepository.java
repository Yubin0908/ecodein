package com.project.ecodein.repository;


import com.project.ecodein.entity.OrderDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    @Query(value = "select * from order_detail where order_no = :approval_no", nativeQuery = true)
    List<OrderDetail> findAllByOrderId(int approval_no);

    @Query(value = "select count(*) count from order_detail where order_no = :approval_no", nativeQuery = true)
    int getOrderDetailCountByOrderNo(int approval_no);

    @Transactional
    @Query(value = "insert into order_detail (item_no, order_no, quantity) " +
            "values (:order_no, :orderNo, :quantitie)", nativeQuery = true)
    int addsave(int order_no, int orderNo, int quantitie);

    @Transactional
    @Modifying
    @Query(value = "delete from order_detail where order_no = :orderNo", nativeQuery = true)
    void deleteAllByOrderNo(@Param("orderNo") int orderNo);


    // 발주상세
    //insert into order_detail (item_no, order_no, quantity) values (:item_no, :order_no, :quantity);


}
