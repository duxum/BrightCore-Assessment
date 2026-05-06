package com.brightcore.suppliermanagement.service;

import com.brightcore.suppliermanagement.dto.SupplierDto;

public interface SupplierService {
    SupplierDto.Response create(SupplierDto.CreateRequest request);
    SupplierDto.Response update(Long id, SupplierDto.UpdateRequest request);
    SupplierDto.Response getById(Long id);
    void delete(Long id);
}
