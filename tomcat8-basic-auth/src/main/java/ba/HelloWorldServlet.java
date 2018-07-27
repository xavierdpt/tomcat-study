package ba;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/helloworld")
public class HelloWorldServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter w = resp.getWriter();
		w.println("Hello World !");
		w.println(req.getAuthType());
		Principal p = req.getUserPrincipal();
		if (p != null) {
			w.print(p.getName());
		}
		req.login("user", "user");
		w.println(req.getAuthType());
		p = req.getUserPrincipal();
		if (p != null) {
			w.print(p.getName());
		}
	}
}
