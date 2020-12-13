package tools;

import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessFetchedHistoricalData {
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	public static void processDataAndAnalyze(final Map<Stock, List<HistoricalQuote>> getAllData, int percentage_diff_btwn_200DMA) throws IOException {
		Map<String, String> LessThan200_DMA = new HashMap<>();
		Map<String, String> LessThan50_DMA = new HashMap<>();
		Map<String, String> LessThan50_And_200_DMA = new HashMap<>();
		Map<String, String> Price_50_200_DMA_CurrentPrice = new HashMap<>();

		for (Stock myStock : getAllData.keySet()) {

			BigDecimal Two_Hundred_Day_Avg = getXDayMovingAvg(format, getAllData.get(myStock), 200);
			BigDecimal Fifty_Day_Avg = getXDayMovingAvg(format, getAllData.get(myStock), 50);

			BigDecimal Current_Price = getAllData.get(myStock).get(0).getClose();

			/*
			 * System.out.println("List of stock history : " + myStock.getSymbol() +
			 * " Price : " + Current_Price + " Avg :" + getXDayMovingAvg(format,
			 * getAllData.get(myStock), 200));
			 */
			int result_Price_Less_Than_50 = Fifty_Day_Avg.compareTo(Current_Price);
			int result_50_Less_Than_200 = Two_Hundred_Day_Avg.compareTo(Fifty_Day_Avg);

			// 200 dma = 200
			// price = 180
			// -1 0 1
			// 1 => 200*1/100 =20 220 180

			BigDecimal percentage_of200DMA = Two_Hundred_Day_Avg.multiply(new BigDecimal(percentage_diff_btwn_200DMA))
					.divide(new BigDecimal(100));

			System.out.println("<<<<< Stock price : " + myStock.getSymbol() + " is : " + Current_Price + " 200DMA : "
					+ Two_Hundred_Day_Avg);

			int result_200 = (Two_Hundred_Day_Avg.add(percentage_of200DMA)).compareTo(Current_Price);
			
	//		int diff_200_CMP = ((Two_Hundred_Day_Avg).add(percentage_of200DMA)).subtract(Current_Price));
			
			
			int result_50 = Fifty_Day_Avg.compareTo(Current_Price);

			if (result_50_Less_Than_200 == 1 && result_Price_Less_Than_50 == 1) {
				/*
				 * System.out.println("<<<<<<#########>>>>>Stock price for : " +
				 * myStock.getSymbol() + " is : " + Current_Price + " 50 DMA :" + Fifty_Day_Avg
				 * + " 200DMA : " + Two_Hundred_Day_Avg);
				 */
				Price_50_200_DMA_CurrentPrice.put(myStock.getSymbol(),
						": " + Current_Price + ", 50 DMA :  " + Fifty_Day_Avg + ", 200 DMA : " + Two_Hundred_Day_Avg);
			}

			if (result_200 == 1 && result_50 == 1) {
				/*
				 * System.out.println("<<<<<<#########>>>>>Stock price for : " +
				 * myStock.getSymbol() + " is : " + Current_Price + " 50 DMA :" + Fifty_Day_Avg
				 * + " 200DMA : " + Two_Hundred_Day_Avg);
				 */
				LessThan50_And_200_DMA.put(myStock.getSymbol(),
						": " + Current_Price + ", 50 DMA :  " + Fifty_Day_Avg + ", 200 DMA : " + Two_Hundred_Day_Avg);
			}

			if (result_200 == 1) {
				LessThan200_DMA.put(myStock.getSymbol(), ": " + Current_Price + ", 200 DMA : " + Two_Hundred_Day_Avg);
			}


			if (result_50 == 1) {
				LessThan50_DMA.put(myStock.getSymbol(), ": " + Current_Price + ", 50 DMA : " + Fifty_Day_Avg);
			}

		}

		printXDMAFromMap(LessThan200_DMA, "LessThan200_DMA");
	}

	private static BigDecimal getXDayMovingAvg(SimpleDateFormat format, List<HistoricalQuote> stockHistory,
			int numberOfDays) throws IOException {

		BigDecimal Sum = new BigDecimal(0);
		BigDecimal Avg = new BigDecimal(0);
		int counter = 1;

		// System.out.println("Total number of outdays : " + outputs.size());
		// for (HistoricalQuote output: stockHistory) {
		if (numberOfDays > stockHistory.size()) {
			numberOfDays = stockHistory.size();
		} 

		for (int i = 0; i < numberOfDays; i++) {
			HistoricalQuote output = stockHistory.get(i);
			if (output.getClose() != null) {
				Sum = Sum.add(output.getClose());
			}
			Avg = Sum.divide(new BigDecimal(numberOfDays), 8, RoundingMode.HALF_UP);
		}

		return Avg;
	}

	private static void printXDMAFromMap(Map<String, String> xDMAMapToPrint, String s) {
		if (xDMAMapToPrint.size() > 0) {
			System.out.println("<<<<<<<<<" + s + ">>>>>>>>");
			for (String s1 : xDMAMapToPrint.keySet()) {
				System.out.println("Stock :" + s1 + ": " + xDMAMapToPrint.get(s1));
			}
		}
	}
}
