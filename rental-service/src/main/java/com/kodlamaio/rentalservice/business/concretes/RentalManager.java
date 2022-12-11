package com.kodlamaio.rentalservice.business.concretes;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kodlamaio.common.dataAccess.CreateRentalPaymentRequest;
import com.kodlamaio.common.events.payments.PaymentReceivedEvent;
import com.kodlamaio.common.events.rentals.RentalCreatedEvent;
import com.kodlamaio.common.events.rentals.RentalDeletedEvent;
import com.kodlamaio.common.events.rentals.RentalUpdatedEvent;
import com.kodlamaio.common.utilities.exceptions.BusinessException;
import com.kodlamaio.common.utilities.mapping.ModelMapperService;
import com.kodlamaio.common.utilities.results.DataResult;
import com.kodlamaio.common.utilities.results.Result;
import com.kodlamaio.common.utilities.results.SuccessDataResult;
import com.kodlamaio.common.utilities.results.SuccessResult;
import com.kodlamaio.rentalservice.business.abstracts.RentalService;
import com.kodlamaio.rentalservice.business.constants.Messages;
import com.kodlamaio.rentalservice.business.requests.create.CreateRentalRequest;
import com.kodlamaio.rentalservice.business.requests.update.UpdateRentalRequest;
import com.kodlamaio.rentalservice.business.responses.create.CreateRentalResponse;
import com.kodlamaio.rentalservice.business.responses.get.GetAllRentalsResponse;
import com.kodlamaio.rentalservice.business.responses.get.GetRentalResponse;
import com.kodlamaio.rentalservice.business.responses.update.UpdateRentalResponse;
import com.kodlamaio.rentalservice.client.CarClient;
import com.kodlamaio.rentalservice.client.PaymentClient;
import com.kodlamaio.rentalservice.dataAccess.RentalRepository;
import com.kodlamaio.rentalservice.entities.Rental;
import com.kodlamaio.rentalservice.kafka.RentalProducer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RentalManager implements RentalService {

	private RentalRepository rentalRepository;//kiralanan araç bilgilerini içerir.(aracid,kac gün,gunluk fyat,baslangıc tarihi,total fiyat gibi)
	private ModelMapperService modelMapperService;//mapper yapmak için.
	private RentalProducer rentalProducer;
	private CarClient carClient;//CarClient kullanarak arabanın musailt olup olmadıgını sekron ıletişim
	private PaymentClient paymentClient;//PaymentClient kullanarak Payment Servis ile SEKRON İLETİŞİM KURUYORUZ

	@Override
	public DataResult<List<GetAllRentalsResponse>> getAll() {
		List<Rental> rentals = rentalRepository.findAll();//rentalRepository'deki bütün araçları buluyor
		List<GetAllRentalsResponse> responses = rentals.stream()
				.map(rental -> modelMapperService.forResponse().map(rental, GetAllRentalsResponse.class)).toList();
		return new SuccessDataResult<List<GetAllRentalsResponse>>(responses, Messages.RentalListed);

	}

	@Override
	public DataResult<CreateRentalResponse> add(CreateRentalRequest createRentalRequest) {//araç kiralama bilgilerini ekleme
		
		carClient.checkCarAvailable(createRentalRequest.getCarId());
		//ilk olarak  arabanın  olup olmadıgını anlamak ve  arabaınn musait olup olmadıgını anlamak için  stock servisi  ile letişime geçmeliydik
		//bunun içinde olusturdugumuz  carCliet kullanarak  stock'un belirtgimiz apisine  istek atıp o fonksıyonu kullanmayı sagladık.
		
		
		checkIfRentalExistsByCarId(createRentalRequest.getCarId());//zaten yukarıda bunu yapıyoruz bunu neden gerek  olsun??


		//araç var ve musait o zaman gelen istegi Rental nesnesine çevir
		Rental rental = modelMapperService.forRequest().map(createRentalRequest, Rental.class);//
		rental.setId(UUID.randomUUID().toString());
		double totalPrice = createRentalRequest.getDailyPrice() * createRentalRequest.getRentedForDays();
		rental.setTotalPrice(totalPrice);

	    
		CreateRentalPaymentRequest paymentRequest = new CreateRentalPaymentRequest();
		modelMapperService.forRequest().map(createRentalRequest.getPaymentRequest(), paymentRequest);//payment bilgilerini alıyoruz.
		
		paymentRequest.setPrice(totalPrice);
		
		//payment Request istegi olusturduk
		
		paymentClient.checkIfPaymentSuccessful(paymentRequest);// öyle bir kart var mı ve  varsa paran yetiyor mu?(sekron  iletişim => payment ile)

		this.rentalRepository.save(rental);//aracı kirala
		
		System.out.println("arabayı kiraladım şimdi diger servislere haber vericem");

		rentalCreatedEvent(rental);//araç  kiralandıktan sonra bir event oluştur.=>stock tüketecek.(aracın state'ni degiştirecek)
		paymentReceivedEvent(createRentalRequest, rental, totalPrice);//payment olustuktan sonra bir event oluştur.Invoice bunu tuketecek(fatura ekleme yapacak)

		CreateRentalResponse response = modelMapperService.forResponse().map(rental, CreateRentalResponse.class);//
		
		
		return new SuccessDataResult<CreateRentalResponse>(response, Messages.RentalCreated);

	}

	@Override
	public DataResult<UpdateRentalResponse> update(UpdateRentalRequest updateRentalRequest) {
		
		carClient.checkCarAvailable(updateRentalRequest.getCarId());//yeni arabanın carId.ve bu arabanın  musait olup olmaması.
		checkIfRentalNotExistsById(updateRentalRequest.getId());//eski araba bilgilerini bulabilmek için   getId   alıyoruz ve acaba dogru ıd yazdık diye rentalRepostory'den kontrol ediliyor.

		Rental rental = modelMapperService.forRequest().map(updateRentalRequest, Rental.class);
		rentalUpdatedEvent(updateRentalRequest.getId(), rental);//event olusturup kafkaya atcaz ve bunu stock tuketecek

		UpdateRentalResponse response = modelMapperService.forResponse().map(rental, UpdateRentalResponse.class);
		return new SuccessDataResult<UpdateRentalResponse>(response, Messages.RentalUpdated);
	}

	@Override
	public DataResult<GetRentalResponse> getById(String id) {
		checkIfRentalNotExistsById(id);
		Rental rental = this.rentalRepository.findById(id).get();
		GetRentalResponse response = modelMapperService.forResponse().map(rental, GetRentalResponse.class);
		return new SuccessDataResult<GetRentalResponse>(response);
	}

	@Override
	public Result deleteById(String id) {
		
		checkIfRentalNotExistsById(id);
		rentalDeletedEvent(id);
		RentalDeletedEvent event = new RentalDeletedEvent();
		event.setCarId(rentalRepository.findById(id).orElseThrow().getCarId());
		event.setMessage("Rental Deleted");
		rentalProducer.sendMessage(event);
		this.rentalRepository.deleteById(id);

		return new SuccessResult(Messages.RentalDeleted);

	}

	private void checkIfRentalExistsByCarId(String id) {
		if (this.rentalRepository.findByCarId(id).isPresent()) {
			throw new BusinessException("RENTAL.EXISTS");
		}
	}

	private void checkIfRentalNotExistsById(String id) {
		if (!this.rentalRepository.findById(id).isPresent()) {
			throw new BusinessException("RENTAL.NOT.EXISTS");
		}
	}

	private void rentalCreatedEvent(Rental rental) {//kiralama olunca bir event olustur.
		RentalCreatedEvent rentalCreatedEvent = new RentalCreatedEvent();
		rentalCreatedEvent.setCarId(rental.getCarId());
		rentalCreatedEvent.setMessage("Rental Created");
		rentalProducer.sendMessage(rentalCreatedEvent);
	}

	private void rentalUpdatedEvent(String id, Rental rental) {
		RentalUpdatedEvent rentalUpdatedEvent = new RentalUpdatedEvent();
		rental.setId(id);
		rentalUpdatedEvent.setOldCarId(rentalRepository.findById(id).orElseThrow().getCarId());
		rentalRepository.save(rental);//rental veri tabanında duzeltmesini yaptin
		rentalUpdatedEvent.setNewCarId(rental.getCarId());
		rentalUpdatedEvent.setMessage("Rental Updated");
		rentalProducer.sendMessage(rentalUpdatedEvent);//mesajını attın(stock)
	}

	private void rentalDeletedEvent(String id) {
		RentalDeletedEvent event = new RentalDeletedEvent();
		event.setCarId(rentalRepository.findById(id).orElseThrow().getCarId());
		event.setMessage("Rental Deleted");
		rentalProducer.sendMessage(event);
	}

	private void paymentReceivedEvent(CreateRentalRequest request, Rental rental, double totalPrice) {//Invoice olusturmak ıcın event olustur
	
		
		PaymentReceivedEvent paymentReceivedEvent = new PaymentReceivedEvent();
		paymentReceivedEvent.setCarId(rental.getCarId());//carID=>request.getCarId;
		paymentReceivedEvent.setFullName(request.getPaymentRequest().getFullName());//kiralayanın adı
		paymentReceivedEvent.setDailyPrice(request.getDailyPrice());//kiralayanın gunlük ücret
		paymentReceivedEvent.setTotalPrice(totalPrice);
		paymentReceivedEvent.setRentedForDays(request.getRentedForDays());
		paymentReceivedEvent.setRentedAt(rental.getDateStarted());
	
		paymentReceivedEvent.setBrandName(carClient.getCarResponse(rental.getCarId()).getBrandName());//arabanın marka
		paymentReceivedEvent.setModelName(carClient.getCarResponse(rental.getCarId()).getModelName());//araba model
		paymentReceivedEvent.setModelYear(carClient.getCarResponse(rental.getCarId()).getModelYear());//araba yılı
		
		rentalProducer.sendMessage(paymentReceivedEvent);
		
	}

}
