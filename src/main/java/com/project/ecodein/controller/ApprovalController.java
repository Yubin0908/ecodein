package com.project.ecodein.controller;

import com.project.ecodein.dto.ApprovalDTO;
import com.project.ecodein.dto.ApprovalStatusLableDTO;
import com.project.ecodein.dto.OrderDetailDTO;
import com.project.ecodein.dto.OrderingDTO;
import com.project.ecodein.service.ApprovalService;
import com.project.ecodein.service.ApprovalStatusLableService;
import com.project.ecodein.service.OrderingService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/approval")
public class ApprovalController {

    private final ApprovalService APPROVAL_SERVICE;
    private final OrderingService ORDERING_SERVICE;
    private final ApprovalStatusLableService APPROVAL_STATUS_LABLE_SERVICE;

    public ApprovalController(ApprovalService approvalService, OrderingService orderingService, ApprovalStatusLableService approvalStatusLableService) {
        APPROVAL_SERVICE = approvalService;
        ORDERING_SERVICE = orderingService;
        APPROVAL_STATUS_LABLE_SERVICE = approvalStatusLableService;
    }

    @GetMapping({"/{page}", "/{page}/{status}"})
    public String approvalList(Model modal,
                               @PathVariable(name = "page") Integer page,
                               @PathVariable(name = "status", required = false) byte status,
                               @RequestParam(name = "keyword", required = false) Integer keyword) {

        if (page == null) {
            page = 1;
        }

        if (status == 5 || keyword != null) {
            Page<ApprovalDTO> lists = APPROVAL_SERVICE.getApprovals((int) page, keyword);
            log.info(String.valueOf(lists));
            modal.addAttribute("approvals", lists);
        } else {
            Page<ApprovalStatusLableDTO> lists = APPROVAL_STATUS_LABLE_SERVICE.getApprovalStatusLables((int) page, status);
            log.info(String.valueOf(lists));
            modal.addAttribute("approvalLables", lists);
        }

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
        List<OrderingDTO> order = ORDERING_SERVICE.getOrderById(approval_no);
        List<OrderDetailDTO> orderDetails = APPROVAL_SERVICE.getOrderDetails(approval_no);
        log.error(String.valueOf(order));
        model.addAttribute("order", order);
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
