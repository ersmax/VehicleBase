package services;

import java.time.Year;
import java.util.Map;

import models.Vehicles;
import utils.Validation;

public abstract class VehicleAbstract {
		
	public static final String[] FUEL_TYPES  = {"benzina", "diesel", "gpl", "metano", "elettrica", "ibrida", "manuale"};
	public static final String[] CATEGORIES  = {"strada", "citycar", "utilitaria", "berlina", "suv", "sportiva", "fuoristrada", "corsa"};
	public static final String[] COLORS      = {"bianco", "nero", "grigio", "argento", "rosso", "blu", "verde", "giallo", "arancione", "marrone"};
		
	protected static void fillProperties(Vehicles vehicle, String type, Map<String, String> parameters) {
		
		vehicle.setVehicleType(type); 
		vehicle.setNumberWheels(Validation.getInt(parameters, "ruote"));
		vehicle.setFuelType(Validation.getOneOf(parameters, "alim", FUEL_TYPES));
		vehicle.setCategory(Validation.getOneOf(parameters, "cat", CATEGORIES));
		vehicle.setColor(Validation.getOneOf(parameters, "colore", COLORS));
		vehicle.setBrand(Validation.getString(parameters, "marca"));
		vehicle.setProductionYear(Validation.getIntInRange(parameters, "anno", 1886, Year.now().getValue()));
		vehicle.setModel(Validation.getString(parameters, "modello"));
	}
	
	/**
	 * Add custom parameters to the specific vehicle type.
	 * @param parameters	: the pair key value of parameters
	 */
	public abstract void add(Map<String, String> parameters);
	
}
