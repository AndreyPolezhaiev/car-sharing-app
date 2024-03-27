package com.polezhaiev.carsharingapp.service.payment.impl;

import com.polezhaiev.carsharingapp.dto.payment.PaymentDto;
import com.polezhaiev.carsharingapp.dto.payment.PaymentRequestDto;
import com.polezhaiev.carsharingapp.exception.app.EntityNotFoundException;
import com.polezhaiev.carsharingapp.mapper.PaymentMapper;
import com.polezhaiev.carsharingapp.model.Car;
import com.polezhaiev.carsharingapp.model.Payment;
import com.polezhaiev.carsharingapp.model.Rental;
import com.polezhaiev.carsharingapp.repository.payment.PaymentRepository;
import com.polezhaiev.carsharingapp.repository.rental.RentalRepository;
import com.polezhaiev.carsharingapp.service.notification.NotificationService;
import com.polezhaiev.carsharingapp.service.payment.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final double FINE_MULTIPLIER = 1.5;
    private static final String DOMAIN = "http://localhost:8080";
    private static final String SUCCESS_URL = "/payments/success?sessionId={CHECKOUT_SESSION_ID}";
    private static final String CANCEL_URL = "/payments/cancel?sessionId={CHECKOUT_SESSION_ID}";

    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final PaymentMapper paymentMapper;
    private final NotificationService notificationService;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    public List<PaymentDto> findAllByUserId(Long userId) {
        return paymentRepository.findAllByRentalUserId(userId)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public List<PaymentDto> findAllByStatus(Long userId, String status) {
        return paymentRepository.findAllByRentalUserIdAndStatus(
                userId, Payment.PaymentStatus.valueOf(status.toUpperCase()))
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public PaymentDto createPaymentSession(PaymentRequestDto requestDto) {
        Optional<Payment> paymentFromDb
                = paymentRepository.findAllByRentalId(requestDto.getRentalId())
                .stream()
                .filter(p -> p.getPaymentType() == requestDto.getPaymentType())
                .filter(p -> p.getStatus() != Payment.PaymentStatus.CANCELLED)
                .findFirst();

        if (paymentFromDb.isPresent()) {
            Payment payment = paymentFromDb.get();
            if (payment.getStatus() == Payment.PaymentStatus.PAID) {
                throw new EntityNotFoundException("This rental has been paid");
            }
            if (payment.getStatus() == Payment.PaymentStatus.PENDING) {
                return paymentMapper.toDto(payment);
            }
        }

        Payment payment = new Payment();
        Rental rental = rentalRepository
                .findById(requestDto.getRentalId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find rental by id "
                        + requestDto.getRentalId()));
        payment.setRental(rental);
        payment.setPaymentType(requestDto.getPaymentType());

        Car car = rental.getCar();
        BigDecimal dailyFee = car.getDailyFee();
        long days;
        if (requestDto.getPaymentType() == Payment.PaymentType.PAYMENT) {
            LocalDateTime rentalDate = rental.getRentalDate();
            LocalDateTime returnDate = rental.getReturnDate();
            Duration duration = Duration.between(rentalDate, returnDate);
            days = duration.toDays() + 1;
        } else {
            LocalDateTime returnDate = rental.getReturnDate();
            LocalDateTime actualReturnDate = rental.getActualReturnDate();
            Duration duration = Duration.between(returnDate, actualReturnDate);
            dailyFee = dailyFee.multiply(BigDecimal.valueOf(FINE_MULTIPLIER));
            days = duration.toDays() + 1;
            notificationService.sendMessageAboutOverdueRental(rental);
        }

        BigDecimal amountToPay = dailyFee.multiply(BigDecimal.valueOf(days));

        payment.setAmountToPay(amountToPay);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        Payment saved = paymentRepository.save(checkout(car, payment));
        return paymentMapper.toDto(saved);
    }

    private Payment checkout(Car car, Payment payment) {
        SessionCreateParams.Builder builder = new
                SessionCreateParams.Builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setExpiresAt(Instant.now().plus(31, ChronoUnit.MINUTES)
                        .getEpochSecond())
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(payment.getAmountToPay().longValue() * 100L)
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData
                                                .builder()
                                                .setName("Renting " + car.getBrand()
                                                        + " " + car.getModel())
                                                .build())
                                .build()
                        ).setQuantity(1L)
                        .build()
                ).setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(DOMAIN + SUCCESS_URL)
                .setCancelUrl(DOMAIN + CANCEL_URL);
        Session session;
        try {
            session = Session.create(builder.build());
            payment.setSessionId(session.getId());
            payment.setSessionUrl(new URL(session.getUrl()));
        } catch (StripeException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return payment;
    }

    @Override
    public PaymentDto setSuccessfulPayment(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("There is no session by id " + sessionId));
        payment.setStatus(Payment.PaymentStatus.PAID);
        Payment saved = paymentRepository.save(payment);
        notificationService.sendMessageAboutSuccessfulPayment(saved);
        return paymentMapper.toDto(saved);
    }

    @Override
    public PaymentDto setCancelledPayment(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("There is no session by id " + sessionId));
        payment.setStatus(Payment.PaymentStatus.CANCELLED);
        Payment saved = paymentRepository.save(payment);
        notificationService.sendMessageAboutCancelledPayment(saved);
        return paymentMapper.toDto(saved);
    }
}
