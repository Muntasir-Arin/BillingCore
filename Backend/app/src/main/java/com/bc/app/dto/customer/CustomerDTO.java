package com.bc.app.dto.customer;

import lombok.Data;

@Data
public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private boolean active;
    private Long branchId;
    private String branchName;
} 