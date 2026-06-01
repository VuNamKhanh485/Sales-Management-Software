package com.g4fpt.sms.voucher.service;

import com.g4fpt.sms.voucher.dto.request.VoucherCreateRequest;
import com.g4fpt.sms.voucher.dto.request.VoucherUpdateRequest;
import com.g4fpt.sms.voucher.dto.response.VoucherResponse;
import com.g4fpt.sms.voucher.entity.Voucher;

import java.util.List;

public interface VoucherService {
    List<VoucherResponse> search(String keyword, String status);
    VoucherResponse getById(Long id);
    Voucher getEntityById(Long id); // dùng cho confirm-delete
    void create(VoucherCreateRequest request);
    void update(Long id, VoucherUpdateRequest request);
    void delete(Long id);
}