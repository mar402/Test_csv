package org.example;

import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Exports data from memory to html (csv) files.
 */
public class DataExporter {

    static NumberFormat f1 = new DecimalFormat("###,###");
    static NumberFormat f2 = new DecimalFormat("###.#");
    static NumberFormat f3 = new DecimalFormat("###");

    /**
     * Exports data to html (each table is exported into separate .html file).
     * @param data      data (list of tables to be exported into .html files)
     * @param savePath  path to .html files
     */
    public static void exportDataToHtml(Data data, String savePath) {
        for (int i = 0; i < data.getTables().size(); i++) {
            Data.Table table = data.getTables().get(i);
            int j = i + 1;
            double totalUnits = 0.;
            double totalShare = 0.;
            try (BufferedWriter out = new BufferedWriter(new FileWriter(savePath + "_" + table.getCountry() + "_" + table.getPeriod() + ".html"))) {
                out.write("<html>" + "<body>");
                out.write("Table " + j + ", PC Quarterly Market Share, the " + table.getCountry() + ", " + table.getPeriod());
                out.write("<table border ='1'>" +
                        "<tr>" +
                        "<th style=\"width:120px\">Vendor</th>" +
                        "<th style=\"width:120px\">Units</th>" +
                        "<th style=\"width:120px\">Share</th>" +
                        "</tr>");

                for (Data.Row row : table.getRows()) {
                    out.write("<tr>");
                    out.write("<td align='center'>" + row.getVendor() + "</td>");
                    out.write("<td align='right'>" + f1.format(row.getUnits()) + "</td>");
                    out.write("<td align='right'>" + f2.format(row.getShare()) + "%" + "</td>");
                    out.write("</tr>");

                    totalUnits += row.getUnits();
                    totalShare += row.getShare();
                }
                out.write("<tr style=\"background-color: #FFFACD\">");
                out.write("<td align='center'>" + "Total" + "</td>");
                out.write("<td align='right'>" + f1.format(totalUnits) + "</td>");
                out.write("<td align='right'>" + f3.format(totalShare) + "%" + "</td>");
                out.write("</tr>");
                out.write("</table>");
                out.write("</body>" + "</html>");
            } catch(IOException ex){
            // do nothing
            // todo do something
            }
        }

    }

    /**
     * Exports data to csv (each table is exported into separate .csv file).
     * @param data      data (list of tables to be exported into .csv files)
     * @param savePath  path to .csv files
     */
    public static void exportDataToCsv(Data data, String savePath) {
        HeaderColumnNameMappingStrategy<Bean> strategy = new HeaderColumnNameMappingStrategyBuilder<Bean>().build();
        strategy.setType(Bean.class);
        // todo use better approach for the correct column ordering
        strategy.setColumnOrderOnWrite(Comparator.reverseOrder());
        for (int i = 0; i < data.getTables().size(); i++) {
            Data.Table table = data.getTables().get(i);
            List<Bean> beans = convertTableToBeans(table);
            try (FileWriter out = new FileWriter(savePath + "_" + table.getCountry() + "_" + table.getPeriod() + ".csv")) {
                StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(out).withMappingStrategy(strategy).build();
                beanToCsv.write(beans);
            } catch(IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException ex){
                // do nothing
                // todo do something
            }
        }

    }

    private static List<Bean> convertTableToBeans(Data.Table table) {
        List<Bean> beans = table.getRows().stream().map(r -> new Bean(r.getVendor(), r.getUnits(), r.getShare())).
                collect(Collectors.toList());
        double totalUnits = table.getRows().stream().collect(Collectors.summingDouble(r -> r.getUnits()));
        double totalShare = table.getRows().stream().collect(Collectors.summingDouble(r -> r.getShare()));
        beans.add(new Bean("Total", totalUnits, totalShare));
        return beans;
    }

    private record Bean (String vendor, Double units, Double share){};

}
