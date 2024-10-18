package com.project.ecodein.dto;

import com.project.ecodein.entity.Item;
import com.project.ecodein.entity.Storage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {

	private int stockNo;
	private Storage storage;
	private Item item;
	private int quantity;

}
