package com.fhcs.dlq.auditor.core.domain.bo;

import java.time.Instant;
import java.util.List;

public class SqsOrderMessageBO {

    private String zipCode;
    private Integer customerId;
    private List<OrderItemBO> orderItems;
    private String origin;
    private Instant occurredAt;

    public int calcularQuantidadeTotalProdutos() {
        if (orderItems == null) return 0;
        return orderItems.stream()
                .mapToInt(OrderItemBO::getAmount)
                .sum();
    }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public List<OrderItemBO> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemBO> orderItems) { this.orderItems = orderItems; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }

    @Override
    public String toString() {
        return "{"
            + "\"zipCode\":\"" + zipCode + "\""
            + ",\"customerId\":" + customerId
            + ",\"orderItems\":" + orderItems
            + ",\"origin\":\"" + origin + "\""
            + ",\"occurredAt\":\"" + occurredAt + "\""
            + "}";
    }
}