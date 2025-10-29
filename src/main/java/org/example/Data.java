package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Stores data in memory.
 * Stored data are organized as list of tables. Each table contains data for the given country and period (quarter).
 */
public class Data {
    private List<Table> tables;

    public Data() {
        tables = new ArrayList<>();
    }

    /**
     * Represents the table for the given country and period (quarter).
     * Table columns are vendor, units and share.
     */
    static class Table {
        String country;
        String period;
        List<Row> rows;

        Table (String country, String period, List<Row> rows) {
            this.country = country;
            this.period = period;
            this.rows = rows;
        }

        /**
         *
         * @return country
         */
        String getCountry() {
            return country;
        }

        /**
         *
         * @return period (quarter)
         */
        String getPeriod() {
            return period;
        }

        /**
         * list of rows (each row has vendor, units and share fields)
         * @return table rows
         */
        List<Row> getRows() {
            return rows;
        }
    }

    /**
     * Represents the table row (with vendor, units and share fields).
     */
    static class Row {
        String vendor;
        Double units;
        Double share;

        Row (String vendor, Double units, Double share) {
            this.vendor = vendor;
            this.units = units;
            this.share = share;
        }

        /**
         * @return  vendor
         */
        String getVendor() {
            return vendor;
        }

        /**
         * Number of PCs sold by the vendor during the period
         * @return units
         */
        Double getUnits() {
            return units;
        }

        /**
         * Percentage of the total number of PCs sold during the period for the vendor.
         * @return share
         */
        Double getShare() {
            return share;
        }
    }

    /**
     * @return list of tables (each table contains data for the given country and period)
     */
    public List<Table> getTables() {
        return tables;
    }

    /**
     * Ascertain the units and share values for a given vendor in the given country and during the given period.
     * @param  vendor    vendor
     * @param  country   country
     * @param  period    period (quarter)
     * @return array of double values (units stored in the first array element and share stored in the second array element)
     */
    public double[] getUnitsAndShareForVendorCountryAndPeriod(String vendor, String country, String period) {
        Table table = tables.stream().filter(d -> d.getCountry().equals(country) &&
                d.getPeriod().equals(period)).findAny().orElse(null);
        if (table != null) {
            Row row = table.getRows().stream().filter(r -> r.getVendor().equals(vendor)).findAny().orElse(null);
            if (row != null) return new double[]{row.units, row.share};
        }
        return null;
    }

    /**
     * Ascertain which row contains information about a given vendor in the given country and during the given period.
     * @param  vendor    vendor
     * @param  country   country
     * @param  period    period (quarter)
     * @return index of the row containing information about a given vendor
     *         in the given country and during the given period;
     *         -1 in a case there is no such row
     */
    public int getRowIndexForVendorCountryAndPeriod(String vendor, String country, String period) {
        Table table = this.tables.stream().filter(d -> d.getCountry().equals(country) &&
                d.getPeriod().equals(period)).findAny().orElse(null);
        if (table != null) {
            return table.getRows().stream().filter(r -> r.getVendor().equals(vendor)).map(r -> table.getRows().indexOf(r) + 1).findAny().orElse(-1);
        }
        return -1;
    }

    /**
     * Sort the rows alphabetically (by vendor).
     * @param country   country
     * @param period    period (quarter)
     */
    public void sortRowsForCountryAndPeriodByVendor(String country, String period) {
        Table table = this.tables.stream().filter(d -> d.getCountry().equals(country) &&
                d.getPeriod().equals(period)).findAny().orElse(null);
        if (table != null) {
            table.getRows().sort(Comparator.comparing(Row::getVendor));
        }
    }

    /**
     * Sort the rows by units.
     * @param country   country
     * @param period    period (quarter)
     */
    public void sortRowsForCountryAndPeriodByUnits(String country, String period) {
        Table table = this.tables.stream().filter(d -> d.getCountry().equals(country) &&
                d.getPeriod().equals(period)).findAny().orElse(null);
        if (table != null) {
            table.getRows().sort(Comparator.comparing(Row::getUnits));
        }
    }

}
