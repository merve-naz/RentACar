package com.kodlamaio.rentalservice.client;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kodlamaio.common.dataAccess.GetCarResponse;
import com.kodlamaio.common.utilities.results.Result;

import feign.Headers;

@FeignClient(value = "carclient", url = "http://localhost:9010")//Feign anotasyonu   ile web servis istemcileri  yazabiliyoruz .=>main'e @EnableFeignClients

public interface CarClient {//rental  servis , stock servis ile iletişime geçmesi gerekecek.
	//Cunku Rental servis arabanın state durumunu sorgulayıp ona göre kiraya verecek.
	//Burada iletişim  sekron bir iletişim olmalı.(cunku stock'dan cevap dönmeden  rental  servis çalısmamalı)  istekte bulunacak Rental oldugu içinde bu classın Rental class'da bulunması gerekrdi. 

	
	
	//web servis istemcisi oldugu içinde spring Web anotasyonları kullanılacaktır.
	@RequestMapping(method = RequestMethod.GET, value = "/stock/api/cars/checkCarAvailable/{id}")//stock'daki  kullanmak istedigim api degeri olmalı 
	@Headers(value = "Content-Type: application/json")
	
	Result checkCarAvailable(@PathVariable String id); //böyle bir istek gelirse bu fonksiyonu çalıştır.

	@RequestMapping(method = RequestMethod.GET, value = "/stock/api/cars/getCarResponse/{id}")
	GetCarResponse getCarResponse(@PathVariable String id);//
	//rental carId  STOCK'A göndererek  araç bilgilerini alıyoruz.
}
