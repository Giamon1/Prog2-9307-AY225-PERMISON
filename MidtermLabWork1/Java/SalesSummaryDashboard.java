import java.io.*;
import java.util.*;

public class SalesSummaryDashboard {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        File file;

        // LOOP UNTIL VALID FILE PATH
        while (true) {
            System.out.print("Enter dataset file path: ");
            String path = input.nextLine();

            file = new File(path);

            if (!file.exists()) {
                System.out.println("Error: File does not exist.");
            } else if (!file.isFile()) {
                System.out.println("Error: Path is not a file.");
            } else if (!file.canRead()) {
                System.out.println("Error: File is not readable.");
            } else if (!path.toLowerCase().endsWith(".csv")) {
                System.out.println("Error: File must be a CSV format.");
            } else {
                break; // valid path
            }
        }

        List<DataRecord> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String headerLine = br.readLine();

            if (headerLine == null) {
                System.out.println("Error: File is empty.");
                return;
            }

            String[] headers = headerLine.split(",");
            int salesIndex = -1;

            // FIND "total_sales" COLUMN DYNAMICALLY
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].trim().equalsIgnoreCase("total_sales")) {
                    salesIndex = i;
                    break;
                }
            }

            if (salesIndex == -1) {
                System.out.println("Error: 'total_sales' column not found.");
                return;
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                if (values.length <= salesIndex) continue; // skip malformed rows

                try {
                    String rawValue = values[salesIndex].trim();
                    if (!rawValue.isEmpty()) {
                        double totalSales = Double.parseDouble(rawValue);
                        records.add(new DataRecord(totalSales));
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid numeric row.");
                }
            }

            displayReport(records);

        } catch (IOException e) {
            System.out.println("File processing error: " + e.getMessage());
        }

        input.close();
    }

 
    // DISPLAY EXECUTIVE SALES SUMMARY
    public static void displayReport(List<DataRecord> records) {

        if (records.isEmpty()) {
            System.out.println("No valid records found.");
            return;
        }

        int totalRecords = records.size();
        double totalRevenue = 0;
        double highestTransaction = Double.MIN_VALUE;
        double lowestTransaction = Double.MAX_VALUE;

        for (DataRecord record : records) {
            double sale = record.getTotalSales();
            totalRevenue += sale;
            if (sale > highestTransaction) highestTransaction = sale;
            if (sale < lowestTransaction) lowestTransaction = sale;
        }

        double averageSales = totalRevenue / totalRecords;

        System.out.println("\n=======================================");
        System.out.println("        EXECUTIVE SALES SUMMARY        ");
        System.out.println("=======================================");
        System.out.println("Total Number of Records  : " + totalRecords);
        System.out.printf("Total Sales / Revenue    : %.2f%n", totalRevenue);
        System.out.printf("Average Sales per Transaction : %.2f%n", averageSales);
        System.out.printf("Highest Single Transaction      : %.2f%n", highestTransaction);
        System.out.printf("Lowest Single Transaction       : %.2f%n", lowestTransaction);
        System.out.println("=======================================\n");
    }
}