package ru.ystu;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ystu.controller.AuthServlet;
import ru.ystu.repository.UserRepository;
import ru.ystu.repository.UserRepositoryImpl;
import ru.ystu.util.DatabaseSeeder;

import java.io.File;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        log.info("Starting Embedded Tomcat Server...");

        UserRepository userRepository = new UserRepositoryImpl();

        try {
            DatabaseSeeder.seedAdmin(userRepository);
        } catch (Exception e) {
            log.error("Failed to seed database! Server will continue to start, but auth might fail.", e);
        }

        Tomcat tomcat = new Tomcat();
        String webPort = System.getenv().getOrDefault("PORT", "8080");
        tomcat.setPort(Integer.parseInt(webPort));
        tomcat.getConnector();

        String baseDir = new File("target/tomcat").getAbsolutePath();
        tomcat.setBaseDir(baseDir);

        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());

        AuthServlet authServlet = new AuthServlet(userRepository);

        Tomcat.addServlet(ctx, "AuthServlet", authServlet);
        ctx.addServletMappingDecoded("/api/auth/login", "AuthServlet");

        tomcat.start();
        log.info("Tomcat started successfully on port {}!", webPort);

        tomcat.getServer().await();
    }
}