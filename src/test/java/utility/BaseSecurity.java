package utility;

import org.zaproxy.clientapi.core.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.google.common.base.Preconditions;
import static java.lang.String.format;
import static karate.com.linecorp.armeria.internal.shaded.reflections.Reflections.log;

import org.slf4j.Logger;

public class BaseSecurity {
    private static ClientApi clientApi = new ClientApi("127.0.0.1", 8090, null);
    private static String securityTestReportPath = "build/reports/tests/security.html";

    public static void spiderTarget(String targetURL) throws InterruptedException, ClientApiException {
        ApiResponse apiResponse = clientApi.spider.scan(targetURL, null, null, null, null);
        int progress;
        String scanId = ((ApiResponseElement) apiResponse).getValue();

        do {
            Thread.sleep(5000);
            progress = Integer.parseInt(((ApiResponseElement) clientApi.spider.status(scanId)).getValue());
            log.info("Scan progress: {}%", progress);
        } while (progress < 100);
        log.info("scan complete");
    }

    public static void waitForPassiveScanToComplete() throws ClientApiException {
        log.info("--- Waiting for passive scan to finish ---");
        clientApi.pscan.enableAllScanners();
        ApiResponse response = clientApi.pscan.recordsToScan();

        while (!response.toString().equals("0")) {
            response = clientApi.pscan.recordsToScan();
        }
        log.info("--- Passive scan finished! ---");
    }

    public static void activeScan(String targetURL) throws InterruptedException, ClientApiException {
        log.info("Active scan: {}", targetURL);
        ApiResponse resp = clientApi.ascan.scan(targetURL, "True", "False", null, null, null);
        int progress;
        String scanId = ((ApiResponseElement) resp).getValue();

        do {
            Thread.sleep(5000);
            progress = Integer.parseInt(((ApiResponseElement) clientApi.ascan.status(scanId)).getValue());
            log.info("Scan progress: {}%", progress);
        } while (progress < 100);
        log.info("Active scan complete");
    }

    public static void checkRiskCount(String filterURL) throws ClientApiException {
        log.info("Target URL {}", filterURL);
        int riskCountHigh = 0;
        int riskCountMedium = 0;
        int riskCountLow = 0;
        int riskCountInformational = 0;
        int totalRiskCount;

        List<Alert> alertList = clientApi.getAlerts(filterURL, 0, 9999999);
        for (Alert alert : alertList) {
            String riskName = alert.getRisk().name();
            Alert.Risk risk = alert.getRisk();
            switch (risk) {
                case High:
                    riskCountHigh++;
                    break;
                case Medium:
                    riskCountMedium++;
                    break;
                case Low:
                    riskCountLow++;
                    break;
                case Informational:
                    riskCountInformational++;
                    break;
                default:
                    throw new IllegalStateException(format("Unknown risk level %s", riskName));
            }
        }
        totalRiskCount = riskCountHigh + riskCountMedium + riskCountLow + riskCountInformational;
        log.info("Total risk count on {} = {}", filterURL, totalRiskCount);
    }

    public static void generateScanReport() throws ClientApiException, IOException {
        byte[] bytes = clientApi.core.htmlreport();
        String str = new String(bytes, StandardCharsets.UTF_8);
        File newTextFile = new File(securityTestReportPath);
        try (FileWriter fw = new FileWriter(newTextFile)) {
            fw.write(str);
        }
    }
}
