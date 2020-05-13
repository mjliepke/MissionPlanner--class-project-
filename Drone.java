import java.lang.reflect.InvocationTargetException;

/*Name: Matthew Liepke
 * Start Date: 20180124
 * Class: CS225
 * 
 * Objectives:
 * Drone is to allow user the ability to control it's path, as well as log it's past, and future missions.  
 * Drone is also able to give it's vital information, as well as follow a given flight path
 * 
 * Need: wayPoints and positions inputted to be in (x,y) as the 0 and 1 index
 */

public class Drone {

	// FileIO Initialization
	private FlightLog myLog;

	// Attribute Initialization
	private double maxFlightTime, currentAirTime;
	private double minAltitude, maxAltitude, crusingAltitude, currentAltitude;
	private double minSpeed, maxSpeed, efficientSpeed, goalSpeed;
	private double[] currentSpeed;
	private double[] position;
	private double maxTotalAcceleration;
	private double closeEnough;

	// Used for Waypoint
	private double[] wayPoint;
	private double[] maxWaySpeed;
	private double[] maxAcceleration;
	private String[] wayPointStatus;// Accel, Deccel. accal=positive accel, deccel=neg accel

	// Used for Loiter
	private double[] loiterCenter;
	private double loiterRadius;
	private boolean transitionInitiated, changingOrbit;
	private double currentRadius, transitionRadius, startRadius, startSpeed;// Used for orbital transitions in radii
	private double theta, omega, alpha, radialSpeed;// radial pos, vel, accel, and outward radial speed
	private boolean counterClockwiseLoiter, radialDeccel;
	private double maxOmega;// max speed equivalent

	// Used for ToLoiter
	private double entranceAlpha;
	private double[] maxEntranceSpeed;
	private boolean atCircle;

	// private double[] searchArea;//x-y coordinates of polygon to be searched

	private boolean lowBattery, inAir;
	private String status;// Limit to only a few terms

	public String toString() {
		return "This drone is in " + status;
	}

	// *********************CONSTRUCTORS*********************
	public Drone(double[] position, double minSpeed, double maxSpeed, double maxAccel) {
		this.position = position;
		this.minSpeed = minSpeed;
		this.maxSpeed = maxSpeed;
		this.maxTotalAcceleration = maxAccel;
		goalSpeed = maxSpeed;
		currentSpeed = new double[] { 0, 0 };
		closeEnough = maxSpeed / 2;// For sanity purposes don't set this lower
		myLog = new FlightLog();
		myLog.appendPositions(position);
	}

	public Drone() {// Just a default constructor for testing
		myLog = new FlightLog();
		myLog.appendPositions(position);
	}

	public Drone(int preset) {// Just a default constructor for testing
		myLog = new FlightLog();
		// Only use for testing purposes
		switch (preset) {
		case 1:
			position = new double[] { 0, 0 };
			minSpeed = 0;
			maxSpeed = 10;
			goalSpeed = 10;
			currentSpeed = new double[] { 0, 0 };
			maxTotalAcceleration = 1;
			closeEnough = 5;
			// counterClockwiseLoiter = false;
			break;
		case 2:
			position = new double[] { 200, 200 };
			minSpeed = 0;
			maxSpeed = 10;
			goalSpeed = 10;
			currentSpeed = new double[] { 0, 0 };
			maxTotalAcceleration = 3;
			closeEnough = 5;
			// counterClockwiseLoiter = false;
			break;
		case 3:
			position = new double[] { 50, 50 };
			minSpeed = 0;
			maxSpeed = 10;
			goalSpeed = 10;
			currentSpeed = new double[] { 0, 0 };
			maxTotalAcceleration = 1;
			closeEnough = 5;
			// counterClockwiseLoiter = false;
			break;
		default:
			position = new double[] { 450, 250 };
			minSpeed = 0;
			maxSpeed = 20;
			goalSpeed = 15;
			currentSpeed = new double[] { 0, 0 };
			maxTotalAcceleration = 3;
			closeEnough = 5;
			// counterClockwiseLoiter = false;
			setHover();
		}
		myLog.appendPositions(position);
//		wayPoint = new double[] { 100, 100 };
//
//		setWayPoint(wayPoint);

	}

	// ******************FILE IO*********************************
	public void saveLog(String filename) {
		myLog.saveLog(filename);
	}

	public String getFilePath() {
		return myLog.getFilePath();
	}

	// ***********************Navigation Manipulators*****************************
	public void setWayPoint(double[] newWayPoint) {
		if (wayPoint == newWayPoint) {
			setHover();
		}
		wayPoint = newWayPoint;
		setWayPointStatus();
		setWayPointAccelerationAndSpeed();
		myLog.addWayPoint(wayPoint);
	}

