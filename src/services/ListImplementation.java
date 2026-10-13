package services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import models.Bicycle;
import models.Bike;
import models.Car;
import models.Vehicles;
import process.StartVehicle;
import process.StringParsing;
import singleton.VehicleRegistry;
import utils.FileManager;
import utils.JsonTransfer;
import utils.Validation;
import vehicles.MainVehicles;

@Slf4j
public class ListImplementation {

	private static final List<Vehicles> vehicleRegistry = VehicleRegistry.getSingleton().getVehicleRegistry();
	
	// filters accepted by "list"
	private static final List<String> KNOWN_FILTERS = List.of(
	        "type", "brand", "model", "color", "fuel", "category", "year",
	        "plate", "cc", "doors", "gears", "suspension", "foldable");
	
	/**
	 * Dispatch to the correct class type,
	 * and parse the parameters of the vehicle
	 * @param parameterList : the parameters list for a row representing the vehicle
	 */
	public void addVehicle(String[] parameterList) {
		if (parameterList.length < StartVehicle.PARAMETERS) {
			throw new CustomException("Missing vehicle type");
		}
		
		String[] parameters = Arrays.stream(parameterList)
									.skip(StartVehicle.PARAMETERS)
									.toArray(String[]::new);
		Map<String, String> keysValues = StringParsing.mapKeyValue(parameters);

		switch(parameterList[StartVehicle.VEHICLE_TYPE].toLowerCase()) {
			case "car":
				new CarImplementation().add(keysValues);
				break;
			case "bike":
				new BikeImplementation().add(keysValues);
				break;
			case "bicycle":
				new BicycleImplementation().add(keysValues);
				break;
			default:
				throw new CustomException("Invalid vehicle type: '" + parameterList[StartVehicle.VEHICLE_TYPE] + "'");
		}
	}
	
	/**
	 * Delete a vehicle by plate or by id, whatever its type
	 * e.g. delete,plate=EL234GX  |  delete,id=4
	 * @param parameterList : "delete" followed by plate=... or id=...
	 */
	public void deleteVehicle(String[] parameterList) {
		
		String[] parameters = Arrays.stream(parameterList)
									.skip(StartVehicle.VEHICLE_TYPE)
									.toArray(String[]::new);
		Map<String, String> keyValues = StringParsing.mapKeyValue(parameters);
		
		if (keyValues.isEmpty())
			throw new CustomException("Missing parameter: id or plate");
		
		
		VehicleRegistry registry = VehicleRegistry.getSingleton();
		Vehicles vehicleToBeDeleted = keyValues.containsKey("plate")
									? registry.findByPlate(keyValues.get("plate").toUpperCase())
									: registry.findById(Validation.getInt(keyValues, "id"));
		
		if (vehicleToBeDeleted == null)
			throw new CustomException("Vehicle not found: " + parameters);
		
		registry.removeVehicle(vehicleToBeDeleted);
		log.info("Deleted vehicle id = {} ({})", vehicleToBeDeleted.getId(), vehicleToBeDeleted.getClass().getSimpleName());	
	}
	
	
	
	/**
	 * Pass the parameters of the vehicle to an external JSON
	 * @param parameterList
	 * @throws IOException
	 */
	public void exportVehicles(String[] parameterList) throws IOException {
		JsonTransfer.exportVehicles();
	}
	
	/**
	 * Import the parameters of a Vehicle from a JSON to memory
	 * @param parameterList
	 * @throws IOException
	 */
	public void importVehicles(String[] parameterList) throws IOException {
		JsonTransfer.importVehicles();
	}

	/**
	 * Print all vehicles, or only those matching the filters
	 * e.g. list  |  list,color=white  |  list,color=white,brand=fiat
	 * @throws IOException 
	 */
	public void listVehicles(String[] parameterList) throws IOException {
		String[] rawFilters = Arrays.stream(parameterList)
									.skip(1)
									.toArray(String[]::new);
		Map<String, String> filters = StringParsing.mapKeyValue(rawFilters);
		List<String> vehicles = (filters.isEmpty())
								? storeAll()
								: storeFiltered(filters);
		saveVehicles(vehicles, parameterList);
	}

