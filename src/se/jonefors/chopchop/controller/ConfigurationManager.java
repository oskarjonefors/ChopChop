/*
 * ChopChop - A very simple 1D cut optimizer with printing capability.
 * Copyright (C) 2015  Oskar Jönefors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.jonefors.chopchop.controller;

import se.jonefors.chopchop.view.LengthSpecification;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles the loading and saving of available LengthSpecifications
 * from and to a configuration file.
 *
 * @author Oskar Jönefors
 */

public class ConfigurationManager {

    private static final String CONFIG_PATH_SUFFIX = "/.chopchop/lengths.config";
    private static final int[] DEFAULT_LENGTHS = new int[] {6000, 6100, 10100, 12100};

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
}
