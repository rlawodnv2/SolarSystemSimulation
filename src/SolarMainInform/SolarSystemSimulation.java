package SolarMainInform;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SolarSystemSimulation extends JPanel {
	private static final int WIDTH = 1920;
	private static final int HEIGHT = 1080;
	private static final int NUM_CELESTIALS = 9;
	private static final int NUM_ASTEROIDS = 3;
	private static final double GRAVITATIONAL_CONSTANT = 0.1;
	private static final double TIME_STEP = 0.01;

	private CelestialBody[] bodies;
	private Asteroid[] asteroids;
	private Random random;

	private class CelestialBody {
		double mass;
		double distance; // 초기 거리
		double angle; // 초기 각도
		double speed; // 초기 속도
		double radius; // 행성 크기
		Color color;
		String name; // 행성 이름

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

	private class Asteroid {
		double x, y, dx, dy; // 위치와 속도
		boolean crossesOrbit; // 공전 궤도와 교차 여부

		public Asteroid(double x, double y, double dx, double dy, boolean crossesOrbit) {
			this.x = x;
			this.y = y;
			this.dx = dx;
			this.dy = dy;
			this.crossesOrbit = crossesOrbit;
		}
	}

	public SolarSystemSimulation() {
		bodies = new CelestialBody[NUM_CELESTIALS];
		asteroids = new Asteroid[NUM_ASTEROIDS];
		random = new Random();

		bodies[0] = new CelestialBody(1000, 0, 0, 0, 30, new Color(255, 255, 0), "Sun");
		bodies[1] = new CelestialBody(0.055, 70, 0, 2.7, 5, new Color(160, 160, 160), "Mercury");
		bodies[2] = new CelestialBody(0.815, 110, 0, 2.1, 8, new Color(255, 165, 0), "Venus");
		bodies[3] = new CelestialBody(1, 150, 0, 2, 10, new Color(65, 105, 225), "Earth");
		bodies[4] = new CelestialBody(0.107, 190, 0, 1.6, 7, new Color(220, 20, 60), "Mars");
		bodies[5] = new CelestialBody(317.8, 260, 0, -1, 25, new Color(192, 192, 192), "Jupiter");
		bodies[6] = new CelestialBody(95.2, 320, 0, -0.7, 20, new Color(255, 215, 0), "Saturn");
		bodies[7] = new CelestialBody(14.5, 370, 0, -0.5, 15, new Color(0, 255, 255), "Uranus");
		bodies[8] = new CelestialBody(17.1, 420, 0, -0.4, 15, new Color(0, 0, 128), "Neptune");

		generateAsteroids();

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				handleMouseClick(e.getX(), e.getY());
			}
		});
	}

	private void generateAsteroids() {
		for (int i = 0; i < NUM_ASTEROIDS; i++) {
			boolean crossesOrbit = random.nextBoolean();
			double x = random.nextInt(WIDTH);
			double y = random.nextInt(HEIGHT);
			double dx = random.nextDouble() * 4 - 2;
			double dy = random.nextDouble() * 4 - 2;
			asteroids[i] = new Asteroid(x, y, dx, dy, crossesOrbit);
		}
	}

	private void handleAsteroidCollisions() {
		for (Asteroid asteroid : asteroids) {
			for (CelestialBody body : bodies) {
				// 행성의 충돌 범위 내에 소행성이 있으면 충돌 처리
				int bodyX = (int) (WIDTH / 2 + body.distance * Math.cos(body.angle));
				int bodyY = (int) (HEIGHT / 2 + body.distance * Math.sin(body.angle));
				double distance = Math.sqrt(Math.pow(asteroid.x - bodyX, 2) + Math.pow(asteroid.y - bodyY, 2));

				if (distance <= body.radius) {
					// 충돌 시 소행성 정보를 엑셀에 기록
					writeToExcel(asteroid);

					// 충돌 후 소행성 속도와 각도 처리 (예시)
					asteroid.dx = -asteroid.dx; // 반사된 속도
					asteroid.dy = -asteroid.dy;
					asteroid.x = bodyX + body.radius; // 충돌 후 위치 설정
					asteroid.y = bodyY + body.radius;
				}
			}
		}
	}
	
	private void handleMouseClick(int x, int y) {
		for (CelestialBody body : bodies) {
			int bodyX = (int) (WIDTH / 2 + body.distance * Math.cos(body.angle));
			int bodyY = (int) (HEIGHT / 2 + body.distance * Math.sin(body.angle));
			if (Math.sqrt(Math.pow(x - bodyX, 2) + Math.pow(y - bodyY, 2)) <= body.radius) {
				explainPlanet(body.name);
				return;
			}
		}
	}

	private void explainPlanet(String planetName) {
		for (CelestialBody body : bodies) {
			if (body.name.equals(planetName)) {
				String basicInfo = 
					"Explanation:\n" +
					"Name: " + body.name + "\n" +
					"Mass: " + body.mass + " Earth masses\n" +
					"Orbit Radius: " + body.distance + " units\n" +
					"Orbit Speed: " + body.speed + " units/s\n\n";
				String aiText = generateAIExplanation(body);

				String imagePath = "/SolarMainInform/resource/" + planetName.toLowerCase() + ".jpg";
				ImageIcon planetImage = new ImageIcon(getClass().getResource(imagePath));

				JOptionPane.showMessageDialog(this, basicInfo + aiText, "Planet Info", JOptionPane.INFORMATION_MESSAGE, planetImage);
				return;
			}
		}
	}

	private String generateAIExplanation(CelestialBody body) {
		if(body.name.equals("Sun")) {
			return "태양(太陽, Sun)은 태양계의 중심에 존재하는 항성(별)으로, 태양계의 유일한 항성이자 에너지의 근원이다. "
					+ "\r\n"
					+ "태양이 있기에 지구에 낮과 밤의 구분, 사계절과 기후 더 나아가 생명이 존재할 수 있다. "
					+ "\r\n"
					+ "태양은 우리 은하 내에서도 드물게 존재하는 G형 주계열성으로, "
					+ "\r\n"
					+ "덕분에 4광년 떨어진 센타우루스자리 알파에서도 태양은 맨눈으로 잘 보일 정도로 밝은 별이다.";
		}
		else if (body.name.equals("Earth")) {
			return "지구(地球, Earth)는 태양계에서 세 번째 궤도를 도는 행성이다. "
					+ "\r\n"
					+ "현재까지 알려진 바로는 인류를 비롯한 동물, 식물 등 다양한 생명체가 서식하는 유일한 천체이다. "
					+ "\r\n"
					+ "약 45.4억 년 전에 형성되었으며, 하나의 자연위성인 달을 가지고 있다.\r\n"
					+ "\r\n"
					+ "지구는 태양으로부터 평균 1억 4960만 km 떨어져 있으며, 이는 태양빛이 지구에 도달하는데 약 8분 20초가 걸린다는 것을 의미한다. "
					+ "\r\n"
					+ "지구의 평균 반지름은 6,371km이며, 적도 둘레는 약 40,075km이다.\r\n"
					+ "\r\n"
					+ "지구의 표면은 약 71%가 바다로 덮여있으며, 나머지 29%가 대륙을 이루고 있다. "
					+ "\r\n"
					+ "지구를 둘러싼 대기권은 질소 약 78%, 산소 약 21%로 구성되어 있으며, 이러한 환경은 생명체가 살아가는데 필수적인 조건을 제공한다.\r\n"
					+ "\r\n"
					+ "지구는 자전축이 23.44도 기울어져 있어 계절의 변화가 생기며, 하루에 한 바퀴씩 자전하고 1년에 한 바퀴씩 태양 주위를 공전한다. "
					+ "\r\n"
					+ "이러한 운동은 낮과 밤의 교차, 계절의 변화 등 지구의 기후와 환경에 직접적인 영향을 미친다.";
		} else if(body.name.equals("Mercury")) {
			return "수성(水星 / Mercury)은 태양계의 행성 중 태양과 가장 가까이 있는 천체이다.\r\n"
					+ "\r\n"
					+ "태양계 모형만 보면 감이 잘 오지 않겠지만, 가장 가깝다는 태양과 수성 사이의 거리는 태양 지름의 약 41배나 된다.";
		} else if(body.name.equals("Venus")) {
			return "금성(金星, Venus)은 태양계의 두번째 행성이다. "
					+ "\r\n"
					+ "지구에서 관측할 수 있는 천체 중에서 3번째로 밝은데, 첫 번째는 태양, 두 번째는 달이다. "
					+ "\r\n"
					+ "지구에서 관측되는 이미지는 아름답지만, 실제로는 높은 고온, 고압, 부식성 대기 등의 극한 환경을 가진 행성이다.";
		} else if(body.name.equals("Mars")) {
			return "화성(火星, Mars)은 태양계의 네 번째 행성이다. 산화철로 인한 붉은 빛이 감도는 사막 지형이 형성되어 있다.\r\n"
					+ "\r\n"
					+ "지구를 제외한 태양계 내 모든 행성 중 표면 탐사가 가장 많이 이루어진 행성이며,"
					+ "\r\n"
					+ "물의 존재가 확인되고 테라포밍의 가능성이 보이는 등 인류 문명의 우주 개발에서 중요한 역할을 맡게 될 것으로 여겨지는 천체이다. "
					+ "\r\n"
					+ "화성 표면에 생명체의 존재 가능성이 과거부터 논의되고는 있으나 아직까지 화성에서 생명체는 발견되지 않았다. "
					+ "\r\n"
					+ "애초에 표면온도도 평균수치가 지구의 남극 수준으로 낮은 데다가 대기도 희박하고 태양풍을 막아주는 행성의 자기장도 약해서 고등 생명체가 살기에는 여전히 혹독한 환경이고, "
					+ "\r\n"
					+ "생명체가 만약 존재한다고 쳐도 미생물정도일 것이다.";
		} else if(body.name.equals("Jupiter")) {
			return "태양계의 5번째 궤도를 돌고 있는 목성은 태양계에서 가장 거대한 행성이다.\r\n"
					+ "목성은 태양계 여덟 개 행성을 모두 합쳐 놓은 질량의 2/3 이상을 차지하고 지름이 약 14만 3,000km로 지구의 약 11배에 이른다.";
		} else if(body.name.equals("Saturn")) {
			return "토성(土星, Saturn)은 태양계의 여섯 번째 행성으로, 태양계 내 행성 중에서 두 번째로 큰 크기를 가지고 있다.\r\n"
					+ "\r\n"
					+ "지구와 비교하면 약 95배 정도 무거우며, 부피는 지구의 764배로 거대한 고리를 가진 행성으로 유명하다.";
		} else if(body.name.equals("Uranus")) {
			return "천왕성(天王星, Uranus)은 태양계의 일곱 번째 행성이다.\r\n"
					+ "\r\n"
					+ "핵은 얼음이며 지표는 액체 메테인, 대기는 수소와 헬륨으로 이루어져 있고, 평균 기온은 -224℃이다."
					+ "\r\n"
					+ " 1 천왕성일(자전)은 지구 기준으로 17시간 14분이며, 1 천왕성년(공전)은 지구 기준 84년이다. "
					+ "\r\n"
					+ "의외로 큰 덩치에 비해 중력은 지구의 88%로 지구에서 체중이 100kg인 사람이 천왕성을 가면 88kg이 된다.";
		} else if(body.name.equals("Neptune")) {
			return "해왕성(海王星, Neptune)은 태양계의 8번째 행성으로, 과거 9번째 행성으로 여겨졌던 명왕성이 행성 분류에서 제외된 이후 태양계의 마지막 행성으로 인정되고 있는 행성이다.\r\n"
					+ "\r\n"
					+ "천왕성과 닮은 점이 많은 행성인데, 먼저 반지름이 천왕성보다 지구 지름의 1/5만큼 작은 정도로 거의 비슷한 크기이며, 대기에 포함된 메탄에 의해 푸른색으로 보이는 것도 비슷하다.\r\n"
					+ "\r\n"
					+ "목성의 대적반처럼 표면에 대흑점이 있는데, 지구 지름 정도의 크기로 상당히 크다. 짙은 푸른색 빛이 인상적이라 그런지, 뚜렷한 고리가 있는 토성과 더불어 인기가 꽤 있는 행성이다.";
		} else if(body.name.equals("Pluto")) {
			return "명왕성(冥王星, Pluto)은 태양계의 왜행성 중 하나로, 최초로 발견된 카이퍼 벨트 천체이다. 현재까지 크기가 확인된 해왕성 바깥 태양계 천체 가운데에서 가장 큰 천체다.\r\n"
					+ "\r\n"
					+ "1930년 2월 18일 미국의 천문학자 클라이드 톰보(Clyde Tombaugh)가 발견한 이래 2006년 행성의 기준이 수정되기 전까지 태양계의 아홉 번째 행성으로 인식되었다.";
		}
		return "This celestial body has fascinating characteristics.";
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setBackground(Color.BLACK);

		for (CelestialBody body : bodies) {
			int x = (int) (WIDTH / 2 + body.distance * Math.cos(body.angle));
			int y = (int) (HEIGHT / 2 + body.distance * Math.sin(body.angle));
			int diameter = (int) (2 * body.radius);

			g.setColor(body.color);
			g.fillOval(x - (int) body.radius, y - (int) body.radius, diameter, diameter);
			
			// 행성 이름 그리기
			g.setColor(Color.WHITE);
			Font font = new Font("Arial", Font.PLAIN, 12);
			g.setFont(font);
			g.drawString(body.name, x - body.name.length() * 3, y + diameter / 2 + 15); // 행성 이름 위치 조정

			body.angle += TIME_STEP * body.speed; // 행성 각도 업데이트
		}

		for (Asteroid asteroid : asteroids) {
			g.setColor(asteroid.crossesOrbit ? Color.RED : Color.GRAY);
			g.fillOval((int) asteroid.x, (int) asteroid.y, 5, 5);
			asteroid.x += asteroid.dx;
			asteroid.y += asteroid.dy;

			// 화면 경계 체크
			if (asteroid.x < 0 || asteroid.x > WIDTH || asteroid.y < 0 || asteroid.y > HEIGHT) {
				asteroid.x = random.nextInt(WIDTH);
				asteroid.y = random.nextInt(HEIGHT);
			}
		}
	}

	public void updateSimulation() {
		for (CelestialBody body : bodies) {
			body.angle += body.speed * TIME_STEP;
			if (body.angle > 2 * Math.PI) {
				body.angle -= 2 * Math.PI;
			}
		}

		for (Asteroid asteroid : asteroids) {
			asteroid.x += asteroid.dx;
			asteroid.y += asteroid.dy;

			if (asteroid.x < 0 || asteroid.x > WIDTH || asteroid.y < 0 || asteroid.y > HEIGHT) {
				generateAsteroids();
			}
		}

		handleAsteroidCollisions(); // 소행성 충돌 감지 및 처리
		
		repaint();
	}
	
	// 엑셀 파일 생성
	private void createExcelFile() {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Asteroid Collisions");

		// 헤더 생성
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("Asteroid X");
		header.createCell(1).setCellValue("Asteroid Y");
		header.createCell(2).setCellValue("Asteroid Speed X");
		header.createCell(3).setCellValue("Asteroid Speed Y");
		header.createCell(4).setCellValue("Asteroid Angle");

		try (FileOutputStream fileOut = new FileOutputStream("asteroid_collisions.xlsx")) {
			workbook.write(fileOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 엑셀 파일에 데이터 추가
	private void writeToExcel(Asteroid asteroid) {
		Workbook workbook;
		Sheet sheet;

		try {
			FileInputStream fileIn = new FileInputStream("asteroid_collisions.xlsx");
			
			workbook = new XSSFWorkbook(fileIn);
			sheet = workbook.getSheetAt(0);

			
			// 파일이 없다면 생성, 있으면 읽기
			// 기존 데이터를 불러오는 로직은 생략
			if(sheet == null) {
				sheet = workbook.createSheet("Asteroid Collisions");
				
				Row header = sheet.createRow(0);
				header.createCell(0).setCellValue("Asteroid X");
				header.createCell(1).setCellValue("Asteroid Y");
				header.createCell(2).setCellValue("Asteorid Speed X");
				header.createCell(3).setCellValue("Asteorid Speed Y");
				header.createCell(4).setCellValue("Asteroid Angle");
			}

			// 데이터 추가
			int rowCount = sheet.getPhysicalNumberOfRows();
			Row row = sheet.createRow(rowCount);
			row.createCell(0).setCellValue(asteroid.x);
			row.createCell(1).setCellValue(asteroid.y);
			row.createCell(2).setCellValue(asteroid.dx);
			row.createCell(3).setCellValue(asteroid.dy);
			row.createCell(4).setCellValue(Math.atan2(asteroid.dy, asteroid.dx)); // 속도 벡터의 각도

			// 파일 저장
			try (FileOutputStream fileOut = new FileOutputStream("asteroid_collisions.xlsx")) {
				workbook.write(fileOut);
			}
			
			workbook.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Solar System Simulation");
		SolarSystemSimulation simulation = new SolarSystemSimulation();
		frame.add(simulation);
		frame.setSize(WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		// 엑셀 파일 생성
		simulation.createExcelFile();

		Timer timer = new Timer(50, e -> simulation.updateSimulation());
		timer.start();
	}
}