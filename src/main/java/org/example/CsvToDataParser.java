package org.example;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CsvToDataParser {

    /**
     * Reads and parses data from the .csv input file and stores it in the Data object
     * @param pathToFile  path to .csv input file
     * @return  Data object
     */
    public static Data parseData(String pathToFile) {
        Data data = new Data();
        try {
            // read and parse input data
            Reader reader = new FileReader(pathToFile);
            List<DataBean> parsed = new CsvToBeanBuilder<DataBean>(reader).
                    withSeparator(',').withQuoteChar('\'').withType(DataBean.class).
                    withVerifier(CsvToDataParser::verify).build().parse();

            // store data in table like objects
            Map<String, Map<String, Map<String, List<DataBean>>>> dataMap =
                    parsed.stream().collect(Collectors.groupingBy(d -> d.country, Collectors.groupingBy(d -> d.timescale, Collectors.groupingBy(d -> d.vendor))));
            // loop over the country
            for (String country : dataMap.keySet()) {
                // loop over the timescales
                for (String timescale : dataMap.get(country).keySet()) {
                    Map<String, List<DataBean>> vendors = dataMap.get(country).get(timescale);
                    // count total units for all vendors within the timescale
                    Double totalUnits = vendors.values().stream().flatMap(v -> v.stream()).map(d -> d.units).
                            collect(Collectors.summingDouble(Double::doubleValue));
                    List<Data.Row> rows = new ArrayList<>();
                    // loop over the vendors within the timescale
                    for (String vendor : vendors.keySet()) {
                        // count units for the particular vendor within the particular timescale
                        Double units = vendors.get(vendor).stream().map(d -> d.units).collect(Collectors.summingDouble(Double::doubleValue));
                        rows.add(new Data.Row(vendor, units, units * 100. / totalUnits));
                    }
                    data.getTables().add(new Data.Table(country, timescale, rows));
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return data;
    }

    private static boolean verify(DataBean d) {
        return d.country != null && !d.country.isBlank() &&
               d.timescale != null && !d.timescale.isBlank() &&
               d.vendor != null && !d.vendor.isBlank();
    }

    public static class DataBean {
        @CsvBindByName(column = "Country")
        String country;
        @CsvBindByName(column = "Timescale")
        String timescale;
        @CsvBindByName(column = "Vendor")
        String vendor;
        @CsvBindByName(column = "Units")
        Double units;
    }
}