	public void setLoiterTurn(double radius, double[] center) {
		loiterRadius = radius;
		loiterCenter = center;
		maxOmega = goalSpeed / loiterRadius;
		setLoiterStatus();

		// System.out.println("Its in Loiter");
		// setWayPointStatus();
		status = "Loiter";
		myLog.addLoiter(radius, center);
	}

	public void setLoiterRadius(double radius) {
		loiterRadius = radius;

		if (!atCircle) {
			if (this.loiterCenter == null) {
				System.out.println("loiterCenter is null you bafoon, may have failed to setLoiterRadius");
				return;
			}
			setLoiterTurn(radius, this.loiterCenter);
		}
		myLog.addLoiter(loiterRadius, loiterCenter);
	}

	private void setWayPointStatus() {
		String wayPointStatusX = "Deccel";
		String wayPointStatusY = "Deccel";
		if (position[0] < wayPoint[0]) {
			wayPointStatusX = "Accel";
		}
		if (position[1] < wayPoint[1]) {
			wayPointStatusY = "Accel";
		}
		wayPointStatus = new String[] { wayPointStatusX, wayPointStatusY };
		status = "wayPoint";
//		System.out.println("the positions are :" + position[0] + " , " + position[1]);
//		System.out.println("the wayPoints are :" + wayPoint[0] + " , " + wayPoint[1]);
//		System.out.println("the waypoint statuses are :" + wayPointStatusX + " , " + wayPointStatusY);
	}

	private void setWayPointAccelerationAndSpeed() {
		double slope = (wayPoint[1] - position[1]) / (wayPoint[0] - position[0]);
		double deg = Math.atan(slope);
		double maxAccelX = maxTotalAcceleration * Math.cos(deg);
		double maxAccelY = maxTotalAcceleration * Math.sin(deg);
		double maxSpeedX = goalSpeed * Math.cos(deg);
		double maxSpeedY = goalSpeed * Math.sin(deg);

		if (wayPoint[0] == position[0] && wayPoint[1] < position[1]) {
			// if straight up/down, needed in order to prevent a negative
			// maxAccelY/maxSpeedY
			maxAccelX = 0;
			maxAccelY = maxTotalAcceleration;
			maxSpeedX = 0;
			maxSpeedY = goalSpeed;
		}
		if (maxAccelY < 0) {
			maxAccelY *= -1;
			printWarning();
		}
		if (maxAccelX < 0) {
			maxAccelX *= -1;
			printWarning();
		}
		if (maxSpeedY < 0) {
			maxSpeedY *= -1;
			printWarning();
		}
		if (maxSpeedX < 0) {
			maxSpeedX *= -1;
			printWarning();
		}
		maxAcceleration = new double[] { maxAccelX, maxAccelY };
		System.out.println("maxAccelX:" + maxAccelX + "\tmaxAccelY:" + maxAccelY);
		maxWaySpeed = new double[] { maxSpeedX, maxSpeedY };
		// System.out.println("maxWayX: " + maxWaySpeed[0] + " maxWayY: " +
		// maxWaySpeed[1]);
	}

	private void printWarning() {
		System.out.println("in setWayPointAccelerationAndSpeed a speed/accel was negated from neg");
	}

	private void setLoiterTurnFromToLoiter() {
		setTheta();
		setOmega();
		radialSpeed = 0;
	}

	public void setClockwiseLoiter() {
		counterClockwiseLoiter = false;
	}

	public void setCounterClockwiseLoiter() {
		counterClockwiseLoiter = true;
	}

	// HOVER SETTERS
	public void setHover() {
		status = "Hover";
		currentSpeed = new double[] { 0, 0 };
		nullWayPoint();
		myLog.addHover();
	}

	// TO-LOITER SETTERS
	private void setLoiterStatus() {
		status = "Loiter";
		changingOrbit = false;
		counterClockwiseLoiter = counterClockwiseLoiter;
		// System.out.println("loiterCenter: " + loiterCenter[0] + " , " +
		// loiterCenter[1]);
		// System.out.println("position:" + position[0] + " , " + position[1]);
		if (getDistanceFrom(loiterCenter) < loiterRadius + closeEnough) {
			atCircle = true;
			System.out.println("NOW IN THE REAL LOITER");
			setLoiterTurnFromToLoiter();
			currentRadius = getDistanceFrom(loiterCenter);
			nullWayPoint();
			if (getDistanceFrom(loiterCenter) < loiterRadius - closeEnough) {
				radialDeccel = false;
			}
			// setLoiterTurn(loiterRadius, loiterCenter);ENDLESS RECURSION BOYS
		} else {
			atCircle = false;
			setToLoiterStatus();
			setWayPointStatus();
			System.out.println("BACK TO TOLOITER");
		}

	}

