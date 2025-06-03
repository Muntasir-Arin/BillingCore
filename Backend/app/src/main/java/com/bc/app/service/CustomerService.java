package com.bc.app.service;

import com.bc.app.dto.customer.CustomerDTO;
import com.bc.app.dto.customer.CreateCustomerRequest;
import com.bc.app.dto.customer.UpdateCustomerRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    CustomerDTO createCustomer(CreateCustomerRequest request);
    CustomerDTO updateCustomer(Long id, UpdateCustomerRequest request);
    void deleteCustomer(Long id);
    CustomerDTO getCustomer(Long id);
    List<CustomerDTO> getCustomersByBranch(Long branchId);
    List<CustomerDTO> getActiveCustomersByBranch(Long branchId);
    Page<CustomerDTO> getCustomersByBranch(Long branchId, Pageable pageable);
    void activateCustomer(Long id);
    void deactivateCustomer(Long id);
} 