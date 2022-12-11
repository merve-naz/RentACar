package com.kodlamaio.inventoryService.kafka.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.kodlamaio.common.events.inventories.cars.rentals.CarRentalCreatedEvent;
import com.kodlamaio.common.events.inventories.cars.rentals.CarRentalDeletedEvent;
import com.kodlamaio.common.events.inventories.cars.rentals.CarRentalUpdatedEvent;
import com.kodlamaio.common.events.rentals.RentalCreatedEvent;
import com.kodlamaio.common.events.rentals.RentalDeletedEvent;
import com.kodlamaio.common.events.rentals.RentalUpdatedEvent;
import com.kodlamaio.inventoryService.business.abstracts.CarService;
import com.kodlamaio.inventoryService.kafka.producers.InventoryProducer;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RentalConsumer {//rental sonucu uretilen eventler tüketilecek ve invoice servise yeni bir event üretilecek.

	private static final Logger LOGGER = LoggerFactory.getLogger(RentalConsumer.class);
	private final CarService carService;
	private final InventoryProducer producer;//

	@KafkaListener(topics = "rental-created", groupId = "rental-create")//rental-created olayı gelince bu çalıstırılacak
	
	
	public void consume(RentalCreatedEvent event) {
		LOGGER.info(String.format("Order event received in stock service => %s", event.toString()));
		carService.updateCarState(event.getCarId(), 3); // 1-available 2-under maintenance 3-rented//arabanın state güncelle(3)
		LOGGER.info(event.getCarId() + " state changed");
		
		CarRentalCreatedEvent carRentalCreatedEvent = new CarRentalCreatedEvent();
		carRentalCreatedEvent.setCarId(event.getCarId());
		carRentalCreatedEvent.setMessage("Car rented!");
		producer.sendMessage(carRentalCreatedEvent);//kiralama tablosuna bir araç eklendiginde rental=>stock servise event attı.Stock'da bu eventi consume ederek aracın hem statüsünü degiştircek hem de 
		//yeni bir event uretecek(produce) bunu da filter' tuketecek.
		LOGGER.info("Car rented!");
	}

	@KafkaListener(topics = "rental-updated", groupId = "rental-update")//update de  bu çalıstırılacak.
	
	public void consume(RentalUpdatedEvent event) {
		
		LOGGER.info(String.format("Order event received in stock service => %s", event.toString()));
		carService.updateCarState(event.getOldCarId(), 1);//eski arabayı state'ni güncelle
		carService.updateCarState(event.getNewCarId(), 3);//yeni arabanın state'ni gunceşşe
		
		CarRentalUpdatedEvent carRentalUpdatedEvent = new CarRentalUpdatedEvent();
		carRentalUpdatedEvent.setNewCarId(event.getNewCarId());//
		carRentalUpdatedEvent.setOldCarId(event.getOldCarId());//
		carRentalUpdatedEvent.setMessage("Car rented state updated!");
		producer.sendMessage(carRentalUpdatedEvent);
		LOGGER.info("Car rented state updated!");
	}

	@KafkaListener(topics = "rental-deleted", groupId = "rental-delete")
	public void consume(RentalDeletedEvent event) {
		carService.updateCarState(event.getCarId(), 1);
		CarRentalDeletedEvent carRentalDeletedEvent = new CarRentalDeletedEvent();
		carRentalDeletedEvent.setCarId(event.getCarId());
		carRentalDeletedEvent.setMessage("Car deleted from rental!");
		producer.sendMessage(carRentalDeletedEvent);
		LOGGER.info("Car deleted from rental!");
	}
}