	private void setToLoiterStatus() {

		wayPoint = new double[] { 0, 0 };
		entranceAlpha = maxTotalAcceleration - Math.pow(getSpeed(), 2) / loiterRadius;
		setMaxEntranceSpeed();
		setToLoiterWayPointX();
		setToLoiterWayPointY();

		setWayPointAccelerationAndSpeed();
		setWayPointStatus();
		setTheta();
		setOmega();
		// printToLoiterTroubleshooting();
		if (wayPoint[0] == position[0] && wayPoint[1] < position[1]) {
			System.out.println(
					"If you see this then in setToLoiterStatus you need to accept the option that waypoint and position[0] are equal");
//		 This should be unneeded due to the fact that Math functions rarely produce
//		 ints Where the angle would be -pi/2
		}

	}

	private void printToLoiterTroubleshooting() {
		double slopeToCenter = (loiterCenter[1] - position[1]) / (loiterCenter[0] - position[0]);
		double angleToCenter = Math.atan(slopeToCenter);
		double distanceFromCenter = getDistanceFrom(loiterCenter);

		double angleFromLoiterCenterToTangent = Math.atan(loiterRadius / distanceFromCenter);
		double distanceFromRadius = loiterRadius / Math.sin(angleFromLoiterCenterToTangent);
		System.out.println("DistanceFromRadius, angle to center, angle from center:\n\t" + distanceFromRadius + "\t"
				+ angleToCenter + "\t" + angleFromLoiterCenterToTangent);
		System.out.println(
				"the angle to radius if counterClock is :\t" + (angleToCenter - angleFromLoiterCenterToTangent));
		System.out.println("the waypoint is: " + wayPoint[0] + ", " + wayPoint[1]);
	}

	private void setMaxEntranceSpeed() {
		double slopeToCenter = (loiterCenter[1] - position[1]) / (loiterCenter[0] - position[0]);
		double angleToCenter = Math.atan(slopeToCenter);
		double distanceFromCenter = getDistanceFrom(loiterCenter);

		double angleFromLoiterCenterToTangent = Math.atan(loiterRadius / distanceFromCenter);
		double distanceFromRadius = loiterRadius / Math.sin(angleFromLoiterCenterToTangent);

		double maxEntranceVelocity = Math.sqrt(loiterRadius * maxTotalAcceleration);
		maxEntranceSpeed = new double[2];
		if (counterClockwiseLoiter) {
			maxEntranceSpeed[0] = maxEntranceVelocity
					* Math.abs(Math.cos(angleToCenter - angleFromLoiterCenterToTangent));
		} else {
			maxEntranceSpeed[1] = maxEntranceVelocity
					* Math.abs(Math.cos(angleToCenter + angleFromLoiterCenterToTangent));
		}
		System.out.println("***sin(angleFromLoiterCenterToTangent:" + Math.cos(angleFromLoiterCenterToTangent) + " , "
				+ Math.sin(angleFromLoiterCenterToTangent));

	}

	private void setToLoiterWayPointY() {
		double slopeToCenter = (loiterCenter[1] - position[1]) / (loiterCenter[0] - position[0]);
		double angleToCenter = Math.atan(slopeToCenter);
		double distanceFromCenter = getDistanceFrom(loiterCenter);

		double angleFromLoiterCenterToTangent = Math.asin(loiterRadius / distanceFromCenter);// This should be the
																								// distanceToTangent
		double distanceFromRadius = loiterRadius / Math.tan(angleFromLoiterCenterToTangent);

		if (loiterCenter[1] < position[1]) {

			if (loiterCenter[0] < position[0]) {
				if (counterClockwiseLoiter) {
					wayPoint[1] = position[1]
							- distanceFromRadius * Math.sin(angleToCenter - angleFromLoiterCenterToTangent);
				} else {
					wayPoint[1] = position[1]
							- distanceFromRadius * Math.sin(angleToCenter + angleFromLoiterCenterToTangent);
				}
			} else {
				System.out.println("loiterCenter[1]<position[1]");
				if (counterClockwiseLoiter) {
					wayPoint[1] = position[1]
							+ distanceFromRadius * Math.sin(angleToCenter - angleFromLoiterCenterToTangent);
				} else {
					wayPoint[1] = position[1]
							+ distanceFromRadius * Math.sin(angleToCenter + angleFromLoiterCenterToTangent);
				}
			}
		} else {
			if (loiterCenter[0] < position[0]) {
				if (counterClockwiseLoiter) {
					wayPoint[1] = position[1]
							- distanceFromRadius * Math.sin(angleToCenter - angleFromLoiterCenterToTangent);
				} else {
					wayPoint[1] = position[1]
							- distanceFromRadius * Math.sin(angleToCenter + angleFromLoiterCenterToTangent);
				}
			} else {
				if (counterClockwiseLoiter) {
					wayPoint[1] = position[1]
							+ distanceFromRadius * Math.sin(angleToCenter - angleFromLoiterCenterToTangent);
				} else {
					wayPoint[1] = position[1]
							+ distanceFromRadius * Math.sin(angleToCenter + angleFromLoiterCenterToTangent);
				}
			}

		}
	}

