package com.polezhaiev.carsharingapp.dto.car;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CarDetailedInfoDto {
    private Long id;
    private String brand;
    private String model;
    private String typeName;
    private Double dailyFee;
    private int inventory;
}
