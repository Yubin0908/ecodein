package com.project.ecodein.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "return_item")
@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReturnItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "return_item_id")
    private Long returnItemId;

    @ManyToOne
    @JoinColumn(name = "return_item_no", referencedColumnName = "item_no")
    private Item item;

    @Column(name = "return_item_qty")
    private int returnItemQty;

    @ManyToOne
    @JoinColumn(name = "return_id", referencedColumnName = "return_id")
    private Return aReturn;

    @ManyToOne
    @JoinColumn(name = "return_storage_no", referencedColumnName = "storage_no")
    private Storage storage;

}
