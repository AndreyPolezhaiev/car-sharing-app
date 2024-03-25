package com.polezhaiev.carsharingapp.controller;

import com.polezhaiev.carsharingapp.dto.payment.PaymentDto;
import com.polezhaiev.carsharingapp.dto.payment.PaymentRequestDto;
import com.polezhaiev.carsharingapp.model.User;
import com.polezhaiev.carsharingapp.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Get all payments by user id",
            description = "Get all payments by user id")
    @GetMapping
    public List<PaymentDto> getAllByUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return paymentService.findAllByUserId(user.getId());
    }

    @Operation(summary = "Get all payments by status and user id",
            description = "Get all payments by status and user id")
    @GetMapping("/status")
    public List<PaymentDto> getAllByStatus(
            Authentication authentication, @RequestParam String status) {
        User user = (User) authentication.getPrincipal();
        return paymentService.findAllByStatus(user.getId(), status);
    }

    @Operation(summary = "Create a payment session",
            description = "Create a payment session")
    @PostMapping("/pay")
    public PaymentDto createPaymentSession(@RequestBody @Valid PaymentRequestDto requestDto) {
        return paymentService.createPaymentSession(requestDto);
    }

    @Operation(summary = "Endpoint for successful stripe redirection",
            description = "Endpoint for successful stripe redirection")
    @PostMapping("/success")
    public String setSuccessPayment(@RequestParam String sessionId) {
        paymentService.setSuccessfulPayment(sessionId);
        return "success";
    }

    @Operation(summary = "Endpoint for cancel stripe redirection",
            description = "Endpoint for successful stripe redirection")
    @PostMapping("/cancel")
    public String setCancelledPayment(@RequestParam String sessionId) {
        paymentService.setCancelledPayment(sessionId);
        return "cancel";
    }
}
