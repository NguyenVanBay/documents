package admin;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.Constant;
import dao.BillDAO;
import dao.ProductDAO;
import model.Bill;
import model.Product;

/**
 * Servlet implementation class Dasboard
 */

public class DasboardController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DasboardController() {
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
			response.sendRedirect(this.getServletContext().getInitParameter("contextPath") + "admin/Login");
			return;
		}
		
		// lấy danh sách sản phẩm sắp hết hàng.
		ArrayList<Product> listProduct = new ProductDAO()
				.getProductBySQL("SELECT * FROM products WHERE count < " + Constant.soLuongConLaiNhoHon);
		request.setAttribute("listwarningProduct", listProduct);

		// lấy đơn hàng đã giao trong tháng này.
		Date date = new Date();
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int month = localDate.getMonthValue();
		String sqlGetBillInMonth = "SELECT * FROM bills WHERE MONTH(create_at) = " + month + " AND status = 1";
		ArrayList<Bill> listBill = new BillDAO().geBySQL(sqlGetBillInMonth);
		request.setAttribute("listBill", listBill);

		request.setAttribute("donchuaduyet", new BillDAO().countByStatus(0));
		request.setAttribute("dondagiao", new BillDAO().countByStatus(1));
		request.setAttribute("donhuy", new BillDAO().countByStatus(3));
		request.setAttribute("dondanggiao", new BillDAO().countByStatus(2));

		RequestDispatcher rd = getServletContext().getRequestDispatcher("/admin/dasboard.jsp");
		rd.forward(request, response);
		return;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
