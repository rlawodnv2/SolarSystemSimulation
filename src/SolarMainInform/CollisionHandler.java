package SolarMainInform;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CollisionHandler {
	public static boolean checkCollision(Asteroid asteroid, CelestialBody body, int width, int height) {
		double dx = asteroid.x - (width / 2 + body.distance * Math.cos(body.angle));
		double dy = asteroid.y - (height / 2 + body.distance * Math.sin(body.angle));
		double distance = Math.sqrt(dx * dx + dy * dy);
		return distance < (asteroid.radius + body.radius);
	}

	public static void saveCollisionData(Asteroid asteroid1, Asteroid asteroid2) {
		try (FileWriter writer = new FileWriter("collision_data.xlsx", true)) {
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("Collisions");

			Row header = sheet.createRow(0);
			header.createCell(0).setCellValue("Time");
			header.createCell(1).setCellValue("Asteroid 1 X");
			header.createCell(2).setCellValue("Asteroid 1 Y");
			header.createCell(3).setCellValue("Asteroid 2 X");
			header.createCell(4).setCellValue("Asteroid 2 Y");

			Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
			row.createCell(0).setCellValue(System.currentTimeMillis());
			row.createCell(1).setCellValue(asteroid1.x);
			row.createCell(2).setCellValue(asteroid1.y);
			row.createCell(3).setCellValue(asteroid2.x);
			row.createCell(4).setCellValue(asteroid2.y);

			FileOutputStream fileOut = new FileOutputStream("collision_data.xlsx");
			workbook.write(fileOut);
			fileOut.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}