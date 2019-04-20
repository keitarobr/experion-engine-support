package br.ufsc.ppgcc.experion;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.EnvironmentConfiguration;

/**
 * Global Experion Support Class
 */
public class Experion {

    private static Experion instance = new Experion();

    private Configuration config = new EnvironmentConfiguration();

    public static Experion getInstance() {
        return instance;
    }

    public Configuration getConfig() {
        return config;
    }
}
