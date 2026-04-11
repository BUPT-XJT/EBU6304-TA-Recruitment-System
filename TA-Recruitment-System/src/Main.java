import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        Server server = new Server(port);

        ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ctx.setContextPath("/");

        Path webappPath = Paths.get("src/webapp").toAbsolutePath();
        ctx.setBaseResource(Resource.newResource(webappPath.toUri()));
        ctx.setWelcomeFiles(new String[]{"index.html"});

        ctx.addServlet(new ServletHolder("api", new ApiServlet()), "/api/*");

        ServletHolder defaultHolder = new ServletHolder("default", DefaultServlet.class);
        defaultHolder.setInitParameter("dirAllowed", "false");
        ctx.addServlet(defaultHolder, "/");

        server.setHandler(ctx);
        server.start();
        System.out.println("=================================================");
        System.out.println("  BUPT TA Recruitment System started!");
        System.out.println("  Open http://localhost:" + port + " in your browser");
        System.out.println("=================================================");
        server.join();
    }
}
