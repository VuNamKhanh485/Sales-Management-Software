package com.g4fpt.sms.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.TreeMap;

@Getter
@Setter
public class PosSessionData {

    private Map<Integer, PosCart> carts = new TreeMap<>();
    private int activeIndex = 1;
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

    private int getSmallestUnusedIndex() {
        int i = 1;
        while (carts.containsKey(i)) {
            i++;
        }
        return i;
    }

    public void addNewOrder() {
        if (canAddOrder()) {
            int newIndex = getSmallestUnusedIndex();
            carts.put(newIndex, new PosCart());
            activeIndex = newIndex;
        }
    }

    public void removeOrder(int index) {
        carts.remove(index);
        if (carts.isEmpty()) {
            carts.put(1, new PosCart());
            activeIndex = 1;
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