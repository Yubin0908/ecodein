package com.project.ecodein.service;



import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.ecodein.dto.ApprovalStatusLableDTO;
import com.project.ecodein.dto.OrderPoolDTO;
import com.project.ecodein.dto.OrderingDTO;
import com.project.ecodein.dto.StockDTO;
import com.project.ecodein.entity.Admin;
import com.project.ecodein.entity.Approval;
import com.project.ecodein.entity.ApprovalStatusLable;
import com.project.ecodein.entity.Buyer;
import com.project.ecodein.entity.Item;
import com.project.ecodein.entity.OrderDetail;
import com.project.ecodein.entity.Ordering;
import com.project.ecodein.entity.Stock;
import com.project.ecodein.entity.User;
import com.project.ecodein.repository.AdminRepository;
import com.project.ecodein.repository.ApprovalRepository;
import com.project.ecodein.repository.ApprovalStatusLableRepository;
import com.project.ecodein.repository.ItemRepository;
import com.project.ecodein.repository.OrderDetailRepository;
import com.project.ecodein.repository.OrderingRepository;
import com.project.ecodein.repository.StockRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class OrderingService {

    @PersistenceContext
    private EntityManager entityManager;

	private final OrderingRepository ORDERING_REPOSITORY;
    private final StockRepository STOCK_REPOSITORY;
    private final OrderDetailRepository ORDER_DETAIL_REPOSITORY;
    private final ApprovalRepository APPROVAL_REPOSITORY;
    private final ApprovalStatusLableRepository APPROVAL_STATUSLABLE_REPOSITORY;
    private final ModelMapper MODEL_MAPPER;
    private final HttpSession SESSION;
    private final AdminRepository ADMIN_REPOSITORY;
    private final ItemRepository ITEM_REPOSITORY;

    public OrderingService(OrderingRepository orderingRepository,
                           StockRepository stockRepository,
                           OrderDetailRepository orderDetailRepository,
                           ApprovalRepository approvalRepository,
                           ApprovalStatusLableRepository approvalStatusLableRepository,
                           ModelMapper modelMapper, HttpSession session,
                           AdminRepository adminRepository,
                           ItemRepository itemRepository, DefaultSslBundleRegistry sslBundleRegistry) {
        this.MODEL_MAPPER = modelMapper;
        this.ORDERING_REPOSITORY = orderingRepository;
        this.STOCK_REPOSITORY = stockRepository;
        this.ORDER_DETAIL_REPOSITORY = orderDetailRepository;
        this.APPROVAL_REPOSITORY = approvalRepository;
        this.APPROVAL_STATUSLABLE_REPOSITORY = approvalStatusLableRepository;
        this.SESSION = session;
        this.ADMIN_REPOSITORY = adminRepository;
        this.ITEM_REPOSITORY = itemRepository;
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
            Page<Ordering> ordering =
                ORDERING_REPOSITORY.searchByQuery(query, pageable);
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

        Approval prevApproval = APPROVAL_REPOSITORY.findTopByOrderByApprovalNoDesc();
        Ordering prevOrder = ORDERING_REPOSITORY.findTopByOrderByOrderNoDesc();

        User user = (User)SESSION.getAttribute("user");


        Approval approval = new Approval();
        Buyer buyer = user.getBuyerCode();
        Admin admin = ADMIN_REPOSITORY.findByAdminName("자동생성시스템");

        approval.setBuyer(buyer);
        approval.setApprovalRegistDate(LocalDateTime.now());
        approval.setAdmin(admin);
        approval.setSubject(null);

        approval = APPROVAL_REPOSITORY.save(approval);
        autoSaveApprovalStatusble(approval);

        Ordering ordering = new Ordering();
        ordering.setOrderNo(approval.getApprovalNo());
        ordering.setBuyerCode(buyer);
        ordering.setUserId(user);
        ordering.setOrderDate(Date.valueOf(LocalDate.now()));
        ordering.setDueDate(Date.valueOf(LocalDate.now()));
        ordering.setIsDelivery((byte) 0);

        ordering = ORDERING_REPOSITORY.save(ordering);


        for (int idx = 0; idx < orderPool.getOrder_nos().size(); idx++) {
            Item item = new Item();
            item.setItemNo(orderPool.getOrder_nos().get(idx));
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(ordering);
            orderDetail.setItem(item);
            orderDetail.setQuantity(orderPool.getQuantities().get(idx));
            ORDER_DETAIL_REPOSITORY.save(orderDetail);
        }

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
        List<Item> items = ITEM_REPOSITORY.findAllByItemNameContaining(name);
        List<Stock> stocks = STOCK_REPOSITORY.findAllByItem_ItemNameContaining(name);


        List<Stock> stockList = new ArrayList<Stock>();
        Stock stock = null;

        for (Item item : items) {
            if (!item.getIsMaterial()) {
                stock = new Stock();
                stock.setItem(item);
                stockList.add(stock);
            }
        }

        for (int i = 0; i < stocks.size(); i++) {
            for (int j = 0; j < stockList.size(); j++) {
                if (stockList.get(j).getItem().getItemNo() == stocks.get(i).getItem().getItemNo()) {
                    stockList.get(j).setQuantity(stockList.get(j).getQuantity() + stocks.get(i).getQuantity());
                    stockList.get(j).setStockNo(stocks.get(i).getStockNo());
                    stockList.get(j).setStorage(stocks.get(i).getStorage());
                    break;
                }
            }
        }

        log.error("stockList - {}", stockList);

        return stockList.stream().map(stockArr -> MODEL_MAPPER.map(stockArr, StockDTO.class)).toList();
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

    public List<OrderingDTO> getOrderById (Integer approvalNo) {
        return Collections.singletonList(MODEL_MAPPER.map(ORDERING_REPOSITORY.findById(approvalNo), OrderingDTO.class));
    }
}