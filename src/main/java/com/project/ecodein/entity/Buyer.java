package com.project.ecodein.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "buyer")
public class Buyer {

    @Id
    @Column(name = "buyer_code", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long buyerCode;

    @Column (name = "buyer_name", nullable = false)
    private String buyerName;

    @Column (name = "buyer_number", nullable = false)
    private String buyerNumber;

    @Column (name = "buyer_agent", nullable = false)
    private String buyerAgent;

    @Column (name = "buyer_tel", nullable = false)
    private String buyerTel;

    @Column (name = "buyer_address", nullable = false)
    private String buyerAddress;

    @Column (name = "buyer_regist_date", nullable = false)
    private LocalDateTime buyerResistDate;

    @Column (name = "buyer_status", nullable = false)
    private boolean buyerStatus;

    @Column (name = "buyer_secure_code", nullable = false)
    private String buyerSecureCode;

}