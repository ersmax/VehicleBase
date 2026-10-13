package services;

import java.util.List;
import java.util.Map;

import exception.CustomException;
import models.Bicycle;
import singleton.VehicleRegistry;
import utils.Validation;

public class BicycleImplementation extends VehicleAbstract {

	public static final List<String> SUSPENSIONS = List.of("with", "without");
	
	@Override
	public void add(Map<String, String> parameters) {
		Bicycle bicycle = new Bicycle();
		VehicleAbstract.fillProperties(bicycle, "bicycle", parameters);
		bicycle.setFoldable(Validation.getBoolean(parameters, "foldable"));
		bicycle.setNumberGears(Validation.getIntInRange(parameters, "gears", 1, 100));
		bicycle.setTypeSuspensions(Validation.getOneOf(parameters, "suspension", SUSPENSIONS));
		
		if (bicycle.getNumberWheels() != 2)
			throw new CustomException("A bicycle must have 2 wheels");
		
		String fuel = bicycle.getFuelType();
		if (!fuel.equalsIgnoreCase("manual") && !fuel.equalsIgnoreCase("electric"))
			throw new CustomException("A bicycle can only be electric or manual");
		
		VehicleRegistry.getSingleton().addVehicle(bicycle, null);
	}
	

}
