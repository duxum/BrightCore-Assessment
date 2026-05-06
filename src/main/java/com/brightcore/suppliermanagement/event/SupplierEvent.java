package com.brightcore.suppliermanagement.event;

import com.brightcore.suppliermanagement.dto.SupplierDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierEvent {

    public enum EventType { CREATED, UPDATED, RETRIEVED, DELETED }

    private String eventId;
    private EventType eventType;
    private Long supplierId;
    private SupplierDto.Response payload;
    private LocalDateTime timestamp;
    private String source;
}
