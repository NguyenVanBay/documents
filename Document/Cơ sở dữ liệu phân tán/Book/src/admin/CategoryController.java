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
import model.Category;
import model.User;

public class CategoryController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public CategoryController() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();

		RequestDispatcher rd;
		if (check(request, response, session) == 0)
			return;

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");

		Long id;
		String action = request.getParameter("action");

		switch (action) {
		// xem danh sách thể loại.
		case "list":

			int page = 0;
			if (request.getParameter("page") != null) {
				page = Integer.parseInt(request.getParameter("page"));
				if (page < 0)
					page = 0;
			}

			ArrayList<Category> allCategory = new CategoryDAO().getAll();
			String name = (request.getParameter("name") == null || request.getParameter("name") == "") ? ""
					: request.getParameter("name");

			// check điều kiện hiển thị.
			if (name != "") {
				allCategory = new CategoryDAO().getWhere(name, page);
			} else {
				allCategory = new CategoryDAO().getAllPage(page);
			}

			if ("delete".equals(request.getParameter("error"))) {
				request.setAttribute("error", "Xóa không thành công");
			}

			if ("delete".equals(request.getParameter("success"))) {
				request.setAttribute("success", "Xóa thành công");
			}

			// add
			if ("add".equals(request.getParameter("success"))) {
				request.setAttribute("success", "Thêm thành công");
			}

			// edit
			if ("edit".equals(request.getParameter("success"))) {
				request.setAttribute("success", "Sửa thành công");
			}

			if (request.getParameter("error") == null) {
				request.setAttribute("error", "");
			}

			if (request.getParameter("success") == null) {
				request.setAttribute("success", "");
			}

			String url = "name=" + name;

			request.setAttribute("url", url);
			request.setAttribute("page", page);

			request.setAttribute("categorys", allCategory);
			rd = getServletContext().getRequestDispatcher("/admin/danh-sach-the-loai");
			rd.forward(request, response);
			return;

		case "add":
			rd = getServletContext().getRequestDispatcher("/admin/them-the-loai");
			rd.forward(request, response);
			return;

		case "edit":

			id = Long.parseLong(request.getParameter("id"));
			Category category = new CategoryDAO().getCategoryById(id);
			request.setAttribute("category", category);
			rd = getServletContext().getRequestDispatcher("/admin/sua-the-loai");
			rd.forward(request, response);
			return;

		case "delete":
			id = Long.parseLong(request.getParameter("id"));
			try {
				new CategoryDAO().deleteById(id);
				response.sendRedirect(this.getServletContext().getInitParameter("contextPath")
						+ "admin/category?action=list&success=delete");
			} catch (Exception e) {
				response.sendRedirect(this.getServletContext().getInitParameter("contextPath")
						+ "admin/category?action=list&error=delete");
			}
			return;
		default:
			break;
		}
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

		HttpSession session = request.getSession();
		check(request, response, session);

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");

		Category c;
		String name, url;
		String action = request.getParameter("action");

		switch (action) {
		case "add":
			request.setCharacterEncoding("UTF-8");

			name = request.getParameter("name");
			url = request.getParameter("url");

			if (name.equals("") || url.equals("")) {
				response.sendRedirect("/Book/admin/category?action=list&error=add?error=add");
				return;
			}

			c = new Category();

			c.setName(name);
			c.setUrl(url);
			c.setCreateBy(Long.parseLong(session.getAttribute("idAdmin").toString()));

			new CategoryDAO().insertCategory(c);

			response.sendRedirect("/Book/admin/category?action=list&success=add");
			break;

		case "edit":
			String id = request.getParameter("id");
			name = request.getParameter("name");
			url = request.getParameter("url");
			if (name.equals("") || url.equals("")) {
				response.sendRedirect("/Book/admin/category?action=edit&id=" + id + "&error=edit");
				return;
			}
			c = new CategoryDAO().getCategoryById(Long.parseLong(id));
			c.setName(name);
			c.setUrl(url);
			new CategoryDAO().editCategory(c);
			response.sendRedirect("/Book/admin/category?action=list&success=edit");
			return;

		default:
			break;
		}

	}
}
