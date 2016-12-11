package de.bach.thwildau.jarvis.operations.test;

import org.junit.Test;

import de.bach.thwildau.jarvis.operations.WeatherForecastBerlin;

public class WeatherForecastBerlinTest {

	@Test
	public void testGetWeatherForecastBerlin(){
		System.out.println(WeatherForecastBerlin.getInstance("Wettervorhersage für Berlin:").operate());
	}
	
	
}
