package com.bc.app.repository;

import com.bc.app.model.ReturnItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReturnItemRepository extends JpaRepository<ReturnItem, Long> {
    List<ReturnItem> findByReturnEntityId(Long returnId);
    void deleteByReturnEntityId(Long returnId);
} 