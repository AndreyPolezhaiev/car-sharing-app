package com.polezhaiev.carsharingapp.service.notification.impl;

import com.polezhaiev.carsharingapp.model.Payment;
import com.polezhaiev.carsharingapp.model.Rental;
import com.polezhaiev.carsharingapp.service.notification.NotificationService;
import com.polezhaiev.carsharingapp.telegram.CarRentalBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final CarRentalBot carRentalBot;

    @Override
    public void sendMessageAboutNewRentalCreated(Rental rental) {
        String message = "It was created a new rental:"
                + "\nid: " + rental.getId()
                + "\nrental date: " + rental.getRentalDate().toString()
                + "\nreturn date: " + rental.getReturnDate().toString()
                + "\ncar: "
                + "\n brand: " + rental.getCar().getBrand()
                + "\n model: " + rental.getCar().getModel();
        carRentalBot.sendMessage(message);
    }

    @Override
    public void sendMessageAboutOverdueRental(Rental rental) {
        String message = "It was overdue the rental:"
                + "\nid: " + rental.getId()
                + "\nrental date: " + rental.getRentalDate().toString()
                + "\nreturn date: " + rental.getReturnDate().toString()
                + "\nactual return date: " + rental.getActualReturnDate().toString()
                + "\ncar: "
                + "\n brand: " + rental.getCar().getBrand()
                + "\n model: " + rental.getCar().getModel();
        carRentalBot.sendMessage(message);
    }

    @Override
    public void sendMessageAboutSuccessfulPayment(Payment payment) {
        String message = "It was successfully paid for the car:"
                + "\nid: " + payment.getRental().getCar().getId()
                + "\nbrand: " + payment.getRental().getCar().getBrand()
                + "\nmodel: " + payment.getRental().getCar().getModel()
                + "\npaid: " + payment.getAmountToPay();
        carRentalBot.sendMessage(message);
    }

    @Override
    public void sendMessageAboutCancelledPayment(Payment payment) {
        String message = "It was cancelled the payment:"
                + "\nid: " + payment.getId()
                + "\nstatus: " + payment.getStatus().name()
                + "\nhad to be paid: " + payment.getAmountToPay();
        carRentalBot.sendMessage(message);
    }
}
