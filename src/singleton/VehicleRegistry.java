package singleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exception.CustomException;
import models.Vehicles;

public class VehicleRegistry {

	private static VehicleRegistry instance = null;

	private List<Vehicles> vehicles = new ArrayList<>();
	private Map<String, Vehicles> vehiclesByPlate = new HashMap<>();
	private int nextId = 1;


	private VehicleRegistry() {
		super();
	}

	public static VehicleRegistry getSingleton() {
		if (instance == null) {
			instance = new VehicleRegistry();
		}
		return instance;
	}

	/**
	 * Check the plate is unique, assigns an unique id, store the vehicle
	 * @param vehicle 	: vehicle to save
	 * @param plate		: the plate, or null for vehicles without one (bicycles)
	 */
	public void add(Vehicles vehicle, String plate) {
		String cleanedPlate = null;
		
		// Null plate is for bicycles
		if (plate != null) {	
			cleanedPlate = plate.trim().toUpperCase();
			if (cleanedPlate.isEmpty())
				throw new CustomException("Plate cannot be empty");
		}
		
		
		Vehicles existing = vehiclesByPlate.get(cleanedPlate);
		if (existing != null)
			throw new CustomException("Plate " + cleanedPlate 
									+ " already used by vehicle id: " + existing.getId());
		
		vehicle.setId(nextId++);
		vehicles.add(vehicle);
		
		if (cleanedPlate != null)
			vehiclesByPlate.put(cleanedPlate, vehicle);
	}
	
	/**
	 * Find a vehicle by plate.
	 * @param plate : the plate passed in raw format
	 * @return		: the vehicle whose plate belongs to 
	 */
	public Vehicles findByPlate(String plate) {
		if (plate == null)
			return null;
		return vehiclesByPlate.get(plate.trim().toUpperCase());
	}

	public List<Vehicles> getVehicles() {
		return Collections.unmodifiableList(vehicles);
	}
}
