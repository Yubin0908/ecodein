package com.project.ecodein.service;

import com.project.ecodein.dto.*;
import com.project.ecodein.entity.Buyer;
import com.project.ecodein.entity.Return;
import com.project.ecodein.entity.ReturnItem;
import com.project.ecodein.entity.User;
import com.project.ecodein.repository.BuyerRepository;
import com.project.ecodein.repository.ReturnItemRepository;
import com.project.ecodein.repository.ReturnRepository;
import com.project.ecodein.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Optional;

@Service
@Slf4j
public class ReturnService {

    private final ReturnRepository RETURN_REPOSITORY;
    private final ReturnItemRepository RETURN_ITEM_REPOSITORY;
    private final UserRepository USER_REPOSITORY;
    private final BuyerRepository BUYER_REPOSITORY;
    private final ModelMapper MODEL_MAPPER;
    private final HttpSession SESSION;

    public ReturnService(ReturnRepository returnRepository, ReturnItemRepository returnItemRepository,
                         UserRepository userRepository, ModelMapper modelMapper, BuyerRepository buyerRepository ,HttpSession session) {
        this.RETURN_REPOSITORY = returnRepository;
        this.RETURN_ITEM_REPOSITORY = returnItemRepository;
        this.USER_REPOSITORY = userRepository;
        this.BUYER_REPOSITORY = buyerRepository;
        this.MODEL_MAPPER = modelMapper;
        this.SESSION = session;
    }

    // 반품등록 프로세스
    public void saveAll (ReturnItemPoolDTO returnItems) {
        User user = USER_REPOSITORY.findByUserId(returnItems.getUser_id()).get();
        String buyerName = user.getBuyerCode().getBuyerName();
        String returnId = createdPrimaryKey(buyerName);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date date = new Date(returnItems.getDate().getTime());

        // ReturnDTO 데이터 입력
        ReturnDTO returnDTO = new ReturnDTO();
        returnDTO.setReturnId(returnId);
        returnDTO.setBuyer(user.getBuyerCode());
        returnDTO.setUser(user);
        returnDTO.setReturnReason(returnItems.getReturn_promise());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(returnItems.getDate());
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);

        returnDTO.setReturnRegistDate(new java.sql.Date(calendar.getTimeInMillis()));

        Return entity = RETURN_REPOSITORY.save(MODEL_MAPPER.map(returnDTO, Return.class));

