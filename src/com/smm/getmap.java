package com.smm;

import java.io.*;

import java.text.*;
 
import java.util.*;
 
import javax.servlet.*;
 
import javax.servlet.http.*;
 
public class getmap extends HttpServlet {
 
	public void init(ServletConfig config) throws ServletException {	
		super.init(config);
	}
 
	public void destroy() {
 
	}
 
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
		PrintWriter out = response.getWriter();
		String in = request.getParameter("test");
		out.println(in);
	}
 
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
		doPost(request,response);
	}
}