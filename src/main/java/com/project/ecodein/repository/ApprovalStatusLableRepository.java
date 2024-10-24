package com.project.ecodein.repository;

import com.project.ecodein.entity.Approval;
import com.project.ecodein.entity.ApprovalStatusLable;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ApprovalStatusLableRepository extends JpaRepository<ApprovalStatusLable, Integer> {

    @Query(value = "select * from approval_status_lable where approval_no = :approval_no order by update_lable_date " +
        "desc limit 1", nativeQuery = true)
    ApprovalStatusLable findApprovalStatusLableById(int approval_no);

    ApprovalStatusLable findTopByApprovalOrderByStatusDesc(Approval approval);
    // 최초 생성 시, 전자결재 상태 업데이트 시 사용.
    @Transactional
    @Query(value = "insert into approval_status_lable (status, admin_id, approval_no) values (1, 'auto', :order_no)",
            nativeQuery = true)
    void autoSaveApprovalStatusble(Integer order_no);

    // 생성된 발주건 업데이트 시 사용.
    @Transactional
    @Modifying
    @Query(value = "insert into approval_status_lable (status, admin_id, approval_no) values (:status, :admin_id, " +
        ":approval_no)", nativeQuery = true)
    void updateApprovalStatus(byte status, String admin_id, Integer approval_no);

    @Query(value = "SELECT a.*, o.status_no, o.update_lable_date, o.status, o.admin_id " +
            "FROM approval a LEFT JOIN (" +
            "    SELECT approval_no, status_no, update_lable_date, status, admin_id" +
            "    FROM (SELECT asl.*, ROW_NUMBER() OVER (PARTITION BY approval_no ORDER BY status DESC) AS rn" +
            "             FROM approval_status_lable asl) AS ranked" +
            "    WHERE rn = 1 " +
            ") AS o ON o.approval_no = a.approval_no " +
            "WHERE o.status = :status", nativeQuery = true)
    Page<ApprovalStatusLable> findByStatus(byte status, Pageable pageable);
}
