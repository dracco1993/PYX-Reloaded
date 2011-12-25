package net.socialgamer.cah;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.dialect.PostgreSQLDialect;


/**
 * Servlet implementation class Schema
 */
@WebServlet("/Schema")
public class Schema extends HttpServlet {
  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public Schema() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // TODO Auto-generated method stub
    AnnotationConfiguration c = new AnnotationConfiguration();
    c.configure();
    String[] ls = c.generateSchemaCreationScript(new PostgreSQLDialect());
    PrintWriter out = response.getWriter();
    for (String l : ls) {
      out.println(l + ";");
    }
  }
}