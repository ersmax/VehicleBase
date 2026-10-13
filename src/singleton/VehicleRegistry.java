package singleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exception.CustomException;
import models.Bike;
import models.Car;
import models.Vehicles;

public class VehicleRegistry {

	private static VehicleRegistry instance = null;

	private final List<Vehicles> vehicleRegistry = new ArrayList<>();
	private final Map<String, Vehicles> vehiclesByPlate = new HashMap<>();
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
	 * @param plate		: the plate, or null for vehicles without plate (bicycles)
	 */
	public void addVehicle(Vehicles vehicle, String plate) {
		// bike has null plate
		if (plate != null) {
			Vehicles existing = vehiclesByPlate.get(plate);

			if (existing != null) {
				throw new CustomException("Plate " + plate
						+ " already used by vehicle id: " + existing.getId());
			}
		}
		vehicle.setId(nextId++);
		vehicleRegistry.add(vehicle);

		if (plate != null) {
			vehiclesByPlate.put(plate, vehicle);
		}
	}
	
	/**
	 * Replace the registry content with the imported vehicles (old IDs are kept)
	 * @param vehicles	: list of vehicles read
	 */
	public void loadVehiclesFromJson(List<Vehicles> vehicles) {
		vehicleRegistry.clear();
		vehiclesByPlate.clear();
		
		for (Vehicles vehicle : vehicles) {
			vehicleRegistry.add(vehicle);
			String plate = getPlate(vehicle);
			if (plate != null)	vehiclesByPlate.put(plate, vehicle);			
		}
		// nextId after the highest imported ID
		nextId = vehicles.stream()
						 .mapToInt(Vehicles::getId)
						 .max().orElse(0) + 1;
	}
	
	/**
	 * Helper function to get a plate from vehicle
	 * @param vehicle
	 * @return
	 */
	private String getPlate(Vehicles vehicle) {
		if (vehicle instanceof Car car)		return car.getPlate();
		if (vehicle instanceof Bike bike)	return bike.getPlate();
		return null;	// bicycles
	}

	public List<Vehicles> getVehicleRegistry() {
		return Collections.unmodifiableList(vehicleRegistry);
	}

	public Vehicles findById(int id) {
		return vehicleRegistry.stream()
							  .filter(vehicle -> vehicle.getId() == id)
							  .findFirst()
							  .orElse(null);
	}

	public Vehicles findByPlate(String plate) {
		return vehiclesByPlate.get(plate);
	}

	public void removeVehicle(Vehicles vehicle) {
		vehicleRegistry.remove(vehicle);
		if (getPlate(vehicle) != null)
			vehiclesByPlate.remove(getPlate(vehicle));
		
	}

}