	private void setToLoiterWayPointX() {
		double slopeToCenter = (loiterCenter[1] - position[1]) / (loiterCenter[0] - position[0]);
		double angleToCenter = Math.atan(slopeToCenter);
		double distanceFromCenter = getDistanceFrom(loiterCenter);

		double angleFromLoiterCenterToTangent = Math.asin(loiterRadius / distanceFromCenter);
		double distanceFromRadius = loiterRadius / Math.tan(angleFromLoiterCenterToTangent);

		if (loiterCenter[0] < position[0]) {// need to see if loiterCenter[0]==position[0] BTW
			if (counterClockwiseLoiter) {
				wayPoint[0] = position[0]
						- distanceFromRadius * Math.cos(angleToCenter - angleFromLoiterCenterToTangent);
			} else {
				wayPoint[0] = position[0]
						- distanceFromRadius * Math.cos(angleToCenter + angleFromLoiterCenterToTangent);
			}
		} else {
			if (counterClockwiseLoiter) {
				wayPoint[0] = position[0]
						+ distanceFromRadius * Math.cos(angleToCenter - angleFromLoiterCenterToTangent);
			} else {
				wayPoint[0] = position[0]
						+ distanceFromRadius * Math.cos(angleToCenter + angleFromLoiterCenterToTangent);
			}
		}
	}

	// LOITER SETTERS
	private void setTheta() {
		theta = 0;
		double slopeOnCircle = (position[1] - loiterCenter[1]) / (position[0] - loiterCenter[0]);
		if (position[0] < loiterCenter[0]) {// if on "right" side of circle
			theta = Math.PI + Math.atan(slopeOnCircle);
		} else if (position[0] > loiterCenter[0]) {// if on "left" of circle
			theta = Math.atan(slopeOnCircle);
		} else {// x values are the same
			if (position[1] > loiterCenter[1]) {
				theta = Math.PI / 2;
			} else if (position[1] < loiterCenter[1]) {
				theta = -Math.PI / 2;
			} else {
				position[0] += .01;// will solve many problems, even through fudging the system to make
									// loiterCenter!=position
				System.out.println("theta was undefined in move(case: Loiter setTheta()");
			}
		}
	}

	private void setOmega() {
		double tempOmega = 0;
		tempOmega = getSpeed() / loiterRadius;
		// System.out.println("OMEGA IS NOW: " + tempOmega);
		omega = tempOmega;

	}

//	public void setAltitude(double altitude) {
//		currentAltitude = altitude;
//	}

	public void setSpeed(double speed) {
		goalSpeed = speed;
		if (speed > maxSpeed) {
			System.out.println("you can't set the speed to" + speed + ", it will be reduced to this drone's max speed");
			goalSpeed = maxSpeed;
		}
		myLog.addSpeedChange(speed);
	}

	// NAVIGATION ACTION (MOVEMENT)*****************************
	public void move(double time) {
		// moves the drone's coordinates based on status and currentSpeed
		double[] move = new double[] { -1, -1 };
		// double[] newSpeeds=currentSpeed.clone();
		if (status.equals("") || status.equals(null)) {
			System.out.println("move() was called in Drone without a command, thus drone.status was null");
			System.out.println("I have caught it this time, but next time you might not be so fortunate");
			status = "Hover";
		}
		switch (status) {
		case "Loiter":
			System.out.println("\n\nYOUR IN LOITER\n\n\n");
			if (atCircle) {
				move = moveLoiter(time);
			} else if (!atCircle) {
				move = moveToLoiter(time);
				// System.out.println("Your not at the circle, in TOLoiter status");
			}
			break;
		case "wayPoint":
			move = moveWayPoint(time);
			System.out.println("wayPointing");
			break;
		case "Hover":
			move = new double[] { 0, 0 };
		default:
			System.out.println(
					"\nthe drone doesn't have a path described yet.  it must be Loiter or wayPoint.  Neither was detected\n");
		}

		// System.out.println("The current Speed from getSpeed is :" + getSpeed());
		if (overShot(move)) {
			System.out.println("WE HAVE BEEN OVERRUN BOIS, WE HAVE OVERSHOT AN EVENT");
		}
		if (move[0] == -1 || move[1] == -1) {
			System.out.println("A move has been wrongly excecuted, no new Move[] have been assigned");
		}
		position[0] += move[0];
		position[1] += move[1];
		// System.out.println("move:\t" + move[0] + ", \t" + move[1]);
		myLog.appendPositions(position);

	}

