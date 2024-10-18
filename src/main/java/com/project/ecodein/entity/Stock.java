package com.project.ecodein.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "stock")
public class Stock {

	@Id
	@Column (name = "stock_no", nullable = false)
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private int stockNo;

	@ManyToOne
	@JoinColumn (name = "storage_no", referencedColumnName = "storage_no", nullable = false)
	private Storage storage;

	@ManyToOne
	@JoinColumn (name = "item_no", referencedColumnName = "item_no", nullable = false)
	private Item item;

	@Column (name = "quantity", nullable = false)
	private int quantity;

}