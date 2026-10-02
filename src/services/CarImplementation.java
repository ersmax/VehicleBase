package services;

import java.util.Map;

import exception.CustomException;
import models.Car;
import singleton.VehicleRegistry;
import utils.Validation;

public class CarImplementation extends VehicleAbstract {

	@Override
	public void add(Map<String, String> parameters) {
		Car car = new Car();
		fillProperties(car, "macchina", parameters);
		car.setPlate(Validation.getPlate(parameters, "targa", Validation.CAR_PLATE));
		car.setNumberDoors(Validation.getIntInRange(parameters, "porte", 2, 7));
		car.setCc(Validation.getIntInRange(parameters, "cc", 50, 8000));
		
		if (car.getNumberWheels() < 3)
			throw new CustomException("A car must have at least 3 wheels");
		
		if (car.getFuelType().equals("manuale"))
			throw new CustomException("A car cannot run without fuel");
		
		VehicleRegistry.getSingleton().add(car, car.getPlate());
	}

}
