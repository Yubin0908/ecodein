package com.project.ecodein.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;
import com.project.ecodein.entity.Item;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class ReturnItemPoolDTO {
    private List<Item> items;
    private int orderNo;
    private List<Integer> quantitys;
    private Date date;
    private String user_id;
    private String return_promise;
}
