package MerchainRunning;

import java.io.IOException;

import weka.core.Instances;

public class ExcelToAI {
	
	public static void main(String[] args) throws Exception {
		String filePath = "asteroid_collisions.xlsx";  // 엑셀 파일 경로

		try {
            Instances data = ExcelReader.loadDataFromExcel(filePath);
            
            // 모델 학습 및 평가
    		ModelTrainer.trainAndEvaluateModel(data);
    		
            System.out.println(data); // 데이터셋 출력
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
