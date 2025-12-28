package utils;

import java.util.Random;

import net.datafaker.Faker;

public class RandomDataExtractor {

	public static Faker faker = new Faker();
	public static Random random = new Random();
	
	public static String generateFirstName() {
		
	  return faker.name().firstName();
	  
		 
	}
	
	public static String genrateNameWithnumber(String questionMark) {
		
		return faker.name().firstName()+faker.letterify(questionMark);
	}
	
	
	public static String genrateNameNumber() {
		
		return faker.bothify("###??????");
	}
	
	public static String generateNumber() {
		
		return String.valueOf( random.doubles(3));
		
	}
	
	
	public static String randomIntegerNumber() {
		
	return faker.number().digits(5);
	}
	
	
}
