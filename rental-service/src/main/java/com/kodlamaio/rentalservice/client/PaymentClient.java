package com.kodlamaio.rentalservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kodlamaio.common.dataAccess.CreateRentalPaymentRequest;
import com.kodlamaio.common.utilities.results.Result;

import feign.Headers;

@FeignClient(value = "paymentclient", url = "http://localhost:9010")
public interface PaymentClient {
	@RequestMapping(method = RequestMethod.POST, value = "/payment/api/payments/check")
	@Headers(value = "Content-Type: application/json")
	Result checkIfPaymentSuccessful(@RequestBody CreateRentalPaymentRequest request);
}
