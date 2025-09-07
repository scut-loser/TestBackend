package com.financial.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import com.financial.entity.PredictionResult;
import com.financial.service.PredictionResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 算法集成服务类
 * 用于连接预测模型
 */
@Service
public class AlgorithmService {
    private static final Logger log = LoggerFactory.getLogger(AlgorithmService.class);

    @Autowired
    private PredictionResultService predictionResultService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${financial.algorithm.local-model-path}")
    private String localModelPath;
    
    @Value("${financial.algorithm.cloud-model-url}")
    private String cloudModelUrl;
    
    @Value("${financial.algorithm.prediction-window}")
    private int predictionWindow;
    
    @Value("${financial.algorithm.anomaly-threshold}")
    private double anomalyThreshold;
    
    @Value("${financial.algorithm.local-data-file}")
    private String localDataFile;

    @Value("${financial.algorithm.default-feature:bid_price}")
    private String defaultFeature;

    @Value("${financial.algorithm.python-exec:python}")
    private String pythonExec;
    
    /**
     * 执行本地预测算法
     */ 
    public Map<String, Object> executeLocalPrediction(String symbol, String algorithmName) {
        try {
            Map<String, Object> result = callLocalModel(symbol, algorithmName, null);
            
            // 保存预测结果到数据库
            PredictionResult predictionResult = new PredictionResult();
            predictionResult.setSymbol(symbol);
            predictionResult.setAlgorithmName(algorithmName);
            predictionResult.setPredictionType("price_prediction");
            predictionResult.setPredictionTime(LocalDateTime.now());
            
            // 解析预测值
            Object predictedValueObj = result.get("predicted_value");
            if (predictedValueObj != null) {
                if (predictedValueObj instanceof Number) {
                    predictionResult.setPredictedValue(new BigDecimal(predictedValueObj.toString()));
                } else {
                    predictionResult.setPredictedValue(new BigDecimal(predictedValueObj.toString()));
                }
            }
            
            // 解析置信度
            Object confidenceObj = result.get("confidence_score");
            if (confidenceObj != null) {
                if (confidenceObj instanceof Number) {
                    predictionResult.setConfidenceScore(new BigDecimal(confidenceObj.toString()));
                } else {
                    predictionResult.setConfidenceScore(new BigDecimal(confidenceObj.toString()));
                }
            }
            
            // 异常检测（基于置信度阈值）
            if (confidenceObj != null && confidenceObj instanceof Number) {
                double confidence = ((Number) confidenceObj).doubleValue();
                predictionResult.setIsAnomaly(confidence < this.anomalyThreshold);
            }
            
            // 保存到数据库
            predictionResultService.save(predictionResult);
            
            // 在返回结果中添加数据库ID
            result.put("prediction_id", predictionResult.getId());
            result.put("saved_to_database", true);
            
            return result;
        } catch (Exception e) {
            log.error("本地预测执行失败: {}", e.getMessage(), e);
            throw new RuntimeException("本地预测执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行本地预测算法（支持指定单一特征）
     */
    public Map<String, Object> executeLocalPrediction(String symbol, String algorithmName, String feature) {
        try {
            Map<String, Object> result = callLocalModel(symbol, algorithmName, feature);

            // 保存预测结果到数据库
            PredictionResult predictionResult = new PredictionResult();
            predictionResult.setSymbol(symbol);
            predictionResult.setAlgorithmName(algorithmName);
            predictionResult.setPredictionType("price_prediction");
            predictionResult.setPredictionTime(LocalDateTime.now());

            Object predictedValueObj = result.get("predicted_value");
            if (predictedValueObj != null) {
                predictionResult.setPredictedValue(new BigDecimal(predictedValueObj.toString()));
            }

            Object confidenceObj = result.get("confidence_score");
            if (confidenceObj != null) {
                predictionResult.setConfidenceScore(new BigDecimal(confidenceObj.toString()));
            }

            if (confidenceObj != null && confidenceObj instanceof Number) {
                double confidence = ((Number) confidenceObj).doubleValue();
                predictionResult.setIsAnomaly(confidence < this.anomalyThreshold);
            }

            predictionResultService.save(predictionResult);
            result.put("prediction_id", predictionResult.getId());
            result.put("saved_to_database", true);
            return result;
        } catch (Exception e) {
            log.error("本地预测执行失败: {}", e.getMessage(), e);
            throw new RuntimeException("本地预测执行失败: " + e.getMessage(), e);
        }
    }

    
    /**
     * 执行云端预测算法
     */
    public Map<String, Object> executeCloudPrediction(String symbol, String algorithmName, String predictionType) {
        try {
            // 调用云端模型进行预测
            Map<String, Object> predictionResult = callCloudModel(symbol, algorithmName);
            
            // 添加元数据
            predictionResult.put("symbol", symbol);
            predictionResult.put("algorithmName", algorithmName);
            predictionResult.put("predictionType", predictionType);
            predictionResult.put("predictionTime", LocalDateTime.now());
            predictionResult.put("dataSource", "CLOUD");
            
            return predictionResult;
            
        } catch (Exception e) {
            throw new RuntimeException("云端预测失败: " + e.getMessage());
        }
    }
    
    /**
     * 执行异常检测算法
     */
    public Map<String, Object> executeAnomalyDetection(String symbol, String algorithmName) {
        try {
            // 调用异常检测模型
            Map<String, Object> anomalyResult = callAnomalyDetectionModel(symbol, algorithmName);
            
            // 添加元数据
            anomalyResult.put("symbol", symbol);
            anomalyResult.put("algorithmName", algorithmName);
            anomalyResult.put("detectionTime", LocalDateTime.now());
            anomalyResult.put("dataSource", "LOCAL");
            
            return anomalyResult;
            
        } catch (Exception e) {
            throw new RuntimeException("异常检测失败: " + e.getMessage());
        }
    }
    
    /**
     * 调用本地模型
     */
    private Map<String, Object> callLocalModel(String symbol, String algorithmName, String feature) {
        try {
            // 基于运行目录，定位到模块根目录 “FinancialForecastingSystem”
            Path runDir = Paths.get(System.getProperty("user.dir")).normalize();
            Path relScript = Paths.get(this.localModelPath);
            Path relData   = Paths.get(this.localDataFile);
            Path candidateModuleRoot = runDir.resolve("FinancialForecastingSystem").normalize();
            Path candidateRunRoot = runDir;

            Path scriptPath = candidateModuleRoot.resolve(relScript).normalize();
            Path dataPath   = candidateModuleRoot.resolve(relData).normalize();

            if (!Files.exists(scriptPath)) {
                // 尝试运行目录
                Path altScript = candidateRunRoot.resolve(relScript).normalize();
                Path altData   = candidateRunRoot.resolve(relData).normalize();
                if (Files.exists(altScript)) {
                    scriptPath = altScript;
                    dataPath   = altData;
                }
            }
            Path outDir = scriptPath.getParent() != null
                    ? scriptPath.getParent().resolve("out").normalize()
                    : candidateRunRoot.resolve("models/out").normalize();
            Files.createDirectories(outDir);
            
            // 选择使用的特征（单特征算法时生效）
            String usedFeature = (feature == null || feature.isBlank()) ? this.defaultFeature : feature;

            ProcessBuilder pb = new ProcessBuilder(
                this.pythonExec,
                scriptPath.toString(),
                "--data", dataPath.toString(),
                "--window", String.valueOf(this.predictionWindow),
                "--epochs", "50",
                "--out_dir", outDir.toString(),
                "--algorithm", algorithmName,
                "--feature", usedFeature
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                int exit = p.waitFor();
                if (exit != 0) {
                    throw new RuntimeException("local model exit code: " + exit + ", output: " + sb);
                }
                // 解析JSON
                Map<String, Object> parsed = com.alibaba.fastjson.JSON.parseObject(sb.toString(), Map.class);
                if (parsed == null || parsed.containsKey("error")) {
                    throw new RuntimeException("local model returned error: " + sb);
                }
                // 添补信息
                parsed.putIfAbsent("algorithm", algorithmName);
                parsed.putIfAbsent("symbol", symbol);
                parsed.putIfAbsent("feature", usedFeature);
                return parsed;
            }
        } catch (Exception ex) {
            throw new RuntimeException("调用本地模型失败: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * 调用云端模型
     */
    private Map<String, Object> callCloudModel(String symbol, String algorithmName) {
        try {
            // 构建请求参数
            Map<String, Object> request = new HashMap<>();
            request.put("symbol", symbol);
            request.put("algorithm", algorithmName);
            request.put("prediction_window", predictionWindow);
            
            // 调用云端API
            Map<String, Object> response = restTemplate.postForObject(
                cloudModelUrl, 
                request, 
                Map.class
            );
            
            if (response == null) {
                throw new RuntimeException("云端API返回空响应");
            }
            
            return response;
            
        } catch (Exception e) {
            throw new RuntimeException("云端API调用失败: " + e.getMessage());
        }
    }
    
    /**
     * 调用异常检测模型
     */
    private Map<String, Object> callAnomalyDetectionModel(String symbol, String algorithmName) {
        // 这里需要集成师兄的具体异常检测算法
        // 暂时返回模拟数据
        Map<String, Object> result = new HashMap<>();
        result.put("is_anomaly", false);
        result.put("anomaly_score", 0.15);
        result.put("threshold", anomalyThreshold);
        result.put("algorithm", algorithmName);
        result.put("symbol", symbol);
        
        return result;
    }
    
    /**
     * 获取可用算法列表
     */
    public List<String> getAvailableAlgorithms() {
        return List.of(
            "SINGLE_LSTM",
            "SINGLE_TRANSFORMER",
            "SERIAL_LSTM_TRANSFORMER",
            "FUSION_LSTM_TRANSFORMER"
        );
    }
    
    /**
     * 获取算法详细信息
     */
    public Map<String, Object> getAlgorithmInfo(String algorithmName) {
        Map<String, Object> info = new HashMap<>();
        
        switch (algorithmName.toUpperCase()) {
            case "SINGLE_LSTM":
                info.put("name", "SINGLE_LSTM");
                info.put("displayName", "单一特征 LSTM 模型");
                info.put("description", "基于单一特征的 LSTM 时序预测模型");
                info.put("type", "深度学习");
                info.put("input", "单变量时序");
                break;
            case "SINGLE_TRANSFORMER":
                info.put("name", "SINGLE_TRANSFORMER");
                info.put("displayName", "单一特征 Transformer 模型");
                info.put("description", "基于单一特征的 Transformer 时序预测模型");
                info.put("type", "深度学习");
                info.put("input", "单变量时序");
                break;
            case "SERIAL_LSTM_TRANSFORMER":
                info.put("name", "SERIAL_LSTM_TRANSFORMER");
                info.put("displayName", "串联式混合模型（LSTM→Transformer）");
                info.put("description", "先用 LSTM 编码，再由 Transformer 建模的串联混合模型");
                info.put("type", "深度学习混合");
                info.put("input", "单变量/低维时序");
                break;
            case "FUSION_LSTM_TRANSFORMER":
                info.put("name", "FUSION_LSTM_TRANSFORMER");
                info.put("displayName", "特征融合型混合模型（多维+可学习位置编码）");
                info.put("description", "多维特征输入，LSTM 与 Transformer 融合，并带可学习位置编码");
                info.put("type", "深度学习混合");
                info.put("input", "多变量时序");
                break;
            default:
                return null;
        }
        
        return info;
    }

}
