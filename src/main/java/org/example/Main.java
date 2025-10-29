package org.example;

public class Main {
    public static void main(String[] args) {
        // read and parse data
        Data data = CsvToDataParser.parseData("src/main/resources/data.csv");

        // export data
        DataExporter.exportDataToHtml(data, "src/main/resources/");
        DataExporter.exportDataToCsv(data, "src/main/resources/");

        // Ascertain the units and share values for the vendor
        System.out.println("Ascertain the units and share values for the vendor: Fujitsu Siemens" );
        for (Data.Table table : data.getTables()) {
            double[] res = data.getUnitsAndShareForVendorCountryAndPeriod("Fujitsu Siemens", table.getCountry(), table.getPeriod());
            if (res != null) {
                System.out.printf(" Country: %s Period: %s Units: %f Share: %f \n",
                        table.getCountry(), table.getPeriod(), res[0], res[1]);
            } else {
                System.out.println("N/A \n");
            }
        }
        System.out.println("\n");

        // Ascertain which row contains information about the vendor
        System.out.println("Ascertain which row contains information about the vendor: Fujitsu Siemens");
        for (Data.Table table : data.getTables()) {
            int index = data.getRowIndexForVendorCountryAndPeriod("Fujitsu Siemens", table.getCountry(), table.getPeriod());
            System.out.printf(" Country: %s Period: %s Index: %d \n",
                    table.getCountry(), table.getPeriod(), index);
        }
        System.out.println("\n");

        //Sort the rows alphabetically (by vendor).
        for (Data.Table dataTable : data.getTables()) {
            data.sortRowsForCountryAndPeriodByVendor(dataTable.getCountry(), dataTable.getPeriod());
        }
        DataExporter.exportDataToHtml(data, "src/main/resources/SortedByVendor" );

        // Sort the rows by unit values.
        for (Data.Table dataTable : data.getTables()) {
            data.sortRowsForCountryAndPeriodByUnits(dataTable.getCountry(), dataTable.getPeriod());
        }
        DataExporter.exportDataToHtml(data, "src/main/resources/SortedByUnits" );

    }

}
