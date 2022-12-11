package com.kodlamaio.invoiceService.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.kodlamaio.common.events.payments.PaymentReceivedEvent;
import com.kodlamaio.common.utilities.mapping.ModelMapperService;
import com.kodlamaio.invoiceService.business.abstracts.InvoiceService;
import com.kodlamaio.invoiceService.entities.Invoice;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RentalConsumer {//rentalden gelen eventi tüketecek(invoice)
	private static final Logger LOGGER = LoggerFactory.getLogger(RentalConsumer.class);
	private final InvoiceService service;//InvoiceService =>
	private final ModelMapperService mapper;// ModelMapperService=>
	// private final CarClient client;

	@KafkaListener(topics = "payment-received", groupId = "payment-receive")
	public void consume(PaymentReceivedEvent event) {
		Invoice invoice = mapper.forRequest().map(event, Invoice.class);
		service.createInvoice(invoice);//odeme olunca yapacagı  şeey =>invoice oluşturma
		LOGGER.info("Invoice created for : {}", event.getFullName());
	}
}
