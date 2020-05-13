
public class TestFinalFileIOReadResults {
	
	final static String path="TestComplexMissionFileIO1/drone_1";
	
	public static void main(String[] args) {
		FlightLog myLog=new FlightLog(path);
		myLog.readLog();
		myLog.printPositions();
		myLog.printCommands();
	}
}
