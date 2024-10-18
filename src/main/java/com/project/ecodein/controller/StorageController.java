package com.project.ecodein.controller;

import java.util.List;

import com.project.ecodein.dto.StockDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.project.ecodein.dto.StorageDTO;
import com.project.ecodein.entity.Stock;
import com.project.ecodein.entity.Storage;
import com.project.ecodein.service.StorageService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/storage")
public class StorageController {

    private final StorageService STORAGE_SERVICE;

    public StorageController (StorageService storageService) {
        this. STORAGE_SERVICE = storageService;
    }

    @GetMapping({"/{page}/{keyword}/{storage_status}", "/{page}", "/{page}/{keyword}"})
    public String storage(Model model,
                          @PathVariable(name = "page") Integer page,
                          @PathVariable(name = "keyword", required = false) String keyword,
                          @PathVariable(name = "storage_status", required = false) String storage_status) {

        if (page == null) {
            page = 1;
        }

        model.addAttribute("storages", STORAGE_SERVICE.storages(page, keyword, storage_status));

        return "storage/storage";
    }
    
    
    @GetMapping("/detail/{storage_no}")
    @ResponseBody
    public StorageDTO storageDetail (@PathVariable Integer storage_no) {

        return STORAGE_SERVICE.getStorageByStorageNo (storage_no);
    }

    @GetMapping("/storageStock/{storage_no}")
    @ResponseBody
    public List<StockDTO> storageStock (@PathVariable int storage_no) {
        List<StockDTO> stockList = STORAGE_SERVICE.getItemStockByStorage(storage_no);

        if (stockList.isEmpty()) {
            return null;
        } else {
            return stockList;
        }

    }
    
    @PostMapping("/add")
    public String storageAdd (@ModelAttribute Storage storage) {
    	
    	STORAGE_SERVICE.storageAdd (storage);
    	
    	return "redirect:/storage/1";
    }
    
    @GetMapping("/remove/{storage_no}")
    public String storageRemove (@PathVariable int storage_no) {
    	
    	STORAGE_SERVICE.storageRemove (storage_no);
    	
    	return "redirect:/storage/1";
    }

    @GetMapping("/status_modify/{storage_no}/{storage_status}")
    public String storageStatusModify (@PathVariable Integer storage_no, @PathVariable String storage_status) {

        STORAGE_SERVICE.storageStatusUpdate(storage_no, storage_status);

        return "redirect:/storage/1";
    }

    @PostMapping("/modify")
    @ResponseBody
    public ResponseEntity<Integer> storageModify(@ModelAttribute Storage storage) {
        STORAGE_SERVICE.storageUpdate(storage);

        return ResponseEntity.ok(storage.getStorageNo());
    }

}
