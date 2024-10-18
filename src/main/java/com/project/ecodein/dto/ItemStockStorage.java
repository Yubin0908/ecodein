package com.project.ecodein.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@ToString
public class ItemStockStorage {
    private Integer item_no;
    private String item_name;
    private Integer item_price;
    private Boolean is_material;
    private String item_image;
    private Integer quantity;
    private Integer storage_no;

    public ItemStockStorage (Integer item_no, String item_name, Integer item_price, Boolean is_material, String item_image, Integer quantity, Integer storage_no) {
        this.item_no = item_no;
        this.item_name = item_name;
        this.item_price = item_price == null ? 0 : item_price;
        this.is_material = is_material;
        this.item_image = item_image;
        this.quantity = quantity;
        this.storage_no = storage_no;
    }
}