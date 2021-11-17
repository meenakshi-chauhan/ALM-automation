package com.nagarro.driven.core.util;

import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CSVReader reads the csv and perform various actions on it.
 *
 * @author nagarro
 */
public class CSVutil {

  private static final Logger log = LoggerFactory.getLogger(CSVutil.class);

  // Static factory
  private CSVutil() {
  }

  /**
   * It reads the csv for data provider in test case and perform action accordingly.
   *
   * @param testName the test name
   * @param separator the csv data seperator
   * @param filename the file name of csv
   * @return the list of string array
   */
  public static List<String[]> reader(String testName, String separator, String filename) {
    log.info(" : CSVDataProvider Method Called");
    List<String[]> dataArr = new ArrayList<>();
    String[] values;
    String line;
    List<String[]> strArray = null;
    File file = new File(filename);
    try(BufferedReader bufferReader = new BufferedReader(new FileReader(file))) {
      while (null != (line = bufferReader.readLine())) {
        if ((testName.equals(line.substring(0, line.indexOf(separator)))) || testName.isEmpty()) {
          if (!testName.isEmpty()) {
            line = line.substring(line.indexOf(separator) + 1);
          }
          values = line.replace("\"", "").split(separator);
          dataArr.add(values);
          strArray = dataArr;
        }
      }
    } catch (FileNotFoundException ex) {
      throw new AutomationFrameworkException("Cannot find the CSV file at " + filename);
    } catch (IOException ex) {
      log.error("Exception occurred while reading the csv for data : {}", ex.getMessage());
    }
    if (null == strArray) {
      throw new AutomationFrameworkException(
              "Something wrong in either CSV file or parameters are sent wrong.");
    }
    return strArray;
  }

  public static void writer(List<String[]> data, String filePath) {
    log.info("CSV writer Method Called");
    // first create file object for file placed at location
    // specified by filepath
    File file = new File(filePath);

    try {
      // create FileWriter object with file as parameter
      try (FileWriter outputfile = new FileWriter(file)) {
        // create CSVWriter object filewriter object as parameter
        // dependency of com.opencsv added
        try (CSVWriter writer = new CSVWriter(outputfile)) {
          writer.writeAll(data);
          writer.flush();
        }
      }
    } catch (Exception ex) {
      log.error("Exception occurred while writing in csv : {}", ex.getMessage());
    }
  }
}