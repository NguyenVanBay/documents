package admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.UserDAO;
import model.User;

public class CustomerController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public CustomerController() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");

		
		RequestDispatcher rd;
		if(check(request, response, session) == 0) return;

		String action = request.getParameter("action");

		switch (action) {
		case "list":
			
			int page = 0;
			if (request.getParameter("page") != null) {
				page = Integer.parseInt(request.getParameter("page"));
				if(page < 0) page = 0;
			};
			
			ArrayList<User> allUser = new UserDAO().getAllCustomer(page);
			String name = (request.getParameter("name") == null || request.getParameter("name") == "") ? ""
					: request.getParameter("name");
			String email = (request.getParameter("email") == null || request.getParameter("email") == "") ? ""
					: request.getParameter("email");
			String phone = (request.getParameter("phone") == null || request.getParameter("phone") == "") ? ""
					: request.getParameter("phone");

			//check điều kiện search.
			if (name != "" || email != "" || phone != "") {
				allUser = new UserDAO().getWhereCustomer(name, email, phone, page);
			} else {
				allUser = new UserDAO().getAllCustomer(page);
			}
			
			String url = "name="+name+"&email="+email+"&phone="+phone;

			request.setAttribute("url", url);
			request.setAttribute("page", page);

			request.setAttribute("users", allUser);
			rd = getServletContext().getRequestDispatcher("/admin/listCustomer.jsp");
			rd.forward(request, response);
			return;
			
		case "delete" : 
			Long id = Long.parseLong(request.getParameter("id"));
			String type = (request.getParameter("type") == null) ? "" : request.getParameter("type");

			// delete khách hàng.
			if (type.equals("customer")) {
				try {
					new UserDAO().deleteById(id);
					response.sendRedirect("/Book/admin/customer?action=list");
					return;
				} catch (SQLException e) {
					response.sendRedirect("/Book/admin/customer?action=list&error=delete");
					return;
				}

			}
		}
		rd = getServletContext().getRequestDispatcher("/admin/listCustomer.jsp");
		rd.forward(request, response);
	}

	private int check(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws IOException, ServletException {
		// check login.
		if (null == session.getAttribute("email")) {
			// User is not logged in.
			response.sendRedirect(this.getServletContext().getInitParameter("contextPath") + "admin/Login");
			return 0;
		}
		return 1;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

}
