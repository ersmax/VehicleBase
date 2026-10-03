package services;

import java.util.Map;

import exception.CustomException;
import models.Car;
import singleton.VehicleRegistry;
import utils.Validation;

public class CarImplementation extends VehicleAbstract {

	// formats: car AA123AA, motorbike AA12345
	public static final String CAR_PLATE  = "[A-Z]{2}[0-9]{3}[A-Z]{2}";
	
	@Override
	public void add(Map<String, String> parameters) {
		Car car = new Car();
		VehicleAbstract.fillProperties(car, "macchina", parameters);
		car.setPlate(Validation.getPlate(parameters, "targa", CAR_PLATE));
		car.setNumberDoors(Validation.getIntInRange(parameters, "porte", 2, 7));
		car.setCc(Validation.getIntInRange(parameters, "cc", 50, 8000));
		
		if (car.getNumberWheels() < 3)
			throw new CustomException("A car must have at least 3 wheels");
		if (car.getFuelType().equalsIgnoreCase("manuale"))
			throw new CustomException("A car cannot run without fuel");
		
		VehicleRegistry.getSingleton().add(car, car.getPlate());
	}

}
