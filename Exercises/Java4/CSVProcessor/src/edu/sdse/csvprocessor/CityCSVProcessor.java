package edu.sdse.csvprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CityCSVProcessor {
	
	private List<CityRecord> allRecords = new ArrayList<CityRecord>(); //array for storing records
	private Map<String, List<CityRecord>> recordsByCity = new HashMap<String, List<CityRecord>>(); //hashmap for storing records by city
	
	public void readAndProcess(File file) {
		//first make sure records list is empty
		this.allRecords.clear();
		this.recordsByCity.clear();
		
		//Try with resource statement (as of Java 8)
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			//Discard header row
			br.readLine();
			
			String line;
			
			while ((line = br.readLine()) != null) {
				// Parse each line
				String[] rawValues = line.split(",");
				
				int id = convertToInt(rawValues[0]);
				int year = convertToInt(rawValues[1]);
				String city = convertToString(rawValues[2]);
				int population = convertToInt(rawValues[3]);
								
				CityRecord record = new CityRecord(id, year, city, population);
				allRecords.add(record);
				addCityRecord(recordsByCity, city, record); //call addCityRecord to add record to city
				System.out.println(record);
			}
			
			//process data for each city
			System.out.println("\nBeginning to process city data");
			processCities(); //call method to process city data
			
		} catch (Exception e) {
			System.err.println("An error occurred:");
			e.printStackTrace();
		}
	}
	
	private String cleanRawValue(String rawValue) {
		return rawValue.trim();
	}
	
	private int convertToInt(String rawValue) {
		rawValue = cleanRawValue(rawValue);
		return Integer.parseInt(rawValue);
	}
	
	private String convertToString(String rawValue) {
		rawValue = cleanRawValue(rawValue);
		
		if (rawValue.startsWith("\"") && rawValue.endsWith("\"")) {
			return rawValue.substring(1, rawValue.length() - 1);
		}
		
		return rawValue;
	}
	
	private Map<String, List<CityRecord>> addCityRecord(Map<String, List<CityRecord>> currRecords,
			String city, CityRecord newRecord){
		//Add city and map to recordsByCity Map
		//If city is already in Map, add new record
		if (currRecords.containsKey(city)) {
			List<CityRecord> tmpRecords = currRecords.get(city); //get current list of records for city
			tmpRecords.add(newRecord); //add new record to said list
			currRecords.put(city, tmpRecords); //add updated list of records to that city
		} else {
			//else create new ArrayList to store records, and add the new city to Map
			List<CityRecord> tmpRecords = new ArrayList<CityRecord>(); //list to hold records
			tmpRecords.add(newRecord); //add record to list
			currRecords.put(city, tmpRecords); //add new city and record to Map
		}
		return currRecords;
	}
	
	private void processCities() {
		//process each city
		for (Entry<String, List<CityRecord>> entry : this.recordsByCity.entrySet()) {
			String city = entry.getKey();
			List<CityRecord> records = entry.getValue();
			int numCityEntries = getNumCityEntries(city, records); //get number of entries for a city
			int minYear = getMinYear(city, records); //get min year for each city
			int maxYear = getMaxYear(city, records); //get max year for each city
			double avgPopulation = getAvgPopulation(city, records);
			System.out.println("Average population of " + city +
					" (" + minYear + "-" + maxYear + "; " + numCityEntries + "): " + avgPopulation);
		}
	}
	
	private int getNumCityEntries(String city, List<CityRecord> records) {
		//Method to get number of records for a city
		return records.size();
	}
	
	private int getMinOrMaxYear(String city, List<CityRecord> records, int factor) {
		/*
		 * Get min or max year for a city. Helper function for getMinYear and getMaxYear.
		 * Method assigns the first year record for a city as the min_year.
		 * Then it compares every following year record with the current min_year,
		 * and if a year record is smaller than the current min_year, then it is
		 * assigned to be the new min_year. The method works the same whether called from min or max
		 * the only difference is that years are made negative before comparison if the method is
		 * called from max. The result is returned.
		 */
		int min_year = records.get(0).year * factor; //min year is first year
		for (int i = 1; i < records.size(); i++) { //for the following years
			int tmp_year = records.get(i).year * factor;
			if (tmp_year < min_year) { //compare year * factor to smallest year so far
				min_year = tmp_year; //if year * factor is smaller than min_year, assign that year to min_year
			}
		}
		return min_year * factor; //turn min_year into positive number again if factor is -1
	}
	
	private int getMinYear(String city, List<CityRecord> records) {
		//Method to get min year of city
		return getMinOrMaxYear(city, records, 1);
	}
	
	private int getMaxYear(String city, List<CityRecord> records) {
		//Method to get max year of city
		return getMinOrMaxYear(city, records, -1);
	}
	
	private double getAvgPopulation(String city, List<CityRecord> records) {
		//Method to get average population of city
		int nYears = records.size(); //number of records
		int populationSum = 0;
		for (CityRecord record : records) {
			populationSum += record.population;
		}
		return populationSum / nYears;
	}
	
	public static final void main(String[] args) {
		CityCSVProcessor reader = new CityCSVProcessor();
		
		File dataDirectory = new File("data/");
		File csvFile = new File(dataDirectory, "Cities.csv");
		
		reader.readAndProcess(csvFile);
	}
}
