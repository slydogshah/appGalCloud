package io.appgal.cloud.preprocess;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

@WebFilter(urlPatterns = "/*")
public class ReactRouteFilter extends HttpFilter {

    private static final Pattern FILE_NAME_PATTERN = Pattern.compile(".*[.][a-zA-Z\\d]+");

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;



        chain.doFilter(request, response);


        if (response.getStatus() == 404) {
            System.out.println("*****0******");
            String path = request.getRequestURI().substring(
                    request.getContextPath().length()).replaceAll("[/]+$", "");



            System.out.println("PATH: "+path);
            //if (!FILE_NAME_PATTERN.matcher(path).matches()) {
            if (path.startsWith("/dashboard")) {
                // We could not find the resource, i.e. it is not anything known to the server (i.e. it is not a REST
                // endpoint or a servlet), and does not look like a file so try handling it in the front-end routes
                // and reset the response status code to 200.

                if(path.equals("/dashboard"))
                {
                    path = "/";
                }
                else
                {
                    int startIndex = "/dashboard".length();
                    path = path.substring(startIndex);
                }

                System.out.println("FORWARD: "+path);
                response.setStatus(200);
                request.getRequestDispatcher(path).forward(request, response);
                //response.getOutputStream().close();
            }
        }
    }
}