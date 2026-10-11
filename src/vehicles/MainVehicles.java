package vehicles;

import lombok.extern.slf4j.Slf4j;
import process.StartVehicle;

@Slf4j
public class MainVehicles {

	public static final String INPUT_FILE 	= "data/readData.txt";
	public static final String OUTPUT_FILE 	= "data/writeData.txt";
	public static final String JSON_FILE	= "data/vehicles.json";
		
	
	public static void main(String[] args) {
		
				
		log.info("Start Veicoli");
		new StartVehicle().execute();
	}

}
