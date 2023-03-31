package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Invest {
    static final Logger log = LoggerFactory.getLogger(Invest.class);

    static String token;

    static {
        try (InputStream input = Invest.class.getClassLoader().getResourceAsStream("token.properties")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            token = prop.getProperty("SandBox");

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}
