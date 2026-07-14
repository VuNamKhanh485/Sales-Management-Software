package com.g4fpt.sms.voucher.service;

import org.springframework.data.domain.Page;
import com.g4fpt.sms.voucher.dto.request.VoucherRequest;
import com.g4fpt.sms.voucher.dto.response.VoucherResponse;
import com.g4fpt.sms.voucher.enums.VoucherStatus;

public interface VoucherService {

    VoucherResponse create(VoucherRequest request);

    VoucherResponse update(Long id, VoucherRequest request);

    VoucherResponse getById(Long id);

    VoucherResponse getByCode(String code);

    Page<VoucherResponse> search(String keyword, VoucherStatus status, int page, int size);

    Page<VoucherResponse> getActiveVouchers(int page, int size);

    void delete(Long id);

    VoucherResponse toggleStatus(Long id);
}