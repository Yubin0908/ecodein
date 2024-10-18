package com.project.ecodein.service;

import com.project.ecodein.dto.ReturnItemDTO;
import com.project.ecodein.entity.Return;
import com.project.ecodein.entity.ReturnItem;
import com.project.ecodein.repository.ReturnItemRepository;
import com.project.ecodein.repository.ReturnRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReturnItemService {

    private final ReturnItemRepository RETURN_ITEM_REPOSITORY;
    private final ReturnRepository RETURN_REPOSITORY;
    private final ModelMapper MODEL_MAPPER;

    public ReturnItemService (ReturnItemRepository returnItemRepository, ModelMapper modelMapper, ReturnRepository returnRepository) {
        this.RETURN_ITEM_REPOSITORY = returnItemRepository;
        this.MODEL_MAPPER = modelMapper;
        this.RETURN_REPOSITORY = returnRepository;
    }

    public List<ReturnItemDTO> findByReturnId(String returnId) {
        Return returns = RETURN_REPOSITORY.findById(returnId).orElseThrow(() -> new IllegalArgumentException(
            "반환된 객체가 존재하지 않음."));
        List<ReturnItem> returnItem = RETURN_ITEM_REPOSITORY.findByaReturn(returns);
        return returnItem.stream().map(returnItem1 -> MODEL_MAPPER.map(returnItem1, ReturnItemDTO.class)).toList();
    }
}
