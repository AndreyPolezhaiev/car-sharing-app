package com.polezhaiev.carsharingapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.net.URL;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(nullable = false, name = "payment_type")
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "rental_id",
            nullable = false)
    private Rental rental;

    @Column(name = "session_url", nullable = false)
    private URL sessionUrl;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "amount_to_pay", nullable = false)
    private BigDecimal amountToPay;

    public enum PaymentStatus {
        PENDING, PAID, CANCELLED
    }

    public enum PaymentType {
        PAYMENT, FINE
    }
}
