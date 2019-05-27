package admin;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.BillDAO;
import dao.ProductDAO;
import model.Bill;
import model.BillDetail;
import model.User;

public class BillController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public BillController() {
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

		String action = request.getParameter("action");
		Long id;
		Bill bill;

		switch (action) {

		// xem danh đơn hàng.
		case "list":
			
			int page = 0;
			if (request.getParameter("page") != null) {
				page = Integer.parseInt(request.getParameter("page"));
				if(page < 0) page = 0;
			};

			ArrayList<Bill> bills = new ArrayList<>();
			String name = (request.getParameter("name") == null || request.getParameter("name") == "") ? ""
					: request.getParameter("name");
			String address = (request.getParameter("address") == null || request.getParameter("address") == "") ? ""
					: request.getParameter("address");
			String phone = (request.getParameter("phone") == null || request.getParameter("phone") == "") ? ""
					: request.getParameter("phone");
			String sumFrom = (request.getParameter("sumFrom") == null || request.getParameter("sumFrom") == "") ? ""
					: request.getParameter("sumFrom");
			String sumTo = (request.getParameter("sumTo") == null || request.getParameter("sumTo") == "") ? ""
					: request.getParameter("sumTo");
			String createFrom = (request.getParameter("createFrom") == null || request.getParameter("createFrom") == "")
					? ""
					: request.getParameter("createFrom");
			String createTo = (request.getParameter("createTo") == null || request.getParameter("createTo") == "") ? ""
					: request.getParameter("createTo");

			// hiển thị sản phẩm.
			if (name != "" || address != "" || phone != "" || sumFrom != "" || sumTo != "" || createFrom != ""
					|| createTo != "") {
				bills = new BillDAO().getWhere(name, address, phone, sumFrom, sumTo, createFrom, createTo, page);
			} else {
				bills = new BillDAO().getAll(page);
			}

			String url = "name="+name+"&address="+ address +"&phone="+phone+"&sumFrom="+sumFrom+"&sumTo="+sumTo+"&createFrom"+createFrom+"&createTo=" + createTo;
			
			request.setAttribute("url", url);
			request.setAttribute("page", page);
			request.setAttribute("bills", bills);
			rd = getServletContext().getRequestDispatcher("/admin/danh-sach-don-hang");
			rd.forward(request, response);
			return;

		// tạo hóa đơn xuất sản phẩm.
		case "add":
			id = Long.parseLong(request.getParameter("id"));
			bill = new BillDAO().getBillById(id);
			request.setAttribute("bill", bill);
			rd = getServletContext().getRequestDispatcher("/admin/createBill.jsp");
			rd.forward(request, response);
			return;

		// xem chi tiết đơn hàng.
		case "detail":

			id = Long.parseLong(request.getParameter("id"));
			bill = new BillDAO().getBillById(id);
			request.setAttribute("bill", bill);
			rd = getServletContext().getRequestDispatcher("/admin/chi-tiet-don-hang");
			rd.forward(request, response);
			return;

		// sửa đơn hàng.
		case "edit":

			id = Long.parseLong(request.getParameter("id"));
			Long status = Long.parseLong(request.getParameter("status"));

			Bill b = new BillDAO().getBillById(id);
			b.setStatus(status);

			// nếu là bắt đầu giao hàng thì sản phẩm trong kho phải trử đi.
			if (status == Bill.DANGGIAODON) {
				for (BillDetail billDetail : b.getListBillDetail()) {
					System.out.println("so luong tru " + (billDetail.getProduct().getCount() - billDetail.getCount()));
					new ProductDAO().updateCount(billDetail.getProduct().getId(),
							billDetail.getProduct().getCount() - billDetail.getCount());
				}
			}

			// nếu khách hàng trả lại hàng thì số lượng sản phẩm phải tăng lên.
			if (status == Bill.HUYDON) {
				for (BillDetail billDetail : b.getListBillDetail()) {
					System.out.println("so luong them " + (billDetail.getProduct().getCount() + billDetail.getCount()));
					new ProductDAO().updateCount(billDetail.getProduct().getId(),
							billDetail.getProduct().getCount() + billDetail.getCount());
				}
			}

			new BillDAO().editBill(b);
			response.sendRedirect(this.getServletContext().getInitParameter("contextPath") + "admin/bill?action=list");
			return;
		default:
			break;
		}
		rd = getServletContext().getRequestDispatcher("/admin/dasboard");
		rd.forward(request, response);
		return;
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
