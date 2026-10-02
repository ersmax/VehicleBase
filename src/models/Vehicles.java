package models;

public class Vehicles {
	private Integer id;				// unique id of object
	private String 	vehicleType;	// car, bike, etc
	private Integer numberWheels;	// depends on the vehicle
	private String 	fuelType;		// gas, petrol, electric, hybrid
	private String 	category;		// SUV, coupe, utilitarian
	private String 	color;
	private String 	brand;
	private Integer productionYear;
	private String 	model;
	
	
	
	@Override
	public String toString() {
		return "id=" + id + ", vehicleType=" + vehicleType + ", numberWheels=" + numberWheels + ", fuelType="
				+ fuelType + ", category=" + category + ", color=" + color + ", brand=" + brand + ", productionYear="
				+ productionYear + ", model=" + model;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	public Integer getNumberWheels() {
		return numberWheels;
	}
	public void setNumberWheels(Integer numberWheels) {
		this.numberWheels = numberWheels;
	}
	public String getFuelType() {
		return fuelType;
	}
	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public Integer getProductionYear() {
		return productionYear;
	}
	public void setProductionYear(Integer productionYear) {
		this.productionYear = productionYear;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}	
}
