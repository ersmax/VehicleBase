package services;

import java.util.Map;

import models.Vehicles;
import utils.Validation;

public abstract class VehicleAbstract {
	
	
	protected static void fillProperties(Vehicles vehicle, String type, Map<String, String> parameters) {
		
		vehicle.setVehicleType(type); 
		vehicle.setNumberWheels(Validation.getInt(parameters, "ruote"));
		vehicle.setFuelType(Validation.getOneOf(parameters, "alim", Validation.FUEL_TYPES));
		vehicle.setCategory(Validation.getOneOf(parameters, "cat", Validation.CATEGORIES));
		vehicle.setColor(Validation.getOneOf(parameters, "colore", Validation.COLORS));
		vehicle.setBrand(Validation.getString(parameters, "marca"));
		vehicle.setProductionYear(Validation.getYear(parameters, "anno"));
		vehicle.setModel(Validation.getString(parameters, "modello"));
	}
	
	public abstract void add(Map<String, String> parameters);
	
}
