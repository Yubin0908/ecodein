package com.project.ecodein.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.project.ecodein.entity.Admin;
import jakarta.transaction.Transactional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {

	@Query("SELECT u "
		+ "FROM Admin u "
		+ "WHERE u.adminId LIKE :admin_id")
	Optional<Admin> findByAdminId (@Param("admin_id") String admin_id);

    // [240927] 장유빈 기능 추가
    // 기능 상세 : 관리자 메뉴 -> 관리자 승인 요청 조회 기능
    @Query(value = "select * from admin where admin_recognize = 0", nativeQuery = true)
    List<Admin> findByAdminRecognize();

    @Modifying
    @Transactional
    @Query(value = "update admin set admin_recognize = 1 where admin_id like :adminId", nativeQuery = true)
    void updateAdminAuthByAdminId (String adminId);

    Admin findByAdminName(String adminName);
}
