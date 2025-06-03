package com.bc.app.dto.branch;

import lombok.Data;

@Data
public class BranchDTO {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private boolean active;
} 