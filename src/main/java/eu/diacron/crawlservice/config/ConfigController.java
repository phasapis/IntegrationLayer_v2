/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.diacron.crawlservice.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eleni
 */
public final class ConfigController {

    private final Properties prop = new Properties();

    private static ConfigController instance = null;

    protected ConfigController() {
        readProperties();

    }

    public static ConfigController getInstance() {
        if (instance == null) {            
            instance = new ConfigController();
        }
        return instance;
    }

    public void readProperties() {
        try {
            InputStream input;
            
            String initFilePath = "C:\\jboss-as-7.1.1.Final\\modules\\diachron\\configuration\\main\\integration_layer_config.properties";

            //input = this.getClass().getClassLoader().getResourceAsStream("diacrawl.properties");

            prop.load(new FileInputStream(initFilePath));

            // load a properties file
            Configuration.BROKER_URL                = prop.getProperty("BROKER_URL").trim();
            Configuration.REMOTE_CRAWLER_URL        = prop.getProperty("REMOTE_CRAWLER_URL").trim();
            Configuration.REMOTE_CRAWLER_USERNAME   = prop.getProperty("REMOTE_CRAWLER_USERNAME");
            Configuration.REMOTE_CRAWLER_PASS       = prop.getProperty("REMOTE_CRAWLER_PASS");
            Configuration.REMOTE_CRAWLER_URL_CRAWL_INIT  = prop.getProperty("REMOTE_CRAWLER_URL_CRAWL_INIT");
            Configuration.REMOTE_CRAWLER_URL_CRAWL  = prop.getProperty("REMOTE_CRAWLER_URL_CRAWL");
            Configuration.TMP_FOLDER_CRAWL          = prop.getProperty("TMP_FOLDER_CRAWL");
            Configuration.TMP_SERIALIZATION_RDF_FORMAT = prop.getProperty("TMP_SERIALIZATION_RDF_FORMAT");
            Configuration.TMP_SERIALIZATION_RDF_FILEEXT = prop.getProperty("TMP_SERIALIZATION_RDF_FILEEXT");

            //input.close();
        } catch (IOException ex) {
            Logger.getLogger(ConfigController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}//EoC

