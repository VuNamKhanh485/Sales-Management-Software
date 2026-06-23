package com.g4fpt.sms.customer.service;

import com.g4fpt.sms.customer.entity.CustomerRank;
import java.util.List;

public interface CustomerRankService {
    List<CustomerRank> getAllRanks();
    CustomerRank createRank(CustomerRank customerRank);
    CustomerRank getRankById(Long id);
}