package com.polezhaiev.carsharingapp.service.notification;

import com.polezhaiev.carsharingapp.model.Payment;
import com.polezhaiev.carsharingapp.model.Rental;

public interface NotificationService {
    void sendMessageAboutNewRentalCreated(Rental rental);

    void sendMessageAboutOverdueRental(Rental rental);

    void sendMessageAboutSuccessfulPayment(Payment payment);

    void sendMessageAboutCancelledPayment(Payment payment);
}
