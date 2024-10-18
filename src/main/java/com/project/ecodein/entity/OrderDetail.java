package com.project.ecodein.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
@Table(name = "order_detail")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_no", nullable = false)
    private int detailNo;

	@ManyToOne
	@JoinColumn(name = "item_no", referencedColumnName = "item_no", nullable = false)
	private Item item;

	@ManyToOne
	@JoinColumn(name = "order_no", referencedColumnName = "order_no", nullable = false)
	private Ordering order;
	
	@Column(name = "quantity", nullable = false)
	private int quantity;

}
