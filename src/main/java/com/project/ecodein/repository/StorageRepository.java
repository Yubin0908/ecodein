package com.project.ecodein.repository;

import java.util.List;

import com.project.ecodein.dto.StorageDTO;
import com.project.ecodein.entity.Storage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Integer> {

    // 상태별 조회
    public Page<Storage> findByStorageStatus(@Param("storage_status") String storageStatus, Pageable pageable);

    // 키워드 조회
    @Query(value = "select * from storage where storage_name like concat('%', :keyword, '%') " +
                    "or storage_site like concat('%', :keyword, '%')", nativeQuery = true)
    public Page<Storage> findAllByStorageNameOrStorageSite (@Param("keyword") String keyword,
                                                            Pageable pageable);

    // 창고 상태 변경
    @Modifying
    @Transactional
    @Query(value = "update storage set storage_status = :storage_status where storage_no = :storage_no",
        nativeQuery = true)
    public void statusUpdate (Integer storage_no, String storage_status);

}
