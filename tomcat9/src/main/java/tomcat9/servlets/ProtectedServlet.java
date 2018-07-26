package tomcat9.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/protected")
public class ProtectedServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter writer = resp.getWriter();
		writer.println("Protected space !");
		writer.println(req.getAuthType());

		if ("CLIENT_CERT".equals(req.getAuthType())) {
			writer.println("Hello " + req.getUserPrincipal().getName() + " !");
		}
	}
}
