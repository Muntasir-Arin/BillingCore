package com.bc.app.service.impl;

import com.bc.app.dto.return_.ReturnDTO;
import com.bc.app.dto.return_.CreateReturnRequest;
import com.bc.app.dto.return_.UpdateReturnRequest;
import com.bc.app.dto.return_.ReturnItemDTO;
import com.bc.app.exception.BusinessException;
import com.bc.app.exception.ResourceNotFoundException;
import com.bc.app.model.*;
import com.bc.app.repository.*;
import com.bc.app.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReturnServiceImpl implements ReturnService {

    private final ReturnRepository returnRepository;
    private final ReturnItemRepository returnItemRepository;
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReturnDTO createReturn(CreateReturnRequest request) {
        // Get the invoice
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + request.getInvoiceId()));

        // Get the branch
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + request.getBranchId()));

        // Get current authenticated user
        User currentUser = getCurrentUser();

        // Create the return
        Return returnEntity = Return.builder()
                .returnNumber(generateReturnNumber(branch))
                .invoice(invoice)
                .customer(invoice.getCustomer())
                .branch(branch)
                .organization(invoice.getOrganization())
                .employee(currentUser)
                .reason(request.getReason())
                .status(ReturnStatus.PENDING)
                .total(BigDecimal.ZERO)
                .build();

        // Calculate total and add items
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CreateReturnRequest.ReturnItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.getProductId()));

            // Verify the product was in the original invoice
            boolean productInInvoice = invoice.getItems().stream()
                    .anyMatch(invoiceItem -> invoiceItem.getProduct().getId().equals(itemRequest.getProductId()));
            
            if (!productInInvoice) {
                throw new BusinessException("Product " + product.getName() + " was not in the original invoice");
            }

            ReturnItem returnItem = ReturnItem.builder()
                    .returnEntity(returnEntity)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .total(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())))
                    .reason(itemRequest.getReason() != null ? itemRequest.getReason() : request.getReason())
                    .build();

            returnEntity.getItems().add(returnItem);
            totalAmount = totalAmount.add(returnItem.getTotal());

            // Update product stock
            product.setStockQuantity(product.getStockQuantity() + itemRequest.getQuantity());
            productRepository.save(product);
        }

        returnEntity.setTotal(totalAmount);
        Return savedReturn = returnRepository.save(returnEntity);
        
        return mapToDTO(savedReturn);
    }

    @Override
    @Transactional
    public ReturnDTO updateReturn(Long id, UpdateReturnRequest request) {
        Return returnEntity = returnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Return not found with id: " + id));

        if (returnEntity.getStatus() != ReturnStatus.PENDING) {
            throw new BusinessException("Cannot update return with status: " + returnEntity.getStatus());
        }

        // Update reason if provided
        if (request.getReason() != null) {
            returnEntity.setReason(request.getReason());
        }

        // Update items if provided
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            // Restore stock for old items
            returnEntity.getItems().forEach(item -> {
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
                productRepository.save(product);
            });

            // Clear old items
            returnEntity.getItems().clear();

            // Add new items
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (UpdateReturnRequest.ReturnItemRequest itemRequest : request.getItems()) {
                Product product = productRepository.findById(itemRequest.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.getProductId()));

                ReturnItem returnItem = ReturnItem.builder()
                        .returnEntity(returnEntity)
                        .product(product)
                        .quantity(itemRequest.getQuantity())
                        .unitPrice(product.getPrice())
                        .total(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())))
                        .reason(itemRequest.getReason() != null ? itemRequest.getReason() : returnEntity.getReason())
                        .build();

                returnEntity.getItems().add(returnItem);
                totalAmount = totalAmount.add(returnItem.getTotal());

                // Update product stock
                product.setStockQuantity(product.getStockQuantity() + itemRequest.getQuantity());
                productRepository.save(product);
            }

            returnEntity.setTotal(totalAmount);
        }

        Return savedReturn = returnRepository.save(returnEntity);
        return mapToDTO(savedReturn);
    }

    @Override
    @Transactional
    public void deleteReturn(Long id) {
        Return returnEntity = returnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Return not found with id: " + id));

        if (returnEntity.getStatus() != ReturnStatus.PENDING) {
            throw new BusinessException("Cannot delete return with status: " + returnEntity.getStatus());
        }

        // Restore stock for all items
        returnEntity.getItems().forEach(item -> {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        });

        returnRepository.delete(returnEntity);
    }

    @Override
    public ReturnDTO getReturn(Long id) {
        Return returnEntity = returnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Return not found with id: " + id));
        return mapToDTO(returnEntity);
    }

    @Override
    public List<ReturnDTO> getReturnsByBranch(Long branchId) {
        return returnRepository.findByBranchId(branchId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReturnDTO> getReturnsByCustomer(Long customerId) {
        return returnRepository.findByCustomerId(customerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReturnDTO> getReturnsByDateRange(Long branchId, LocalDateTime startDate, LocalDateTime endDate) {
        return returnRepository.findByBranchIdAndCreatedAtBetween(branchId, startDate, endDate).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ReturnDTO> getReturnsByBranch(Long branchId, Pageable pageable) {
        return returnRepository.findByBranchId(branchId, pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional
    public void updateReturnStatus(Long id, String status) {
        Return returnEntity = returnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Return not found with id: " + id));

        try {
            ReturnStatus returnStatus = ReturnStatus.valueOf(status.toUpperCase());
            returnEntity.setStatus(returnStatus);
            returnRepository.save(returnEntity);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid return status: " + status);
        }
    }

    @Override
    @Transactional
    public void cancelReturn(Long id) {
        Return returnEntity = returnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Return not found with id: " + id));

        if (returnEntity.getStatus() == ReturnStatus.COMPLETED || returnEntity.getStatus() == ReturnStatus.CANCELLED) {
            throw new BusinessException("Cannot cancel return with status: " + returnEntity.getStatus());
        }

        // If pending, restore stock
        if (returnEntity.getStatus() == ReturnStatus.PENDING) {
            returnEntity.getItems().forEach(item -> {
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
                productRepository.save(product);
            });
        }

        returnEntity.setStatus(ReturnStatus.CANCELLED);
        returnRepository.save(returnEntity);
    }

    private String generateReturnNumber(Branch branch) {
        String prefix = branch.getName().length() >= 3 
            ? branch.getName().substring(0, 3).toUpperCase() 
            : branch.getName().toUpperCase();
        String timestamp = String.valueOf(System.currentTimeMillis());
        return "RET-" + prefix + "-" + timestamp;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("User not authenticated");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Current user not found: " + username));
    }

    private ReturnDTO mapToDTO(Return returnEntity) {
        ReturnDTO dto = new ReturnDTO();
        dto.setId(returnEntity.getId());
        dto.setReturnNumber(returnEntity.getReturnNumber());
        dto.setInvoiceId(returnEntity.getInvoice().getId());
        dto.setInvoiceNumber(returnEntity.getInvoice().getInvoiceNumber());
        dto.setCustomerId(returnEntity.getCustomer().getId());
        dto.setCustomerName(returnEntity.getCustomer().getName());
        dto.setBranchId(returnEntity.getBranch().getId());
        dto.setBranchName(returnEntity.getBranch().getName());
        dto.setStatus(returnEntity.getStatus().name());
        dto.setReason(returnEntity.getReason());
        dto.setTotalAmount(returnEntity.getTotal());
        dto.setCreatedAt(returnEntity.getCreatedAt());

        dto.setItems(returnEntity.getItems().stream()
                .map(item -> {
                    ReturnItemDTO itemDTO = new ReturnItemDTO();
                    itemDTO.setId(item.getId());
                    itemDTO.setProductId(item.getProduct().getId());
                    itemDTO.setProductName(item.getProduct().getName());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setUnitPrice(item.getUnitPrice());
                    itemDTO.setReason(item.getReason());
                    return itemDTO;
                })
                .collect(Collectors.toList()));

        return dto;
    }
} 