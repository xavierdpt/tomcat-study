package wo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ils2")
public class IncludeLoopServlet2 extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getWriter().println("ILS 2");
//		if (req.getAttribute("count") == null) {
//			req.setAttribute("count", 10);
//		}
//		if (((int) (req.getAttribute("count"))) > 0) {
//			req.setAttribute("count", ((int) (req.getAttribute("count"))) - 1);
			req.getRequestDispatcher("/ils1").include(req, resp);

//		}
	}

}
