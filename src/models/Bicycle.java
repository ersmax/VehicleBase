package models;

public class Bicycle extends Vehicles {
	
	private Integer numberGears;
	private String 	typeSuspensions;	// with or without
	private Boolean foldable;
	
	@Override
	public String toString() {
		return "Bicycle [" + super.toString() 
			 + ", gears=" + numberGears 
			 + ", type suspensions=" + typeSuspensions 
			 + ", foldable=" + foldable + "]";
	}
	public Integer getNumberGears() {
		return numberGears;
	}
	public void setNumberGears(Integer numberGears) {
		this.numberGears = numberGears;
	}
	
	public String getTypeSuspensions() {
		return typeSuspensions;
	}
	public void setTypeSuspensions(String typeSuspensions) {
		this.typeSuspensions = typeSuspensions;
	}
	public Boolean getFoldable() {
		return foldable;
	}
	public void setFoldable(Boolean foldable) {
		this.foldable = foldable;
	}
}
