/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import connect.DBConnect;
import constant.Constant;
import model.Image;
import model.Product;

/**
 *
 * @author NVB
 */
public class ImageDAO implements ImageInterface {

	// add image.
	public boolean insertImage(Image i) {
		Connection connection = DBConnect.getConnection();
		String sql = "INSERT INTO images(name, id_product, type) VALUES(?,?,?)";
		try {
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setString(1, i.getName());
			ps.setLong(2, i.getProduct().getId());
			ps.setLong(3, i.getType());

			ps.executeUpdate();
			connection.close();
			return true;
		} catch (SQLException ex) {
			Logger.getLogger(ImageDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	// add list image.
	public boolean insertListImage(ArrayList<Image> listImg) {
		Connection connection = DBConnect.getConnection();
		String sql = "INSERT INTO images(name, id_product, type) VALUES(?,?,?)";
		for (Image i : listImg) {
			try {
				PreparedStatement ps = connection.prepareCall(sql);
				ps.setString(1, i.getName());
				ps.setLong(2, i.getProduct().getId());
				ps.setLong(3, i.getType());
				ps.executeUpdate();
			} catch (SQLException ex) {
				Logger.getLogger(ImageDAO.class.getName()).log(Level.SEVERE, null, ex);
				return false;
			}
		}

		try {
			connection.close();
		} catch (SQLException e) {
			return false;
		}

		return true;
	}

	// edit Image by id.
	public boolean editImage(Image i) {
		Connection connection = DBConnect.getConnection();
		String sql = "UPDATE images set name = ?, id_product = ?, type = ? WHERE id = ?";
		try {
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setString(1, i.getName());
			ps.setLong(2, i.getProduct().getId());
			ps.setLong(3, i.getType());
			ps.setLong(6, i.getId());
			ps.executeUpdate();
			connection.close();
			return true;
		} catch (SQLException ex) {
			Logger.getLogger(ImageDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	public Image getImageById(long id) {
		try {
			Connection connection = DBConnect.getConnection();
			String sql = "SELECT * FROM images WHERE id = ?";
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			Image i = new Image();
			while (rs.next()) {
				
				Product p = new ProductDAO().getProductById(rs.getLong("id_product"));

				i.setId(rs.getLong("id"));
				i.setName(rs.getString("name"));
				i.setProduct(p);
				i.setType(rs.getLong("type"));
			}
			connection.close();
			return i;
		} catch (SQLException ex) {
			Logger.getLogger(ImageDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
	
	public ArrayList<Image> getImageByProductIdAndType(long idProduct, long type) {
		
		ArrayList<Image> listImage = new ArrayList<>();
		
		try {
			Connection connection = DBConnect.getConnection();
			String sql = "SELECT * FROM images WHERE id_product = ? AND type = ?";
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setLong(1, idProduct);
			ps.setLong(2, type);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				Image i = new Image();
				i.setId(rs.getLong("id"));
				i.setName(rs.getString("name"));
				i.setType(rs.getLong("type"));
				
				listImage.add(i);
			}
			connection.close();
			return listImage;
		} catch (SQLException ex) {
			Logger.getLogger(ImageDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return listImage;
	}

	public ArrayList<Image> getAll() {

		ArrayList<Image> allImage = new ArrayList<>();

		try {
			Connection connection = DBConnect.getConnection();
			String sql = "SELECT * FROM images";
			PreparedStatement ps = connection.prepareCall(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				
				Image i = new Image();
				i.setId(rs.getLong("id"));
				i.setName(rs.getString("name"));
				i.setType(rs.getLong("type"));
				allImage.add(i);
			}
			connection.close();
			return allImage;
		} catch (SQLException ex) {
			Logger.getLogger(ImageDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public void deleteById(Long id) {
		try {
			Connection connection = DBConnect.getConnection();

			String query = "delete from images where id = ?";
			PreparedStatement preparedStmt = connection.prepareStatement(query);
			preparedStmt.setLong(1, id);
			preparedStmt.execute();

			connection.close();

		} catch (SQLException ex) {
			Logger.getLogger(ImageDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	// delete list image.
		public void deleteListImage(ArrayList<Image> listImage) {
			
			Connection connection = DBConnect.getConnection();
			String query = "delete from images where id = ?";
			
			for (Image image : listImage) {

				try {
					PreparedStatement preparedStmt = connection.prepareStatement(query);
					preparedStmt.setLong(1, image.getId());
					preparedStmt.execute();

				} catch (SQLException ex) {
					Logger.getLogger(ImageDAO.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

			try {
				connection.close();
			} catch (SQLException e) {
			}
		}
		
		// delete list image.
		public void deleteImageByIdProduct(Long idProduct) {
			
			Product product = new ProductDAO().getProductById(idProduct);
			
			Connection connection = DBConnect.getConnection();
			String query = "delete from images where id_product = ?";
			
			PreparedStatement preparedStmt;
			try {
				preparedStmt = connection.prepareStatement(query);
				preparedStmt.setLong(1, idProduct);
				preparedStmt.execute();
				connection.close();

				File file = new File(Constant.UPLOAD_DIRECTORY + product.getThumbnail().getName());     
		        file.delete();
				
		        for (Image image : product.getListImage()) {
		        	file = new File(Constant.UPLOAD_DIRECTORY + image.getName());     
			        file.delete();
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	public static void main(String[] args) {
		new ImageDAO().getImageByProductIdAndType(1, 1).get(0);
	}
}
