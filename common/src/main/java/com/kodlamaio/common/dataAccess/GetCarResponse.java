package com.kodlamaio.common.dataAccess;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCarResponse {

	private String id;//araba idsi
	private double dailyPrice;//günlük fyat
	private int modelYear;//model
	private String plate;//plaka
	private int state;//durum
	private String brandName;//marka adı
	private String modelName;//model adı

}
