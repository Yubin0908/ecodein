package com.project.ecodein.controller;

import com.project.ecodein.dto.ApprovalDTO;
import com.project.ecodein.dto.ApprovalStatusLableDTO;
import com.project.ecodein.dto.OrderDetailDTO;
import com.project.ecodein.service.ApprovalService;
import com.project.ecodein.service.OrderingService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/approval")
public class ApprovalController {

    private final ApprovalService APPROVAL_SERVICE;
    private final OrderingService ORDERING_SERVICE;

    public ApprovalController(ApprovalService approvalService, OrderingService orderingService) {
        APPROVAL_SERVICE = approvalService;
        ORDERING_SERVICE = orderingService;
    }

    @GetMapping("/{page}")
    public String approvalList(Model modal, @PathVariable(name = "page") Integer page) {
        if (page == null) {
            page = 1;
        }

        Page<ApprovalDTO> lists = APPROVAL_SERVICE.getApprovals((int) page);

        modal.addAttribute("approvals", lists);
        return "approval/approval-list";
    }

    @GetMapping("/detail/{approval_no}")
    public String approvalDetail(@PathVariable("approval_no") Integer approval_no, Model model, HttpSession session) {
        // 전자결재 정보
        ApprovalDTO approval = APPROVAL_SERVICE.getApproval(approval_no, session);
        model.addAttribute("approval", approval);

        // 전자결재 상태
        ApprovalStatusLableDTO statuslable = APPROVAL_SERVICE.getApprovalStatus(approval_no);
        log.info(String.valueOf(statuslable));
        model.addAttribute("statuslable", statuslable);

        // 발주정보
        List<OrderDetailDTO> orderDetails = APPROVAL_SERVICE.getOrderDetails(approval_no);
        model.addAttribute("orderDetails", orderDetails);

        // 발주수량
        model.addAttribute("count", APPROVAL_SERVICE.getOrderDetailCount(approval_no));

        return "approval/approval-detail";
    }

    @GetMapping("/status-update/{approval_no}/{status}")
    @ResponseBody
    public void approvalStatusUpdate(@PathVariable("approval_no") Integer approval_no,
                                       @PathVariable("status") byte status, HttpSession session) {
        APPROVAL_SERVICE.approvalStatusUpdate(approval_no, status, session);
        ORDERING_SERVICE.updateIsDeliveryByOrderNo(approval_no);
    }
}
