import java.io.*;
import java.util.ArrayList;

public class FlightLog {
	private String filePath;
	private final String commandExtention = "_commands";
	private final String positionExtention = "_positions";
	private String[] commands;
	private double[][] positions;
	final char commandSeparation = '#';

	public FlightLog() {
		// System.out.println("Flight log has been created, although no variables are
		// initilized yet");
		positions = new double[0][2];
		commands = new String[0];
	}

	public FlightLog(String fileName) {
		positions = new double[0][2];
		commands = new String[0];
		filePath = fileName;
	}

	public String toString() {
		String me = "I don't know who I am yet, please initilize in FlightLog.toString";
		return me;
	}

	public void appendPositions(double[] position) {
		double[][] tempPos = new double[this.positions.length + 1][2];
		System.arraycopy(this.positions, 0, tempPos, 0, this.positions.length);
		tempPos[tempPos.length - 1] = position.clone();
		this.positions = tempPos;
	}

	public void appendCommands(String command) {
		String[] tempCommands = new String[this.commands.length + 1];
		System.arraycopy(this.commands, 0, tempCommands, 0, this.commands.length);
		tempCommands[tempCommands.length - 1] = command;
		this.commands = tempCommands;
	}

	public void addLoiter(double radius, double[] position) {
		String command = "MAV_CMD_NAV_LOITER_UNLIM(,,";// 2x empty params
		command += Double.toString(radius);
		command += ",,";// yaw angle required here for operation
		command += Double.toString(position[1]) + ", " + Double.toString(position[0]);// in Lat/Long
		command += ",)";// include Alt here for operation
		appendCommands(command);
	}

	public void addWayPoint(double[] position) {
		String command = "MAV_CMD_NAV_WAYPOINT(1,5,0,NaN";// Hold time, AcceptanceRadius(m), distanceFrom, Yaw ang
		command += Double.toString(position[1]) + "," + Double.toString(position[0]);// in Lat/long
		command += ",";// include Alt here for operation
		appendCommands(command);
	}

	public void addHover() {
		String command = "MAV_CMD_DO_PAUSE_CONTINUE(0)";// 0 for hold, 1 for continue
		appendCommands(command);
	}

	public void addSpeedChange(double speed) {
		String command = "MAV_CMD_DO_CHANGE_SPEED(0,";// 0 for airspeed(1=Ground, 2=Climb,3=Descent
		command += Double.toString(speed);
		command += ",-1,0)";// Throttle(-1 means no change), Absolute?(0 means yes)
		appendCommands(command);
	}

	public void saveLog(String filename) {
		filePath = filename;
		savePositions();
		saveCommands();

	}

	public void saveLog() {
		if (filePath.equals(null)) {
			System.err
					.print("Initialize the file Path through saveLog(filepath) or by construction FlightLog(filePath)");
		} else {
			saveLog(filePath);
		}
	}

	private void savePositions() {
		try {
			FileOutputStream fileStream = new FileOutputStream(filePath + positionExtention);
			DataOutputStream dataStream = new DataOutputStream(fileStream);

			for (double[] d : positions) {
				try {
					dataStream.writeDouble(d[0]);
					dataStream.writeDouble(d[1]);
				} catch (IOException e) {
					System.err.print("FlightLog.saveLog() ran into an issue while saving the positions");
					e.printStackTrace();
				}
			}
			dataStream.close();
		} catch (FileNotFoundException e) {
			System.err.print("The file was not found in FlightLog.saveLog(file)");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.print("There was a problem saving to the file");
			e.printStackTrace();
		}
	}

	private void saveCommands() {
		try {

			FileOutputStream filedStream = new FileOutputStream(filePath + commandExtention);
			DataOutputStream commandStream = new DataOutputStream(filedStream);
			for (String s : commands) {
				s += commandSeparation;
				try {
					commandStream.writeChars(s);
				} catch (IOException e) {
					System.err.print("FlightLog.saveLog() ran into an issue while saving the commands");
					e.printStackTrace();
				}
			}
			commandStream.close();
		} catch (FileNotFoundException e) {
			System.err.print("The file was not found in FlightLog.saveLog(file)");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.print("There was a problem saving to the file");
			e.printStackTrace();
		}
	}

	public void readLog(String filename) {
		filePath = filename;
		readPositions();
		readCommands();

	}

	private void readPositions() {
		ArrayList<Double> positions = new ArrayList<Double>();

		try {
			FileInputStream fileStream = new FileInputStream(filePath + positionExtention);
			DataInputStream dataStream = new DataInputStream(fileStream);

			try {
				while (true) {

					positions.add(dataStream.readDouble());
				}
			} catch (EOFException eof) {
				// System.err.print("EOFException");
			} catch (IOException e) {
				System.err.print("In FlightLog.readLog() there was a problem\n reading the positions");
				e.printStackTrace();
			}
			dataStream.close();
		} catch (FileNotFoundException e) {
			System.err.print("The file wasn't found at FlightLog.readLog()");
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		this.positions = new double[positions.size() / 2][2];
		for (int i = 0; i < positions.size() / 2; i++) {
			this.positions[i][0] = positions.get(i * 2);
			this.positions[i][1] = positions.get((i * 2) + 1);
		}
	}

	private void readCommands() {
		ArrayList<String> commands = new ArrayList<String>();
		try {
			FileInputStream filedStream = new FileInputStream(filePath + commandExtention);
			DataInputStream commandStream = new DataInputStream(filedStream);
			try {
				char temp;
				String com = "";
				while (true) {
					temp = commandStream.readChar();
					if (temp == commandSeparation) {
						commands.add(com);
						com = "";
					} else {
						com += temp;
					}
				}
			} catch (EOFException eof) {
			} catch (IOException e) {
				System.err.print("In FlightLog.readLog() there was a problem reading the commands");
				e.printStackTrace();
			}
			commandStream.close();
		} catch (FileNotFoundException e) {
			System.err.print("The file wasn't found at FlightLog.readLog()");
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		this.commands = new String[commands.size()];
		for (int i = 0; i < commands.size(); i++) {
			this.commands[i] = commands.get(i);
		}
	}

	public void readLog() {
		if (filePath.equals(null)) {
			System.err.print(
					"Initilize the file Path through readLog(filePath) or through contruction FlightLog(filePath)");
		} else {
			readLog(filePath);
		}
	}

	public void printPositions() {
		for (double[] d : positions) {
			System.out.println(d[0] + ", " + d[1]);
		}
	}

	public void printCommands() {
		for (String s : commands) {
			System.out.println(s);
		}
	}

	public double[][] getPositions(){
		return positions;
	}
	
	public double[] getPosition(int index) {
		double[] toGive = new double[2];
		try {
			toGive = positions[index];
		} catch (NullPointerException e) {
			System.err.print("the index in FlightLog.getPosition(index) is invalid");
			e.printStackTrace();
		}
		return toGive;
	}
	
	public String getCommand(int index) {
		String com="";
		try {
			com=commands[index];
		}catch(NullPointerException e) {
			System.err.println("The index in FlightLog.getCommand(index) is invalid");
			e.printStackTrace();
		}
		return com;
	}
	
	public String getFilePath() {
		return filePath;
	}


}
