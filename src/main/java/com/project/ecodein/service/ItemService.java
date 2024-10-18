package com.project.ecodein.service;

import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.project.ecodein.dto.ItemDTO;
import com.project.ecodein.entity.Item;
import com.project.ecodein.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ItemService {

	private final ItemRepository ITEM_REPOSITORY;
	private final ModelMapper modelMapper;

	public ItemService (ItemRepository ITEM_REPOSITORY, ModelMapper modelMapper) {

		this.ITEM_REPOSITORY = ITEM_REPOSITORY;
		this.modelMapper = modelMapper;

	}

	public Page<ItemDTO> findItem (int page, boolean is_item, String search, Integer itemNo) {

		Pageable pageable = PageRequest.of (page - 1, 10, Sort.by ("item_no").descending ());

		if (is_item) {
			return ITEM_REPOSITORY.searchItemOnly (search, pageable).map (i -> modelMapper.map (i, ItemDTO.class));
		} else {
			return ITEM_REPOSITORY.searchItem (search, pageable).map (i -> modelMapper.map (i, ItemDTO.class));
		}

	}
	
	public Optional<ItemDTO> findByItemNo (int itemNo) {

		return ITEM_REPOSITORY.findById(itemNo).map (i -> modelMapper.map (i, ItemDTO.class));
	}
	
	// save
	@Transactional
	public void addItem (ItemDTO item) {

		ITEM_REPOSITORY.save(modelMapper.map (item, Item.class));

	}
	
	@Transactional
	public void updateItem (Item updateItem) {

		Item foundItem = ITEM_REPOSITORY.findById (updateItem.getItemNo())
			.orElseThrow (IllegalArgumentException::new);
		foundItem.setItemName (updateItem.getItemName ());
		foundItem.setItemPrice (updateItem.getItemPrice ());

	}
	
	@Transactional
	public void deleteItem (Integer itemNo) {

		ITEM_REPOSITORY.deleteById (itemNo);

	}

}
