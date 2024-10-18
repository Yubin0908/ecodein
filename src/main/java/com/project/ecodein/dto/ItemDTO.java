package com.project.ecodein.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
	private int itemNo;
	private String itemName;
	private Integer itemPrice;
	private Boolean isMaterial;
	private String itemImage;

	public ItemDTO(int itemNo) {
		this.itemNo = itemNo;
	}
}
