package com.g4fpt.sms.customer.service.impl;

import com.g4fpt.sms.customer.entity.CustomerRank;
import com.g4fpt.sms.customer.repository.CustomerRankRepository;
import com.g4fpt.sms.customer.service.CustomerRankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerRankServiceImpl implements CustomerRankService {

    private final CustomerRankRepository customerRankRepository;

    @Override
    public List<CustomerRank> getAllRanks() {
        return customerRankRepository.findAll();
    }

    @Override
    public CustomerRank getRankById(Long id) {
        return customerRankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hạng khách hàng!"));
    }
    @Override
    public CustomerRank createRank(CustomerRank customerRank) {
        return customerRankRepository.save(customerRank);
    }
}