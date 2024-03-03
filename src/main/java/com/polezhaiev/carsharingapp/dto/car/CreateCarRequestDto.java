package com.polezhaiev.carsharingapp.dto.car;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateCarRequestDto {
    @NotNull
    private String brand;
    @NotNull
    private String model;
    @NotNull
    private String typeName;
    @Min(0)
    private Double dailyFee;
    @Min(0)
    private int inventory;
}
