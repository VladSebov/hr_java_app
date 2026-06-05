package ru.ystu;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ystu.controller.HelloServlet;

import java.io.File;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        log.info("Starting Embedded Tomcat Server...");

        Tomcat tomcat = new Tomcat();

        String webPort = System.getenv().getOrDefault("PORT", "8080");
        tomcat.setPort(Integer.parseInt(webPort));

        tomcat.getConnector();

        String baseDir = new File("target/tomcat").getAbsolutePath();
        tomcat.setBaseDir(baseDir);


        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());

        HelloServlet helloServlet = new HelloServlet();
        Tomcat.addServlet(ctx, "HelloServlet", helloServlet);
        ctx.addServletMappingDecoded("/api/hello", "HelloServlet");

        tomcat.start();
        log.info("Tomcat started successfully on port {}!", webPort);

        tomcat.getServer().await();
    }
}