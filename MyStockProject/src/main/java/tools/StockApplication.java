package tools;

import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Hello world!
 *
 */
public class StockApplication {
    static Map<Stock, List<HistoricalQuote>> getAllData = new HashMap<>();
    static int percentage_diff_btwn_200DMA = 0;

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        if(args.length < 3){
            System.out.println("invalid number of arguments, exiting..");
            System.exit(0);
        }
        String fileName = args[0];
        System.out.println("FileName is: " + fileName);
        int executorThreadCount = 30;
        if(!args[0].isEmpty()) {
            executorThreadCount = Integer.parseInt(args[1]);
            System.out.println("Executor Thread Count, " + executorThreadCount);
        }else{
            System.out.println("Default Executor Thread Count, " + executorThreadCount);
        }
        
        if(!args[2].isEmpty()) {
        	percentage_diff_btwn_200DMA = Integer.parseInt(args[2]);
        	System.out.println("Diff To 200DMA, " + percentage_diff_btwn_200DMA);
        }
        List<String> results = getStocksFromFile(fileName);

       Date startingTime = Calendar.getInstance().getTime();


        List<Future<WorkerTask>> tasks = new ArrayList<>();
        ExecutorService tp = Executors.newFixedThreadPool(executorThreadCount);
        for (final String stockName : results) {
            WorkerTask task = new WorkerTask("Task stock: " + stockName, stockName);
            tasks.add((Future<WorkerTask>) tp.submit(task));
        }

        for (Future<WorkerTask> p : tasks) {
            p.get();
            // with timeout p.get(10, TimeUnit.SECONDS);
        }
        tp.shutdown();

        /* for (String stockName : results) {
           WorkerTask.processStockInfo(stockName);
        }*/

        Date now = Calendar.getInstance().getTime();
        long timeElapsed = now.getTime() - startingTime.getTime();
        if(timeElapsed <= 1000) {
            System.out.println("Completed fetching info... Timetaken: "  + timeElapsed +"milliseconds");
        } else {
            System.out.println("Completed fetching info... Timetaken: " + timeElapsed / 1000 + "seconds");
        }
        
        
        WorkerTask.processDataAndAnalyze(percentage_diff_btwn_200DMA);
        
    }

    
    
    private static List<String> getStocksFromFile(String fileName) {
        List<String> results = new ArrayList<>();
        // Stream<String> lines = Files.lines(Paths.get(file1)).filter(s -> !s.equals(",,,,,,") && !s.isEmpty());

        try (Stream<String> lines = Files.lines(Paths.get(fileName)).filter(s -> !s.startsWith("#") && !s.isEmpty())) {
            results =  lines.map (elem -> new String(elem).concat(".NS")).collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Exception while reading file : " + fileName + " Cause: " + e.getMessage());
        }
        for (String stockName : results) {
           // System.out.println("Stock : " + stockName);
        }
        System.out.println("Total Stock Size : " + results.size());
        return results;
    }


}
