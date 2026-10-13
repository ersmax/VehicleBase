package services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
	        "plate", "cc", "doors", "gears","suspension","foldable");
	
	private static final String PATH_SERVICES = "services";

	// cache: map the vehicle type to the service instance
	private static final Map<String, VehicleAbstract> vehicleCache = new HashMap<>();
	
	/**
	 * Add a vehicle: parse the parameters and send them to the proper vehicle type
	 * e.g. add,car,brand=fiat,...
	 * @param parameterList : the parameters list for a row representing the vehicle
	 */
	public void addVehicle(String[] parameterList) {

		if (parameterList.length < StartVehicle.PARAMETERS) 
			throw new CustomException("Missing vehicle type");

		String[] parameters = Arrays.stream(parameterList)
								    .skip(StartVehicle.PARAMETERS)
								    .toArray(String[]::new);

		Map<String, String> keysValues =  StringParsing.mapKeyValue(parameters);
		String vehicle = parameterList[StartVehicle.VEHICLE_TYPE].toLowerCase();

		// dynamic: load the service by vehicle type (cached), then add (polymorphism)
		VehicleAbstract vehicleImplementation = loadService(vehicle);
		vehicleImplementation.add(keysValues);
	}

	/**
	 * Finds the right class from the vehicle type in the row and returns its (cached) instance
	 * (e.g. "car" -> CarImplementation | "bike" -> BikeImplementation)
	 * @param name	: type of vehicle
	 * @return
	 */
	private VehicleAbstract loadService(String vehicleName) {
		if (vehicleCache.containsKey(vehicleName)) 
			return vehicleCache.get(vehicleName);
		
		String className = Validation.buildClassName(vehicleName, "implementation");
		try {
			// get the class (e.g. CarImplementation)
			Class<?> implementationClass = Class.forName(PATH_SERVICES + "." + className);
			// get the instance
			VehicleAbstract object = (VehicleAbstract) implementationClass.getDeclaredConstructor().newInstance();
			// save the type of vehicle (e.g. car) in cache for future faster retrieval
			vehicleCache.put(vehicleName, object);
			log.debug("New instance of '{}' created and cached", className);
			return object;
		} catch (Exception e) {
			throw new CustomException("Vehicle type not supported: " + vehicleName);
		}
	}
	
	/**
	 * Delete a vehicle by plate or by id, whatever its type
	 * e.g. delete,plate=EL234GX  |  delete,id=4
	 * @param parameterList : "delete" followed by plate=... or id=...
	 */
	public void deleteVehicle(String[] parameterList) {
		String[] parameters = Arrays.stream(parameterList)
									.skip(1)					// drop "delete"
									.toArray(String[]::new);
		Map<String, String> keysValues = StringParsing.mapKeyValue(parameters);

		if (keysValues.isEmpty())
			throw new CustomException("Missing parameter: plate or id");

		VehicleRegistry registry = VehicleRegistry.getSingleton();
		Vehicles vehicleToBeDeleted = keysValues.containsKey("plate")
				? registry.findByPlate(keysValues.get("plate").toUpperCase())
				: registry.findById(Validation.getInt(keysValues, "id"));

		if (vehicleToBeDeleted == null)
			throw new CustomException("Vehicle not found: " + keysValues);

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
	 */
	public void listVehicles(String[] parameterList) throws IOException {
		String[] rawFilters = Arrays.stream(parameterList)		
								 .skip(1)					// everything after "list" is filter
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


//old method
//public void listVehicles() throws IOException {
//
//	List<String> vehicleList = new ArrayList<>();
//	for (Vehicles vehicle : VehicleRegistry.getSingleton().getVehicleRegistry())
//		vehicleList.add(vehicle.toString());
//
//	FileManager.writeFile(MainVehicles.OUTPUT_FILE, vehicleList, false);
//	System.out.println("List updated");
//}



///**
// * Store the vehicles that satisfy the condition
// * @param parameterList		: no parameter/one parameter : list all / vehicle's attribute
// * @throws IOException		: if file cannot be written
// * @throws CustomException	: if the filter is invalid
// */
//public void listVehiclesOld(String[] parameterList) throws IOException {
//
//	if (parameterList.length > 2) {
//		throw new CustomException("Only one filter allowed");
//	}
//
//	List<Vehicles> registry = VehicleRegistry.getSingleton().getVehicleRegistry();
//	List<String> vehicleList = new ArrayList<>();
//
//	if (parameterList.length == 1) {
//		vehicleList	= registry.stream()
//							  .map(Vehicles::toString)	// .map(vehicle -> vehicle.toString())
//							  .toList();
//	} else if (parameterList.length == 2) {
//		String[] keyValue = StringParsing.splitKeyValue(parameterList[1]);
//		vehicleList	= registry.stream()
//				  			  .filter(vehicle -> hasAttribute(vehicle, keyValue[0], keyValue[1]))
//							  .map(Vehicles::toString)
//				  			  .toList();
//	}
//	saveVehicles(vehicleList, parameterList);
//}


///**
// * Check whether the vehicle satisfy the filter: filterAttribute=filterValue
// * @param vehicle	: the object we have to check
// * @param attribute	: the filter property of vehicle (e.g. color)
// * @param value		: the corresponding value of the filter (e.g. red)
// * @return
// */
//private boolean hasAttribute(Vehicles vehicle, String filterAttribute, String filterValue) {
//	String actualValue = getAttributeValue(vehicle, filterAttribute);
//	return actualValue != null && actualValue.equalsIgnoreCase(filterValue);
//}
//
///**
// * Return the value associated with the corresponding vehicle attribute
// * @param vehicle
// * @param attribute
// * @return
// */
//private String getAttributeValue(Vehicles vehicle, String attribute) {
//	switch (attribute) {
//		// Vehicles
//        case "colore":  return vehicle.getColor();
//        case "marca":   return vehicle.getBrand();
//        case "modello": return vehicle.getModel();
//        case "alim":    return vehicle.getFuelType();
//        case "cat":     return vehicle.getCategory();
//        case "tipo":    return vehicle.getVehicleType();
//        case "anno":    return String.valueOf(vehicle.getProductionYear());
//        case "ruote":   return String.valueOf(vehicle.getNumberWheels());
//
//        // Car & Bike
//        case "targa":
//            if (vehicle instanceof Car car) 	return car.getPlate();
//            if (vehicle instanceof Bike bike) 	return bike.getPlate();
//            return null;
//        case "cc":
//            if (vehicle instanceof Car car) 	return String.valueOf(car.getCc());
//            if (vehicle instanceof Bike bike) 	return String.valueOf(bike.getCc());
//			return null;
//
//        // Car
//        case "porte":
//            if (vehicle instanceof Car car) 	return String.valueOf(car.getNumberDoors());
//			return null;
//
//        // Bicycle
//        case "marce":
//            if (vehicle instanceof Bicycle bicycle) return String.valueOf(bicycle.getNumberGears());
//            return null;
//        case "sospensione":
//            if (vehicle instanceof Bicycle bicycle) return bicycle.getTypeSuspensions();
//		    return null;
//        case "pieghevole":
//            if (vehicle instanceof Bicycle bicycle) return bicycle.getFoldable() ? "si" : "no";
//			return null;
//
//        default:
//            throw new CustomException("Unknown filter: " + attribute);
//	}
//}