const fs = require('fs');
const readline = require('readline');

// Create readline interface
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});


// ASK USER FOR CSV FILE PATH
function askFilePath() {
    rl.question("Enter dataset file path: ", function(path) {

        if (!fs.existsSync(path)) {
            console.log("Error: File does not exist.\n");
            return askFilePath();
        }

        if (!path.toLowerCase().endsWith(".csv")) {
            console.log("Error: File must be CSV format.\n");
            return askFilePath();
        }

        try {
            fs.accessSync(path, fs.constants.R_OK);
        } catch (err) {
            console.log("Error: File is not readable.\n");
            return askFilePath();
        }

        console.log("File validated successfully.\n");
        processFile(path);
    });
}


// READ DATA AND COMPUTE ANALYTICS
function processFile(filePath) {

    try {
        const content = fs.readFileSync(filePath, "utf8");
        const lines = content.trim().split("\n");

        if (lines.length <= 1) {
            console.log("Error: File is empty or has no data.");
            rl.close();
            return;
        }

        const headers = lines[0].split(",");
        const salesIndex = headers.findIndex(
            col => col.trim().toLowerCase() === "total_sales"
        );

        if (salesIndex === -1) {
            console.log("Error: 'total_sales' column not found.");
            rl.close();
            return;
        }

        const records = [];

        for (let i = 1; i < lines.length; i++) {
            const values = lines[i].split(",");
            if (values.length <= salesIndex) continue;

            const rawValue = values[salesIndex].trim();
            if (rawValue !== "") {
                const totalSales = parseFloat(rawValue);
                if (!isNaN(totalSales)) {
                    records.push(totalSales);
                }
            }
        }

        displayReport(records);

    } catch (error) {
        console.log("File processing error:", error.message);
    } finally {
        rl.close();
    }
}


// DISPLAY EXECUTIVE SALES SUMMARY
function displayReport(records) {

    if (records.length === 0) {
        console.log("No valid records found.");
        return;
    }

    const totalRecords = records.length;
    const totalRevenue = records.reduce((sum, val) => sum + val, 0);
    const highestTransaction = Math.max(...records);
    const lowestTransaction = Math.min(...records);
    const averageSales = totalRevenue / totalRecords;

    console.log("=======================================");
    console.log("        EXECUTIVE SALES SUMMARY        ");
    console.log("=======================================");
    console.log("Total Number of Records  :", totalRecords);
    console.log("Total Sales / Revenue    :", totalRevenue.toFixed(2));
    console.log("Average Sales per Transaction :", averageSales.toFixed(2));
    console.log("Highest Single Transaction      :", highestTransaction.toFixed(2));
    console.log("Lowest Single Transaction       :", lowestTransaction.toFixed(2));
    console.log("=======================================");
}

// Start program
askFilePath();