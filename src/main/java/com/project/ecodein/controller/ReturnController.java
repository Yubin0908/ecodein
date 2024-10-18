package com.project.ecodein.controller;

import com.project.ecodein.dto.*;
import java.time.LocalDateTime;
import java.util.List;

import com.project.ecodein.entity.OrderDetail;
import com.project.ecodein.entity.Ordering;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.project.ecodein.entity.User;
import com.project.ecodein.repository.OrderDetailRepository;
import com.project.ecodein.service.OrderingService;
import com.project.ecodein.service.ReturnItemService;
import com.project.ecodein.service.ReturnService;
import com.project.ecodein.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/return")
public class ReturnController {

    private final HttpSession httpSession;
    private final OrderingService ORDERING_SERVICE;
    private final UserService USER_SERVICE;
    private final OrderDetailRepository ORDER_DETAIL_SERVICE;
    private final ReturnService RETURN_SERVICE;
    private final ReturnItemService RETURN_ITEM_SERVICE;

    public ReturnController (HttpSession httpSession, OrderingService orderingService, UserService userService,
                             OrderDetailRepository orderDetailRepository, ReturnService returnService, ReturnItemService returnItemService) {
        this.httpSession = httpSession;
        this.ORDERING_SERVICE = orderingService;
        this.USER_SERVICE = userService;
        this.ORDER_DETAIL_SERVICE = orderDetailRepository;
        this.RETURN_SERVICE = returnService;
        this.RETURN_ITEM_SERVICE = returnItemService;
    }

    @GetMapping({"/{page}/{buyerCode}", "/{page}/{buyerCode}/{type}"})
    public String returnPage (@PathVariable Long buyerCode, @PathVariable Integer page,
                              @PathVariable String type, Model model) {
        model.addAttribute("returns", RETURN_SERVICE.findAllByBuyerCode(buyerCode, page, type));
        model.addAttribute("previousMonth", RETURN_SERVICE.countPreviousMonth(buyerCode));
        model.addAttribute("currentMonth", RETURN_SERVICE.countCurrentMonth(buyerCode));
        model.addAttribute("accepted", RETURN_SERVICE.countAcceptedProcess());
        model.addAttribute("rejected", RETURN_SERVICE.countRejectedProcess());
        model.addAttribute("completed", RETURN_SERVICE.countCompletedProcess());
        return "return/return";
    }

    @RequestMapping("/modal/{type}")
    public String modalPage (@PathVariable String type, @RequestBody(required = false) Map<String, String> payload, Model model) {
        String returnId = null;
        if (payload != null) {
            returnId = payload.get("returnId");
        }
        User user = (User) httpSession.getAttribute("user");
        LocalDateTime now = LocalDateTime.now();

        if (type.equals("regist")) {
            log.info("type: {} now: {}", type, now);

            model.addAttribute("tab", "반품 접수 - ");

            if (user != null) {
                model.addAttribute("buyerName", user.getBuyerCode().getBuyerName());
            }
        } else if (type.equals("detail")) {
            model.addAttribute("tab", "반품 상세 정보 - ");
            model.addAttribute("serviceName", RETURN_SERVICE.findById(returnId));
            model.addAttribute("items", RETURN_ITEM_SERVICE.findByReturnId(returnId));
            model.addAttribute("tabName", "반품 상세 정보 - ");
            model.addAttribute("serviceName", user.getBuyerCode().getBuyerName() + "-" + now.getMonthValue() +
                "/" + now.getDayOfMonth());
        }

        return "functional/return-modal";
    }

    @GetMapping("/accept/{returnId}/{returnData}")
    public String returnAccept (@PathVariable String returnId, @PathVariable String returnData) {
        RETURN_SERVICE.returnAccepted(returnId, returnData);

        return "redirect:/return/1/0/all";
    }

    @GetMapping("/fetch-order/{buyer_code}")
    @ResponseBody
    public List<Ordering> fetchOrder (Model model, @PathVariable int buyer_code) {
        return ORDERING_SERVICE.getOrderings(buyer_code);
    }

    @GetMapping("/fetch-user/{buyer_code}")
    @ResponseBody
    public List<UserDTO> fetchUser (Model model, @PathVariable int buyer_code) {
        return USER_SERVICE.findAllByBuyerCode(buyer_code);
    }

    @GetMapping("/fetch-orderDetail/{order_no}")
    @ResponseBody
    public List<OrderDetail> fetchOrderDetail (Model model, @PathVariable int order_no) {
        return ORDER_DETAIL_SERVICE.findAllByOrderId(order_no);
    }

    @PostMapping("/fetch-add")
    public String fetchAdd(@ModelAttribute ReturnItemPoolDTO returnItems) {
        System.out.println(returnItems);
        RETURN_SERVICE.saveAll(returnItems);
        return "redirect:modal/regist";
    }

}
