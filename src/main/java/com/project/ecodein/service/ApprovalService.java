package com.project.ecodein.service;


import com.project.ecodein.entity.Approval;
import com.project.ecodein.dto.ApprovalDTO;
import com.project.ecodein.dto.ApprovalStatusLableDTO;
import com.project.ecodein.dto.OrderDetailDTO;
import com.project.ecodein.entity.Admin;
import com.project.ecodein.entity.ApprovalStatusLable;
import com.project.ecodein.entity.OrderDetail;
import com.project.ecodein.repository.ApprovalRepository;
import com.project.ecodein.repository.ApprovalStatusLableRepository;
import com.project.ecodein.repository.OrderDetailRepository;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApprovalService {

    private final ApprovalRepository APPROVAL_REPOSITORY;
    private final ApprovalStatusLableRepository APPROVAL_STATUS_LABLE;
    private final OrderDetailRepository ORDER_DETAIL_REPOSITORY;
    private final ModelMapper MODEL_MAPPER;

    public ApprovalService(ApprovalRepository APPROVAL_REPOSITORY,
                           ApprovalStatusLableRepository APPROVAL_STATUS_LABLE, OrderDetailRepository ORDER_DETAIL_REPOSITORY, ModelMapper modelMapper) {
        this.APPROVAL_REPOSITORY = APPROVAL_REPOSITORY;
        this.APPROVAL_STATUS_LABLE = APPROVAL_STATUS_LABLE;
        this.ORDER_DETAIL_REPOSITORY = ORDER_DETAIL_REPOSITORY;
        this.MODEL_MAPPER = modelMapper;
    }

    // 목록 출력
    public Page<ApprovalDTO> getApprovals(int page) {

        Sort sort = Sort.by(Sort.Direction.DESC, "approvalNo");
        Pageable pageable = PageRequest.of(page - 1, 10, sort);

//        return APPROVAL_REPOSITORY.findAll(pageable);
        Page<Approval> approval = APPROVAL_REPOSITORY.findAll(pageable);
        return approval.map(approval1 -> MODEL_MAPPER.map(approval1, ApprovalDTO.class));
    }

    // 상세 정보
    public ApprovalDTO getApproval(int approval_no, HttpSession session) {

        if (APPROVAL_STATUS_LABLE.findApprovalStatusLableById(approval_no).getStatus() == 1) {
            APPROVAL_STATUS_LABLE.updateApprovalStatus((byte) 2, ((Admin) session.getAttribute("admin")).getAdminId(), approval_no);
        }

//        return APPROVAL_REPOSITORY.findById(approval_no).orElse(null);
         Optional<Approval> approval = APPROVAL_REPOSITORY.findById(approval_no);
        return approval.map(approval1 -> MODEL_MAPPER.map(approval1, ApprovalDTO.class)).orElse(null);
    }


    // 최신상태 불러오기
    public ApprovalStatusLableDTO getApprovalStatus(int approval_no) {
        Approval approval = new Approval();
        approval.setApprovalNo(approval_no);
//        return APPROVAL_STATUS_LABLE.findApprovalStatusLableById(approval_no);
//        ApprovalStatusLable approvalStatusLable = APPROVAL_STATUS_LABLE.findApprovalStatusLableById(approval_no);
        ApprovalStatusLable approvalStatusLable = APPROVAL_STATUS_LABLE.findTopByApprovalOrderByStatusDesc(approval);
        return MODEL_MAPPER.map(approvalStatusLable, ApprovalStatusLableDTO.class);
    }
    
    // 발주 상품리스트
    public List<OrderDetailDTO> getOrderDetails(int approval_no) {
        List<OrderDetail> orderDetails = ORDER_DETAIL_REPOSITORY.findAllByOrderId(approval_no);
        return orderDetails.stream().map(orderDetail -> MODEL_MAPPER.map(orderDetail, OrderDetailDTO.class)).toList();
    }

    // 발주 상품 수량
    public int getOrderDetailCount(int approval_no) {
        return ORDER_DETAIL_REPOSITORY.getOrderDetailCountByOrderNo(approval_no);

    }

    // 결재 상태 업데이트
    public void approvalStatusUpdate (Integer approval_no, byte status, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        String admin_id = admin.getAdminId();
        APPROVAL_STATUS_LABLE.updateApprovalStatus(status, admin_id, approval_no);
    }
}