	// WAYPOINT NAVIGATION
	private double[] moveWayPoint(double time) {
		double[] oldSpeed = currentSpeed.clone();
		double[] move = { 0, 0 };
		double[] newSpeed;
		if (isAtDestination()) {// && getSpeed() < closeEnough) {
			System.out.println("********FREEDOM FROM WAYPOINT************");
			setHover();
			return move;
		}
		changeWayPointSpeeds(time);
		checkForIllegalWayPointSpeeds();
		move = updateWayPointDeltaPosition(oldSpeed, time);

		return move;
	}

	private void changeWayPointSpeeds(double time) {
		// ********change the X-values******
		double[] newSpeed = currentSpeed.clone();
		if (wayPointStatus[0].equalsIgnoreCase("Accel")) {
			currentSpeed[0] += maxAcceleration[0] * time;
			if (getDistanceFrom(new double[] { wayPoint[0], position[1] }) <= getMinStoppingDistanceX()
					&& currentSpeed[0] > 0) {
				wayPointStatus[0] = "Deccel";
			}
		} else {
			currentSpeed[0] -= maxAcceleration[0] * time;
			if (currentSpeed[0] < 0
					&& getDistanceFrom(new double[] { wayPoint[0], position[1] }) <= getMinStoppingDistanceX()) {
				wayPointStatus[0] = "Accel";
			}
		}
		// ***********change the Y-values*******
		if (wayPointStatus[1].equalsIgnoreCase("Accel")) {
			currentSpeed[1] += maxAcceleration[1] * time;
			if (getDistanceFrom(new double[] { position[0], wayPoint[1] }) <= getMinStoppingDistanceY()
					&& currentSpeed[1] > 0) {
				wayPointStatus[1] = "Deccel";
				System.out.println("*********the speedsY are being negated(accel->deccel");
			}
		} else {
			currentSpeed[1] -= maxAcceleration[1] * time;
			// System.out.println("it's speed is supposed to be " + currentSpeed[1] +
			// "*****");
			if (currentSpeed[1] < 0
					&& getDistanceFrom(new double[] { position[0], wayPoint[1] }) <= getMinStoppingDistanceY()) {
				wayPointStatus[1] = "Accel";
				System.out.println("*********The speedsY are being negated(deccel->accel");
			}
		}
	}

	private void checkForIllegalWayPointSpeeds() {
		// X_STUFF
		// System.out.println("Current Speed:\t\t" + currentSpeed[0] + "\t" +
		// currentSpeed[1]);
		// System.out.println("Current MaxWaySpeed:\t" + maxWaySpeed[0] + "\t" +
		// maxWaySpeed[1]);
		if (currentSpeed[0] > maxWaySpeed[0]) {
			System.out.println("HIT THE BRAKES BOI1(x too fast)");
			currentSpeed[0] = maxWaySpeed[0];
		} else if (Math.abs(currentSpeed[0]) > maxWaySpeed[0]) {
			currentSpeed[0] = -maxWaySpeed[0];
			System.out.println("HIT THE BRAKES BOI2(x too -fast");
		}
		// Y_STUFF
		if (currentSpeed[1] > maxWaySpeed[1]) {
			// (maxWaySpeed[1]-currentSpeed[1]<0
			currentSpeed[1] = maxWaySpeed[1];
			System.out.println("HIT THE BRAKES BOI3(y too fast)");
		} else if (Math.abs(currentSpeed[1]) > maxWaySpeed[1]) {
			currentSpeed[1] = -maxWaySpeed[1];
			System.out.println("HIT THE BRAKES BOI4(y too -fast");
		}
	}

	private double[] updateWayPointDeltaPosition(double[] oldSpeed, double time) {
		// Increment the point
		double xMove = updatedWayPointDeltaX(oldSpeed[0], time);
		double yMove = updatedWayPointDeltaY(oldSpeed[1], time);
		return new double[] { xMove, yMove };
	}

