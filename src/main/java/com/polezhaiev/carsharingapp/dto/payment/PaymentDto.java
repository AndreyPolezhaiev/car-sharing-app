package com.polezhaiev.carsharingapp.dto.payment;

import com.polezhaiev.carsharingapp.model.Payment;
import java.math.BigDecimal;
import java.net.URL;
import lombok.Data;

@Data
public class PaymentDto {
    private Long id;
    private Payment.PaymentStatus status;
    private Payment.PaymentType paymentType;
    private Long rentalId;
    private URL sessionUrl;
    private String sessionId;
    private BigDecimal amountToPay;
}
