package tomcat9.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Session;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;

@WebServlet("/adduser")
public class AddUserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		PrintWriter w = resp.getWriter();
		try {
			doSomething(w);

		} catch (Exception ex) {
			w.println(ex.getClass().getName() + " : " + ex.getMessage());
		}

	}

	private void doSomething(PrintWriter w) throws NamingException {

		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		UserDatabase u = (UserDatabase) envCtx.lookup("UserDatabase");
		Iterator<User> users = u.getUsers();
		while (users.hasNext()) {
			User user = users.next();
			w.println(user.getName());
		}
		// TODO : actually add users here
	}

}