	private double updatedWayPointDeltaX(double oldXSpeed, double time) {
		double xMove = 0;
		if (currentSpeed[0] > 0) {// Positive x direction velocity
			if (currentSpeed[0] != oldXSpeed) {
				// System.out.println("MAXACCELERATION[0]:\t"+maxAcceleration[0]);
				xMove = Math.abs(Math.pow(currentSpeed[0], 2) - Math.pow(oldXSpeed, 2)) / (2 * maxAcceleration[0]);
			} else {
				xMove = currentSpeed[0] * time;
				// System.out.println("THE ELSE HAS BEEN CALLED in
				// updatewayPointDeltaX****************");
			}
		} else if (currentSpeed[0] < 0) {// Negative x direction velocity
			if (currentSpeed[0] != oldXSpeed) {
				xMove = -Math.abs(Math.pow(currentSpeed[0], 2) - Math.pow(oldXSpeed, 2)) / (2 * maxAcceleration[0]);
			} else {
				xMove = currentSpeed[0] * time;
			}
		}
//		System.out.println("****OLDXSPEED:\t"+oldXSpeed);
//		System.out.println("***CURRENTSPEED:\t"+currentSpeed[0]);
//		System.out.println("******XMOVE: \t"+xMove);
		return xMove;
	}

	private double updatedWayPointDeltaY(double oldYSpeed, double time) {
		double yMove = 0;
		if (currentSpeed[1] > 0) {
			if (currentSpeed[1] != oldYSpeed) {
				yMove = Math.abs(Math.pow(currentSpeed[1], 2) - Math.pow(oldYSpeed, 2)) / (2 * maxAcceleration[1]);
			} else {
				yMove = currentSpeed[1] * time;
			}
		} else if (currentSpeed[1] < 0) {
			if (currentSpeed[1] != oldYSpeed) {
				yMove = -Math.abs(Math.pow(currentSpeed[1], 2) - Math.pow(oldYSpeed, 2)) / (2 * maxAcceleration[1]);
			} else {
				yMove = currentSpeed[1] * time;
			}
		}
		return yMove;
	}

	private void nullWayPoint() {
		wayPoint = null;
		maxWaySpeed = null;
	}
	// TO-LOITER NAVIGATION

	private double[] moveToLoiter(double time) {
		double[] oldSpeed = currentSpeed.clone();
		double[] move = { 0, 0 };
		if (isAtDestination()) {// && getSpeed() < closeEnough) {
			System.out.println("********FREEDOM FROM WAYPOINT************");
			setLoiterStatus();
			move(time);
			return move;
		}
		changeToLoiterSpeeds(time);
		move = updateWayPointDeltaPosition(oldSpeed, time);
		checkForIllegalWayPointSpeeds();
		return move;
	}

	private void changeToLoiterSpeeds(double time) {
		// System.out.println("WayPointStatuses: " + wayPointStatus[0] + ", " +
		// wayPointStatus[1]);
		// System.out.println("MIN SLOWING DISTANCE: " + getMinSlowingDistanceX() + " ,"
		// + getMinSlowingDistanceY());
		// System.out.println("Distance to WayPoint: " + getXDistanceFrom(wayPoint) + "
		// , " + getYDistanceFrom(wayPoint));
		if (wayPointStatus[0].equalsIgnoreCase("Accel")) {

			if (getDistanceFrom(new double[] { wayPoint[0], position[1] }) <= getMinSlowingDistanceX()
					&& currentSpeed[0] > 0) {
				negateWayPointStatus();
				changeToLoiterSpeeds(time);
				return;
			}
			currentSpeed[0] += maxAcceleration[0] * time;
		} else {

			if (currentSpeed[0] < 0
					&& getDistanceFrom(new double[] { wayPoint[0], position[1] }) <= getMinSlowingDistanceX()) {
				negateWayPointStatus();
				changeToLoiterSpeeds(time);
				return;
			}
			currentSpeed[0] -= maxAcceleration[0] * time;
		}
		// ***********change the Y-values*******
		if (wayPointStatus[1].equalsIgnoreCase("Accel")) {
			currentSpeed[1] += maxAcceleration[1] * time;
		} else {
			currentSpeed[1] -= maxAcceleration[1] * time;
			// System.out.println("it's speed is supposed to be " + currentSpeed[1] +
			// "*****");
		}
	}

