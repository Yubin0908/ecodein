package com.project.ecodein.controller;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.project.ecodein.common.Pagenation;
import com.project.ecodein.common.PagingButtonInfo;
import com.project.ecodein.dto.ItemDTO;
import com.project.ecodein.entity.Item;
import com.project.ecodein.service.ItemService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping ("/item")
public class ItemController {

	private final ItemService ITEM_SERVICE;

	public ItemController (ItemService ITEM_SERVICE) {

		this.ITEM_SERVICE = ITEM_SERVICE;

	}

	@GetMapping ("")
	public String item (Model model,
		@RequestParam (defaultValue = "") String search,
		@RequestParam (defaultValue = "false") boolean is_item,
		@RequestParam (defaultValue = "") Integer itemNo,
		@RequestParam (defaultValue = "1") int page) {

		Page<ItemDTO> items = ITEM_SERVICE.findItem (page, is_item, search, itemNo);
		PagingButtonInfo paging = Pagenation.getPagingButtonInfo (items, 5);

		model.addAttribute ("items", items);
		model.addAttribute ("paging", paging);
		model.addAttribute ("is_item", is_item);
		model.addAttribute ("itemNo", itemNo);
		model.addAttribute ("search", search);

		return "item/item";

	}

	@ResponseBody
	@GetMapping ("/{itemNo}")
	public ItemDTO itemDetail (@PathVariable int itemNo) {

		return ITEM_SERVICE.findByItemNo (itemNo).orElse (new ItemDTO ());

	}

	@GetMapping ("/add")
	public String addItem (Model model) {

		return "item/addItem";

	}

	@PostMapping ("/add")
	public String addNewBoard (ItemDTO item) {

		ITEM_SERVICE.addItem (item);

		return "redirect:/item";

	}

	// 수정
	@PostMapping ("/edit")
	public String itemEdit (Item editItem) {

		ITEM_SERVICE.updateItem (editItem);
		return "redirect:/item";

	}

	// 삭제
	@GetMapping ("/delete/{itemNo}")
	public String deleteItem (@PathVariable int itemNo) {

		ITEM_SERVICE.deleteItem (itemNo);

		return "redirect:/item";

	}

}
