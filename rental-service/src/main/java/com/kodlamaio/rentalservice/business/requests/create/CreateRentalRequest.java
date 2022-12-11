package com.kodlamaio.rentalservice.business.requests.create;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.kodlamaio.common.dataAccess.CreatePaymentRequestDAL;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRentalRequest {//arac kiralamak istediği zaman

	@NotBlank
	@NotNull
	private String carId;//kİRALAMAK İSTEDİGİN CARID
	@NotNull
	@Min(value = 1)
	private int rentedForDays;//KAÇ GÜN KİRALAYACAGIN
	@NotNull
	@Min(value = 0)
	private double dailyPrice;//GUNLUK FİYAT
	private CreatePaymentRequestDAL paymentRequest;//ödeme bilgilerini
	//CreatePaymentRequestDAL =>odeme  bilgileri hem  payment'da hm de tental istegi olustururken kullanacagımız için boyle bir class gerekliydi.

}
