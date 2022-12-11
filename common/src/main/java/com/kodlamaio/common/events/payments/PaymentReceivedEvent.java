package com.kodlamaio.common.events.payments;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentReceivedEvent {//

	private String carId;//carıd=
	private String fullName;//full ad
	private String modelName;//model adı
	private String brandName;//marka adı
	private int modelYear;//model yili
	private double dailyPrice;//günlük fiyat
	private double totalPrice;//toplam fiyar
	private int rentedForDays;//kaç günlügüne kiralanak
	private LocalDateTime rentedAt;//kiralandıgı zaman
}
