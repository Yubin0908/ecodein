package com.project.ecodein.controller;

import com.project.ecodein.service.BoardService;
import com.project.ecodein.service.OrderingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainContoller {

    private final OrderingService ORDERING_SERVICE;
    private final BoardService BOARD_SERVICE;
    private final HttpSession SESSION;

    public MainContoller(OrderingService orderingService, BoardService boardService, HttpSession session) {
        this.ORDERING_SERVICE = orderingService;
        this.BOARD_SERVICE = boardService;
        this.SESSION = session;
    }

	@RequestMapping ("/main")
	public String index (Model model) {
        model.addAttribute("orders", ORDERING_SERVICE.getOrderings());
        model.addAttribute ("boards", BOARD_SERVICE.mainPageBoardList (SESSION));
		return "main";

	}

    @GetMapping("/admin-management")
    public String adminManagement (Model model) {
        model.addAttribute("menuName", "관리자 승인 요청관리");
        model.addAttribute("auth", "admin-management");
        return "functional/modal";
    }

}
