package com.g4fpt.sms.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class PosSessionData {

    private Map<Integer, PosCart> carts = new LinkedHashMap<>();
    private int activeIndex = 1;
    private int nextIndex = 2;
    private Long activeBranchId;

    public PosSessionData() {
        carts.put(1, new PosCart());
    }

    public PosCart getActiveCart() {
        if (!carts.containsKey(activeIndex)) {
            carts.put(activeIndex, new PosCart());
        }
        return carts.get(activeIndex);
    }

    public boolean canAddOrder() {
        return carts.size() < 5;
    }

    public void addNewOrder() {
        if (canAddOrder()) {
            carts.put(nextIndex, new PosCart());
            activeIndex = nextIndex;
            nextIndex++;
        }
    }

    public void removeOrder(int index) {
        carts.remove(index);
        if (carts.isEmpty()) {
            carts.put(nextIndex, new PosCart());
            activeIndex = nextIndex;
            nextIndex++;
        } else if (activeIndex == index) {
            activeIndex = carts.keySet().iterator().next();
        }
    }

    public void switchOrder(int index) {
        if (carts.containsKey(index)) {
            activeIndex = index;
        }
    }
}