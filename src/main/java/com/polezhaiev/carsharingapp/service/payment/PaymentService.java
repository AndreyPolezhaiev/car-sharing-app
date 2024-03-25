package com.polezhaiev.carsharingapp.service.payment;

import com.polezhaiev.carsharingapp.dto.payment.PaymentDto;
import com.polezhaiev.carsharingapp.dto.payment.PaymentRequestDto;
import java.util.List;

public interface PaymentService {
    List<PaymentDto> findAllByUserId(Long userId);

    List<PaymentDto> findAllByStatus(Long userId, String status);

    PaymentDto createPaymentSession(PaymentRequestDto requestDto);

    PaymentDto setSuccessfulPayment(String sessionId);

    PaymentDto setCancelledPayment(String sessionId);
}
