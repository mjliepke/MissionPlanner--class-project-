
public class Mission {
	protected Fleet fleet;
	protected boolean missionIsAGo = false;
	protected boolean missionHasRun = false;
	protected double time;
	protected int iterations;

	public void runMission() {
		missionHasRun = true;
		System.out.println("runMission in GenericMission is not meant to be called");
	}

	public void armMission() {
		fleet.setAllHover();
		missionIsAGo = true;
	}

	public boolean hasRun() {
		System.out.println("line 1 of hasRun()");
		boolean run = false;
		try {
			run = missionHasRun;
			System.out.println("at the end of the try");
		} catch (NullPointerException e) {
			System.out.println("in the catch");
		}

		return run;
	}

	public double[][][] getFleetPositions() {
		double[][][] poss = new double[fleet.get(0).getPositions().length][fleet.getLength()][2];
		for (int iter = 0; iter < poss.length; iter++) {
			for (int j = 0; j < fleet.getLength(); j++) {
				poss[iter][j] = fleet.get(j).getPosition(iter);
			}
		}

		return poss;
	}

	public double[][] getFleetPosition(int iteration) {
		// A very taxing function with high cost
		double[][] position = new double[fleet.getLength()][2];
		for (int i = 0; i < fleet.getLength(); i++) {
			position[i] = fleet.get(i).getPosition(iteration);
		}
		return position;
	}

	public int getIterations() {
		return iterations;
	}

	protected void moveFleet(double time) {
		for (int i = 0; i < fleet.getLength(); i++) {
			fleet.get(i).move(time);
		}
	}

	public void saveLogs(String folder) {
		fleet.saveAllLogs(folder);
	}

}
