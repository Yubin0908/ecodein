package com.project.ecodein.repository;

import com.project.ecodein.entity.Buyer;
import com.project.ecodein.entity.Return;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.Optional;

public interface ReturnRepository extends JpaRepository<Return, String> {
    Page<Return> findBybuyer (Optional<Buyer> buyer, Pageable pageable);
    Page<Return> findByBuyerAndReturnStatus(Optional<Buyer> buyer, byte returnStatus, Pageable pageable);
    Page<Return> findByReturnStatus(byte returnStatus, Pageable pageable);
    Page<Return> findByBuyerAndReturnRegistDateBetween(Optional<Buyer> buyer, Date from, Date to, Pageable pageable);
    Page<Return> findByReturnRegistDateBetween(Date from, Date to, Pageable pageable);
    int countByBuyerAndReturnRegistDateBetween (Buyer buyer, Date from, Date to);
    int countByBuyerAndReturnStatus (Buyer buyer, byte returnStatus);
    int countByReturnRegistDateBetween (Date from, Date to);
    int countByReturnStatus(byte returnStatus);
}
