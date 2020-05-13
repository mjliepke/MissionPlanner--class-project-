import java.io.File;

public class TestFlightLog {

	public static void main(String[] args) {
		FlightLog log = new FlightLog();
		log.appendPositions(new double[] { 2, 0 });
		log.appendPositions(new double[] { 1, 1 });
		log.appendPositions(new double[] {50,50});
		log.appendCommands("readingthe string");
		log.appendCommands("loiterTurn(100, new double[]{10,10})");
		log.printPositions();
		log.printCommands();
		// create directory
		File folder = new File("Log1");
		boolean flag = folder.mkdir();

		 //Print whether true/false
		System.out.println("Directory created (T/F)? " + flag);
	
		log.saveLog("Log1/drone1");

		
	}

}
