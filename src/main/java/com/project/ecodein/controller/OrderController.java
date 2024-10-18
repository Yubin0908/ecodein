package com.project.ecodein.controller;



import com.project.ecodein.dto.*;
import com.project.ecodein.entity.OrderDetail;
import com.project.ecodein.entity.Ordering;
import com.project.ecodein.service.ApprovalService;
import com.project.ecodein.service.ItemService;
import com.project.ecodein.service.OrderingService;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.project.ecodein.dto.OrderPoolDTO;
import com.project.ecodein.entity.Stock;


@Controller
@RequestMapping("/order")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderingService ORDERING_SERVICE;
    private final ItemService ITEM_SERVICE;
    private final ApprovalService APPROVAL_SERVICE;

    public OrderController(OrderingService ORDERING_SERVICE, ItemService ITEM_SERVICE, ApprovalService approvalService) {
        this.ORDERING_SERVICE = ORDERING_SERVICE;
        this.ITEM_SERVICE = ITEM_SERVICE;
        this.APPROVAL_SERVICE = approvalService;
    }

    // 발주 목록 페이지
    @GetMapping({"/{page}", "/{page}/{status}"})
    public String order(Model model,
                        @PathVariable("page") Integer page,
                        @RequestParam(required = false) String query,
                        @PathVariable(name = "status", required = false) String status) {

        Page<OrderingDTO> orders = ORDERING_SERVICE.getOrders(page, query, status);
        model.addAttribute("orders", orders);
        return "order/order";
    }

    // 삭제 기능 구현
    @GetMapping("/delete/{order_no}")
    public ResponseEntity<Void> deleteOrder(@PathVariable int order_no) {
        ORDERING_SERVICE.deleteOrder(order_no);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 페이지네이션
//	@GetMapping("/list")
//	public String orderList(
//			@RequestParam(name = "page", defaultValue = "1") int page,
//			@RequestParam(name = "query", required = false) String query,
//			Model model) {
//		// 페이지네이션 및 검색 결과 조회
//		Page<Ordering> orders = ORDER_SERVICE.getOrders(page, query);
//		model.addAttribute("orders", orders.getContent());
//		model.addAttribute("totalPages", orders.getTotalPages());
//		model.addAttribute("currentPage", page);
//		model.addAttribute("query", query);
//		return "order/order";
//	}

    // 발주 등록 페이지
    @GetMapping("/add")
    public String orderAdd(Model model) {

        return "order/orderAdd";
    }

    // 발주 등록 페이지_상품 검색 기능
    @GetMapping("/search")
    @ResponseBody
    public List<StockDTO> searchProducts(@RequestParam(name = "query", required = false) String query, Model model) {

        // 검색 로직 수행 (Stock 검색)
        List<StockDTO> stocks = ORDERING_SERVICE.searchStocksByName(query);
        //model.addAttribute("stocks", stocks);
        return stocks;
    }

    // 발주 등록 페이지_발주 등록 처리
    @PostMapping("/add")
    public String addOrder(@ModelAttribute OrderPoolDTO orderPoolDTO) {
        orderPoolDTO.getOrder_nos().forEach(System.out::println);
        log.info(String.valueOf(orderPoolDTO));
        ORDERING_SERVICE.addOrder(orderPoolDTO);
        return "redirect:/order/1/all";
    }

    // 발주검색_전체상품
//    @GetMapping("add")
//    public String stocks(@PathVariable Stock stock) {
//
//        return "redirect:/order/all";
//    }

    // 발주 상세 페이지
    @GetMapping("/detail/{order_no}")
    public String detail(@PathVariable int order_no, Model model) {

        Ordering order = ORDERING_SERVICE.findById(order_no);
        log.info("Order Data: " + order);

        List<OrderDetail> orderDetails = ORDERING_SERVICE.findOrderDetails(order_no);
        log.info("Order Detail: " + orderDetails);

        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);

        return "order/orderDetail";
    }

    // 발주 수정
    @GetMapping("/modify/{order_no}")
    public String modify (@PathVariable int order_no, Model model) {

        model.addAttribute("order", ORDERING_SERVICE.findById(order_no));
        model.addAttribute("orderDetails", ORDERING_SERVICE.findOrderDetails(order_no));

        return "order/orderModify";
    }

    @PostMapping("/modify/1")
    public String modifyOrder (@ModelAttribute OrderPoolDTO orderPoolDTO) {
        ORDERING_SERVICE.deleteOrderDetail(orderPoolDTO.getOrderNo());
        ORDERING_SERVICE.orderModify(orderPoolDTO);
        return "redirect:/order/1/all";
    }

    // 발주 상태
    @GetMapping("/delivery/{orderNo}")
    public String orderDelivery (@PathVariable int orderNo, Model model) {
        ApprovalStatusLableDTO approvalStatusLableDTO = APPROVAL_SERVICE.getApprovalStatus(orderNo);
        log.info("approvalStatusLable - {}", approvalStatusLableDTO);
        if (approvalStatusLableDTO != null && approvalStatusLableDTO.getStatus() == 3) {
            ORDERING_SERVICE.updateIsDelivery(orderNo);
            return "redirect:/order/detail/"+orderNo;
        }

        model.addAttribute("msg", "결재가 완료되지 않았습니다.");
        return "forward:/order/detail/"+orderNo;
    }
}