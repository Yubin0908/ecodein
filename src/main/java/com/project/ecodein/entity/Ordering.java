package com.project.ecodein.entity;

import java.sql.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString(exclude = "approval")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ordering")
public class Ordering {

	@Id
	@Column(name = "order_no", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int orderNo;
	
	@ManyToOne
	@JoinColumn(name = "buyer_code", referencedColumnName = "buyer_code", nullable = false)
	private Buyer buyerCode;
	
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
	private User userId;
	
	@Column(name = "order_date", nullable = false)
	private Date orderDate;
	
	@Column(name = "due_date", nullable = false)
	private Date dueDate;
	
	@Column(name = "is_delivery", nullable = false)
	private byte isDelivery;

}
