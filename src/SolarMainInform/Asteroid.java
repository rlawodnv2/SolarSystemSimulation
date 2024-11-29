package SolarMainInform;

public class Asteroid {
	public double x, y;
	public double dx, dy;
	public boolean crossesOrbit;
	public int radius;

	public Asteroid(double x, double y, double dx, double dy, int radius, boolean crossesOrbit) {
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.radius = radius;
		this.crossesOrbit = crossesOrbit;
	}
}