package MerchainRunning;

import weka.classifiers.functions.LinearRegression;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

public class ModelTrainer {

	public static void trainAndEvaluateModel(Instances data) throws Exception {
		// 마지막 열을 클래스(예측하려는 변수)로 설정
		data.setClassIndex(data.numAttributes() - 1);

		// 회귀 모델 생성 (선형 회귀)
		LinearRegression model = new LinearRegression();
		
		// 모델 학습
		model.buildClassifier(data);

		// 모델 평가 (여기서는 예시로 첫 번째 인스턴스에 대해 예측)
		Instance firstInstance = data.instance(0);
		double predictedValue = model.classifyInstance(firstInstance);
		System.out.println("Predicted Value: " + predictedValue);
		
		// 모델 저장
		SerializationHelper.write("linear_regression_model.model", model);
	}
}