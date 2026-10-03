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
		if (instance == null)
			instance = new VehicleRegistry();
		return instance;
	}

	/**
	 * Check the plate is unique, assigns an unique id, store the vehicle
	 * @param vehicle 	: vehicle to save
	 * @param plate		: the plate, or null for vehicles without one (bicycles)
	 */
	public void add(Vehicles vehicle, String plate) {		
		// Null plate is for bicycles
		if (plate != null) {	
			Vehicles existing = vehiclesByPlate.get(plate);
			if (existing != null)
				throw new CustomException("Plate " + plate 
						+ " already used by vehicle id: " + existing.getId());
		}	
		vehicle.setId(nextId++);
		vehicles.add(vehicle);
		
		if (plate != null)
			vehiclesByPlate.put(plate, vehicle);
	}

	public List<Vehicles> getVehicles() {
		return Collections.unmodifiableList(vehicles);
	}
}
