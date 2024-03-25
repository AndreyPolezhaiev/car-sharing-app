package com.polezhaiev.carsharingapp.repository.payment;

import com.polezhaiev.carsharingapp.model.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByRentalUserId(Long userId);

    List<Payment> findAllByRentalUserIdAndStatus(Long userId, Payment.PaymentStatus status);

    List<Payment> findAllByRentalId(Long rentalId);

    Optional<Payment> findBySessionId(String sessionId);
}
