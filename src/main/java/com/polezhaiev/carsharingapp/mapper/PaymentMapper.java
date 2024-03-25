package com.polezhaiev.carsharingapp.mapper;

import com.polezhaiev.carsharingapp.config.MapperConfig;
import com.polezhaiev.carsharingapp.dto.payment.PaymentDto;
import com.polezhaiev.carsharingapp.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(target = "rentalId", source = "rental.id")
    PaymentDto toDto(Payment payment);
}
