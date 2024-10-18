package com.project.ecodein.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.project.ecodein.dto.ItemDTO;
import com.project.ecodein.dto.StockDTO;
import com.project.ecodein.dto.StorageDTO;
import com.project.ecodein.entity.Stock;
import com.project.ecodein.entity.Storage;
import com.project.ecodein.repository.ItemRepository;
import com.project.ecodein.repository.StockRepository;
import com.project.ecodein.repository.StorageRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StockService {

	private final StockRepository STOCK_REPOSITORY;
	private final StorageRepository STORAGE_REPOSITORY;
	private final ItemRepository ITEM_REPOSITORY;
    private final ModelMapper MODEL_MAPPER;

    public StockService (StockRepository STOCK_REPOSITORY, StorageRepository STORAGE_REPOSITORY,
                         ItemRepository ITEM_REPOSITORY, ModelMapper modelMapper) {
		this.STOCK_REPOSITORY = STOCK_REPOSITORY;
		this.STORAGE_REPOSITORY = STORAGE_REPOSITORY;
		this.ITEM_REPOSITORY = ITEM_REPOSITORY;
        this.MODEL_MAPPER = modelMapper;
    }
	
	public Page<StockDTO> findStock (int page, boolean is_item, String search, Integer storage_no) {
		Pageable pageable = PageRequest.of (page-1, 10, Sort.by ("stock_no").descending ()); 
		
		if(storage_no == null) {
			if(!is_item) {
				return STOCK_REPOSITORY.findAllStockByKeyword(search,pageable).map (s -> MODEL_MAPPER.map (s, StockDTO.class));
			} else {
				return STOCK_REPOSITORY.findStockByKeyword(search, pageable).map (s -> MODEL_MAPPER.map (s, StockDTO.class));
			}
		} else {
			if(!is_item) {
				return STOCK_REPOSITORY.findAllStockByKeywordStorage(search,storage_no,pageable).map (s -> MODEL_MAPPER.map (s, StockDTO.class));
			} else {
				return STOCK_REPOSITORY.findyStockByKeywordStorage(search,storage_no, pageable).map (s -> MODEL_MAPPER.map (s, StockDTO.class));
			}	
		}
		
	}
	
	public List<StorageDTO> findAllStorage () {
        List<Storage> storages = STORAGE_REPOSITORY.findAll ();

		return storages.stream().map(storage -> MODEL_MAPPER.map(storage, StorageDTO.class)).collect(Collectors.toList());
	}

	public Optional<StockDTO> findByStockNo (int stock_no) {

		return STOCK_REPOSITORY.findByStockNO (stock_no).map (s -> MODEL_MAPPER.map (s, StockDTO.class));
	}

	public void editStock (int editStockNo, int editQunaitity) {
		if (editQunaitity == 0) {
			STOCK_REPOSITORY.delete (STOCK_REPOSITORY.findByStockNO (editStockNo).get ());
		} else {
			STOCK_REPOSITORY.updateStock(editStockNo, editQunaitity);
		}
	}

	public Page<ItemDTO> searchStocks (String search, int page) {
		Pageable pageable = PageRequest.of (page-1, 10, Sort.by ("item_name").ascending ());
		
		if(search == null || search.isEmpty ()) {
			search = "";
		}
		
		return ITEM_REPOSITORY.searchItem(search, pageable).map (i -> MODEL_MAPPER.map (i, ItemDTO.class));

	}
	
	public Page<ItemDTO> searchItems (String search, int page) {
		Pageable pageable = PageRequest.of (page-1, 10, Sort.by ("item_name").ascending ());
		
		if(search == null || search.isEmpty ()) {
			search = "";
		}
		
		return ITEM_REPOSITORY.searchItemOnly(search, pageable).map (i -> MODEL_MAPPER.map (i, ItemDTO.class));

	}
	
	public Page<ItemDTO> searchMaterials (String search, int page) {
		Pageable pageable = PageRequest.of (page-1, 10, Sort.by ("item_name").ascending ());
		
		if(search == null || search.isEmpty ()) {
			search = "";
		}
		
		return ITEM_REPOSITORY.searchMaterialOnly(search, pageable).map (i -> MODEL_MAPPER.map (i, ItemDTO.class));
		
	}

	public Page<StorageDTO> searchStorages (String search, int page) {

		Pageable pageable = PageRequest.of (page-1, 10, Sort.by ("storage_name").ascending ());
		
		if(search == null || search.isEmpty ()) {
			search = "";
		}

        Page<Storage> storages = STORAGE_REPOSITORY.findAllByStorageNameOrStorageSite (search, pageable);

		return storages.map(storage -> MODEL_MAPPER.map(storage, StorageDTO.class));

	}

	public void addStock(int item_no, int storage_no, int quantity) {
		
		Optional<Stock> stock = STOCK_REPOSITORY.findStock(item_no, storage_no);
		
		// 등록하려는 재고가 창고에 있을시, 수량만 추가. 없을시 새 재고를 등록
		if(stock.isPresent()) {
			STOCK_REPOSITORY.updateStock(stock.get().getStockNo(), stock.get().getQuantity()+quantity);
		} else {
			STOCK_REPOSITORY.addStock(item_no, storage_no, quantity);
		}
	}

	
	// 생산품 추가 (재료들의 수량을 확인 후, 해당 창고의 재고보다 수량이 클 시 재고 삭제, 작을 시 수량 감소)
	public void addItem (Integer itemNo,
		Integer storage_no, Integer quantity,
		List<Integer> ingredient,
		List<Integer> ingredient_quantity) {
		
		for (int i=0; i<ingredient.size (); i++) {
			
			Optional<Stock> stock = STOCK_REPOSITORY.findStock(ingredient.get (i),storage_no);
			
			if (stock.isPresent()) {
				if(stock.get ().getQuantity () > ingredient_quantity.get (i)) {
					STOCK_REPOSITORY.updateStock(stock.get().getStockNo(), stock.get().getQuantity()-ingredient_quantity.get (i));
				} else {
					STOCK_REPOSITORY.delete (stock.get ());
				}
			}
			
		}

		Optional<Stock> stock = STOCK_REPOSITORY.findStock(itemNo,storage_no);
		
		if (stock.isPresent ()) {
			STOCK_REPOSITORY.updateStock(stock.get().getStockNo(), stock.get().getQuantity()+quantity);
		} else {
			STOCK_REPOSITORY.addStock(itemNo, storage_no, quantity);
		}
		
	}
	
	// 재고 이동 기능 (옮길 창고에 해당 상품이 있을시, 수량만 업데이트)
	public void moveStock (Integer moveStockNo, Integer storage_no, Integer moveQuantity) {

		Optional<Stock> stock = STOCK_REPOSITORY.findById(moveStockNo);
		Optional<Stock> moveStock = STOCK_REPOSITORY.findStock (stock.get ().getItem ().getItemNo (), storage_no);
		
		if(stock.get ().getQuantity () == moveQuantity) {
			STOCK_REPOSITORY.delete (stock.get ());
		} else {
			STOCK_REPOSITORY.updateStock (stock.get ().getStockNo (), stock.get ().getQuantity () - moveQuantity);
		}	
		
		if(moveStock.isPresent ()) {
			STOCK_REPOSITORY.updateStock (moveStock.get ().getStockNo (), moveStock.get ().getQuantity () + moveQuantity);
		} else {
			STOCK_REPOSITORY.addStock (stock.get ().getItem ().getItemNo (), storage_no, moveQuantity);
		}

	}
}
