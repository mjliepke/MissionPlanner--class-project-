//import java.io*;
public class TestReadFlightLog {

	public static void main(String[] args) {

		FlightLog log = new FlightLog();
		log.readLog("Log1/drone1");
		log.printPositions();
		log.printCommands();
	}
}
