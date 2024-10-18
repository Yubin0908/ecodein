package com.project.ecodein.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.project.ecodein.dto.StockDTO;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.project.ecodein.dto.StorageDTO;
import com.project.ecodein.entity.Stock;
import com.project.ecodein.entity.Storage;
import com.project.ecodein.repository.StockRepository;
import com.project.ecodein.repository.StorageRepository;

@Service
public class StorageService {

    private final StorageRepository STORAGE_REPOSITORY;
    private final ModelMapper MODEL_MAPPER;
    private final StockRepository STOCK_REPOSITORY;

    public StorageService (StorageRepository storageRepository, StockRepository stockRepository, ModelMapper modelMapper) {
        this.STORAGE_REPOSITORY = storageRepository;
        this.MODEL_MAPPER = modelMapper;
        this.STOCK_REPOSITORY = stockRepository;
    }

    public Page<StorageDTO> storages (int page, String keyword, String storage_status) {
        Sort sort = Sort.by (Sort.Order.desc ("storageNo"));
        Pageable pageable = PageRequest.of (page - 1, 10, sort);
        
        if (keyword == null && storage_status == null) {
            Page<Storage> storages = STORAGE_REPOSITORY.findAll(pageable);

            return storages.map(storage -> MODEL_MAPPER.map(storage, StorageDTO.class));
        } else if (!storage_status.equals ("200")) {
            Page<Storage> storages = STORAGE_REPOSITORY.findByStorageStatus(storage_status, pageable);

            return storages.map(storage -> MODEL_MAPPER.map(storage, StorageDTO.class));
        }
        Page<Storage> storages = STORAGE_REPOSITORY.findAllByStorageNameOrStorageSite(keyword, pageable);

    	return storages.map(storage -> MODEL_MAPPER.map(storage, StorageDTO.class));

    }
    
    public StorageDTO getStorageByStorageNo (Integer storage_no) {
        Optional<Storage> storages = STORAGE_REPOSITORY.findById(storage_no);

        return storages.map(storage -> MODEL_MAPPER.map(storage, StorageDTO.class)).orElse(null);
    }

	public List<StockDTO> getItemStockByStorage (int storage_no) {
        Storage storage = STORAGE_REPOSITORY.findById(storage_no).get();
        List<Stock> stocks = STOCK_REPOSITORY.findByStorage(storage);

        return stocks.stream().map(stock -> MODEL_MAPPER.map(stock, StockDTO.class)).toList();
	}

	public void storageAdd (Storage storage) {
		storage.setStorageRegist (LocalDate.now ());
		storage.setStorageStatus ("정상");

		STORAGE_REPOSITORY.save (MODEL_MAPPER.map(storage, Storage.class));
	}

	public void storageRemove (int storage_no) {
		STORAGE_REPOSITORY.deleteById (storage_no);
	}

    public void storageUpdate (Storage storage) {

        storage.setStorageRegist (LocalDate.now ());

        STORAGE_REPOSITORY.save (MODEL_MAPPER.map(storage, Storage.class));
    }

    public void storageStatusUpdate (Integer storage_no, String storage_status) {
        STORAGE_REPOSITORY.statusUpdate(storage_no, storage_status);
    }
}
