package com.kodlamaio.filterService.dataAccess.abstracts;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.kodlamaio.filterService.entities.CarFilter;

public interface CarFilterRepository extends MongoRepository<CarFilter, String> {
	//buyuk ve kucuk harfleri ignore etmesi için

	List<CarFilter> findByBrandNameIgnoreCase(String brandName);

	List<CarFilter> findByModelNameIgnoreCase(String modelName);

	CarFilter findByPlateIgnoreCase(String plate);

	List<CarFilter> findByPlateContainingIgnoreCase(String plate);

	List<CarFilter> findByBrandNameContainingIgnoreCase(String brandName);

	List<CarFilter> findByModelNameContainingIgnoreCase(String modelName);

	List<CarFilter> findByModelYear(int modelYear);

	List<CarFilter> findByModelId(String modelId);

	List<CarFilter> findByBrandId(String brandId);

	List<CarFilter> findByState(int state);

	CarFilter findByCarId(String carId);

	void deleteByCarId(String carId);

	void deleteAllByBrandId(String brandId);

	void deleteAllByModelId(String modelId);
	
	boolean existsByPlate(String plate);
}
