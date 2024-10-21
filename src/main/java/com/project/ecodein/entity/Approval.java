package com.project.ecodein.entity;

import java.time.LocalDateTime;

import com.project.ecodein.entity.Ordering;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "approval")
public class Approval {

    @Id
    @Column(name = "approval_no", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int approvalNo;

    @ManyToOne
    @JoinColumn(name = "buyer_code", referencedColumnName = "buyer_code", nullable = false)
    private Buyer buyer;

    @Column(name = "approval_update_date", nullable = true)
    private LocalDateTime approvalUpdateDate;

    @Column(name = "approval_regist_date", nullable = false)
    private LocalDateTime approvalRegistDate;

    @ManyToOne
    @JoinColumn(name = "approval_admin", referencedColumnName = "admin_id")
    private Admin admin;

    @Column(name = "subject", nullable = true)
    private String subject;


}
