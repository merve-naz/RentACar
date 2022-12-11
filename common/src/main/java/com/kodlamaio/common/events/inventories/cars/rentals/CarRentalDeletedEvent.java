package com.kodlamaio.common.events.inventories.cars.rentals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarRentalDeletedEvent {
	private String message;
    private String carId;
}
