package services;

import java.util.Arrays;
import java.util.Map;

import exception.CustomException;
import models.Vehicles;
import process.StartVehicle;
import process.StringParsing;
import singleton.VehicleRegistry;

public class ListImplementation {

	/**
	 * Dispatch to the correct class type,
	 * and parse the parameters of the vehicle
	 * @param listItem is the single string of items
	 */
	public void addVehicle(String[] listItem) {
		if (listItem.length < StartVehicle.PARAMS) {
			throw new CustomException("Missing vehicle type");
		}
		
		String[] normalizedListItems = Arrays.copyOfRange(listItem, StartVehicle.PARAMS, listItem.length); 
		Map<String, String> params = StringParsing.mapKeyValue(normalizedListItems);

		switch(listItem[StartVehicle.VEHICLE].toLowerCase()) {
			case "car", "macchina":
				new CarImplementation().add(params);
				break;
			case "moto", "bike":
				new BikeImplementation().add(params);
				break;
			case "bicicletta", "bici", "bicycle":
				new BicycleImplementation().add(params);
				break;
			default:
				throw new CustomException("Invalid vehicle type: '" + listItem[StartVehicle.VEHICLE] + "'");
		}
	}

	/**
	 * Print all the vehicles stored in the singleton
	 */
	public void listVehicle() {
		for (Vehicles vehicle : VehicleRegistry.getSingleton().getVehicles()) {
			System.out.println(vehicle);
		}
	}
}
