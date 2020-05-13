/*
 * Name: Matthew Liepke
 * Start Date: 20180124
 * Class: 225
 * 
 * Objectives:
 * Control the Fleet to loiter around a target
 */
public class LoiterAround extends Mission {
	public double minDistance;
	public double[] center;
	public int counter;
	public double droneSeparation = 25;

	public LoiterAround(double[] point, double minDistance, Fleet Fleet, int iterations, double droneSeparation) {///Testing Only
		missionIsAGo = false;
		this.minDistance = minDistance;
		this.center = point;
		this.fleet = Fleet;
		this.droneSeparation=droneSeparation;
		center = new double[] { 300, 300 };
		counter = 0;
		time=500;//DEFAULT FOR CONSTRUCTOR TESTANG
		this.iterations = iterations;
		missionHasRun=false;
	}

	public LoiterAround(double[] point, double minDistance, Fleet fleet, double time, double droneSeparation) {
		missionIsAGo = false;
		this.minDistance = minDistance;
		this.droneSeparation=droneSeparation;
		this.center = point;
		this.fleet = fleet;
		center = new double[] { 300, 300 };
		counter = 0;
		this.time=time;
		this.iterations = (int) (time*4);
		missionHasRun=false;
	}
	public LoiterAround(double[] point, double minDistance, Fleet Fleet, int iterations) {///Testing Only
		missionIsAGo = false;
		this.minDistance = minDistance;
		this.center = point;
		this.fleet = Fleet;
		center = new double[] { 300, 300 };
		counter = 0;
		time=500;//DEFAULT FOR CONSTRUCTOR TESTANG
		this.iterations = iterations;
		missionHasRun=false;
	}

	public LoiterAround(double[] point, double minDistance, Fleet fleet, double time) {
		missionIsAGo = false;
		this.minDistance = minDistance;
		this.center = point;
		this.fleet = fleet;
		center = new double[] { 300, 300 };
		counter = 0;
		this.time=time;
		this.iterations = (int) (time*4);
		missionHasRun=false;
	}

	// @Override
	public void runMission() {
		// Make sure there is a fleet
		if (fleet.getLength() < 1) {
			System.out.println("there isn't a fleet to LoiterAround");
			return;
		}
		if (!missionIsAGo) {
			System.out.println("You have not armed the fleet yet, mission will not run");
		}else {
			missionHasRun=true;
		}

		// The Mission Execution Occurs here
		fleet.sortBySpeed();
		while (missionIsAGo) {
			// fleet.sortByRadius();
			//System.out.println("Going through Iteration " + counter + " in LoiterAround");
			deployFleet();
			circleFleet();

			moveFleet(time / iterations);
			// collisionAvoidance(1);
			counter++;
			if (counter >= iterations) {
				System.out.println("Counter and iterations: " + counter + ", " + iterations);
				missionIsAGo = false;
			}
		}
		

	}

	private void deployFleet() {
		double tooClose = droneSeparation * 1.5;// needs to be less than droneSeparation to prevent grouping when
												// entering
		// Send out the drones to their starting positions one at a time
		for (int i = 0; i < fleet.getLength(); i++) {
			if (!fleet.get(i).getStatus().equalsIgnoreCase("Loiter")
					&& (i == 0 || fleet.get(i - 1).getDistanceFrom(fleet.get(i).getPosition()) > tooClose)) {
				fleet.get(i).setSpeed(fleet.get(i).getMaxSpeed());
				fleet.get(i).setLoiterTurn(minDistance + (i * droneSeparation), new double[] { center[0], center[1] });
				// return;
			}
		}
	}

	private void circleFleet() {// Needs Revamping
		// If the drone is closeEnough close to it's waypoint it will begin to loiter
		// around it's destination
		double closeEnough = droneSeparation;
		double incrumentation = droneSeparation;

		// *****Collapse formation if possible*****

		// *****Expand formation if needed******
		avoidCollisions(droneSeparation, incrumentation);

	}

	private void avoidCollisions(double tooClose, double incrumentation) {
		boolean expand;
		double maxRadius = getMaxRadius();
		for (int i = fleet.getLength() - 1; i > 0; i--) {// Cycle through each drone backwards(slow-fast)
			expand = false;

			for (int j = i - 1; j >= 0; j--) {

				if (fleet.get(i).getDistanceFrom(fleet.get(j).getPosition()) < tooClose
						&& fleet.get(i).getLoiterRadius() >= fleet.get(j).getLoiterRadius()
						&& !fleet.get(i).isChangingOrbit()) {
					expand = true;
				}

			}
			System.out.print("drone_" + i + ": ");
			if (expand) {
				expandOutward(i, incrumentation);
				// fleet.get(i).setLoiterRadius(fleet.get(i).getLoiterRadius() +
				// incrumentation);
			}

			expand = false;
			if (!(fleet.get(i).getLoiterCenter() == null) && fleet.get(i).getLoiterCenter()[0] != 300) {
				System.out.println("DRONE NULL CENTER" + i);
			}
		}
	}

	private double getMaxRadius() {
		double maxRad = minDistance;
		for (Drone d : fleet.getFleet()) {
			if (d.getStatus().equalsIgnoreCase("loiter") && d.getLoiterRadius() > maxRad) {
				maxRad = d.getLoiterRadius();
			}
		}
		//System.out.println("maxRadius:\t" + maxRad);
		return maxRad;
	}

	private void expandOutward(int droneIndex, double radiusChange) {
		fleet.get(droneIndex).setLoiterRadius(fleet.get(droneIndex).getLoiterRadius() + radiusChange);

	}

	private void expandInward(int droneIndex, double radiusChange) {
		expandOutward(droneIndex, -radiusChange);
	}

}
