package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import connect.DBConnect;
import model.Chinhanh;

public class ChinhanhDAO {

	// add Category.
	public boolean insertChinhanh(Chinhanh c) {
		Connection connection = DBConnect.getConnection();
		String sql = "INSERT INTO chinhanh(name, address, phone) VALUES(?, ?, ?)";
		try {
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setString(1, c.getName());
			ps.setString(2, c.getDiachi());
			ps.setString(3, c.getSodienthoai());
			ps.executeUpdate();
			connection.close();
			return true;
		} catch (SQLException ex) {
		}
		return false;
	}	

	// edit Category by id.
	public boolean editCategory(Chinhanh c) {
		
		System.out.println("ten la  "+ c.getName());
		
		Connection connection = DBConnect.getConnection();
		String sql = "UPDATE chinhanh set name = ?, address = ?, phone = ? WHERE id = ?";
		try {
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setString(1, c.getName());
			ps.setString(2, c.getDiachi());
			ps.setString(3, c.getSodienthoai());
			ps.setLong(4, c.getId());
			ps.executeUpdate();
			connection.close();
			return true;
		} catch (SQLException ex) {
		}
		return false;
	}

	public Chinhanh getChinhanhById(long id) {
		Chinhanh c = new Chinhanh();
		try {
			Connection connection = DBConnect.getConnection();
			String sql = "SELECT * FROM chinhanh WHERE id = ?";
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {

				c.setId(rs.getLong("id"));
				c.setName(rs.getString("name"));
				c.setDiachi(rs.getString("address"));
				c.setSodienthoai(rs.getString("phone"));
			}
			connection.close();
			return c;
		} catch (SQLException ex) {
		}
		return c;
	}

	public ArrayList<Chinhanh> getAll() {

		ArrayList<Chinhanh> allCategory = new ArrayList<>();

		try {
			Connection connection = DBConnect.getConnection();
			String sql = "SELECT * FROM chinhanh";
			PreparedStatement ps = connection.prepareCall(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Chinhanh c = new Chinhanh();
				c.setId(rs.getLong("id"));
				c.setName(rs.getString("name"));
				c.setDiachi(rs.getString("address"));
				c.setSodienthoai(rs.getString("phone"));
				allCategory.add(c);
			}
			connection.close();			
			return allCategory;
		} catch (SQLException ex) {
		}
		return allCategory;
	}
}
