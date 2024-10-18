package com.project.ecodein.entity;

import java.sql.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "returns")
public class Return {

    @Id
    @Column(name = "return_id")
    private String returnId;

    @ManyToOne
    @JoinColumn(name = "return_buyer_code", referencedColumnName = "buyer_code")
    private Buyer buyer;

    @ManyToOne
    @JoinColumn(name = "return_buyer_manager", referencedColumnName = "user_id")
    private User user;

    @Column(name = "return_reason")
    private String returnReason;

    @Column(name = "return_regist_date")
    private Date returnRegistDate;

    @Column(name = "return_status")
    private byte returnStatus;

    @Column(name = "return_status_update_date")
    private LocalDateTime returnStatusUpdateDate;

}
