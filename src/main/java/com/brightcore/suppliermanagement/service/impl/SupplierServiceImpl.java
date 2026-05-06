package com.brightcore.suppliermanagement.service.impl;

import com.brightcore.suppliermanagement.dto.SupplierDto;
import com.brightcore.suppliermanagement.entity.Supplier;
import com.brightcore.suppliermanagement.event.SupplierEvent;
import com.brightcore.suppliermanagement.exception.ResourceNotFoundException;
import com.brightcore.suppliermanagement.kafka.SupplierKafkaProducer;
import com.brightcore.suppliermanagement.repository.SupplierRepository;
import com.brightcore.suppliermanagement.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private static final String SOURCE = "supplier-management";

    private final SupplierRepository supplierRepository;
    private final SupplierKafkaProducer kafkaProducer;

    @Override
    @Transactional
    public SupplierDto.Response create(SupplierDto.CreateRequest request) {
        if (supplierRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Supplier with email '" + request.getEmail() + "' already exists");
        }

        Supplier supplier = Supplier.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .companyName(request.getCompanyName())
                .active(true)
                .build();

        Supplier saved = supplierRepository.save(supplier);
        SupplierDto.Response response = toResponse(saved);
        publish(SupplierEvent.EventType.CREATED, saved.getId(), response);
        return response;
    }

    @Override
    @Transactional
    public SupplierDto.Response update(Long id, SupplierDto.UpdateRequest request) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        if (!existing.getEmail().equalsIgnoreCase(request.getEmail())
                && supplierRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email '" + request.getEmail() + "' already in use");
        }

        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setPhoneNumber(request.getPhoneNumber());
        existing.setAddress(request.getAddress());
        existing.setCompanyName(request.getCompanyName());
        if (request.getActive() != null) {
            existing.setActive(request.getActive());
        }

        Supplier updated = supplierRepository.save(existing);
        SupplierDto.Response response = toResponse(updated);
        publish(SupplierEvent.EventType.UPDATED, updated.getId(), response);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierDto.Response getById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        SupplierDto.Response response = toResponse(supplier);
        publish(SupplierEvent.EventType.RETRIEVED, id, response);
        return response;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        SupplierDto.Response snapshot = toResponse(supplier);
        supplierRepository.delete(supplier);
        publish(SupplierEvent.EventType.DELETED, id, snapshot);
    }

    private void publish(SupplierEvent.EventType type, Long id, SupplierDto.Response payload) {
        SupplierEvent event = SupplierEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(type)
                .supplierId(id)
                .payload(payload)
                .timestamp(LocalDateTime.now())
                .source(SOURCE)
                .build();
        kafkaProducer.publish(event);
    }

    private SupplierDto.Response toResponse(Supplier s) {
        return SupplierDto.Response.builder()
                .id(s.getId())
                .name(s.getName())
                .email(s.getEmail())
                .phoneNumber(s.getPhoneNumber())
                .address(s.getAddress())
                .companyName(s.getCompanyName())
                .active(s.getActive())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