	private void negateWayPointStatus() {
		if (wayPointStatus[0].equalsIgnoreCase("accel")) {
			wayPointStatus[0] = "Deccel";
		} else if (wayPointStatus[0].equalsIgnoreCase("deccel")) {
			wayPointStatus[0] = "Accel";
		} else {
			System.out.println("WayPointStatus[0] has not been initialized, found in negateWayPointSTatus");
		}
		if (wayPointStatus[1].equalsIgnoreCase("accel")) {
			wayPointStatus[1] = "Deccel";
		} else if (wayPointStatus[1].equalsIgnoreCase("deccel")) {
			wayPointStatus[1] = "Accel";
		} else {
			System.out.println("WayPointStatus[0] has not been initialized, found in negateWayPointSTatus");
		}
	}

	// lOITER NAVIGATION

	private double[] moveLoiter(double time) {
		double oldOmega = omega;
		double[] move = { 0, 0 };
		// currentRadius = getDistanceFrom(loiterCenter);
		// System.out.println("currentRadius:"+currentRadius);
		double newMaxAccel = maxTotalAcceleration;

		updateRadius(time);
		changeLoiterOmega(time, newMaxAccel);
		checkForIllegalOmegas();
		changeLoiterTheta(time, oldOmega);
		move = updateLoiterDeltaXY();

		currentSpeed[0] = move[0] / time;// Need these next two lines for getSpeed to function correctly while
		currentSpeed[1] = move[1] / time;// Loitering
		// System.out.println("goalSpeed is: " + goalSpeed);
		// System.out.println("New theta" + theta);
		return move;
	}

	private void updateRadius(double time) {
		if (currentRadius < loiterRadius + closeEnough && currentRadius > loiterRadius - closeEnough) {
			changingOrbit = false;
		} else {
			changingOrbit = true;
			System.out.println("Changing Orbit***");
			if (currentRadius < loiterRadius) {
				currentRadius += getSpeed() * time * .2;
			} else {
				currentRadius -= getSpeed() * time * .2;
			}
		}
	}

	private void changeLoiterOmega(double time, double maxUsableAccel) {
		alpha = (maxUsableAccel - Math.pow(getSpeed(), 2) / currentRadius) / currentRadius;
		omega += alpha * time;
		// System.out.println("New OMEGA from changeLoiterOmega :" + omega);
		// System.out.println("the new alpha from changeLoiterOmega :" + alpha);
	}

	private void checkForIllegalOmegas() {
		if (omega > maxOmega) {
			System.out.println("HIT THE LOITER BRAKES(too +fast");
			omega = maxOmega;
		} else if (Math.abs(omega) > maxOmega) {
			omega = -maxOmega;
		}
	}

	private void changeLoiterTheta(double time, double oldOmega) {
		// System.out.println(maxTotalAcceleration);
		// System.out.println("Alpha: " + alpha);
		if (counterClockwiseLoiter) {
			if (oldOmega != omega) {
				theta += (Math.pow(omega, 2) - Math.pow(oldOmega, 2)) / (2 * alpha);
			} else {
				theta += omega * time;
			}
		} else if (!counterClockwiseLoiter) {
			if (oldOmega != omega) {
				theta -= (Math.pow(omega, 2) - Math.pow(oldOmega, 2)) / (2 * alpha);
			} else {
				theta -= omega * time;
			}
		} else {
			System.out.println(
					"counterClockwiseLoiter has not been ititilized, and move has been called when the drone is in Loiter.  Set this attribute");
		}
	}

	private double[] updateLoiterDeltaXY() {
		double xMove, yMove;
		xMove = loiterCenter[0] - position[0] + (currentRadius * Math.cos(theta));
		yMove = loiterCenter[1] - position[1] + (currentRadius * Math.sin(theta));
		return new double[] { xMove, yMove };
	}

	private boolean isAtDestination() {
		boolean isThere = false;
		if (status.equalsIgnoreCase("wayPoint")) {
			if (getDistanceFrom(wayPoint) < closeEnough) {
				isThere = true;
			}
		} else if (status.equalsIgnoreCase("loiter")) {
			if (getDistanceFrom(wayPoint) < closeEnough) {
				isThere = true;
			}
		}
		return isThere;
	}

	// OVERSHOOTING METHODS (IF MOVE SKIPS OVER A WAYPOINT/EVENT) B/C LARGE TIME
	// INTERVAL
	private boolean overShot(double[] move) {
		boolean overShotIt = false;
		switch (status) {
		case "wayPoint":
			if (!(Math.signum(wayPoint[0] - position[0]) == Math.signum(wayPoint[0] - position[0] + move[0]))) {
				// if opposite sign(overshot);

			}
		}
		return overShotIt;
	}

