package com.project.ecodein.dto;

import com.project.ecodein.entity.Item;
import com.project.ecodein.entity.Ordering;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
public class OrderDetailDTO {

	private int detailNo;
	private Item item;
	private Ordering order;
	private int quantity;

}
