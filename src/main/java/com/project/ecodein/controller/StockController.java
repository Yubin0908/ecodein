package com.project.ecodein.controller;

import java.util.List;
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
import com.project.ecodein.dto.StockDTO;
import com.project.ecodein.dto.StorageDTO;
import com.project.ecodein.service.StockService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/stock")
public class StockController {
	
	private final StockService STOCK_SERVICE;

	public StockController (StockService STOCK_SERVICE) {
		this.STOCK_SERVICE = STOCK_SERVICE;
	}

	@GetMapping("")
	public String stock (Model model, @RequestParam(defaultValue = "") String search,
		@RequestParam(defaultValue = "false") boolean isItem,
		@RequestParam(defaultValue = "") Integer storageNo,
		@RequestParam(defaultValue = "1") int page) {
		
		Page<StockDTO> stocks = STOCK_SERVICE.findStock (page, isItem, search, storageNo);
		PagingButtonInfo paging = Pagenation.getPagingButtonInfo (stocks, 5);
		
		model.addAttribute ("stocks", stocks);
		model.addAttribute ("storages", STOCK_SERVICE.findAllStorage ());
		model.addAttribute ("paging", paging);
		model.addAttribute ("isItem", isItem);
		model.addAttribute ("selectedStorage", storageNo);
		model.addAttribute ("search", search);
		
		return "stock/stock";
	}
	
	@ResponseBody
	@GetMapping("/{stock_no}")
	public StockDTO stockDetail (@PathVariable int stock_no) {
		return STOCK_SERVICE.findByStockNo(stock_no).orElse (new StockDTO());
	}
	
	@PostMapping("/edit")
	public String stockEdit (@RequestParam int editStockNo, @RequestParam int editQuantity) {
		
		STOCK_SERVICE.editStock(editStockNo, editQuantity);
		
		return "redirect:/stock";
	}
	
	@GetMapping("/add")
	public String addStock () {
				
		return "stock/addStock";
	}
	
	@GetMapping("/add/item")
	public String itemModal (@RequestParam(value = "search", required = false) String search,
		@RequestParam(value = "page", defaultValue = "1") int page, Model model) {
		
		
		Page<ItemDTO> items = STOCK_SERVICE.searchStocks(search, page);
		PagingButtonInfo paging = Pagenation.getPagingButtonInfo(items);
		
		model.addAttribute ("items", items);
		model.addAttribute("paging", paging);
		model.addAttribute("itemModalSearch", search);
		
		
		return "stock/stockModal :: itemModalContent";
		
	}
	
	@GetMapping("/add/storage")
	public String storageModal (@RequestParam(value = "search", required = false) String search,
		@RequestParam(value = "page", defaultValue = "1") int page, Model model) {
		
		
		Page<StorageDTO> storages = STOCK_SERVICE.searchStorages(search, page);
		PagingButtonInfo paging = Pagenation.getPagingButtonInfo(storages);
		
		model.addAttribute ("storages", storages);
		model.addAttribute("paging", paging);
		model.addAttribute("storageModalSearch", search);
		
		return "stock/stockModal :: storageModalContent";
		
	}
	
	@PostMapping("/add")
	public String addStockPost (@RequestParam int storageNo, @RequestParam int itemNo, @RequestParam int quantity) {
		
		STOCK_SERVICE.addStock (itemNo, storageNo, quantity);
		
		return "redirect:/stock";
	}
	
	@GetMapping("/addItem")
	public String addItem () {
		return "stock/addItem";
	}
	
	@GetMapping("/addItem/item")
	public String onlyItemModal (@RequestParam(value = "search", required = false) String search,
		@RequestParam(value = "page", defaultValue = "1") int page, Model model) {
		
		
		Page<ItemDTO> items = STOCK_SERVICE.searchItems(search, page);
		PagingButtonInfo paging = Pagenation.getPagingButtonInfo(items);
		
		model.addAttribute ("items", items);
		model.addAttribute("paging", paging);
		model.addAttribute("itemModalSearch", search);
		
		return "stock/stockModal :: itemModalContent";
		
	}
	
	@GetMapping("/addItem/stock")
	public String onlyStockModal (@RequestParam(value = "search", required = false) String search,
		@RequestParam(value = "page", defaultValue = "1") int page, Model model) {
		
		
		Page<ItemDTO> items = STOCK_SERVICE.searchMaterials(search, page);
		PagingButtonInfo paging = Pagenation.getPagingButtonInfo(items);
		
		model.addAttribute ("items", items);
		model.addAttribute("paging", paging);
		model.addAttribute("stockModalSearch", search);
		
		return "stock/stockModal :: stockModalContent";
		
	}
	
	@PostMapping("/addItem")
	public String addItemPost(@RequestParam Integer itemNo, @RequestParam Integer storageNo, @RequestParam Integer quantity 
		,@RequestParam List<Integer> ingredient, @RequestParam List<Integer> ingredientQuantity) {
		
		STOCK_SERVICE.addItem (itemNo, storageNo, quantity, ingredient, ingredientQuantity);
		
		return "redirect:/stock";
	}
	
	@PostMapping("/move")
	public String Move (@RequestParam Integer moveStockNo, @RequestParam Integer storageNo,
		@RequestParam Integer moveQuantity) {
		
		STOCK_SERVICE.moveStock (moveStockNo, storageNo, moveQuantity);
		
		
		return "redirect:/stock";
	}
}
