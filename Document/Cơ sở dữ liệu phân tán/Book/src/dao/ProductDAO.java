/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import connect.DBConnect;
import constant.Constant;
import model.Category;
import model.Image;
import model.Product;

/**
 *
 * @author NVB
 */
public class ProductDAO implements ProductInterface {

	// add Product.
	public boolean insertProduct(Product p) {
		Connection connection = DBConnect.getConnection();
		
		
		
		String sql = "INSERT INTO products(name, author, publics, count, price, id_category, title, description, type, create_by, url) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
		System.out.println(sql);
		Long id = (long)0;
		
		try {
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setString(1, p.getName());
			ps.setString(2, p.getAuthor());
			ps.setTimestamp(3, p.getPublicAt());
			ps.setLong(4, p.getCount());
			ps.setDouble(5, p.getPrice());
			ps.setLong(6, p.getCategory().getId());
			ps.setString(7, p.getTitle());
			ps.setString(8, p.getDescription());
			ps.setString(9, p.getType());
			ps.setLong(10, p.getCreateBy());
			ps.setString(11, p.getUrl());
			ps.executeUpdate();
			connection.close();
			
			id = this.getLastId();
			
			p.getThumbnail().setProduct(new Product(id));
			new ImageDAO().insertImage(p.getThumbnail());
			
			for (Image img : p.getListImage()) {
				img.setProduct(new Product(id));
				new ImageDAO().insertImage(img);
			}
			
			return true;
		}catch(

	SQLException ex)
	{
			Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
		}return false;
	}

	// edit Product by id.
	public boolean editProduct(Product p) {
		Connection connection = DBConnect.getConnection();
		String sql = "UPDATE products set name = ?, author = ?, publics = ?, count= ? , price= ?, id_category = ?, title = ?, description = ? , type = ?, url = ?  WHERE id = ?";
		System.out.println(sql);
		try {
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setString(1, p.getName());
			ps.setString(2, p.getAuthor());
			ps.setTimestamp(3, p.getPublicAt());
			ps.setLong(4, p.getCount());
			ps.setDouble(5, p.getPrice());
			ps.setLong(6, p.getCategory().getId());
			ps.setString(7, p.getTitle());
			ps.setString(8, p.getDescription());
			ps.setString(9, p.getType());
			ps.setString(10, p.getUrl());
			ps.setLong(11, p.getId());
			ps.executeUpdate();
			connection.close();
			return true;
		} catch (SQLException ex) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	public Product getProductById(long id) {
		Product p = new Product();

		try {
			Connection connection = DBConnect.getConnection();
			String sql = "SELECT * FROM products WHERE id = ?";
			System.out.println(sql);
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			Long categoryId = (long) 0;

			while (rs.next()) {

				categoryId = rs.getLong("id_category");

				p.setId(rs.getLong("id"));
				p.setName(rs.getString("name"));
				p.setAuthor(rs.getString("author"));
				p.setPublicAt(rs.getTimestamp("publics"));
				p.setCount(rs.getLong("count"));
				p.setPrice(rs.getDouble("price"));
				p.setTitle(rs.getString("title"));
				p.setDescription(rs.getString("description"));
				p.setUrl(rs.getString("url"));
				p.setType(rs.getString("type"));

			}
			connection.close();

			Category category = new CategoryDAO().getCategoryById(categoryId);
			ArrayList<Image> listImage = new ImageDAO().getImageByProductIdAndType(p.getId(), 2);
			Image thumbnail = new ImageDAO().getImageByProductIdAndType(p.getId(), 1).get(0);

			p.setCategory(category);
			p.setListImage(listImage);
			p.setThumbnail(thumbnail);

			return p;
		} catch (SQLException ex) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return p;
	}

	public ArrayList<Product> getAll() {

		ArrayList<Product> allProduct = new ArrayList<>();

		ArrayList<Long> allCategory = new ArrayList<>();

		Connection connection = DBConnect.getConnection();
		String sql = "SELECT * FROM products ORDER BY id DESC ";
		System.out.println(sql);
		PreparedStatement ps;
		try {
			ps = connection.prepareCall(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Product p = new Product();
				p.setId(rs.getLong("id"));
				p.setName(rs.getString("name"));
				p.setAuthor(rs.getString("author"));
				p.setPublicAt(rs.getTimestamp("publics"));
				p.setCount(rs.getLong("count"));
				p.setPrice(rs.getDouble("price"));
				p.setTitle(rs.getString("title"));
				p.setDescription(rs.getString("description"));
				p.setType(rs.getString("type"));
				p.setUrl(rs.getString("url"));
				allProduct.add(p);
				allCategory.add(rs.getLong("id_category"));

			}
			connection.close();

			for (int i = 0; i < allCategory.size(); i++) {

				Product p = allProduct.get(i);

				Category category = new CategoryDAO().getCategoryById(allCategory.get(i));
				ArrayList<Image> listImage = new ImageDAO().getImageByProductIdAndType(p.getId(), 2);

				Image imagesThumbnail = new Image();

				for (Image image : new ImageDAO().getImageByProductIdAndType(p.getId(), 1)) {
					p.setThumbnail(image);
					imagesThumbnail = image;
				}

				p.setCategory(category);
				p.setListImage(listImage);
				p.setThumbnail(imagesThumbnail);

				allProduct.set(i, p);
			}

		} catch (SQLException e) {
		}
		return allProduct;
	}

	public long getLastId() {
		long id = 0;
			Connection connection = DBConnect.getConnection();
			String sql = "SELECT MAX(id) AS lastid FROM products";
			System.out.println(sql);
			PreparedStatement ps;
			try {
				ps = connection.prepareCall(sql);
				
				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					id = rs.getLong("lastid");
				}
			} catch (SQLException e) {
			}
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}

