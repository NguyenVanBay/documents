package admin;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import constant.Constant;
import dao.CategoryDAO;
import dao.ProductDAO;
import model.Category;
import model.Image;
import model.Product;
import model.User;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 50)
public class ProductController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public ProductController() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		
		RequestDispatcher rd;
		if(check(request, response, session) == 0) return;
		
		String action = request.getParameter("action");
		ArrayList<Category> listCategory;
		Product product;
		long id;
		
		switch (action) {
		
		// danh sách sản phẩm.
		case "list":
			
			int page = 0;
			if (request.getParameter("page") != null) {
				page = Integer.parseInt(request.getParameter("page"));
				if(page < 0) page = 0;
			};
			
			ArrayList<Product> allProduct = new ArrayList<>();
			String name = (request.getParameter("name") == null || request.getParameter("name") == "") ? ""
					: request.getParameter("name");
			String author = (request.getParameter("author") == null || request.getParameter("author") == "") ? ""
					: request.getParameter("author");
			String title = (request.getParameter("title") == null || request.getParameter("title") == "") ? ""
					: request.getParameter("title");
			String priceFrom = (request.getParameter("priceFrom") == null
					|| request.getParameter("priceFrom") == "") ? "" : request.getParameter("priceFrom");
			String priceTo = (request.getParameter("priceTo") == null || request.getParameter("priceTo") == "") ? ""
					: request.getParameter("priceTo");
			String idCategory = (request.getParameter("idCategory") == null
					|| request.getParameter("idCategory") == "") ? "-1" : request.getParameter("idCategory");
			String publicFrom = (request.getParameter("publicFrom") == null
					|| request.getParameter("publicFrom") == "") ? "" : request.getParameter("publicFrom");
			String publicTo = (request.getParameter("publicTo") == null || request.getParameter("publicTo") == "") ? ""
					: request.getParameter("publicTo");

			// nếu bất kì trường tìm kiếm nào khác khoảng trống thì sẽ tìm where.
			if (name != "" || author != "" || title != "" || priceFrom != "" || priceTo != "" || publicFrom != "" || publicTo != ""
					|| idCategory != "-1") {
				allProduct = new ProductDAO().getWhere(name, author, title, priceFrom, priceTo, idCategory, publicFrom, publicTo, page);
			// lấy tất cả sản phẩm.
			} else {
				allProduct = new ProductDAO().getAllPage(page);
			}

			if(idCategory == "-1") idCategory = "";			
			String url = "name="+name+"&author="+author+"&title="+title+"&priceFrom="+priceFrom+"&priceTo="+priceTo+"&publicFrom="+publicFrom+"&publicTo="+publicTo+"&idCategory="+ idCategory;

			request.setAttribute("url", url);
			request.setAttribute("page", page);

			listCategory = new CategoryDAO().getAll();
			request.setAttribute("categorys", listCategory);
			request.setAttribute("products", allProduct);
			rd = getServletContext().getRequestDispatcher("/admin/danh-sach-san-pham");
			rd.forward(request, response);
			return;
		
		// thêm sản phẩm.
		case "add":
			listCategory = new CategoryDAO().getAll();
			request.setAttribute("categorys", listCategory);
			rd = getServletContext().getRequestDispatcher("/admin/them-san-pham");
			rd.forward(request, response);
			return;
		
		// sửa sản phẩm.
		case "edit" : 
			id = Long.parseLong(request.getParameter("id"));

			listCategory = new CategoryDAO().getAll();
			request.setAttribute("categorys", listCategory);

			product = new ProductDAO().getProductById(id);
			request.setAttribute("product", product);

			rd = getServletContext().getRequestDispatcher("/admin/sua-san-pham");
			rd.forward(request, response);
			return;
		case "delete" :
			
			id = Long.parseLong(request.getParameter("id"));
			Product productTemp = new ProductDAO().getProductById(id);
			
			Files.delete(new File(Constant.UPLOAD_DIRECTORY + "/" + productTemp.getThumbnail().getName()).toPath());
			Files.delete(new File(Constant.UPLOAD_DIRECTORY + "/" + productTemp.getListImage().get(0).getName()).toPath());
			
			new ProductDAO().deleteById(id);
			response.sendRedirect(this.getServletContext().getInitParameter("contextPath") + "admin/product?action=list");
			return;
		case "detail" : 
			id = Long.parseLong(request.getParameter("id"));
			product = new ProductDAO().getProductById(id);
			request.setAttribute("product", product);
			rd = getServletContext().getRequestDispatcher("/admin/detailProduct.jsp");
			rd.forward(request, response);
			return;
		default:
			break;
		}	
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		
		check(request, response, session);
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		
		String action = request.getParameter("action");
		
		String userName, idCategory, author, publicAt, price, count, title, description, type, url;
		int milliseconds = 0;
		Date d;
		SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
		
		switch (action) {
		
		// thêm sản phẩm
		case "add":
			
			userName = request.getParameter("name");
			idCategory = request.getParameter("idCategory");
			author = request.getParameter("author");
			publicAt = request.getParameter("publicAt");
			count = request.getParameter("count");
			price = request.getParameter("price");
			title = request.getParameter("title");
			description = request.getParameter("description");
			type = request.getParameter("type");
			url = request.getParameter("url");

			try {
				d = f.parse(publicAt);
				milliseconds = (int) d.getTime();
			} catch (Exception e) {
				System.out.println("chưa nhập ngày");
			}

			if (userName.equals("") || idCategory.equals("") || author.equals("") || publicAt.equals("")
					|| count.equals("") || price.equals("") || title.equals("") || description.equals("")
					|| type.equals("") || url.equals("")) {
				response.sendRedirect("/Book/admin/addProduct?error=add");
				return;
			}

			Product p = new Product();
			p.setName(userName);
			p.setPrice(Double.parseDouble(price));
			p.setAuthor(author);
			p.setCategory(new Category(Long.parseLong(idCategory)));
			p.setCount(Long.parseLong(count));
			p.setPublicAt(new Timestamp(milliseconds));
			p.setTitle(title);
			p.setDescription(description);
			p.setType(type);
			p.setUrl(url);
			p.setCreateBy(Long.parseLong(session.getAttribute("idAdmin").toString()));

			String nameImg = "image" + new Date().getTime();

			// set thumbnail.
			Image thumbnail = new Image();
			thumbnail.setName(nameImg + "1.png");
			thumbnail.setType((long) 1);
			p.setThumbnail(thumbnail);
			// add image to product.
			ArrayList<Image> listImg = new ArrayList<>();
			Image img1 = new Image();
			img1.setName(nameImg + "2.png");
			img1.setType((long) 2);
			listImg.add(img1);
			p.setListImage(listImg);

			if (new ProductDAO().insertProduct(p)) {
				// upload image.
				Part part1 = request.getPart("thubnail");
				Part part2 = request.getPart("image1");
				String savePath1 = Constant.UPLOAD_DIRECTORY + File.separator + nameImg + "1.png";
				String savePath2 = Constant.UPLOAD_DIRECTORY + File.separator + nameImg + "2.png";
				part1.write(savePath1 + File.separator);
				part2.write(savePath2 + File.separator);

			}

			response.sendRedirect("/Book/admin/product?action=list");
			return;
			
		// sửa sản phẩm.
		case "edit":
		String id = request.getParameter("id");
		userName = request.getParameter("name");
		idCategory = request.getParameter("idCategory");
		author = request.getParameter("author");
		publicAt = request.getParameter("publicAt");
		count = request.getParameter("count");
		price = request.getParameter("price");
		title = request.getParameter("title");
		description = request.getParameter("description");
		type = request.getParameter("type");
		url = request.getParameter("url");

		// check param.
		if (userName.equals("") || idCategory.equals("") || author.equals("") || publicAt.equals("")
				|| count.equals("") || price.equals("") || title.equals("") || description.equals("")
				|| type.equals("") || url.equals("")) {
			response.sendRedirect("/Book/admin/product?action=edit&id=" + id + "&error=edit");
			return;
		}

		try {
			d = f.parse(publicAt);
			milliseconds = (int) d.getTime();
		} catch (Exception e) {
			System.out.println("chưa nhập ngày");
		}

		Product product = new ProductDAO().getProductById(Long.parseLong(id));

		product.setName(userName);
		product.setCategory(new Category(Long.parseLong(idCategory)));
		product.setAuthor(author);
		product.setCount(Long.parseLong(count));
		product.setPrice(Double.parseDouble(price));
		product.setPublicAt(new Timestamp(milliseconds));
		product.setTitle(title);
		product.setDescription(description);
		product.setType(type);
		product.setUrl(url);

		if(new ProductDAO().editProduct(product)) {
			if(request.getParameter("changeimg").equals("false")) {
				System.out.println(product.getThumbnail().getName());
				System.out.println(product.getListImage().get(0).getName());
			} else {
				
				Part part1 = request.getPart("thumbnail");
				Part part2 = request.getPart("imageDetail");
				String savePath1 = Constant.UPLOAD_DIRECTORY + File.separator + product.getThumbnail().getName();
				String savePath2 = Constant.UPLOAD_DIRECTORY + File.separator + product.getListImage().get(0).getName();
			
				part1.write(savePath1 + File.separator);
				part2.write(savePath2 + File.separator);
			}
		}
		response.sendRedirect("/Book/admin/product?action=list");
		return;
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
	
}
