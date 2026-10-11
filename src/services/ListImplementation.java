package services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import models.Vehicles;
import process.StartVehicle;
import process.StringParsing;
import singleton.VehicleRegistry;
import utils.FileManager;
import vehicles.MainVehicles;

@Slf4j
public class ListImplementation {

	private static final List<Vehicles> vehicleRegistry = VehicleRegistry.getSingleton().getVehicleRegistry();
	
	/**
	 * Dispatch to the correct class type,
	 * and parse the parameters of the vehicle
	 * @param listItem is the single string of items
	 */
	public void addVehicle(String[] parametersList) {
		if (parametersList.length < StartVehicle.PARAMS) {
			throw new CustomException("Missing vehicle type");
		}
		
		String[] normalizedListItems = Arrays.copyOfRange(parametersList, StartVehicle.PARAMS, parametersList.length); 
		Map<String, String> params = StringParsing.mapKeyValue(normalizedListItems);

		switch(parametersList[StartVehicle.VEHICLE].toLowerCase()) {
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
				throw new CustomException("Invalid vehicle type: '" + parametersList[StartVehicle.VEHICLE] + "'");
		}
	}

	/**
	 * Print all vehicles, or only those matching the filters
	 * e.g. list  |  list,colore=bianco  |  list,colore=bianco,marca=fiat
	 * @throws IOException 
	 */
	public void listVehicle(String[] parameterList) throws IOException {
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
		String title = parameterList.toString();
		
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
		// TODO
		
	}

	
}
