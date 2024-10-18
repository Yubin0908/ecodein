package com.project.ecodein.dto;

import com.project.ecodein.entity.Admin;
import com.project.ecodein.entity.Buyer;
import com.project.ecodein.entity.Ordering;
import lombok.*;

import java.time.LocalDateTime;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class ApprovalDTO {

    private int approvalNo;
    private Buyer buyer;
    private LocalDateTime approvalUpdateDate;
    private LocalDateTime approvalRegistDate;
    private Admin admin;
    private String subject;
    private Ordering ordering;

}
