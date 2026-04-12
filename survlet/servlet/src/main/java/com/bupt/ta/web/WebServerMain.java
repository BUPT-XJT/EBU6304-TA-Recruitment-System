package com.bupt.ta.web;

import com.bupt.ta.web.servlet.AdminServlet;
import com.bupt.ta.web.servlet.LoginServlet;
import com.bupt.ta.web.servlet.LogoutServlet;
import com.bupt.ta.web.servlet.MoServlet;
import com.bupt.ta.web.servlet.RegisterServlet;
import com.bupt.ta.web.servlet.TaServlet;

import jakarta.servlet.ServletContext;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configurations;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WebServerMain {
    /**
     * Resolves {@code src/webapp} without relying on the JVM working directory (so IDE runs from any folder work).
     * Override with {@code -Dta.webapp=E:\path\to\src\webapp}.
     */
    static Path resolveWebappRoot() throws Exception {
        String override = System.getProperty("ta.webapp");
        if (override != null && !override.isBlank()) {
            Path p = Paths.get(override).toAbsolutePath().normalize();
            if (Files.isDirectory(p.resolve("WEB-INF"))) {
                return p;
            }
            throw new IllegalStateException("ta.webapp is not a webapp (missing WEB-INF): " + p);
        }

        Path cwdWebapp = Paths.get("").toAbsolutePath().resolve("src/webapp");
        if (Files.isDirectory(cwdWebapp.resolve("WEB-INF"))) {
            return cwdWebapp.normalize();
        }

        var codeSource = WebServerMain.class.getProtectionDomain().getCodeSource();
        if (codeSource != null && codeSource.getLocation() != null) {
            URI codeUri = codeSource.getLocation().toURI();
            Path codePath = Paths.get(codeUri);
            if (Files.isDirectory(codePath)) {
                Path moduleRoot = codePath.getParent().getParent();
                Path fromModule = moduleRoot.resolve("src/webapp");
                if (Files.isDirectory(fromModule.resolve("WEB-INF"))) {
                    return fromModule.normalize();
                }
            }
        }

        throw new IllegalStateException(
                "Cannot find src/webapp (need WEB-INF). CWD webapp was: " + cwdWebapp
                        + ". Set -Dta.webapp=<absolute path to src/webapp> or run with working directory = servlet module root.");
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        Server server = new Server(port);

        Configurations configs = Configurations.setServerDefault(server);
        configs.add(new AnnotationConfiguration());
        configs.sort();

        WebAppContext ctx = new WebAppContext();
        ctx.setContextPath("/");
        Path webappPath = resolveWebappRoot();
        ProjectPaths.syncModuleRootWithWebapp(webappPath);
        ctx.setBaseResource(Resource.newResource(webappPath.toUri()));
        ctx.setWelcomeFiles(new String[]{"index.html"});
        ctx.setDescriptor(null);

        File jspTmp = Files.createTempDirectory("jetty-jsp").toFile();
        jspTmp.deleteOnExit();
        ctx.setTempDirectory(jspTmp);
        ctx.setAttribute(ServletContext.TEMPDIR, jspTmp);

        ctx.addServlet(new ServletHolder(new LoginServlet()), "/login");
        ctx.addServlet(new ServletHolder(new LogoutServlet()), "/logout");
        ctx.addServlet(new ServletHolder(new RegisterServlet()), "/register");
        ctx.addServlet(new ServletHolder(new TaServlet()), "/ta/*");
        ctx.addServlet(new ServletHolder(new MoServlet()), "/mo/*");
        ctx.addServlet(new ServletHolder(new AdminServlet()), "/admin/*");

        ServletHolder defaultHolder = new ServletHolder("default", DefaultServlet.class);
        defaultHolder.setInitParameter("dirAllowed", "false");
        ctx.addServlet(defaultHolder, "/");

        server.setHandler(ctx);
        server.start();
        System.out.println("=================================================");
        System.out.println("  Webapp root: " + webappPath);
        Path usersPath = ProjectPaths.moduleRoot().resolve("data/users.txt");
        System.out.println("  Data dir:    " + ProjectPaths.moduleRoot().resolve("data"));
        System.out.println("  users.txt:   " + usersPath + (Files.isRegularFile(usersPath) ? " (ok)" : " (MISSING — login will fail)"));
        System.out.println("  BUPT TA Recruitment System started!");
        System.out.println("  Open http://localhost:" + port + "/login  (Servlet + JSP UI)");
        System.out.println("  JSP demo: http://localhost:" + port + "/jsp/open-positions.jsp");
        System.out.println("=================================================");
        server.join();
    }
}
