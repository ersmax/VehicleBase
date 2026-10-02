package models;

public class Bike extends Vehicles {

	private String plate;
	private Integer cc;
	
	@Override
	public String toString() {
		return "Bike [" + super.toString() 
			 + ", plate=" + plate 
			 + ", cc=" + cc + "]";
	}
	public String getPlate() {
		return plate;
	}
	public void setPlate(String targa) {
		this.plate = targa;
	}
	public Integer getCc() {
		return cc;
	}
	public void setCc(Integer cc) {
		this.cc = cc;
	}
	
	
}
