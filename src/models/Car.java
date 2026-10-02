package models;

public class Car extends Vehicles {

	private String carPlate;	// must be unique
	private Integer cc;			
	private Integer numberDoors;
	
	@Override
	public String toString() {
		return "Car [" + super.toString() 
			 + ", plate=" + carPlate 
			 + ", cc=" + cc 
			 + ", numberDoors=" + numberDoors + "]";
	}
	public String getPlate() {
		return carPlate;
	}
	public void setPlate(String carPlate) {
		this.carPlate = carPlate;
	}
	public Integer getCc() {
		return cc;
	}
	public void setCc(Integer cc) {
		this.cc = cc;
	}
	public Integer getNumberDoors() {
		return numberDoors;
	}
	public void setNumberDoors(Integer numberDoors) {
		this.numberDoors = numberDoors;
	}
}
