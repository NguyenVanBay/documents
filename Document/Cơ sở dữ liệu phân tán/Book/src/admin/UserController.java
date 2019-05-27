package admin;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.CategoryDAO;
import dao.ChinhanhDAO;
import dao.UserDAO;
import model.Chinhanh;
import model.User;
import tool.MD5;

public class UserController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public UserController() {
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
		if (check(request, response, session) == 0)
			return;

		String action = request.getParameter("action");
		long id;

		switch (action) {

		// danh sách nhân viên.
		case "list":
			
			int page = 0;
			if (request.getParameter("page") != null) {
				page = Integer.parseInt(request.getParameter("page"));
				if(page < 0) page = 0;
			};
			
			ArrayList<User> allUser = null;

			String name = (request.getParameter("name") == null || request.getParameter("name") == "") ? ""
					: request.getParameter("name");
			String email = (request.getParameter("email") == null || request.getParameter("email") == "") ? ""
					: request.getParameter("email");
			String phone = (request.getParameter("phone") == null || request.getParameter("phone") == "") ? ""
					: request.getParameter("phone");
			String role = (request.getParameter("role") == null || request.getParameter("role") == "0") ? "0"
					: request.getParameter("role");

			// get dữ liệu vs điều kiện.
			if (name != "" || email != "" || phone != "" || role != "") {
				allUser = new UserDAO().getWhereUser(name, email, phone, role, page);
			} else {
				allUser = new UserDAO().getAllUser(page);
			}
			
			String url = "name="+name+"&email="+email+"&phone="+phone+"&role="+role;

			request.setAttribute("url", url);
			request.setAttribute("page", page);
			
			request.setAttribute("users", allUser);
			rd = getServletContext().getRequestDispatcher("/admin/danh-sach-nguoi-dung");
			rd.forward(request, response);
			return;
		case "add":
			ArrayList<Chinhanh> listChinhanh = new ChinhanhDAO().getAll();
			request.setAttribute("chinhanh", listChinhanh);
			rd = getServletContext().getRequestDispatcher("/admin/them-nguoi-dung");
			rd.forward(request, response);
			return;
		case "edit":
			id = Long.parseLong(request.getParameter("id"));
			User user = new UserDAO().getUserById(id);

			// check điều kiện user đó là người tạo ra tài khoản này thì mới cho sửa.
			if (user.getCreateBy() == Long.parseLong(session.getAttribute("idAdmin") + "")) {
				request.setAttribute("user", user);
				rd = getServletContext().getRequestDispatcher("/admin/sua-nguoi-dung");
				rd.forward(request, response);
			} else {
				response.sendRedirect(this.getServletContext().getInitParameter("contextPath") + "admin/user?action=list&error=edit");
				return;
			}
			return;

		case "delete":
			
			Long idLogin = Long.parseLong(session.getAttribute("idAdmin") + "");
			id = Long.parseLong(request.getParameter("id"));
			User u = new UserDAO().getUserById(id);

			// nếu người tạo là user ms có quyền xóa.
			if (u.getCreateBy().equals(idLogin)) {
				try {
					new UserDAO().deleteById(id);
					response.sendRedirect(
							this.getServletContext().getInitParameter("contextPath") + "admin/user?action=list");
					return;
				} catch (Exception e) {
				}
			}
			
			response.sendRedirect(this.getServletContext().getInitParameter("contextPath")
					+ "admin/user?action=list&error=delete");
			return;
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");

		// check quyền.
		if (check(request, response, session) == 0)
			return;

		String action = request.getParameter("action");
		String address, email, password, rePassword, phone, role, name;
		long idchinhanh = 0;
		User u;
		
		switch (action) {
		case "add":
			address = request.getParameter("address");
			email = request.getParameter("email");
			name = request.getParameter("name");
			password = request.getParameter("password");
			rePassword = request.getParameter("re-password");
			phone = request.getParameter("phone");
			role = request.getParameter("role");
			idchinhanh = Long.parseLong(request.getParameter("idchinhanh"));

			// check param add.
			if (address.equals("") || email.equals("") || name.equals("") || password.equals("")
					|| rePassword.equals("") || phone.equals("") || role.equals("")) {

				if (new UserDAO().checkEmail(email)) {
					response.sendRedirect(this.getServletContext().getInitParameter("contextPath") + "admin/user?action=add&error=add&email=exists");
					return;
				} else {
					response.sendRedirect(this.getServletContext().getInitParameter("contextPath") + "admin/user?action=add&error=add");
					return;
				}
			} else {
				if (new UserDAO().checkEmail(email)) {
					response.sendRedirect(this.getServletContext().getInitParameter("contextPath") + "admin/user?action=list&error=add&email=exists");
					return;
				}
			}

			if (checkUser(email, password, rePassword) == 1) {

				if (new UserDAO().checkEmail(email)) {
					response.sendRedirect(this.getServletContext().getInitParameter("contextPath") + "admin/user?action=list&error=add?email=exist");
				} else {

					u = new User();
					u.setAddress(address);
					u.setEmail(email);
					u.setName(name);
					u.setPhone(phone);
					u.setRole(Long.parseLong(role));
					u.setIdchinhanh(idchinhanh);
					u.setCreateBy(Long.parseLong(session.getAttribute("idAdmin").toString()));
					password = MD5.encryption(password);
					u.setPassword(password);
					
					new UserDAO().insertUser(u);
					response.sendRedirect(this.getServletContext().getInitParameter("contextPath") + "admin/user?action=list&email=" + email);
				}
			}
			return;

		case "edit":
			String id = request.getParameter("id");
			address = request.getParameter("address");
			email = request.getParameter("email");
			name = request.getParameter("name");
			phone = request.getParameter("phone");
			role = request.getParameter("role");

			// check dk sua.
			if (address.equals("") || email.equals("") || name.equals("") || phone.equals("") || role.equals("")) {
				response.sendRedirect(this.getServletContext().getInitParameter("contextPath") + "admin/user?action=edit&id=" + id + "&error=edit");
				return;
			}

			u = new UserDAO().getUserById(Long.parseLong(id));

			if (!u.getEmail().equals(email)) {

				if (!new UserDAO().checkEmail(email)) {
					u.setId(Long.parseLong(id));
					u.setAddress(address);
					u.setEmail(email);
					u.setName(name);
					u.setPhone(phone);
					u.setRole(Long.parseLong(role));

					new UserDAO().editUser(u);
					response.sendRedirect(this.getServletContext().getInitParameter("contextPath") + "admin/user?action=list&email=" + email);
				} else {
					response.sendRedirect(this.getServletContext().getInitParameter("contextPath") + "admin/user?action=edit&id=" + id + "&error=edit&email=exists");
				}

			} else {

				u = new User();
				u.setId(Long.parseLong(id));
				u.setAddress(address);
				u.setEmail(email);
				u.setName(name);
				u.setPhone(phone);
				u.setRole(Long.parseLong(role));
				new UserDAO().editUser(u);
				response.sendRedirect(this.getServletContext().getInitParameter("contextPath") + "admin/user?action=list&email=" + email);
			}
			break;
		}
	}

	// check Trùng pass.
	private int checkUser(String email, String password, String rePassword) {

		if (password.equals(rePassword)) {
			return 1;
		}
		return 0;

	}

	// check quyền.
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
}