        // ReturnItemDTO 데이터 입력
        for(int idx = 0; idx < returnItems.getQuantitys().size(); idx++) {
            ReturnItemDTO returnItemDTO = new ReturnItemDTO();
            returnItemDTO.setItem(returnItems.getItems().get(idx));
            returnItemDTO.setReturnItemQty(returnItems.getQuantitys().get(idx));
            returnItemDTO.setAReturn(entity);
            returnItemDTO.setStorage(null);
            RETURN_ITEM_REPOSITORY.save(MODEL_MAPPER.map(returnItemDTO, ReturnItem.class));
        }

    }

    // 반품목록 조회 프로세스
    public Page<ReturnDTO> findAllByBuyerCode(Long buyerCode, Integer page, String type) {

        Optional<Buyer> buyer = BUYER_REPOSITORY.findById(buyerCode);
        Pageable pageable = PageRequest.of (page - 1, 10, Sort.by(Sort.Order.desc("returnId")));
        Calendar calendar = Calendar.getInstance();

        Page<Return> returns = null;
        Byte returnStatus = switch (type) {
            case "accepted" -> 0;
            case "rejected" -> 1;
            case "completed" -> 2;
            default -> null;
        };
        if (returnStatus != null) {
            if (buyerCode == 0) {
                returns = RETURN_REPOSITORY.findByReturnStatus(returnStatus, pageable);
            } else {
                returns = RETURN_REPOSITORY.findByBuyerAndReturnStatus(buyer, returnStatus, pageable);
            }
        } else if (type.equals("previous")) {
            calendar.add(Calendar.MONTH, -1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            Date from = new Date(calendar.getTimeInMillis());
            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, -1);
            Date to = new Date(calendar.getTimeInMillis());

            if (buyerCode == 0) {
                returns = RETURN_REPOSITORY.findByReturnRegistDateBetween(from, to, pageable);
            } else {
                returns = RETURN_REPOSITORY.findByBuyerAndReturnRegistDateBetween(buyer, from, to, pageable);
            }
        } else if (type.equals("current")) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            Date from = new Date(calendar.getTimeInMillis());
            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, -1);
            Date to = new Date(calendar.getTimeInMillis());

            if (buyerCode == 0) {
                returns = RETURN_REPOSITORY.findByReturnRegistDateBetween(from, to, pageable);
            } else {
                returns = RETURN_REPOSITORY.findByBuyerAndReturnRegistDateBetween(buyer, from, to, pageable);
            }
        } else {
            if (buyerCode != 0) {
                returns = RETURN_REPOSITORY.findBybuyer(buyer, pageable);
            } else {
                returns = RETURN_REPOSITORY.findAll(pageable);
            }
        }

        return returns.map(aReturn -> MODEL_MAPPER.map(aReturn, ReturnDTO.class));
    }

    // 전월 반품 수량 조회 프로세스
    public int countPreviousMonth (Long buyerCode) {
        Buyer buyer = null;
        if (buyerCode != 0) {
            buyer = BUYER_REPOSITORY.findById(buyerCode).get();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, -1);

        Date from = new Date(calendar.getTimeInMillis());

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, 1);
        Date to = new Date(calendar.getTimeInMillis());

        if (buyerCode == 0) {
            return RETURN_REPOSITORY.countByReturnRegistDateBetween(from, to);
        }
        return RETURN_REPOSITORY.countByBuyerAndReturnRegistDateBetween(buyer, from, to);
    }

    // 당월 반품 수량 조회 프로세스
    public int countCurrentMonth (Long buyerCode) {
        Buyer buyer = null;
        if (buyerCode != 0) {
            buyer = BUYER_REPOSITORY.findById(buyerCode).get();
        }

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date from = new Date(calendar.getTimeInMillis());

        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, -1);

        Date to = new Date(calendar.getTimeInMillis());

        if (buyerCode == 0) {
            return RETURN_REPOSITORY.countByReturnRegistDateBetween(from, to);
        }
        return RETURN_REPOSITORY.countByBuyerAndReturnRegistDateBetween(buyer, from, to);
    }

    // 접수 상태 수량조회 프로세스
    public int countAcceptedProcess () {
        byte returnStatus = 0;
        Buyer buyer = null;
        User user = (User) SESSION.getAttribute("user");

        if (user != null) {
            buyer = user.getBuyerCode();
        }

        if (buyer == null) {
            return RETURN_REPOSITORY.countByReturnStatus(returnStatus);
        }

        return RETURN_REPOSITORY.countByBuyerAndReturnStatus(buyer, returnStatus);
    }

    // 진행중 상태 수량조회 프로세스
    public int countRejectedProcess () {
        byte returnStatus = 1;
        Buyer buyer = null;
        User user = (User) SESSION.getAttribute("user");

        if (user != null) {
            buyer = user.getBuyerCode();
        }

        if (buyer == null) {
            return RETURN_REPOSITORY.countByReturnStatus(returnStatus);
        }

        return RETURN_REPOSITORY.countByBuyerAndReturnStatus(buyer, returnStatus);
    }

    // 완료 상태 수량조회 프로세스
    public int countCompletedProcess () {
        byte returnStatus = 2;
        Buyer buyer = null;
        User user = (User) SESSION.getAttribute("user");

        if (user != null) {
            buyer = user.getBuyerCode();
        }

        if (buyer == null) {
            return RETURN_REPOSITORY.countByReturnStatus(returnStatus);
        }

        return RETURN_REPOSITORY.countByBuyerAndReturnStatus(buyer, returnStatus);
    }

    // 반품 상세 조회 프로세스
    public ReturnDTO findById (String returnId) {
        Optional<Return> returns = RETURN_REPOSITORY.findById(returnId);

        if (returns.get().getReturnStatus() != 2) {
            returnAccepted(returnId, "1");
        }

        return MODEL_MAPPER.map(returns, ReturnDTO.class);
    }

    // PK 생성 메서드
    private String createdPrimaryKey(String buyerName) {
        LocalDateTime now = LocalDateTime.now();

        return buyerName + "-" + now.getYear() + "-" + now.getMonthValue() + "-" + now.getHour() + "-" + now.getMinute();
    }

    // 반품 상태 변경 프로세스
    public void returnAccepted (String returnId, String returnData) {
        byte returnStatus = (byte) (returnData.equals("accept") ? 2 : 1);

        Return returns = RETURN_REPOSITORY.findById(returnId).get();

        returns.setReturnStatus(returnStatus);
        RETURN_REPOSITORY.save(returns);
    }
}
