package com.brightcore.suppliermanagement.kafka;

import com.brightcore.suppliermanagement.event.SupplierEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class SupplierKafkaProducer {

    private final KafkaTemplate<String, SupplierEvent> kafkaTemplate;

    @Value("${app.kafka.topic.supplier-events}")
    private String topic;

    public void publish(SupplierEvent event) {
        String key = event.getSupplierId() == null ? "na" : event.getSupplierId().toString();
        CompletableFuture<SendResult<String, SupplierEvent>> future =
                kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Published {} event for supplierId={} to topic={} offset={}",
                        event.getEventType(), event.getSupplierId(), topic,
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish event {}: {}", event, ex.getMessage(), ex);
            }
        });
    }
}
