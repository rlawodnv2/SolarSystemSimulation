package SolarMainInform;

import java.awt.Color;

public class CelestialBody {
	public double mass;
	public double distance;
	public double angle;
	public double speed;
	public double radius;
	public Color color;
	public String name;

	public CelestialBody(double mass, double distance, double angle, double speed, double radius, Color color, String name) {
		this.mass = mass;
		this.distance = distance;
		this.angle = angle;
		this.speed = speed;
		this.radius = radius;
		this.color = color;
		this.name = name;
	}
}