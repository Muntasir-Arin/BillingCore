package com.bc.app.service.impl;

import com.bc.app.dto.branch.BranchDTO;
import com.bc.app.dto.branch.CreateBranchRequest;
import com.bc.app.dto.branch.UpdateBranchRequest;
import com.bc.app.exception.BusinessException;
import com.bc.app.exception.ResourceNotFoundException;
import com.bc.app.model.Branch;
import com.bc.app.repository.BranchRepository;
import com.bc.app.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {
    private final BranchRepository branchRepository;

    @Override
    @Transactional
    public BranchDTO createBranch(CreateBranchRequest request) {
        if (branchRepository.existsByName(request.getName())) {
            throw new BusinessException("Branch name already exists");
        }

        Branch branch = new Branch();
        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setPhone(request.getPhone());
        branch.setActive(true);

        return mapToDTO(branchRepository.save(branch));
    }

    @Override
    @Transactional
    public BranchDTO updateBranch(Long id, UpdateBranchRequest request) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + id));

        if (request.getName() != null && !request.getName().equals(branch.getName())) {
            if (branchRepository.existsByName(request.getName())) {
                throw new BusinessException("Branch name already exists");
            }
            branch.setName(request.getName());
        }

        if (request.getAddress() != null) {
            branch.setAddress(request.getAddress());
        }

        if (request.getPhone() != null) {
            branch.setPhone(request.getPhone());
        }

        if (request.getActive() != null) {
            branch.setActive(request.getActive());
        }

        return mapToDTO(branchRepository.save(branch));
    }

    @Override
    @Transactional
    public void deleteBranch(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + id));

        if (!branch.getEmployees().isEmpty()) {
            throw new BusinessException("Cannot delete branch with active employees");
        }

        if (!branch.getProducts().isEmpty()) {
            throw new BusinessException("Cannot delete branch with active products");
        }

        branchRepository.delete(branch);
    }

    @Override
    public BranchDTO getBranch(Long id) {
        return branchRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + id));
    }

    @Override
    public List<BranchDTO> getAllBranches() {
        return branchRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BranchDTO> getActiveBranches() {
        return branchRepository.findByActive(true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BranchDTO> getBranches(Pageable pageable) {
        return branchRepository.findAll(pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional
    public void activateBranch(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + id));
        branch.setActive(true);
        branchRepository.save(branch);
    }

    @Override
    @Transactional
    public void deactivateBranch(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + id));
        branch.setActive(false);
        branchRepository.save(branch);
    }

    private BranchDTO mapToDTO(Branch branch) {
        BranchDTO dto = new BranchDTO();
        dto.setId(branch.getId());
        dto.setName(branch.getName());
        dto.setAddress(branch.getAddress());
        dto.setPhone(branch.getPhone());
        dto.setActive(branch.isActive());
        return dto;
    }
} 