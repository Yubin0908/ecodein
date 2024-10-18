package com.project.ecodein.dto;

import com.project.ecodein.entity.Item;
import com.project.ecodein.entity.Return;
import com.project.ecodein.entity.Storage;
import lombok.*;



@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReturnItemDTO {

    private Long returnItemId;
    private Item item;
    private int returnItemQty;
    private Return aReturn;
    private Storage storage;

}

