package se.jonefors.chopchop.controller;

import se.jonefors.chopchop.view.LengthSpecification;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oskar Jönefors
 */

public class ConfigurationManager {

    private static final String CONFIG_PATH_SUFFIX = "/.chopchop/lengths.config";
    private static final int[] DEFAULT_LENGTHS = new int[] {6000, 6100, 10100, 12100};

    private static List<LengthSpecification> readFromConfigFile(File file) {

        List<LengthSpecification> lengths = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                String[] dataLine = currentLine.split(":");

                if (dataLine.length != 2) {
                    throw new IllegalArgumentException("Malformed configuration file!");
                }

                int length = Integer.parseInt(dataLine[0]);

                final LengthSpecification newLen = new LengthSpecification(length);

                switch (dataLine[1]) {
                    case "0":
                        newLen.setStatus(false);
                        break;
                    case "1":
                        newLen.setStatus(true);
                        break;
                    default:
                        throw new IllegalArgumentException("Malformed configuration file!");
                }

                lengths.add(newLen);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lengths;
    }

    private static List<LengthSpecification> loadDefaults() {
        List<LengthSpecification> lengths = new ArrayList<>();
        for (int i : DEFAULT_LENGTHS) {
            lengths.add(new LengthSpecification(i));
        }
        return lengths;
    }

    private static String getConfigData(List<LengthSpecification> lengths) {
        StringBuilder sb = new StringBuilder();

        for (LengthSpecification len : lengths) {
            if (len.getLength() > 0) {
                sb.append(len.getLength());
                sb.append(":");
                sb.append(len.isActive() ? "1" : "0");
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Get the LengthSpecifications previously saved to the configuration file,
     * or the default values if no configuration file has been saved.
     */
    public static List<LengthSpecification> getSavedLengths() {
        File configFile = new File(System.getProperty("user.home") + CONFIG_PATH_SUFFIX);
        if (configFile.exists()) {
            return readFromConfigFile(configFile);
        } else {
            return loadDefaults();
        }
    }

    /**
     * Write the given LengthSpecifications to the configuration file.
     */
    public static void writeConfig(List<LengthSpecification> lengths) {
        File configFile = new File(System.getProperty("user.home") + CONFIG_PATH_SUFFIX);

        try {
            if (configFile.exists() ||
                    (configFile.getParentFile().mkdirs() && configFile.createNewFile())) {
                FileWriter fw = new FileWriter(configFile.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(getConfigData(lengths));
                bw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