	public ArrayList<Product> getAllPage(int page) {

		ArrayList<Product> allProduct = new ArrayList<>();

		ArrayList<Long> allCategory = new ArrayList<>();

		Connection connection = DBConnect.getConnection();
		String sql = "SELECT * FROM products ORDER BY id DESC OFFSET " + page * Constant.SOPHANTUMOTTRANG
				+ " ROWS FETCH NEXT " + Constant.SOPHANTUMOTTRANG + " ROWS ONLY";
		System.out.println(sql);
		PreparedStatement ps;
		try {
			ps = connection.prepareCall(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Product p = new Product();
				p.setId(rs.getLong("id"));
				p.setName(rs.getString("name"));
				p.setAuthor(rs.getString("author"));
				p.setPublicAt(rs.getTimestamp("publics"));
				p.setCount(rs.getLong("count"));
				p.setPrice(rs.getDouble("price"));
				p.setTitle(rs.getString("title"));
				p.setDescription(rs.getString("description"));
				p.setType(rs.getString("type"));
				p.setUrl(rs.getString("url"));
				allProduct.add(p);
				allCategory.add(rs.getLong("id_category"));

			}
			connection.close();

			for (int i = 0; i < allCategory.size(); i++) {

				Product p = allProduct.get(i);

				Category category = new CategoryDAO().getCategoryById(allCategory.get(i));
				ArrayList<Image> listImage = new ImageDAO().getImageByProductIdAndType(p.getId(), 2);

				Image imagesThumbnail = new Image();

				for (Image image : new ImageDAO().getImageByProductIdAndType(p.getId(), 1)) {
					p.setThumbnail(image);
					imagesThumbnail = image;
				}

				p.setCategory(category);
				p.setListImage(listImage);
				p.setThumbnail(imagesThumbnail);

				allProduct.set(i, p);
			}

		} catch (SQLException e) {
		}
		return allProduct;
	}

