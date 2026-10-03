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
				throw new CustomException("Operation not supported: " + operation.toString());
		}
	}

}
