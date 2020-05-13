
public class test3 {
	public static void main(String[] args) {
	Drone d=new Drone(2);
	d.setLoiterTurn(30,new double[] {200,200});
	d.move(2);

	System.out.println(d);
	System.out.println(d.getXPosition());
	System.out.println(d.getYPosition());
	System.out.println(d.getDistanceFrom(new double[] {200,200}));
	}
}
