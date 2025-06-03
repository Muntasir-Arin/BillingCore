package com.bc.app.service;

import com.bc.app.dto.branch.BranchDTO;
import com.bc.app.dto.branch.CreateBranchRequest;
import com.bc.app.dto.branch.UpdateBranchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BranchService {
    BranchDTO createBranch(CreateBranchRequest request);
    BranchDTO updateBranch(Long id, UpdateBranchRequest request);
    void deleteBranch(Long id);
    BranchDTO getBranch(Long id);
    List<BranchDTO> getAllBranches();
    List<BranchDTO> getActiveBranches();
    Page<BranchDTO> getBranches(Pageable pageable);
    void activateBranch(Long id);
    void deactivateBranch(Long id);
} 