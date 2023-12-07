package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatReader {

    private String fileContent;

    public DatReader(String filename) {
        Path filePath = Path.of(filename);
        try {
            fileContent = Files.readString(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write the necessary Regular Expression to extract the filename given as the string and
     * surrounded by double quotation marks
     *
     * @return the result as String
     */
    public String getStringVar(String varName) {
        // TODO: Your code goes here
        String value = "";
        String pattern = varName + "\\s*=\\s*\"([^\"]*)\"";

        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(fileContent);

        if (matcher.find()) {
            value = matcher.group(1);
        }

        return value;
    }

    /**
     * Write the necessary Regular Expression to extract floating point numbers from the input file
     *
     * @return the result as Double
     */
    public Double getDoubleVar(String varName) {
        // TODO: Your code goes here
        Double value = 0.0;

        Pattern pattern = Pattern.compile(varName + "\\s*=\\s*(-?\\d+(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(fileContent);

        if (matcher.find()) {
            value = Double.parseDouble(matcher.group(1));
        }
        return value;
    }

    /**
     * Method with a Regular Expression to extract integer numbers from the input file
     *
     * @return the result as int
     */
    public int getIntVar(String varName) {
        Pattern p = Pattern.compile("[\\t ]*" + varName + "[\\t ]*=[\\t ]*([0-9]+)");
        Matcher m = p.matcher(fileContent);
        m.find();
        return Integer.parseInt(m.group(1));
    }

    /**
     * Write the necessary Regular Expression to extract a Point object from the input file
     * points are given as an x and y coordinate pair surrounded by parentheses and separated by a comma
     *
     * @return the result as a Point object
     */
    public Point getPointVar(String varName) {
        Point p = new Point(0, 0);
        // TODO: Your code goes here

        Pattern pattern = Pattern.compile(varName + "\\s*=\\s*\\((\\s*-?\\d+\\s*),(\\s*-?\\d+\\s*)\\)");
        Matcher matcher = pattern.matcher(fileContent);

        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1).trim());
            int y = Integer.parseInt(matcher.group(2).trim());
            p = new Point(x, y);
        }

        return p;
    }

}