	public ArrayList<Product> getProductByCategoryId(Long categoryId) {

		ArrayList<Product> allProduct = new ArrayList<>();
		ArrayList<Long> allCategory = new ArrayList<>();

		try {
			Connection connection = DBConnect.getConnection();
			String sql = "SELECT * FROM products where id_category = ? ";
			
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setLong(1, categoryId);
			System.out.println(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Product p = new Product();
				p.setId(rs.getLong("id"));
				p.setName(rs.getString("name"));
				p.setAuthor(rs.getString("author"));
				p.setPublicAt(rs.getTimestamp("publics"));
				p.setCount(rs.getLong("count"));
				p.setPrice(rs.getDouble("price"));
				p.setTitle(rs.getString("title"));
				p.setDescription(rs.getString("description"));
				p.setType(rs.getString("type"));
				p.setUrl(rs.getString("url"));
				allProduct.add(p);
				allCategory.add(rs.getLong("id_category"));

			}
			connection.close();

			for (int i = 0; i < allProduct.size(); i++) {

				Product p = allProduct.get(i);

				Category category = new CategoryDAO().getCategoryById(allCategory.get(i));
				ArrayList<Image> listImage = new ImageDAO().getImageByProductIdAndType(p.getId(), 2);
				Image thumbnail = new ImageDAO().getImageByProductIdAndType(p.getId(), 1).get(0);

				p.setCategory(category);
				p.setListImage(listImage);
				p.setThumbnail(thumbnail);
				allProduct.set(i, p);
			}

			return allProduct;
		} catch (SQLException ex) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return allProduct;
	}

	public ArrayList<Product> getProductBySQL(String sql) {

		ArrayList<Product> allProduct = new ArrayList<>();
		ArrayList<Long> allCategory = new ArrayList<>();
		System.out.println("try van" + sql);

		Connection connection = DBConnect.getConnection();

		PreparedStatement ps;
		try {
			ps = connection.prepareCall(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Product p = new Product();
				p.setId(rs.getLong("id"));
				p.setName(rs.getString("name"));
				p.setAuthor(rs.getString("author"));
				p.setPublicAt(rs.getTimestamp("publics"));
				p.setCount(rs.getLong("count"));
				p.setPrice(rs.getDouble("price"));
				p.setTitle(rs.getString("title"));
				p.setDescription(rs.getString("description"));
				p.setType(rs.getString("type"));
				p.setUrl(rs.getString("url"));
				allProduct.add(p);
				allCategory.add(rs.getLong("id_category"));

			}
			connection.close();

			for (int i = 0; i < allCategory.size(); i++) {

				Product p = allProduct.get(i);

				Category category = new CategoryDAO().getCategoryById(allCategory.get(i));
				ArrayList<Image> listImage = new ImageDAO().getImageByProductIdAndType(p.getId(), 2);

				Image imagesThumbnail = new Image();

				for (Image image : new ImageDAO().getImageByProductIdAndType(p.getId(), 1)) {
					p.setThumbnail(image);
					imagesThumbnail = image;
				}

				p.setCategory(category);
				p.setListImage(listImage);
				p.setThumbnail(imagesThumbnail);

				allProduct.set(i, p);
			}

		} catch (SQLException e) {
		}
		return allProduct;
	}

