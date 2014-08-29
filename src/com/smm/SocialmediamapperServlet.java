package com.smm;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class SocialmediamapperServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
	      
	            resp.setContentType("text/plain");
	            resp.getWriter().println("Hello, " );
	}
}

