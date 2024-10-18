package com.project.ecodein.dto;

import com.project.ecodein.entity.Buyer;
import com.project.ecodein.entity.User;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderingDTO {

    private int orderNo;
    private Buyer buyerCode;
    private User userId;
    private Date orderDate;
    private Date dueDate;
    private byte isDelivery;

}