package MerchainRunning;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExcelReader {

	public static Instances loadDataFromExcel(String filePath) throws IOException {
		// 엑셀 파일 열기
		FileInputStream fis = new FileInputStream(new File(filePath));
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 선택

		List<Attribute> attributes = new ArrayList<>();
		List<Instance> instances = new ArrayList<>();
		List<HashMap<String, Integer>> nominalMappings = new ArrayList<>();

		// 첫 번째 행을 속성명으로 사용
		Row headerRow = sheet.getRow(0);
		int numAttributes = headerRow.getPhysicalNumberOfCells();

		for (int i = 0; i < numAttributes; i++) {
			Cell cell = headerRow.getCell(i);
			String attributeName = cell.getStringCellValue();

			// 범주형 데이터 초기화
			nominalMappings.add(new HashMap<>());

			// 속성을 기본적으로 연속형으로 추가
			attributes.add(new Attribute(attributeName));
		}

		// 데이터 읽기
		for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
			Row row = sheet.getRow(i);
			double[] values = new double[numAttributes];

			for (int j = 0; j < numAttributes; j++) {
				Cell cell = row.getCell(j);

				if (cell == null || cell.getCellType() == CellType.BLANK) {
					values[j] = Double.NaN; // 빈 셀 처리
				} else if (cell.getCellType() == CellType.NUMERIC) {
					values[j] = cell.getNumericCellValue();
				} else if (cell.getCellType() == CellType.STRING) {
					String stringValue = cell.getStringCellValue();

					// 문자열을 범주형 데이터로 처리
					HashMap<String, Integer> mapping = nominalMappings.get(j);
					if (!mapping.containsKey(stringValue)) {
						mapping.put(stringValue, mapping.size());
					}
					values[j] = mapping.get(stringValue);
				} else if (cell.getCellType() == CellType.BOOLEAN) {
					values[j] = cell.getBooleanCellValue() ? 1.0 : 0.0; // Boolean 처리
				} else {
					// 미지원 타입은 로그 출력 및 NaN 처리
					System.out.println("Unsupported cell type at row " + i + ", column " + j);
					values[j] = Double.NaN;
				}
			}

			// DenseInstance 생성
			Instance instance = new DenseInstance(1.0, values);
			instances.add(instance);
		}

		// 속성 수정: 범주형 속성으로 변환
		for (int i = 0; i < numAttributes; i++) {
			HashMap<String, Integer> mapping = nominalMappings.get(i);
			if (!mapping.isEmpty()) {
				// 범주형 속성 생성
				List<String> nominalValues = new ArrayList<>(mapping.keySet());
				attributes.set(i, new Attribute(attributes.get(i).name(), nominalValues));
			}
		}

		// 데이터셋 생성
		Instances data = new Instances("Dataset", new ArrayList<>(attributes), instances.size());
		for (Instance inst : instances) {
			data.add(inst);
		}

		workbook.close();
		return data;
	}
}