package com.project.ecodein.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import com.project.ecodein.dto.BuyerDTO;
import com.project.ecodein.entity.Buyer;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.project.ecodein.repository.BuyerRepository;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;

@Service
@Slf4j
public class BuyerService {

	private final BuyerRepository BUYER_REPOSITORY;
    private final ModelMapper MODEL_MAPPER;

    public BuyerService (BuyerRepository buyerRepository, ModelMapper modelMapper) {
		this.BUYER_REPOSITORY = buyerRepository;
        this.MODEL_MAPPER = modelMapper;
    }

	public Page<BuyerDTO> buyers (int page, String keyword, String buyer_status) {
		
		boolean status = false;

        Sort sort = Sort.by (Sort.Order.desc ("buyerCode"));

        Pageable pageable = PageRequest.of (page - 1, 10, sort);
		
		if (keyword == "") {
            if (buyer_status.equals("default")) {
                status = true;
            }

            Page<Buyer> buyers = BUYER_REPOSITORY.findByBuyerStatus (pageable, status);

            return buyers.map(buyer -> MODEL_MAPPER.map(buyer, BuyerDTO.class));
		}

		if (buyer_status.equals ("default")) {
            Page<Buyer> buyers = BUYER_REPOSITORY.findByBuyerStatusAndBuyerName(true, keyword, pageable);

			return buyers.map(buyer -> MODEL_MAPPER.map(buyer, BuyerDTO.class));
		} else {
            Page<Buyer> buyers = BUYER_REPOSITORY.findByBuyerStatusAndBuyerName(false, keyword, pageable);
			return buyers.map(buyer -> MODEL_MAPPER.map(buyer, BuyerDTO.class));
		}
		
	}

	public BuyerDTO buyerInsert (BuyerDTO buyerDTO) {
        buyerDTO.setBuyerSecureCode (seruceCodeCreate ());
        buyerDTO.setBuyerStatus (true);
        buyerDTO.setBuyerResistDate(LocalDateTime.now());

        Buyer buyer = BUYER_REPOSITORY.save(MODEL_MAPPER.map(buyerDTO, Buyer.class));

        return MODEL_MAPPER.map(buyer, BuyerDTO.class);
	}

	public Optional<BuyerDTO> buyerDetail (Long buyer_code) {
        Optional<Buyer> buyer = BUYER_REPOSITORY.findById (buyer_code).stream ().findFirst ();

		return buyer.map(buy -> MODEL_MAPPER.map(buy, BuyerDTO.class));

	}
	
	public void updateStatus (BuyerDTO buyer) {
        Optional<BuyerDTO> oldBuyer = Optional.of(buyerDetail(buyer.getBuyerCode()).get());

        buyer.setBuyerStatus (false);
        buyer.setBuyerResistDate(oldBuyer.get().getBuyerResistDate());

		BUYER_REPOSITORY.save (MODEL_MAPPER.map(buyer, Buyer.class));
	}
	
	@Transactional
	public void updateBuyer (@ModelAttribute BuyerDTO buyer) {
        Optional<BuyerDTO> oldBuyer = Optional.of(buyerDetail(buyer.getBuyerCode()).get());
        buyer.setBuyerResistDate(oldBuyer.get().getBuyerResistDate());
        buyer.setBuyerSecureCode (oldBuyer.get().getBuyerSecureCode());
        buyer.setBuyerStatus(true);

        BUYER_REPOSITORY.save(MODEL_MAPPER.map(buyer, Buyer.class));
	}
	

	// 보안코드 생성 메서드
	private String seruceCodeCreate () {
		StringBuilder sb = new StringBuilder ();
		Random rd = new Random ();

		for (int i = 0; i < 5; i++) {
			sb.append ((char) (rd.nextInt (26) + 65));
		}

		return sb.toString ();
	}
	
}
