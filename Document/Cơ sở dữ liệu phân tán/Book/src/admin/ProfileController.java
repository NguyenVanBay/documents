package admin;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.UserDAO;
import model.User;
import tool.MD5;

/**
 * Servlet implementation class LoginController
 */
@WebServlet("/admin/profile")
public class ProfileController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ProfileController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();

		if (null == session.getAttribute("email")) {
			// User is not logged in.
			response.sendRedirect("/Book/admin/Login");
			return;
		}

		Long idAdmin = Long.parseLong(session.getAttribute("idAdmin").toString());
		User userLogin = new UserDAO().getUserById(idAdmin);
		String type = request.getParameter("type");
		request.setAttribute("user", userLogin);
		if (type.equals("infomation")) {
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/admin/profile.jsp");
			rd.forward(request, response);
		} else if (type.equals("edit")) {
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/admin/editProfile.jsp");
			rd.forward(request, response);
		}
	}

	/**
	 * @throws IOException
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		HttpSession session = request.getSession();

		if (null == session.getAttribute("email")) {
			// User is not logged in.
			response.sendRedirect("/Book/admin/Login");
			return;
		}

		request.setCharacterEncoding("UTF-8");

		String address = request.getParameter("address");
		String email = request.getParameter("email");
		String name = request.getParameter("name");
		String phone = request.getParameter("phone");
		String password = request.getParameter("password");
		password = MD5.encryption(password);

		String status = request.getParameter("status");
		String newpassword = request.getParameter("newpass");
		String newrepassword = request.getParameter("newpass_re");

		Long idLogin = Long.parseLong(session.getAttribute("idAdmin").toString());
		User userOld = new UserDAO().getUserById(idLogin);

		if (password.equals(userOld.getPassword())) {
			if (!userOld.getEmail().equals(email)) {

				// email chưa tồn tại.
				if (!new UserDAO().checkEmail(email)) {
					User u = userOld;
					u.setAddress(address);
					u.setEmail(email);
					u.setName(name);
					u.setPhone(phone);
					u.setPassword(password);

					if (status.equals("1")) {
						if (newpassword.equals(newrepassword)) {
							u.setPassword(MD5.encryption(newpassword));
						} else {
							response.sendRedirect(this.getServletContext().getInitParameter("contextPath") + "admin/profile?type=edit&repass=false");
						}
					}

					new UserDAO().editUser(u);
					response.sendRedirect(this.getServletContext().getInitParameter("contextPath") + "admin/profile?type=infomation");
				} else {
					response.sendRedirect(this.getServletContext().getInitParameter("contextPath") + "admin/profile?type=edit&error=edit&email=exists");
				}

			} else {

				User u = userOld;
				u.setAddress(address);
				u.setEmail(email);
				u.setName(name);
				u.setPhone(phone);

				if (status.equals("1")) {
					if (newpassword.equals(newrepassword)) {
						u.setPassword(MD5.encryption(newpassword));
					} else {
						response.sendRedirect(this.getServletContext().getInitParameter("contextPath") + "admin/profile?type=edit&repass=false");
					}
				}

				new UserDAO().editUser(u);
				session.setAttribute("password", password);
				response.sendRedirect(this.getServletContext().getInitParameter("contextPath") + "admin/profile?type=infomation");
			}
		} else {
			response.sendRedirect(this.getServletContext().getInitParameter("contextPath") + "admin/profile?type=edit&error=passfalse");
		}
	}
}
