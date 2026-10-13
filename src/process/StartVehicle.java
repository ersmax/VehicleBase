package process;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import enums.Operation;
import exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import services.ListImplementation;
import utils.FileManager;
import vehicles.MainVehicles;

@Slf4j
public class StartVehicle {

	public static final int COMMAND = 0;
	public static final int VEHICLE_TYPE = 1;
	public static final int PARAMETERS = 2;
	
	// one instance of service for all rows
	private static final ListImplementation service = new ListImplementation();
	
	private static final Map<Operation, Method> methodsCache = new HashMap<>();

	/**
	 * Clear writing file, Read rows and process each single row
	 */
	public void execute() {
		
		try {
			FileManager.writeFile(MainVehicles.OUTPUT_FILE, new ArrayList<>(), false);
			List<String> rows = FileManager.readFile(MainVehicles.INPUT_FILE);
			processRows(rows);			
		} catch (IOException e) {
			log.error("Error reading the input file: {}", e.getMessage());
		}	
	}
	
	/**
	 * Decode the elements list, row by row,
	 * get the operation (COMMAND), 
	 * then execute each service 
	 * @throws IOException 
	 */
	private void processRows(List<String> rows) {
		int idxRow = 0;
		for (String row : rows) {
			idxRow++;
			try {
				String[] elementsRow = StringParsing.rowParser(row);
				Operation operation = getOperation(elementsRow[COMMAND]);
				handleOperation(operation, elementsRow);
			} catch (CustomException e) {
				log.error("Row {} not added due to: {}", idxRow, e.getMessage());
			} catch (IOException e) {
				log.error("Error in reading the file");
			}
		}
	}

	/**
	 * Transform the String parameter into enum
	 * @param operation	: the operation passed as a string
	 * @return			: the Enum of the operation
	 */
	private Operation getOperation(String operation) {
		try {
			return Operation.valueOf(operation.trim().toUpperCase());
		} catch (NullPointerException e) {
			throw new CustomException("Missing operation");
		} catch (IllegalArgumentException e) {
			throw new CustomException("Invalid operation: '" + operation + "'");
		}
	}

	/**
	 * Dispatch to the correct operation handling (etc addVehicle), or handle errors.
	 * The operation is in ListImplementation
	 * @param operation			: the operation code in Enum format
	 * @param parametersList	: the list of parameters in a row
	 * @throws IOException 
	 */
	private void handleOperation(Operation operation, String[] parametersList) throws IOException {
		switch (operation) {
			case ADD:
				service.addVehicle(parametersList);
				break;
			case DELETE:
				service.deleteVehicle(parametersList);
				break;
			case LIST:
				service.listVehicles(parametersList);
				break;
			case EXPORT:
				service.exportVehicles(parametersList);
				break;
			case IMPORT:
				service.importVehicles(parametersList);
				break;
			default:
				throw new CustomException("Operation not supported: " + operation.toString());
		}
	}

}
