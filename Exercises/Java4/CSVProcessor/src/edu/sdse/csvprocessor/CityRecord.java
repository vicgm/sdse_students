package edu.sdse.csvprocessor;

public class CityRecord {
	int id;
	int year;
	String city;
	int population;
	
	public CityRecord(int id, int year, String city, int population) {
//		super();
		this.id = id;
		this.year = year;
		this.city = city;
		this.population = population;
	}
	
	@Override
	public String toString() {
		return String.format("id: " + this.id + ", year: " + this.year + ", city: " + this.city + ", population: " + this.population);
	}
	
	
}
