package com.bc.app.model;

public enum ActionType {
    // User actions
    USER_LOGIN,
    USER_LOGOUT,
    USER_CREATED,
    USER_UPDATED,
    USER_DEACTIVATED,

    // Branch actions
    BRANCH_CREATED,
    BRANCH_UPDATED,
    BRANCH_DEACTIVATED,

    // Product actions
    PRODUCT_CREATED,
    PRODUCT_UPDATED,
    PRODUCT_DEACTIVATED,
    STOCK_UPDATED,

    // Customer actions
    CUSTOMER_CREATED,
    CUSTOMER_UPDATED,
    CUSTOMER_DEACTIVATED,

    // Invoice actions
    INVOICE_CREATED,
    INVOICE_UPDATED,
    INVOICE_CANCELLED,
    INVOICE_REFUNDED,

    // Payment actions
    PAYMENT_RECEIVED,
    PAYMENT_REFUNDED,

    // Return actions
    RETURN_CREATED,
    RETURN_APPROVED,
    RETURN_REJECTED,

    // System actions
    SYSTEM_ERROR,
    CONFIGURATION_CHANGED
} 