package com.project.ecodein.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item")
public class Item {
	@Id
	@Column(name = "item_no", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int itemNo;
	
	@Column(name = "item_name", nullable = false)
	private String itemName;
	
	@Column(name = "item_price", nullable = true)
	private Integer itemPrice;
	
	@Column(name = "is_material", nullable = false)
	private Boolean isMaterial;
	
	@Column(name = "item_image", nullable = false)
	private String itemImage;


}
