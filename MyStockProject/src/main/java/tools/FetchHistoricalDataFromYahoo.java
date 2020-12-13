package tools;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FetchHistoricalDataFromYahoo {

    public static void fetchStockHistory(final String stockName, Map<Stock, List<HistoricalQuote>> getAllData ) {
        try {
            Stock stock = YahooFinance.get(stockName);
            List<HistoricalQuote> stockHistoryList = getAllHistoricalData(stock, 300);
            Collections.reverse(stockHistoryList);
            getAllData.put(stock, stockHistoryList);

        } catch (IOException e) {
            System.out.println("Exception while fetching stock : " + stockName + " Cause: " + e.getMessage());
        }
    }

    private static List<HistoricalQuote> getAllHistoricalData(Stock stock, int numberOfDays) throws IOException {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -numberOfDays);
        return stock.getHistory(cal, Interval.DAILY);
    }
}
