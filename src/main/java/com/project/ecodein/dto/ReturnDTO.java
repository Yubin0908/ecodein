package com.project.ecodein.dto;

import java.sql.Date;
import com.project.ecodein.entity.Buyer;
import lombok.*;

import java.sql.Date;
import java.time.LocalDateTime;

import com.project.ecodein.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReturnDTO {

    private String returnId;
    private Buyer buyer;
    private User user;
    private String returnReason;
    private byte returnStatus;
    private Date returnRegistDate;
    private LocalDateTime returnStatusUpdateDate;

}
