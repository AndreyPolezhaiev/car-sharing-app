package com.polezhaiev.carsharingapp.dto.payment;

import com.polezhaiev.carsharingapp.model.Payment;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PaymentRequestDto {
    @Min(1)
    private Long rentalId;
    private Payment.PaymentType paymentType;
}