	public void deleteById(Long id) {

		Product product = this.getProductById(id);

		new ImageDAO().deleteImageByIdProduct(product.getId());

		try {
			Connection connection = DBConnect.getConnection();

			String query = "delete from products where id = '" + id + "'";
			System.out.println(query);
			PreparedStatement preparedStmt = connection.prepareStatement(query);
			preparedStmt.execute();

			connection.close();

		} catch (SQLException ex) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public ArrayList<Product> getWhere(String name, String author, String title, String priceFrom, String priceTo,
			String idCategory, String publicFrom, String publicTo, int page) {
		ArrayList<Product> allProduct = new ArrayList<>();
		ArrayList<Long> allCategory = new ArrayList<>();

		try {
			Connection connection = DBConnect.getConnection();
			String sql = "SELECT * FROM products where 1 = 1 ";

			if (name != "") {
				sql += " AND name like '%" + name + "%' ";
			}

			if (author != "") {
				sql += " AND author like '%" + author + "%' ";
			}

			if (title != "") {
				sql += " AND title like '%" + title + "%' ";
			}

			if (idCategory != "-1") {
				sql += " AND id_category = " + idCategory + " ";
			}

			if (priceFrom != "" && priceTo != "") {
				sql += " AND price BETWEEN " + priceFrom + " AND " + priceTo + " ";
			}

			if (publicFrom != "" && publicTo != "") {
				sql += " AND public BETWEEN '" + publicFrom + "' AND '" + publicTo + "' ";
			}

			sql += " ORDER BY id DESC OFFSET " + page * Constant.SOPHANTUMOTTRANG + " ROWS FETCH NEXT "
					+ Constant.SOPHANTUMOTTRANG + " ROWS ONLY";
			System.out.println(sql);
			PreparedStatement ps = connection.prepareCall(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Product p = new Product();
				p.setId(rs.getLong("id"));
				p.setName(rs.getString("name"));
				p.setAuthor(rs.getString("author"));
				p.setPublicAt(rs.getTimestamp("publics"));
				p.setCount(rs.getLong("count"));
				p.setPrice(rs.getDouble("price"));
				p.setTitle(rs.getString("title"));
				p.setDescription(rs.getString("description"));
				p.setType(rs.getString("type"));
				p.setUrl(rs.getString("url"));
				allProduct.add(p);
				allCategory.add(rs.getLong("id_category"));

			}
			connection.close();

			for (int i = 0; i < allProduct.size(); i++) {

				Product p = allProduct.get(i);

				Category category = new CategoryDAO().getCategoryById(allCategory.get(i));
				ArrayList<Image> listImage = new ImageDAO().getImageByProductIdAndType(p.getId(), 2);
				Image thumbnail = new ImageDAO().getImageByProductIdAndType(p.getId(), 1).get(0);

				p.setCategory(category);
				p.setListImage(listImage);
				p.setThumbnail(thumbnail);
				allProduct.set(i, p);
			}

			return allProduct;
		} catch (SQLException ex) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return allProduct;
	}

	public ArrayList<Product> getWhereInClient(String name, String author, String priceFrom, String priceTo,
			String publicFrom, String publicTo) {
		ArrayList<Product> allProduct = new ArrayList<>();
		ArrayList<Long> allCategory = new ArrayList<>();

		try {
			Connection connection = DBConnect.getConnection();
			String sql = "SELECT * FROM products where 1 = 1 ";

			if (name != "") {
				sql += " AND name like '%" + name + "%'";
			}

			if (author != "") {
				sql += " AND author like '%" + author + "%'";
			}

			if (priceFrom != "" && priceTo != "") {
				sql += " AND price BETWEEN " + priceFrom + " AND " + priceTo + " ";
			}

			if (publicFrom != "" && publicTo != "") {
				sql += " AND public BETWEEN '" + publicFrom + "' AND '" + publicTo + "' ";
			}
			sql += " ORDER BY id DESC ";
			System.out.println(sql);
			PreparedStatement ps = connection.prepareCall(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Product p = new Product();
				p.setId(rs.getLong("id"));
				p.setName(rs.getString("name"));
				p.setAuthor(rs.getString("author"));
				p.setPublicAt(rs.getTimestamp("publics"));
				p.setCount(rs.getLong("count"));
				p.setPrice(rs.getDouble("price"));
				p.setTitle(rs.getString("title"));
				p.setDescription(rs.getString("description"));
				p.setType(rs.getString("type"));
				p.setUrl(rs.getString("url"));
				allProduct.add(p);
				allCategory.add(rs.getLong("id_category"));

			}
			connection.close();

			for (int i = 0; i < allProduct.size(); i++) {

				Product p = allProduct.get(i);

				Category category = new CategoryDAO().getCategoryById(allCategory.get(i));
				ArrayList<Image> listImage = new ImageDAO().getImageByProductIdAndType(p.getId(), 2);
				Image thumbnail = new ImageDAO().getImageByProductIdAndType(p.getId(), 1).get(0);

				p.setCategory(category);
				p.setListImage(listImage);
				p.setThumbnail(thumbnail);
				allProduct.set(i, p);
			}

			return allProduct;
		} catch (SQLException ex) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return allProduct;
	}

	@Override
	public boolean updateCount(Long idProduct, Long count) {

		Connection connection = DBConnect.getConnection();
		String sql = "UPDATE products set count = ?  WHERE id = ?";
		try {
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setLong(1, count);
			ps.setLong(2, idProduct);
			ps.executeUpdate();
			connection.close();
			return true;
		} catch (SQLException ex) {
			System.out.println("update so luong sp khong thanh cong.");
		}
		System.out.println(sql);
		return false;
	}

	public static void main(String[] args) {
		ArrayList<Product> listProduct = new ProductDAO().getAll();

		for (Product pr : listProduct) {
			System.out.println(pr.getName());
		}

	}
}
