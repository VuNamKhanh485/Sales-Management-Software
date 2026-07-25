package com.g4fpt.sms.voucher.service;

import org.springframework.data.domain.Page;
import com.g4fpt.sms.voucher.dto.VoucherDTO;
import com.g4fpt.sms.voucher.enums.VoucherStatus;

public interface VoucherService {

    VoucherDTO create(VoucherDTO request);

    VoucherDTO update(Long id, VoucherDTO request);

    VoucherDTO getById(Long id);

    VoucherDTO getByCode(String code);

    Page<VoucherDTO> search(String keyword, VoucherStatus status, int page, int size);

    Page<VoucherDTO> getActiveVouchers(int page, int size);

    void delete(Long id);

    VoucherDTO toggleStatus(Long id);
}