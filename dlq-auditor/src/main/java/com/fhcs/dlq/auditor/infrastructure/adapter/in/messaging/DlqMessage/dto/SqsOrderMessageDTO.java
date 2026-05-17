package com.fhcs.dlq.auditor.infrastructure.adapter.in.messaging.DlqMessage.dto;

import java.time.Instant;
import java.util.List;

public class SqsOrderMessageDTO {

    private String zipCode;

    private Integer customerId;

    private List<OrderItemDTO> orderItems;

    private String origin;

    private Instant occurredAt;

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public List<OrderItemDTO> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemDTO> orderItems) { this.orderItems = orderItems; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }

}