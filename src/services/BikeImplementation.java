package services;

import java.util.Map;

import exception.CustomException;
import models.Bike;
import singleton.VehicleRegistry;
import utils.Validation;

public class BikeImplementation extends VehicleAbstract {

	@Override
	public void add(Map<String, String> parameters) {
		Bike bike = new Bike();
		fillProperties(bike, "moto", parameters);
		bike.setCc(Validation.getIntInRange(parameters, "cc", 50, 2500));
		bike.setPlate(Validation.getPlate(parameters, "targa", Validation.BIKE_PLATE));
		
		if (bike.getNumberWheels() != 2)
			throw new CustomException("A bike must have 2 wheels");
		
		VehicleRegistry.getSingleton().add(bike, bike.getPlate());
	}

}
