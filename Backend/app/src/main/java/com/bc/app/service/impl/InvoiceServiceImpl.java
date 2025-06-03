package com.bc.app.service.impl;

import com.bc.app.dto.invoice.InvoiceDTO;
import com.bc.app.dto.invoice.InvoiceItemDTO;
import com.bc.app.dto.invoice.CreateInvoiceRequest;
import com.bc.app.dto.invoice.UpdateInvoiceRequest;
import com.bc.app.exception.BusinessException;
import com.bc.app.exception.ResourceNotFoundException;
import com.bc.app.model.*;
import com.bc.app.repository.*;
import com.bc.app.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;

    @Override
    @Transactional
    public InvoiceDTO createInvoice(CreateInvoiceRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + request.getBranchId()));

        Invoice invoice = new Invoice();
        invoice.setCustomer(customer);
        invoice.setBranch(branch);
        invoice.setInvoiceNumber(generateInvoiceNumber(branch));
        invoice.setPaymentStatus(PaymentStatus.PENDING);
        invoice.setCreatedAt(LocalDateTime.now());

        request.getItems().forEach(item -> {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + item.getProductId()));

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new BusinessException("Insufficient stock for product: " + product.getName());
            }

            InvoiceItem invoiceItem = new InvoiceItem();
            invoiceItem.setProduct(product);
            invoiceItem.setQuantity(item.getQuantity());
            invoiceItem.setUnitPrice(product.getPrice());
            invoiceItem.setInvoice(invoice);

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);

            invoice.getItems().add(invoiceItem);
        });

        return mapToDTO(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional
    public InvoiceDTO updateInvoice(Long id, UpdateInvoiceRequest request) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        if (invoice.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new BusinessException("Cannot update invoice with status: " + invoice.getPaymentStatus());
        }

        request.getItems().forEach(item -> {
            InvoiceItem invoiceItem = invoice.getItems().stream()
                    .filter(i -> i.getProduct().getId().equals(item.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Invoice item not found for product id: " + item.getProductId()));

            Product product = invoiceItem.getProduct();
            int quantityDifference = item.getQuantity() - invoiceItem.getQuantity();

            if (product.getStockQuantity() < quantityDifference) {
                throw new BusinessException("Insufficient stock for product: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - quantityDifference);
            productRepository.save(product);

            invoiceItem.setQuantity(item.getQuantity());
        });

        return mapToDTO(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional
    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        if (invoice.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new BusinessException("Cannot delete invoice with status: " + invoice.getPaymentStatus());
        }

        invoice.getItems().forEach(item -> {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        });

        invoiceRepository.delete(invoice);
    }

    @Override
    public InvoiceDTO getInvoice(Long id) {
        return invoiceRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    }

    @Override
    public List<InvoiceDTO> getInvoicesByBranch(Long branchId) {
        return invoiceRepository.findByBranchId(branchId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDTO> getInvoicesByCustomer(Long customerId) {
        return invoiceRepository.findByCustomerId(customerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDTO> getInvoicesByDateRange(Long branchId, LocalDateTime startDate, LocalDateTime endDate) {
        return invoiceRepository.findByBranchIdAndCreatedAtBetween(branchId, startDate, endDate).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<InvoiceDTO> getInvoicesByBranch(Long branchId, Pageable pageable) {
        return invoiceRepository.findByBranchId(branchId, pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional
    public void updatePaymentStatus(Long id, String status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        try {
            PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
            invoice.setPaymentStatus(paymentStatus);
            invoiceRepository.save(invoice);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid payment status: " + status);
        }
    }

    @Override
    @Transactional
    public void cancelInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        if (invoice.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new BusinessException("Cannot cancel invoice with status: " + invoice.getPaymentStatus());
        }

        invoice.getItems().forEach(item -> {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        });

        invoice.setPaymentStatus(PaymentStatus.CANCELLED);
        invoiceRepository.save(invoice);
    }

    private String generateInvoiceNumber(Branch branch) {
        String prefix = branch.getName().substring(0, 3).toUpperCase();
        String timestamp = String.valueOf(System.currentTimeMillis());
        return prefix + "-" + timestamp;
    }

    private InvoiceDTO mapToDTO(Invoice invoice) {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setCustomerId(invoice.getCustomer().getId());
        dto.setCustomerName(invoice.getCustomer().getName());
        dto.setBranchId(invoice.getBranch().getId());
        dto.setBranchName(invoice.getBranch().getName());
        dto.setPaymentStatus(invoice.getPaymentStatus().name());
        dto.setCreatedAt(invoice.getCreatedAt());
        
        BigDecimal totalAmount = invoice.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalAmount(totalAmount);
        
        dto.setItems(invoice.getItems().stream()
                .map(item -> {
                    InvoiceItemDTO itemDTO = new InvoiceItemDTO();
                    itemDTO.setId(item.getId());
                    itemDTO.setProductId(item.getProduct().getId());
                    itemDTO.setProductName(item.getProduct().getName());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setUnitPrice(item.getUnitPrice());
                    return itemDTO;
                })
                .collect(Collectors.toList()));
        return dto;
    }
} 