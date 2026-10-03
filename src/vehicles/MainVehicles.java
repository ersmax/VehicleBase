package vehicles;

import java.util.ArrayList;
import java.util.List;

import process.StartVehicle;
import utils.Validation;

public class MainVehicles {

	/** 
	 * Parsing parameters
	 * Depending on type of vehicle:
	 * 		1. check vehicle type
	 * 		2. check specific vehicle type
	 * 
	 * Check validity of parameters value.
	 * (e.g. category = a valid category, fuelType = valid fuel)
	 * Check unique carPlate
	 * Check numeric parameters are numbers only.
	 * 
	 * If okay, add the object inside a common list of vehicles   
	 * (use a progressive unique id)
	 * 
	 * With List function, create a list of objects.
	 */
	
	/**
	 * "Check validity of parameters value" (alim, cat) is the one instruction not covered yet. 
	 * Right now alim=banana is accepted. 
	 * A getOneOf(map, key, "benzina", "diesel", ...) helper in Validation would cover it. 
	 * You could also check rules per type, such as a bicycle having alim=manuale.
	 * 
	 */
	
		
	public static void main(String[] args) {
		
		List<String> param = new ArrayList<String>();
		// Add parameters here
		param.add("add,macchina,ruote=4,alim=benzina,cat=strada,colore=bianco,marca=fiat,anno=2025,modello=500,porte=4,targa=el234gx,cc=1200");
		param.add("add,macchina,ruote=4,alim=benzina,cat=strada,colore=bianco,marca=fiat,anno=2026,modello=panda,porte=4,targa=fl234gx,cc=1300");
		param.add("add,macchina,ruote=4,alim=benzina,cat=strada,colore=bianco,marca=fiat,anno=2026,modello=panda,porte=4,targa=fl234gx,cc=1300");
		param.add("add,moto,ruote=2,alim=benzina,cat=strada,colore=nero,marca=Yamaha,anno=2025,modello=r1,targa=EL22239,cc=900");
		param.add("add,bici,ruote=2,alim=manuale,cat=strada,colore=nero,marca=Bianchi,anno=2025,modello=Grizl 5,marce=10,sospensione=senza,pieghevole=no");
		
		// ---------- VALID ROWS (must be added) ----------
		param.add("add,macchina,ruote=4,alim=benzina,cat=strada,colore=bianco,marca=fiat,anno=2025,modello=500,porte=4,targa=el234gx,cc=1200");
		param.add("add,macchina,ruote=4,alim=benzina,cat=strada,colore=bianco,marca=fiat,anno=2026,modello=panda,porte=4,targa=fl234gx,cc=1300");
		param.add("add,moto,ruote=2,alim=benzina,cat=strada,colore=nero,marca=Yamaha,anno=2025,modello=r1,targa=EL22239,cc=900");
		param.add("add,bici,ruote=2,alim=manuale,cat=strada,colore=nero,marca=Bianchi,anno=2025,modello=Grizl 5,marce=10,sospensione=senza,pieghevole=no");
		// uppercase values are accepted (getOneOf / getBool ignore case)
		param.add("add,macchina,ruote=4,alim=DIESEL,cat=SUV,colore=Rosso,marca=jeep,anno=2022,modello=renegade,porte=5,targa=gh456ij,cc=1600");
		param.add("add,bici,ruote=2,alim=elettrica,cat=citycar,colore=blu,marca=Brompton,anno=2024,modello=electric,marce=3,sospensione=con,pieghevole=SI");

		// ---------- getOneOf: alim, cat, colore ----------
		param.add("add,macchina,ruote=4,alim=banana,cat=strada,colore=rosso,marca=fiat,anno=2020,modello=uno,porte=4,targa=aa001aa,cc=1000");
		param.add("add,macchina,ruote=4,alim=diesel,cat=volante,colore=rosso,marca=fiat,anno=2020,modello=uno,porte=4,targa=aa002aa,cc=1000");
		param.add("add,moto,ruote=2,alim=benzina,cat=strada,colore=fucsia,marca=Honda,anno=2024,modello=cbr,targa=AB12345,cc=600");
		param.add("add,macchina,ruote=4,alim=,cat=strada,colore=rosso,marca=fiat,anno=2020,modello=uno,porte=4,targa=aa003aa,cc=1000");
		param.add("add,macchina,ruote=4,cat=strada,colore=rosso,marca=fiat,anno=2020,modello=uno,porte=4,targa=aa004aa,cc=1000");
		
		// ---------- getYear: valid year  ----------
		param.add("add,macchina,ruote=4,alim=diesel,cat=strada,colore=rosso,marca=fiat,anno=2099,modello=uno,porte=4,targa=aa010aa,cc=1000");
		param.add("add,macchina,ruote=4,alim=diesel,cat=strada,colore=rosso,marca=fiat,anno=1800,modello=uno,porte=4,targa=aa011aa,cc=1000");

		// ---------- getInt: numbers only ----------
		param.add("add,macchina,ruote=4,alim=diesel,cat=strada,colore=rosso,marca=fiat,anno=duemila,modello=uno,porte=4,targa=aa005aa,cc=1000");
		param.add("add,macchina,ruote=quattro,alim=diesel,cat=strada,colore=rosso,marca=fiat,anno=2020,modello=uno,porte=4,targa=aa006aa,cc=1000");
		param.add("add,moto,ruote=2,alim=benzina,cat=strada,colore=nero,marca=Honda,anno=2024,modello=cbr,targa=AB12346,cc=6OO");
		param.add("add,bici,ruote=2,alim=manuale,cat=strada,colore=nero,marca=X,anno=2024,modello=Y,marce=7.5,sospensione=con,pieghevole=no");

		// ---------- getBool: pieghevole ----------
		param.add("add,bici,ruote=2,alim=manuale,cat=strada,colore=nero,marca=X,anno=2024,modello=Y,marce=7,sospensione=con,pieghevole=forse");

		// ---------- wheels per vehicle type ----------
		param.add("add,macchina,ruote=2,alim=diesel,cat=strada,colore=rosso,marca=fiat,anno=2020,modello=uno,porte=4,targa=aa007aa,cc=1000");
		param.add("add,moto,ruote=4,alim=benzina,cat=strada,colore=nero,marca=Honda,anno=2024,modello=cbr,targa=AB12347,cc=600");
		param.add("add,bici,ruote=3,alim=manuale,cat=strada,colore=nero,marca=X,anno=2024,modello=Y,marce=7,sospensione=con,pieghevole=no");

		// ---------- plate: missing, empty, duplicate ----------
		param.add("add,moto,ruote=2,alim=benzina,cat=strada,colore=nero,marca=Honda,anno=2024,modello=cbr,cc=600");
		param.add("add,moto,ruote=2,alim=benzina,cat=strada,colore=nero,marca=Honda,anno=2024,modello=cbr,targa=,cc=600");
		param.add("add,macchina,ruote=4,alim=benzina,cat=strada,colore=bianco,marca=fiat,anno=2026,modello=panda,porte=4,targa=fl234gx,cc=1300");
		param.add("add,moto,ruote=2,alim=benzina,cat=strada,colore=nero,marca=Ducati,anno=2023,modello=monster,targa=el22239,cc=900");

		// ---------- parsing ----------
		param.add("add,macchina,ruote=4,alim=diesel,cat=strada,colore=rosso,marca=fiat,anno=2020,modello=uno,porte4,targa=aa008aa,cc=1000");
		param.add("add,macchina,ruote=4,alim=diesel,alim=benzina,cat=strada,colore=rosso,marca=fiat,anno=2020,modello=uno,porte=4,targa=aa009aa,cc=1000");

		// ---------- operation and vehicle type ----------
		param.add("delete,macchina");
		param.add("add,camion,ruote=6");
		param.add("add");
		param.add("");
		
		
		param.add("list");
				
		System.out.println("Start Veicoli");
		new StartVehicle().execute(param);
	}

}
