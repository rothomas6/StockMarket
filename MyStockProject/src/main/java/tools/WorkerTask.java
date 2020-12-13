package tools;

import java.io.IOException;

public class WorkerTask implements Runnable {

    private String name;
    private String stockName;


    public WorkerTask(String name, String stockName) {
        this.name = name;
        this.stockName = stockName;
    }

    public String getName() {
        return name;
    }

    public String getStock() {
        return stockName;
    }

    @Override
    public void run() {
        try {
          System.out.println("Executing : " + name);
          processStockInfo(stockName);
          System.out.println("Executed : " + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void processStockInfo(String stockName ) {
        FetchHistoricalDataFromYahoo.fetchStockHistory(stockName, StockApplication.getAllData);

    }

    public static void processDataAndAnalyze(int percentage_diff_btwn_200DMA) throws IOException {
        ProcessFetchedHistoricalData.processDataAndAnalyze(StockApplication.getAllData, percentage_diff_btwn_200DMA);
    }


}
