import java.io.File;

/*
 * Name: Matthew Liepke
 * Start Date: 20190124
 * Class: CS225
 * 
 * Objectives:
 * Fleet is a simple object to hold many Drones.  It is close to an extention 
 * of ArrayList, including fleet specs
 */

public class Fleet implements Cloneable {

	// Attribute Initialization
	private Drone[] fleet;
	// Mutators

	public Fleet(Drone[] fleet) {
		if (fleet != null) {
			this.fleet = new Drone[fleet.length];
		}
		this.fleet = fleet;

	}


	public Fleet(int length) {//TESTING ONLY
		fleet = new Drone[length];
		for (int i = 0; i < length; i++) {
			fleet[i] = new Drone(3);
		}
	}

	public String toString() {
		String thisFleet = "";
		for (Drone drone : fleet) {
			thisFleet += drone + "\n";
		}
		return thisFleet;
	}

	public void setAllHover() {
		for (int i = 0; i < fleet.length; i++) {
			fleet[i].setHover();
		}
	}

	public void saveAllLogs(String foldername) {
		// create directory
		File folder = new File(foldername);
		if (!folder.mkdir()) {
			System.err.println(
					"Fleet.saveAllLogs(foldername) has failed to make a new folder.\nPerhaps this foldername already exists");
		}
		String partialDir=foldername+"/drone_";
		for(int i=0;i<fleet.length;i++) {
			fleet[i].saveLog(partialDir+Integer.toString(i));
		}

	}

	public Drone retire(int index) {
		Drone retiree = fleet[index];
		for (int i = index; i < fleet.length - 1; i++) {
			fleet[i] = fleet[i + 1];
		}
		return retiree;
	}

	public void add(Drone newDrone) {
		add(newDrone, fleet.length);
	}

	public void add(Drone newDrone, int index) {
		Drone[] tempFleet = new Drone[fleet.length + 1];
		for (int i = tempFleet.length; i > index; i--) {
			tempFleet[i] = fleet[i - 1];
		}
		tempFleet[index] = newDrone;
		for (int i = index - 1; i >= 0; i--) {
			tempFleet[i] = fleet[i];
		}
		fleet = tempFleet;

	}

	public void sortBySpeed() {
		// Sorts the fleet<Drone> by Drone.minSpeed, uses bubbleSort
		// Fast to slow
		for (int i = 0; i < fleet.length - 1; i++) {
			for (int j = i + 1; j < fleet.length; j++)
				if (fleet[i].getGoalSpeed() < fleet[j].getGoalSpeed()) {
					Drone placeholder = fleet[i];
					fleet[i] = fleet[j];
					fleet[j] = placeholder;
				}
		}
	}

	public void sortByRadius() {
		// Sorts from Close-Far
		for (int i = 0; i < fleet.length - 1; i++) {
			for (int j = i + 1; j < fleet.length; j++)
				if (fleet[i].getLoiterRadius() > fleet[j].getLoiterRadius() && fleet[i].getLoiterRadius() != -1
						&& fleet[j].getLoiterRadius() != -1) {
					Drone placeholder = fleet[i];
					fleet[i] = fleet[j];
					fleet[j] = placeholder;
				}
		}
	}

	// Accessors with Calculations
	public double[][] getPositions() {
		double[][] tempPos = new double[fleet.length][2];
		for (int i = 0; i < fleet.length; i++) {
			tempPos[i] = fleet[i].getPosition();
		}
		return tempPos;
	}

	public int closestTo(double point[]) {
		int closest = -1;
		double closestDistance = fleet[0].getDistanceFrom(point);
		for (int i = 1; i < fleet.length; i++) {
			if (fleet[i].getDistanceFrom(point) < closestDistance) {
				closestDistance = fleet[i].getDistanceFrom(point);
				closest = i;
			}
		}
		return closest;
	}

	public int numberFlying() {
		return -1;
	}

	// Accessors without calculations
	public Drone[] getFleet() {
		return fleet;
	}

	public int getLength() {
		return fleet.length;
	}

	public Drone get(int index) {
		if (index == -1) {
			index = fleet.length - 1;
		}
		Drone returning;
		try {
			returning = fleet[index];
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println(
					"Fleet Index Out Of Bounds at Fleet.get(index).  \nYou can find the length of fleet by calling fleet.getLength()");
			returning = null;

		}
		return returning;
	}

	//*************IMPLEMENTATIONS**********
	protected Fleet clone() {
		try {
			return (Fleet) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
