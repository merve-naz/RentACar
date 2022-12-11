package com.kodlamaio.filterService.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "search-inventory")//mango db kullandıgım için.(@Table => ilişkisel veri tabanlarında)
public class CarFilter {

	@Id
	private String id;
	@Field(name = "carId")//@Column =>ilişkisel veri tabanlarında   
	private String carId;//araba ıd
	
	@Field(name = "modelId")
	private String modelId;//model ıd
	
	@Field(name = "brandId")
	private String brandId;//brand ıd
	
	@Field(name = "modelName")
	private String modelName;//model adı
	
	@Field(name = "brandName")
	private String brandName;//brand adı
	
	@Field(name = "dailyPrice")
	private double dailyPrice;
	
	@Field(name = "modelYear")
	private int modelYear;
	
	@Field(name = "plate")
	private String plate;
	
	@Field(name = "state")
	private int state;
}
