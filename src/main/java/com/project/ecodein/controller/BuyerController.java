package com.project.ecodein.controller;

import java.util.Optional;

import com.project.ecodein.dto.BuyerDTO;
import com.project.ecodein.entity.Buyer;
import org.springframework.data.domain.Page;
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
import com.project.ecodein.dto.Search;
import com.project.ecodein.service.BuyerService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping ("/buyer")
@Slf4j
public class  BuyerController {

	private final BuyerService BUYER_SERVICE;

	public BuyerController (BuyerService buyerService) {

		this.BUYER_SERVICE = buyerService;

	}

	@GetMapping (value = { "/{buyer_status}", "/{buyer_status}/{page}" })
	public String buyerList (
		Model model,
		@PathVariable (name = "buyer_status") String buyer_status,
		@PathVariable (name = "page", required = false) Integer page,
		@RequestParam (name = "keyword", required = false) String keyword) {

		Search search = new Search ();

		if (page == null) {

			page = 1;

		}

		model.addAttribute ("search", search);

		log.info ("search - address-{}, keyword - {}", search, keyword);

		Page<BuyerDTO> list = BUYER_SERVICE.buyers ((int) page, keyword, buyer_status);

		model.addAttribute ("buyers", list);

		return "buyer/buyer";

	}

	@GetMapping ("/add")
	public String buyerAdd () {

		return "buyer/add";

	}

	@PostMapping ("/add")
	public String buyerAdd (@ModelAttribute BuyerDTO buyerDTO, Model model) {

		BuyerDTO buyer = BUYER_SERVICE.buyerInsert (buyerDTO);

		if (buyer != null) {
			model.addAttribute ("message", "거래처 정보가 정상 등록 되었습니다.");
		}

		return "redirect:/buyer/default/1?keyword=";

	}

	@GetMapping ("/detail/{buyer_code}")
	@ResponseBody
	public Optional<BuyerDTO> modifty (@PathVariable ("buyer_code") Long buyer_code) {

		Optional<BuyerDTO> buyer = BUYER_SERVICE.buyerDetail (buyer_code);

		log.info ("buyer - {}", buyer);

		return buyer;

	}
	
	@PostMapping ("/status-update")
	@ResponseBody
	public int statusUpdate (@ModelAttribute BuyerDTO buyer) {
		BUYER_SERVICE.updateStatus (buyer);
		return 1;
	}
	
	@PostMapping ("/modify")
	@ResponseBody
	public ResponseEntity<String> modify(@ModelAttribute BuyerDTO buyer) {

		BUYER_SERVICE.updateBuyer (buyer);

		return ResponseEntity.status(200).body("수정이 완료됨");
	}

}
