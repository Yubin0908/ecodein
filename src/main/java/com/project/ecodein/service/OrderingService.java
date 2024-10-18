package com.project.ecodein.service;



import com.project.ecodein.dto.*;
import com.project.ecodein.entity.*;
import com.project.ecodein.entity.Approval;
import com.project.ecodein.entity.ApprovalStatusLable;
import com.project.ecodein.entity.OrderDetail;
import com.project.ecodein.entity.Ordering;
import com.project.ecodein.repository.*;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import com.project.ecodein.dto.OrderPoolDTO;
import com.project.ecodein.repository.OrderDetailRepository;
import com.project.ecodein.repository.OrderingRepository;
import com.project.ecodein.repository.StockRepository;


@Slf4j
@Service
public class OrderingService {


	private final OrderingRepository ORDERING_REPOSITORY;
    private final StockRepository STOCK_REPOSITORY;
    private final OrderDetailRepository ORDER_DETAIL_REPOSITORY;
    private final ApprovalRepository APPROVAL_REPOSITORY;
    private final ApprovalStatusLableRepository APPROVAL_STATUSLABLE_REPOSITORY;
    private final ModelMapper MODEL_MAPPER;
    private final HttpSession SESSION;
    private final AdminRepository ADMIN_REPOSITORY;

    public OrderingService(OrderingRepository orderingRepository,
                           StockRepository stockRepository,
                           OrderDetailRepository orderDetailRepository,
                           ApprovalRepository approvalRepository,
                           ApprovalStatusLableRepository approvalStatusLableRepository,
                           ModelMapper modelMapper, HttpSession session,
                           AdminRepository adminRepository) {
        this.MODEL_MAPPER = modelMapper;
        this.ORDERING_REPOSITORY = orderingRepository;
        this.STOCK_REPOSITORY = stockRepository;
        this.ORDER_DETAIL_REPOSITORY = orderDetailRepository;
        this.APPROVAL_REPOSITORY = approvalRepository;
        this.APPROVAL_STATUSLABLE_REPOSITORY = approvalStatusLableRepository;
        this.SESSION = session;
        this.ADMIN_REPOSITORY = adminRepository;
    }

    // mainPage에서 사용할 메서드
    public List<OrderingDTO> getOrderings() {
        Sort sort = Sort.by(Sort.Direction.DESC, "orderNo");
        List<Ordering> ordering = ORDERING_REPOSITORY.findAll(sort);
        return ordering.stream().map(ordering1 -> MODEL_MAPPER.map(ordering1, OrderingDTO.class)).toList();
    }

    // To: ReturnController
    public List<Ordering> getOrderings(int buyer_code) {
        Sort sort = Sort.by(Sort.Direction.DESC, "orderNo");
        return ORDERING_REPOSITORY.findAllByBuyerCode(buyer_code);
    }

	// 페이지네이션 및 검색 기능 구현
	public Page<OrderingDTO> getOrders(@Param("page") int page, @Param("query") String query,
                                       @Param("status") String status) {

        Sort sort = Sort.by(Sort.Order.desc(query == null && status.equals("all") ? "orderNo" : "order_no"));
        Pageable pageable = PageRequest.of(page - 1, 10, sort);
        // (전체검색)검색어 없고, 상태 체크 안한 경우
        if (query == null && status.equals("all")) {
            Page<Ordering> ordering = ORDERING_REPOSITORY.findAll(pageable);
            return ordering.map(ordering1 -> MODEL_MAPPER.map(ordering1, OrderingDTO.class));
            // (키워드 검색) 검색어가 있는 경우
        } else if (query != null) {
            Page<Ordering> ordering = ORDERING_REPOSITORY.searchByQuery(query, pageable);
            return ordering.map(ordering1 -> MODEL_MAPPER.map(ordering1, OrderingDTO.class));
            // (상태 검색) 상태코드가 있는 경우
        } else {
            byte statusble = (byte) (status.equals("accept") ? 0 : status.equals("validation") ? 1 : status.equals("progress") ? 2 : 3);
            Page<Ordering> ordering = ORDERING_REPOSITORY.findAllByIsDelivery(statusble, pageable);
            return ordering.map(ordering1 -> MODEL_MAPPER.map(ordering1, OrderingDTO.class));
        }
	}

	// 삭제 기능
	public void deleteOrder(@Param("orderNo") int orderNo) {
        deleteOrderDetail(orderNo);
        ORDERING_REPOSITORY.deleteById(orderNo);
	}

    public void deleteOrderDetail(int orderNo) {
        ORDER_DETAIL_REPOSITORY.deleteAllByOrderNo(orderNo);
    }

