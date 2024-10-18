package com.project.ecodein.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "approval")
@Table(name = "approval_status_lable")
public class ApprovalStatusLable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer status_no;

    @Column(name = "update_lable_date", nullable = false)
    private LocalDateTime updateLableDate;

    @Column(nullable = false)
    private byte status;

    @ManyToOne
    @JoinColumn(name = "admin_id", referencedColumnName = "admin_id", nullable = false)
    private Admin admin;

    @ManyToOne
    @JoinColumn(name = "approval_no", referencedColumnName = "approval_no", nullable = false)
    private Approval approval;
}
