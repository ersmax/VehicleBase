package process;

import java.util.List;

import enums.Operation;
import exception.CustomException;
import services.ListImplementation;


public class StartVehicle {

	public final static int OPERATION = 0;
	public final static int VEHICLE = 1;
	public final static int PARAMS = 2;
	
	private ListImplementation listVehicles = new ListImplementation();

	public void execute(List<String> parameters) {
		// decode row by row the parameters
		// then execute the services

		int row = 0;
		for (String item : parameters) {
			row++;
			try {
				String[] listItem = StringParsing.parsing(item);
				Operation operation = getOperation(listItem[OPERATION]);
				handleOperation(operation, listItem);

			} catch (CustomException e) {
				System.err.println("Row " + row + " skipped: " + e.getMessage());
			}
		}

	}

	/**
	 * Converts the String to an Operation, or throws CustomException
	 * @param operation is the first parameter
	 * @return the ENUM associated with the operation
	 */
	private Operation getOperation(String operation) {
		// Prevents a Null Pointer Exception later
		if (operation == null || operation.trim().isEmpty()) {
			throw new CustomException("Missing operation");
		}

		try {
			return Operation.valueOf(operation.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new CustomException("Invalid operation: '" + operation + "'");
		}
	}

	/**
	 * Dispatch to the proper operation
	 * @param operation is adding or listing
	 * @param listItem to show the items
	 */
	private void handleOperation(Operation operation, String[] listItem) {
		switch (operation) {
			case ADD:
				listVehicles.addVehicle(listItem);
				break;
			case LIST:
				listVehicles.listVehicle();
				break;
			default:
				// unknown operation
				throw new CustomException("Operation not supported: " + listItem[0]);
		}
	}

}