    // 발주등록
    @Transactional
    public void addOrder(OrderPoolDTO orderPool) {
        //Ordering ordering = ORDERING_REPOSITORY.addsave(orderPool.getBuyer_code(), orderPool.getUser_id(), orderPool.getDue_date());
        User user = (User)SESSION.getAttribute("user");
        Ordering ordering = new Ordering();
        Approval approval = new Approval();
        Buyer buyer = new Buyer();
        buyer.setBuyerCode(user.getBuyerCode().getBuyerCode());
        approval.setBuyer(user.getBuyerCode());
        approval.setSubject(null);
        approval.setApprovalRegistDate(LocalDateTime.now());
        ordering.setBuyerCode(buyer);
        ordering.setUserId(new User(orderPool.getUserId()));
        ordering.setDueDate(orderPool.getDue_date());
        ordering.setOrderDate(Date.valueOf(LocalDate.now()));
        approval.setOrdering(ordering);
        buyer.setBuyerCode((long) orderPool.getBuyerCode());

        ordering.setBuyerCode(buyer);
        ordering.setUserId(new User(orderPool.getUserId()));
        ordering.setDueDate(orderPool.getDue_date());
        ordering.setOrderDate(Date.valueOf(LocalDate.now()));

        approval = APPROVAL_REPOSITORY.save(approval);

        ordering.setApproval(approval);
        Ordering order = ORDERING_REPOSITORY.save(ordering);
        autoSaveApprovalStatusble(approval);

        for (int idx = 0; idx < orderPool.getOrder_nos().size(); idx++) {
            Item item = new Item();
            item.setItemNo(orderPool.getOrder_nos().get(idx));
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setItem(item);
            orderDetail.setQuantity(orderPool.getQuantities().get(idx));
            ORDER_DETAIL_REPOSITORY.save(orderDetail);
        }

        // Approval approval = APPROVAL_REPOSITORY.autoSaveApproval(order.getOrderNo(), (long) Math.toIntExact(order.getBuyerCode().getBuyerCode()), null);

    }

    // 발주 수정
    @Modifying
    @Transactional
    public void orderModify(OrderPoolDTO orderPool) {
        Ordering ordering = new Ordering();
        Buyer buyer = new Buyer();
        buyer.setBuyerCode((long) orderPool.getBuyerCode());
        ordering.setOrderNo(orderPool.getOrderNo());
        ordering.setBuyerCode(buyer);
        ordering.setUserId(new User(orderPool.getUserId()));
        ordering.setDueDate(orderPool.getDue_date());
        ordering.setOrderDate(Date.valueOf(LocalDate.now()));
        log.info("orderPool - {}", orderPool);
        Ordering order = ORDERING_REPOSITORY.save(ordering);

        for (int idx = 0; idx < orderPool.getOrder_nos().size(); idx++) {
            OrderDetail orderDetail = new OrderDetail();
            Item item = new Item();
            item.setItemNo(orderPool.getOrder_nos().get(idx));
            orderDetail.setOrder(order);
            orderDetail.setItem(item);
            orderDetail.setQuantity(orderPool.getQuantities().get(idx));
            ORDER_DETAIL_REPOSITORY.save(orderDetail);
        }
        System.out.println("order" + order);
    }

    // 발주등록_Stock을 이름으로 검색하는 메서드 추가
    public List<StockDTO> searchStocksByName(String name) { // StockDTO 추가할 예정!
        Item item = new Item();
        item.setItemName(name);
        List<Stock> stocks = STOCK_REPOSITORY.findByItem(item);
        //        List<Stock> stocks = STOCK_REPOSITORY.orderFindAllStock(name);
        return stocks.stream().map(stock -> MODEL_MAPPER.map(stock, StockDTO.class)).toList();
//        List<Stock> stocks = STOCK_REPOSITORY.orderFindAllStock(name);
//        return stocks.map(stocks1 -> MODEL_MAPPER.map(stocks1, Stock.class));
    }

    // 발주등록_상품등록
    public void registerOrder(List<Integer> productIds, List<Integer> quantities) {

        for (int i = 0; i < productIds.size(); i++) {
            int productId = productIds.get(i);
            int quantity = quantities.get(i);

            Stock stock = STOCK_REPOSITORY.findById(productId).orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
        }
    }

    // 발주상세(마지막 삭제)
//    public void findById(int orderNo) {
//    }

    // 발주 정보 조회
    public Ordering findById(int id) {
        return ORDERING_REPOSITORY.findById(id).orElse(null);
    }

    // 발주 상세 정보 조회
    public List<OrderDetail> findOrderDetails(int orderNo) {
        return ORDER_DETAIL_REPOSITORY.findAllByOrderId(orderNo);
    }

    // 발주 상태 업데이트
    public void updateIsDelivery (int orderNo) {
        ORDERING_REPOSITORY.updateIsDeliveryByOrderNo(orderNo);
        updateStock(orderNo);
    }


    // 상품검색_모든 상품
//    private void stocks () {
//        STOCK_REPOSITORY.findAll();
//    }


    // 발주 상세 페이지 수정
    private void updateStock (int orderNo) {
        List<OrderDetail> orderDetail = ORDER_DETAIL_REPOSITORY.findAllByOrderId(orderNo);

        for (int i = 0; i < orderDetail.size(); i++) {
            int stock_no = STOCK_REPOSITORY.orderFindStock(orderDetail.get(i).getItem().getItemNo());
            STOCK_REPOSITORY.orderUpdateStock(stock_no, orderDetail.get(i).getQuantity());
        }
    }


    // 전자결재 상태
    @Transactional
    protected void autoSaveApprovalStatusble (Approval approval) {
        Admin admin = new Admin();
        admin.setAdminId("auto");

        ApprovalStatusLableDTO approvalStatusLableDTO = new ApprovalStatusLableDTO();
        approvalStatusLableDTO.setAdmin(admin);
        approvalStatusLableDTO.setStatus((byte) 1);
        approvalStatusLableDTO.setApproval(approval);
        approvalStatusLableDTO.setUpdateLableDate(LocalDateTime.now());
        APPROVAL_STATUSLABLE_REPOSITORY.save(MODEL_MAPPER.map(approvalStatusLableDTO, ApprovalStatusLable.class));
    }

    public void updateIsDeliveryByOrderNo(Integer approvalNo) {
        ORDERING_REPOSITORY.updateIsDeliveryTwoByOrderNo(approvalNo);
    }
}