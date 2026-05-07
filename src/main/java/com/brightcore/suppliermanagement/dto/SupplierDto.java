package com.brightcore.suppliermanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

public class SupplierDto {

    private SupplierDto() {}

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        @NotBlank(message = "Name is required")
        @Size(max = 150)
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;

        @Size(max = 30)
        private String phoneNumber;

        @Size(max = 255)
        private String address;

        @Size(max = 150)
        private String companyName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        @Size(max = 150, message = "Name must not exceed 150 characters")
        private String name;

        @Email(message = "Email must be valid")
        @Size(max = 150)
        private String email;

        @Size(max = 30)
        private String phoneNumber;

        @Size(max = 255)
        private String address;

        @Size(max = 150)
        private String companyName;

        private Boolean active;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String email;
        private String phoneNumber;
        private String address;
        private String companyName;
        private Boolean active;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
    }
}
