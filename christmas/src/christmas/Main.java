package christmas;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;

public class Main {

	private static GpioPinDigitalOutput sensorTriggerPin;
	private static GpioPinDigitalInput sensorEchoPin;
	public static GpioPinDigitalOutput redLED;
	public static GpioPinDigitalOutput greenLED;
	final static GpioController gpio = GpioFactory.getInstance();
	int count = 0;

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		new Main().detect();
		//new Main().apiCall();
	}

	public void detect() throws InterruptedException {
		sensorTriggerPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_15);
		sensorEchoPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_16);
		redLED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
		greenLED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04);

		while (true) {
			try {
				redLED.high();
				Thread.sleep(2000);
				sensorTriggerPin.high(); // Make trigger pin HIGH
				Thread.sleep((long) 0.01);// Delay for 10 microseconds
				sensorTriggerPin.low(); // Make trigger pin LOW

				while (sensorEchoPin.isLow()) { // Wait until the ECHO pin gets
												// HIGH

				}
				int startTime = (int) System.nanoTime(); // Store the current
															// time to calculate
															// ECHO pin HIGH
															// time.
				while (sensorEchoPin.isHigh()) { // Wait until the ECHO pin gets
													// LOW

				}
				int endTime = (int) System.nanoTime(); // Store the echo pin
														// HIGH end time to
														// calculate ECHO pin
														// HIGH time.
				int distance = (int) ((((endTime - startTime) / 1e3) / 2) / 29.1);

				if (distance >= 1 && distance <= 8 && count!=1) {
					redLED.low();
					greenLED.high();
					count=1;
					new Main().apiCall();
					Thread.sleep(5000);
					greenLED.low();
					redLED.high();
				}
				else if(distance>10){
					count=0;
				}
//				while (distance >= 1 && distance <= 10) {
//					System.out.println("Waiting for user to change");
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void apiCall() {
		String urlLink = "https://9wy6o2qvae.execute-api.us-west-2.amazonaws.com/christmas";
		try {
			URL url = new URL(urlLink);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String output = br.readLine();
			System.out.println(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
