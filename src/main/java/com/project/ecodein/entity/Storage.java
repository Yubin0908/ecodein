package com.project.ecodein.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name="storage")
@DynamicInsert
@DynamicUpdate
public class Storage {

    @Id
    @Column(name="storage_no", nullable=false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int storageNo;

    @Column(name="storage_name")
    private String storageName;

    @Column(name="storage_status")
    private String storageStatus;

    @Column(name="storage_site")
    private String storageSite;

    @Column(name="storage_regist")
    @ColumnDefault("'current_date()'")
    private LocalDate storageRegist;

    public Storage (String storageName, String storageStatus, String storageSite) {

        this.storageName = storageName;
        this.storageStatus = storageStatus;
        this.storageSite = storageSite;

    }


}