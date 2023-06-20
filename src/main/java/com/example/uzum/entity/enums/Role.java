package com.example.uzum.entity.enums;

import java.util.Set;
import java.util.stream.Collectors;

import static com.example.uzum.entity.enums.Permissions.*;

public enum Role {

    DIRECTOR(Set.of(
            /* EMPLOYEE */
            EMPLOYEE_ADD,
            EMPLOYEE_CONFIRM_PHONE_NUMBER,
            EMPLOYEE_GET_CODE_TO_CONFIRM_PHONE_NUMBER,
            EMPLOYEES_GET,
            EMPLOYEE_GET_BY_ID,
            EMPLOYEE_GET_BY_NAME_PHONE,
            EMPLOYEE_EDIT,
            EMPLOYEE_EDIT_ROLE,
            EMPLOYEE_CONFIRM_NEW_PHONE_NUMBER_TO_EDIT,
            EMPLOYEE_GET_CODE_TO_CONFIRM_NEW_PHONE_NUMBER,
            EMPLOYEE_BLOCK,
            EMPLOYEE_UNBLOCK,
            /* BUYER */
            BUYER_GET_BY_ID,
            BUYER_GET_BY_NAME_AND_PHONE,
            BUYER_GET_AMOUNT,
            BUYER_EDIT,
            BUYER_BLOCK,
            BUYER_UNBLOCK,
            BUYER_CONFIRM_NEW_PHONE,
            BUYER_GET_CODE_TO_CONFIRM_NEW_PHONE,
            BUYER_FILL_BALANCE,
            /* ATTACHMENT */
            ATTACHMENT_UPLOAD,
            ATTACHMENT_GET_INFO,
            ATTACHMENT_EDIT,
            /* SELLER */
            SELLER_ADD,
            SELLER_GET_BY_FILTER,
            SELLER_GET_BY_ID,
            SELLER_EDIT,
            SELLER_DELETE,
            SELLER_RESTORE,
            /* REGION */
            REGION_ADD,
            REGION_EDIT,
            REGION_DELETE,
            REGION_COMPLETELY_DELETE,
            /* BRANCH */
            BRANCH_ADD,
            BRANCH_EDIT,
            BRANCH_DELETE,
            BRANCH_COMPLETELY_DELETE,
            /* BRAND */
            BRAND_ADD,
            BRAND_EDIT,
            BRAND_DELETE,
            /* CATEGORY */
            CATEGORY_ADD,
            CATEGORY_EDIT,
            CATEGORY_DELETE,
            /* PRODUCT */
            PRODUCT_ADD,
            PRODUCT_EDIT,
            PRODUCT_DELETE,
            /* MAIN-PANEL */
            MAIN_PANEL_ADD,
            MAIN_PANEL_EDIT,
            MAIN_PANEL_DELETE,
            MAIN_PANEL_CONNECT_TO_METHOD,
            MAIN_PANEL_CONNECT_TO_SELLER,
            MAIN_PANEL_CONNECT_TO_CATEGORY,
            MAIN_PANEL_CONNECT_TO_PRODUCT,
            MAIN_PANEL_GET_METHOD_NAMES,
            MAIN_PANEL_GET_UNCONNECTED_PANELS,
            MAIN_PANEL_GET_CONNECTED_PANELS,
            MAIN_PANEL_DISCONNECT_FROM_ANYTHING,
            VIEWED_PRODUCTS_GET_BY_FILTER,
            /* COMMENT */
            COMMENT_ADD,
            COMMENT_GET_BY_BUYER_ID,
            COMMENT_GET_UNANSWERED_COMMENTS_BY_PRODUCT_ID,
            COMMENT_REPLY,
            COMMENT_EDIT,
            COMMENT_DELETE,
            /* BASKET */
            BASKET_GET_BY_BUYER_ID,
            BASKET_GET_AMOUNT,
            /* ORDER */
            ORDER_ADD,
            ORDER_GET_BY_PREPARING,
            ORDER_GET_BY_DELIVERING,
            ORDER_GET_BY_RETURNED,
            ORDER_GET_BY_WAITING_CLIENT,
            ORDER_GET_BY_ID,
            ORDER_GET_BY_BUYER_DETAILS,
            ORDER_GET_STAT_BY_BRANCH_ID,
            ORDER_GET_STAT_BY_REGION_ID,
            ORDER_GET_ALL_REGION_STAT,
            ORDER_GET_ALL_BRANCH_STAT_BY_REGION_ID,
            ORDER_GET_BY_BUYER_ID,
            ORDER_GET_TO_HOME_BY_BRANCH_ID,
            ORDER_CHANGE_STATUS,
            ORDER_EDIT
    )),
    ADMIN(Set.of(
            /* EMPLOYEE */
            EMPLOYEE_ADD,
            EMPLOYEE_CONFIRM_PHONE_NUMBER,
            EMPLOYEE_GET_CODE_TO_CONFIRM_PHONE_NUMBER,
            EMPLOYEE_GET_BY_ID,
            EMPLOYEE_GET_BY_NAME_PHONE,
            EMPLOYEE_EDIT,
            EMPLOYEE_EDIT_ROLE,
            EMPLOYEE_CONFIRM_NEW_PHONE_NUMBER_TO_EDIT,
            EMPLOYEE_GET_CODE_TO_CONFIRM_NEW_PHONE_NUMBER,
            EMPLOYEE_BLOCK,
            EMPLOYEE_UNBLOCK,
            /* BUYER */
            BUYER_GET_BY_ID,
            BUYER_GET_BY_NAME_AND_PHONE,
            BUYER_GET_AMOUNT,
            BUYER_EDIT,
            BUYER_BLOCK,
            BUYER_UNBLOCK,
            BUYER_CONFIRM_NEW_PHONE,
            BUYER_GET_CODE_TO_CONFIRM_NEW_PHONE,
            BUYER_FILL_BALANCE,
            /* ATTACHMENT */
            ATTACHMENT_UPLOAD,
            ATTACHMENT_GET_INFO,
            ATTACHMENT_EDIT,
            /* SELLER */
            SELLER_ADD,
            SELLER_GET_BY_FILTER,
            SELLER_GET_BY_ID,
            SELLER_EDIT,
            SELLER_DELETE,
            SELLER_RESTORE,
            /* REGION */
            REGION_ADD,
            REGION_EDIT,
            REGION_DELETE,
            REGION_COMPLETELY_DELETE,
            /* BRANCH */
            BRANCH_ADD,
            BRANCH_EDIT,
            BRANCH_DELETE,
            BRANCH_COMPLETELY_DELETE,
            /* BRAND */
            BRAND_ADD,
            BRAND_EDIT,
            BRAND_DELETE,
            /* CATEGORY */
            CATEGORY_ADD,
            CATEGORY_EDIT,
            CATEGORY_DELETE,
            /* PRODUCT */
            PRODUCT_ADD,
            PRODUCT_EDIT,
            PRODUCT_DELETE,
            /* MAIN-PANEL */
            MAIN_PANEL_ADD,
            MAIN_PANEL_EDIT,
            MAIN_PANEL_DELETE,
            MAIN_PANEL_CONNECT_TO_METHOD,
            MAIN_PANEL_CONNECT_TO_SELLER,
            MAIN_PANEL_CONNECT_TO_CATEGORY,
            MAIN_PANEL_CONNECT_TO_PRODUCT,
            MAIN_PANEL_GET_METHOD_NAMES,
            MAIN_PANEL_GET_UNCONNECTED_PANELS,
            MAIN_PANEL_GET_CONNECTED_PANELS,
            MAIN_PANEL_DISCONNECT_FROM_ANYTHING,
            VIEWED_PRODUCTS_GET_BY_FILTER,
            /* COMMENT */
            COMMENT_ADD,
            COMMENT_GET_BY_BUYER_ID,
            COMMENT_GET_UNANSWERED_COMMENTS_BY_PRODUCT_ID,
            COMMENT_REPLY,
            COMMENT_EDIT,
            COMMENT_DELETE,
            /* BASKET */
            BASKET_GET_BY_BUYER_ID,
            BASKET_GET_AMOUNT,
            /* ORDER */
            ORDER_ADD,
            ORDER_GET_BY_PREPARING,
            ORDER_GET_BY_DELIVERING,
            ORDER_GET_BY_RETURNED,
            ORDER_GET_BY_WAITING_CLIENT,
            ORDER_GET_BY_ID,
            ORDER_GET_BY_BUYER_DETAILS,
            ORDER_GET_STAT_BY_BRANCH_ID,
            ORDER_GET_STAT_BY_REGION_ID,
            ORDER_GET_ALL_REGION_STAT,
            ORDER_GET_ALL_BRANCH_STAT_BY_REGION_ID,
            ORDER_GET_BY_BUYER_ID,
            ORDER_GET_TO_HOME_BY_BRANCH_ID,
            ORDER_CHANGE_STATUS,
            ORDER_EDIT
    )),
    SELLER(Set.of(
            EMPLOYEE_GET_BY_ID,
            EMPLOYEE_EDIT,
            EMPLOYEE_CONFIRM_NEW_PHONE_NUMBER_TO_EDIT,
            EMPLOYEE_GET_CODE_TO_CONFIRM_NEW_PHONE_NUMBER,
            SELLER_GET_BY_ID,
            SELLER_EDIT,
            COMMENT_GET_UNANSWERED_COMMENTS_BY_PRODUCT_ID,
            COMMENT_REPLY,
            COMMENT_EDIT,
            COMMENT_DELETE,
            COMMENT_ADD
    )),
    BUYER(Set.of(
            BUYER_GET_BY_ID,
            BUYER_EDIT,
            BUYER_CONFIRM_NEW_PHONE,
            BUYER_GET_CODE_TO_CONFIRM_NEW_PHONE,
            BUYER_FILL_BALANCE,
            COMMENT_ADD,
            COMMENT_GET_BY_BUYER_ID,
            COMMENT_EDIT,
            COMMENT_DELETE,
            BASKET_GET_BY_BUYER_ID,
            ORDER_ADD,
            ORDER_GET_BY_ID,
            ORDER_GET_BY_BUYER_ID,
            ORDER_EDIT
    ));

    private final Set<Permissions> permissions;

    Role(Set<Permissions> permissions) {
        this.permissions = permissions;
    }

    public Set<String> getPermissions() {
        Set<String> userAuthorities = permissions.stream()
                .map(permission -> permission.name())
                .collect(Collectors.toSet());
        userAuthorities.add("ROLE_" + this.name());
        return userAuthorities;
    }


}
