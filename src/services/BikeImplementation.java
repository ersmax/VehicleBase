package services;

import java.util.Map;

import exception.CustomException;
import models.Bike;
import singleton.VehicleRegistry;
import utils.Validation;

public class BikeImplementation extends VehicleAbstract {

	// formats: car AA123AA, motorbike AA12345
	public static final String BIKE_PLATE = "[A-Z]{2}[0-9]{5}";
	
	@Override
	public void add(Map<String, String> parameters) {
		Bike bike = new Bike();
		VehicleAbstract.fillProperties(bike, "moto", parameters);
		bike.setCc(Validation.getIntInRange(parameters, "cc", 50, 2500));
		bike.setPlate(Validation.getPlate(parameters, "targa", BIKE_PLATE));
		
		if (bike.getNumberWheels() != 2)
			throw new CustomException("A bike must have 2 wheels");
		if (bike.getFuelType().equalsIgnoreCase("manuale"))
			throw new CustomException("A bike cannot run without fuel");
		
		VehicleRegistry.getSingleton().add(bike, bike.getPlate());
	}

}