	/**
	 * Save in the external file the vehicles that satisfy the filter
	 * @param vehicleList	: vehicles that satisfy the filter
	 * @param parameterList	: the only attribute to filter the list
	 * @throws IOException	: if failed to write on a file
	 */
	private void saveVehicles(List<String> vehicleList, String[] parameterList) throws IOException {
		String title = String.join(",", parameterList);
		
		List<String> output = new ArrayList<>();
		output.add("### " + title + " (" + vehicleList.size() + " found) ###");
		output.addAll(vehicleList);
		output.add("\n");
		
		FileManager.writeFile(MainVehicles.OUTPUT_FILE, output, true);
		log.info("Number of written record(s): {}", vehicleList.size());
	}
	
	/**
	 * @return	: the full registry of vehicles
	 */
	private List<String> storeAll() {
		return vehicleRegistry.stream()
							  .map(Vehicles::toString)
							  .toList();
	}
	
	/**
	 * Save and return in a list those vehicles that have the attributes 
	 * (and values) stated in the filters
	 * @param filters
	 * @return
	 */
private List<String> storeFiltered(Map<String, String> filters) {
		
		for (String key : filters.keySet())
		    if (!KNOWN_FILTERS.contains(key))
		        throw new CustomException("Unknown filter: " + key + ". Allowed: " + KNOWN_FILTERS);
		
		// given any vehicle, answer yes. Initialization
		Predicate<Vehicles> predicate = vehicle -> true;		
		
		// Vehicle
		if (filters.get("type") != null)
			predicate = predicate.and(vehicle -> vehicle.getVehicleType().equalsIgnoreCase(filters.get("type")));
		if (filters.get("brand") != null)
			predicate = predicate.and(vehicle -> vehicle.getBrand().equalsIgnoreCase(filters.get("brand")));
		if (filters.get("model") != null)
			predicate = predicate.and(vehicle -> vehicle.getModel().equalsIgnoreCase(filters.get("model")));
		if (filters.get("color") != null)
			predicate = predicate.and(vehicle -> vehicle.getColor().equalsIgnoreCase(filters.get("color")));
		if (filters.get("fuel") != null)
			predicate = predicate.and(vehicle -> vehicle.getFuelType().equalsIgnoreCase(filters.get("fuel")));
		if (filters.get("category") != null)
			predicate = predicate.and(vehicle -> vehicle.getCategory().equalsIgnoreCase(filters.get("category")));
		if (filters.get("year") != null)
			predicate = predicate.and(vehicle -> String.valueOf(vehicle.getProductionYear()).equals(filters.get("year")));
		
		// Car + Bike
		if (filters.get("plate") != null)
			predicate = predicate.and(vehicle -> (vehicle instanceof Car car && 
												  car.getPlate().equalsIgnoreCase(filters.get("plate")))
											  || (vehicle instanceof Bike bike && 
												  bike.getPlate().equalsIgnoreCase(filters.get("plate"))));
		if (filters.get("cc") != null) 
	        predicate = predicate.and(vehicle -> (vehicle instanceof Car car   && 
	        									  String.valueOf(car.getCc()).equals(filters.get("cc")))
	        								  || (vehicle instanceof Bike bike && 
	        									  String.valueOf(bike.getCc()).equals(filters.get("cc"))));
	    // Car only
	    if (filters.get("doors") != null) 
	        predicate = predicate.and(vehicle -> vehicle instanceof Car car && 
	        									 String.valueOf(car.getNumberDoors()).equals(filters.get("doors")));
	    // Bicycle only
	    if (filters.get("gears") != null) 
	        predicate = predicate.and(vehicle -> vehicle instanceof Bicycle bicycle && 
	        									 String.valueOf(bicycle.getNumberGears()).equals(filters.get("gears")));
	    if (filters.get("suspension") != null)
	        predicate = predicate.and(vehicle -> vehicle instanceof Bicycle bicycle &&
	                                             bicycle.getTypeSuspensions().equalsIgnoreCase(filters.get("suspension")));
	    if (filters.get("foldable") != null)
	        predicate = predicate.and(vehicle -> vehicle instanceof Bicycle bicycle &&
	                                             bicycle.getFoldable() == filters.get("foldable").equalsIgnoreCase("yes"));	    
		
	    return vehicleRegistry.stream()
							  .filter(predicate)
							  .map(Vehicles::toString)
							  .toList();
	}

	
}
