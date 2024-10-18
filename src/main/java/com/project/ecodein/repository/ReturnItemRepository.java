package com.project.ecodein.repository;

import com.project.ecodein.entity.Return;
import com.project.ecodein.entity.ReturnItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReturnItemRepository extends JpaRepository<ReturnItem, Integer> {

    List<ReturnItem> findByaReturn(Return aReturn);
}
