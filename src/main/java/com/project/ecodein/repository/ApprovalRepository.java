package com.project.ecodein.repository;

import com.project.ecodein.dto.ApprovalDTO;
import com.project.ecodein.entity.Approval;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ApprovalRepository extends JpaRepository<Approval, Integer> {

    // 전자결재 목록
    @Query(value = "select ap.approval_no, ap.buyer_code, approval_regist_date, status.status " +
        "from approval ap left join (select * from approval_status_lable order by update_lable_date) status " +
        "on ap.approval_no = status.approval_no " +
        "group by ap.approval_no", nativeQuery = true)
    Page<Approval> approvalsByApprovalStatus(Pageable pageable);

    // 발주 시, 실행되는 전자결재 자동생성 기능
    @Transactional
    @Query(value = "insert into approval (approval_no, buyer_code, approval_admin, subject)" +
                "values (:order_no, :buyer_code, 'auto', :subject)", nativeQuery = true)
    Approval autoSaveApproval(Integer order_no, Long buyer_code, String subject);
}
