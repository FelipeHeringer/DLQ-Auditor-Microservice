package com.fhcs.dlq.auditor.core.domain.bo;

public class OrderItemBO {
    private Integer sku;
    private Integer amount;

    public Integer getSku() { return sku; }
    public void setSku(Integer sku) { this.sku = sku; }
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }

    @Override
    public String toString() {
        return "{\"sku\":" + sku + ",\"amount\":" + amount + "}";
    }
}