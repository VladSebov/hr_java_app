package ru.ystu;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ystu.controller.AuthServlet;
import ru.ystu.controller.EmployeeServlet;
import ru.ystu.controller.UserServlet;
import ru.ystu.repository.UserRepository;
import ru.ystu.repository.UserRepositoryImpl;
import ru.ystu.repository.EmployeeRepository;
import ru.ystu.repository.EmployeeRepositoryImpl;
import ru.ystu.util.DatabaseSeeder;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import java.io.File;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        log.info("Starting Embedded Tomcat Server...");

        UserRepository userRepository = new UserRepositoryImpl();
        EmployeeRepository employeeRepository = new EmployeeRepositoryImpl();

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

        ru.ystu.filter.JwtFilter jwtFilter = new ru.ystu.filter.JwtFilter();

        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName("JwtFilter");
        filterDef.setFilter(jwtFilter);
        ctx.addFilterDef(filterDef);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("JwtFilter");
        filterMap.addURLPattern("/api/employees/*");
        ctx.addFilterMap(filterMap);

        AuthServlet authServlet = new AuthServlet(userRepository);
        Tomcat.addServlet(ctx, "AuthServlet", authServlet);
        ctx.addServletMappingDecoded("/api/auth/login", "AuthServlet");

        EmployeeServlet employeeServlet = new EmployeeServlet(employeeRepository); // Позже передадим сюда репозиторий
        Tomcat.addServlet(ctx, "EmployeeServlet", employeeServlet);
        ctx.addServletMappingDecoded("/api/employees/*", "EmployeeServlet");

        UserServlet userServlet = new UserServlet(userRepository);
        Tomcat.addServlet(ctx, "UserServlet", userServlet);
        ctx.addServletMappingDecoded("/api/users/*", "UserServlet");

        tomcat.start();
        log.info("Tomcat started successfully on port {}!", webPort);

        tomcat.getServer().await();
    }
}