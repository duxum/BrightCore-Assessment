package com.brightcore.suppliermanagement.controller;

import com.brightcore.suppliermanagement.dto.SupplierDto;
import com.brightcore.suppliermanagement.dto.SupplierDto.ApiResponse;
import com.brightcore.suppliermanagement.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
@Tag(name = "Supplier Management", description = "Endpoints for managing suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    @Operation(summary = "Create a new supplier")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<SupplierDto.Response>> addSupplier(
            @Valid @RequestBody SupplierDto.CreateRequest request) {
        SupplierDto.Response created = supplierService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<SupplierDto.Response>builder()
                        .success(true)
                        .message("Supplier created successfully")
                        .data(created)
                        .build()
        );
    }

    @Operation(summary = "Update an existing supplier")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<SupplierDto.Response>> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierDto.UpdateRequest request) {
        SupplierDto.Response updated = supplierService.update(id, request);
        return ResponseEntity.ok(
                ApiResponse.<SupplierDto.Response>builder()
                        .success(true)
                        .message("Supplier updated successfully")
                        .data(updated)
                        .build()
        );
    }

    @Operation(summary = "Get supplier by id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierDto.Response>> getSupplier(@PathVariable Long id) {
        SupplierDto.Response response = supplierService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.<SupplierDto.Response>builder()
                        .success(true)
                        .message("Supplier retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Delete a supplier")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteSupplier(@PathVariable Long id) {
        supplierService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Supplier deleted successfully")
                        .build()
        );
    }
}
