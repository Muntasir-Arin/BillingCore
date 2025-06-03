package com.bc.app.service.impl;

import com.bc.app.dto.customer.CustomerDTO;
import com.bc.app.dto.customer.CreateCustomerRequest;
import com.bc.app.dto.customer.UpdateCustomerRequest;
import com.bc.app.exception.BusinessException;
import com.bc.app.exception.ResourceNotFoundException;
import com.bc.app.model.Branch;
import com.bc.app.model.Customer;
import com.bc.app.repository.BranchRepository;
import com.bc.app.repository.CustomerRepository;
import com.bc.app.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final BranchRepository branchRepository;

    @Override
    @Transactional
    public CustomerDTO createCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByPhoneAndBranchId(request.getPhone(), request.getBranchId())) {
            throw new BusinessException("Phone number already exists in this branch");
        }

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + request.getBranchId()));

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setBranch(branch);
        customer.setActive(true);

        return mapToDTO(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(Long id, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        if (request.getName() != null) {
            customer.setName(request.getName());
        }

        if (request.getEmail() != null) {
            customer.setEmail(request.getEmail());
        }

        if (request.getPhone() != null && !request.getPhone().equals(customer.getPhone())) {
            if (customerRepository.existsByPhoneAndBranchId(request.getPhone(), customer.getBranch().getId())) {
                throw new BusinessException("Phone number already exists in this branch");
            }
            customer.setPhone(request.getPhone());
        }

        if (request.getAddress() != null) {
            customer.setAddress(request.getAddress());
        }

        if (request.getActive() != null) {
            customer.setActive(request.getActive());
        }

        return mapToDTO(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customerRepository.delete(customer);
    }

    @Override
    public CustomerDTO getCustomer(Long id) {
        return customerRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    @Override
    public List<CustomerDTO> getCustomersByBranch(Long branchId) {
        return customerRepository.findByBranchId(branchId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerDTO> getActiveCustomersByBranch(Long branchId) {
        return customerRepository.findByBranchIdAndActive(branchId, true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CustomerDTO> getCustomersByBranch(Long branchId, Pageable pageable) {
        return customerRepository.findByBranchId(branchId, pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional
    public void activateCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customer.setActive(true);
        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void deactivateCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customer.setActive(false);
        customerRepository.save(customer);
    }

    private CustomerDTO mapToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        dto.setActive(customer.isActive());
        dto.setBranchId(customer.getBranch().getId());
        dto.setBranchName(customer.getBranch().getName());
        return dto;
    }
} 