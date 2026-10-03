package services;

import java.util.Map;

import exception.CustomException;
import models.Bicycle;
import singleton.VehicleRegistry;
import utils.Validation;

public class BicycleImplementation extends VehicleAbstract {

	public static final String[] SUSPENSIONS = {"con", "senza"};
	
	@Override
	public void add(Map<String, String> parameters) {
		Bicycle bicycle = new Bicycle();
		VehicleAbstract.fillProperties(bicycle, "bici", parameters);
		bicycle.setFoldable(Validation.getBool(parameters, "pieghevole"));
		bicycle.setNumberGears(Validation.getIntInRange(parameters, "marce", 1, 100));
		bicycle.setTypeSuspensions(Validation.getOneOf(parameters, "sospensione", SUSPENSIONS));
		
		if (bicycle.getNumberWheels() != 2)
			throw new CustomException("A bicycle must have 2 wheels");
		
		String fuel = bicycle.getFuelType();
		if (!fuel.equalsIgnoreCase("manuale") && !fuel.equalsIgnoreCase("elettrica"))
			throw new CustomException("A bicycle can only be electric or manual");
		
		VehicleRegistry.getSingleton().add(bicycle, null);
	}
	

}