	// HERE ARE THE ACCESORS WITH CALCULATIONS

	// *********************Accessors with calculations*************************
	private double getMinStoppingDistance() {
		double distance;
		distance = Math.pow(getSpeed(), 2) / (2 * maxAcceleration[0]);
		return distance;
	}

	private double getMinSlowingDistance(double entranceSpeed) {
		double distance;
		distance = (Math.pow(getSpeed(), 2) - Math.pow(entranceSpeed, 2)) / 2 * maxTotalAcceleration;
		return distance;
	}

	private double getMinSlowingDistanceX() {
		double distance;
		distance = Math.abs(Math.pow(maxEntranceSpeed[0], 2) - Math.pow(currentSpeed[0], 2)) / (2 * maxAcceleration[0]);
		return distance;
	}

	private double getMinSlowingDistanceY() {
		double distance;
		distance = Math.abs(Math.pow(maxEntranceSpeed[1], 2) - Math.pow(currentSpeed[1], 2)) / (2 * maxAcceleration[1]);
		return distance;
	}

	private double getMinStoppingDistanceY() {
		double distance;
		distance = Math.pow(currentSpeed[1], 2) / (2 * maxAcceleration[1]);
		return distance;
	}

	private double getMinStoppingDistanceX() {
		double distance;
		distance = Math.pow(currentSpeed[0], 2) / (2 * maxAcceleration[0]);
		return distance;
	}

	private double getMinStoppingRadius() {
		/*
		 * Returns the safe radius for deceleration during a orbit transfer, assuming
		 * any acceleration is feasible (does't take into account maxTotalAcceleration),
		 * thus illegal
		 */
		double radius;
		radius = (2 * loiterRadius + currentRadius) / 3;
		return radius;
	}

	public double getSearchableArea(double distanceFromHub) {
		return -1;
	}

	public double getDistanceFrom(double[] position) {
		double distance = 0;
		try {
			distance = Math.sqrt(
					Math.pow((position[0] - this.position[0]), 2) + Math.pow((position[1] - this.position[1]), 2));
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return distance;
	}

	private double getXDistanceFrom(double[] position) {
		double distance;
		distance = Math.abs(position[0] - this.position[0]);
		return distance;
	}

	private double getYDistanceFrom(double[] position) {
		double distance;
		distance = Math.abs(position[1] - this.position[1]);
		return distance;
	}

	public double getSpeed() {
		double speed;
		speed = Math.sqrt(Math.pow((currentSpeed[0]), 2) + Math.pow((currentSpeed[1]), 2));
		return speed;
	}

	// **********************Accessors for the FlightLog***********
	public double[] getPosition(int iteration) {
		double[] pos;
		pos = myLog.getPosition(iteration);
		return pos;
	}

	public double[][] getPositions() {
		double[][] poss;
		poss = myLog.getPositions();
		return poss;
	}

	public String getCommand(int index) {
		String com = "";
		com = myLog.getCommand(index);
		return com;
	}

	// ACCESORS WITHOUT CALCULATIONS

	// *************************Accessors without logic**************************

	// HERE BELOW ARE ACCESORS W/O CALCULATIONS
	public double[] getPosition() {
		return position;// return x,y, and z
	}

	public double getXPosition() {
		return position[0];
	}

	public double getYPosition() {
		return position[1];
	}

	public double[] getWayPoint() {
		double[] toReturn;
		try {
			toReturn = wayPoint.clone();
		} catch (NullPointerException e) {
			toReturn = null;
		}
		return toReturn;
	}

	public boolean isAtCircle() {
		boolean there = false;
		try {
			there = atCircle;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return there;
	}

	public boolean isInAir() {
		return inAir;
	}

	public boolean isChangingOrbit() {
		boolean change = false;
		try {
			change = changingOrbit;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return change;
	}

	public boolean batteryIsLow() {
		return lowBattery;
	}

	public String getStatus() {
		String toReturn;
		if (status != null) {
			toReturn = status;
		} else {
			toReturn = "Null";
		}
		return toReturn;
	}

//	public double getMinSpeed() {
//		return this.minSpeed;
//	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public double getGoalSpeed() {
		return goalSpeed;
	}

	public double getCruisingSpeed() {
		return efficientSpeed;
	}

	public double getLoiterRadius() {
		double rad = -1;
		try {
			rad = loiterRadius;
		} catch (NullPointerException e) {
			System.err.println("LoiterRadius has not been initilized.  Are you sure this drone is in Loiter?");
			e.printStackTrace();

		}
		return rad;
	}

	public double[] getLoiterCenter() {
		return loiterCenter;
	}

}
