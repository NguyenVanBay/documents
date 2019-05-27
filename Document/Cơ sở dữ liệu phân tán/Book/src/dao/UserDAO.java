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
import model.User;

/**
 *
 * @author NVB
 */
public class UserDAO implements UserInterface {

	// check email exists. co tra ve true.
	public boolean checkEmail(String email) {
		Connection connection = DBConnect.getConnection();
		String sql = "SELECT email FROM users WHERE email = ?";
		try {
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				connection.close();
				return true;
			}
		} catch (SQLException ex) {
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	// add User.
	public boolean insertUser(User u) {
		Connection connection = DBConnect.getConnection();
		String sql = "INSERT INTO users(name, email, password, address, phone, role, create_by, idchinhanh) VALUES(?,?,?,?,?,?,?,?)";

		// là người dùng. k set người tạo tài khoản.
		if (u.getRole() == User.NGUOIDUNG) {
			sql = "INSERT INTO users(name, email, password, address, phone, role, idchinhanh) VALUES(?,?,?,?,?,?,?)";
		}

		try {
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setString(1, u.getName());
			ps.setString(2, u.getEmail());
			ps.setString(3, u.getPassword());
			ps.setString(4, u.getAddress());
			ps.setString(5, u.getPhone());
			ps.setLong(6, u.getRole());

			// ko phải người dùng set người tạo tài khoản.
			if (u.getRole() != User.NGUOIDUNG) {
				ps.setLong(7, u.getCreateBy());
				ps.setLong(8, u.getIdchinhanh());
			} else {
				ps.setLong(7, new ChinhanhDAO().getAll().get(0).getId());
			}
			
			ps.executeUpdate();
			connection.close();
			return true;

		} catch (SQLException ex) {
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	// edit User by id.
	public boolean editUser(User u) {
		Connection connection = DBConnect.getConnection();
		String sql = "UPDATE users set name = ?, email = ?, address= ? , phone= ? , role= ? , password = ? WHERE id = ?";
		try {
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setString(1, u.getName());
			ps.setString(2, u.getEmail());
			ps.setString(3, u.getAddress());
			ps.setString(4, u.getPhone());
			ps.setLong(5, u.getRole());
			ps.setString(6, u.getPassword());
			ps.setLong(7, u.getId());
			ps.executeUpdate();
			connection.close();
			return true;
		} catch (SQLException ex) {
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	// check login user.
	public User loginUser(String email, String password) {
		Connection con = DBConnect.getConnection();
		String sql = "select * from users where email=? and password=? and role= '0'";
		PreparedStatement ps;
		try {
			ps = (PreparedStatement) con.prepareStatement(sql);
			ps.setString(1, email);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				User u = new User();
				u.setId(rs.getLong("id"));
				u.setName(rs.getString("name"));
				u.setPassword(rs.getString("password"));
				u.setAddress(rs.getString("address"));
				u.setPhone(rs.getString("phone"));
				u.setEmail(rs.getString("email"));
				u.setRole(rs.getLong("role"));

				con.close();
				return u;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// check login.
	public User loginAdmin(String email, String password) {
		Connection con = DBConnect.getConnection();
		String sql = "select * from users where email=? and password=? and role!= '0'";
		PreparedStatement ps;
		try {
			ps = (PreparedStatement) con.prepareStatement(sql);
			ps.setString(1, email);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				User u = new User();
				u.setId(rs.getLong("id"));
				u.setName(rs.getString("name"));
				u.setPassword(rs.getString("password"));
				u.setAddress(rs.getString("address"));
				u.setPhone(rs.getString("phone"));
				u.setEmail(rs.getString("email"));
				u.setRole(rs.getLong("role"));
				u.setCreateBy(rs.getLong("create_by"));

				con.close();
				return u;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// check xem nhân viên đã tạo ra thêm nhân viên khác chưa.
	// nếu đã tạo return 1. còn không return 0;
	public int checkCreatedUser(Long id) {
		Connection con = DBConnect.getConnection();
		String sql = "select * from users where create_by = ? AND role!= '0'";
		PreparedStatement ps;
		try {
			ps = (PreparedStatement) con.prepareStatement(sql);
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				con.close();
				return 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public User getUserById(long userID) {
		User u = new User();
		try {
			Connection connection = DBConnect.getConnection();
			String sql = "SELECT * FROM users WHERE id = ?";
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setLong(1, userID);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				u.setId(rs.getLong("id"));
				u.setName(rs.getString("name"));
				u.setPassword(rs.getString("password"));
				u.setAddress(rs.getString("address"));
				u.setPhone(rs.getString("phone"));
				u.setEmail(rs.getString("email"));
				u.setRole(rs.getLong("role"));
				u.setCreateBy(rs.getLong("create_by"));
			}
			connection.close();
			return u;
		} catch (SQLException ex) {
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return u;
	}

	public ArrayList<User> getAllCustomer(int page) {

		ArrayList<User> allUser = new ArrayList<>();

		try {
			Connection connection = DBConnect.getConnection();
			String sql = "SELECT * FROM users where role = 0 ORDER BY id OFFSET " + page * Constant.SOPHANTUMOTTRANG + " ROWS FETCH NEXT "+Constant.SOPHANTUMOTTRANG+" ROWS ONLY";
			PreparedStatement ps = connection.prepareCall(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				User u = new User();
				u.setId(rs.getLong("id"));
				u.setName(rs.getString("name"));
				u.setPassword(rs.getString("password"));
				u.setAddress(rs.getString("address"));
				u.setPhone(rs.getString("phone"));
				u.setEmail(rs.getString("email"));
				u.setRole(rs.getLong("role"));
				u.setCreateBy(rs.getLong("create_by"));
				u.setIdchinhanh(rs.getLong("idchinhanh"));
				allUser.add(u);
			}
			connection.close();
			return allUser;
		} catch (SQLException ex) {
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return allUser;
	}

	public ArrayList<User> getAllUser(int page) {

		ArrayList<User> allUser = new ArrayList<>();

		try {
			Connection connection = DBConnect.getConnection();
			String sql = "SELECT * FROM users where role != '0' ORDER BY id OFFSET " + page * Constant.SOPHANTUMOTTRANG + " ROWS FETCH NEXT "+Constant.SOPHANTUMOTTRANG+" ROWS ONLY";
			PreparedStatement ps = connection.prepareCall(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				User u = new User();
				u.setId(rs.getLong("id"));
				u.setName(rs.getString("name"));
				u.setPassword(rs.getString("password"));
				u.setAddress(rs.getString("address"));
				u.setPhone(rs.getString("phone"));
				u.setEmail(rs.getString("email"));
				u.setRole(rs.getLong("role"));
				u.setCreateBy(rs.getLong("create_by"));
				u.setIdchinhanh(rs.getLong("idchinhanh"));
				allUser.add(u);
			}
			connection.close();
			return allUser;
		} catch (SQLException ex) {
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return allUser;
	}

	public void deleteById(Long id) throws SQLException {
			Connection connection = DBConnect.getConnection();
			String query = "delete from users where id = ?";
			PreparedStatement preparedStmt = connection.prepareStatement(query);
			preparedStmt.setLong(1, id);
			preparedStmt.execute();

			connection.close();

	}

	public ArrayList<User> getWhereUser(String name, String email, String phone, String role, int page) {

		String sql = "SELECT * FROM users where 1 = 1 ";

		if (name != "") {
			
			sql += " AND name like '%" + name + "%'";
		}

		if (email != "") {
			sql += " AND email like '%" + email + "%'";
		}

		if (phone != "") {
			sql += " AND phone = " + phone;
		}

		if (!role.equals("0")) {

			sql += " AND role = " + role;
		} else {
			sql += " AND role != 0 ";
		}
		
		sql += " ORDER BY id OFFSET " + page * Constant.SOPHANTUMOTTRANG + " ROWS FETCH NEXT "+Constant.SOPHANTUMOTTRANG+" ROWS ONLY";

		System.out.println(sql);

		ArrayList<User> allUser = new ArrayList<>();

		try {
			Connection connection = DBConnect.getConnection();

			PreparedStatement ps = connection.prepareCall(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				User u = new User();
				u.setId(rs.getLong("id"));
				u.setName(rs.getString("name"));
				u.setPassword(rs.getString("password"));
				u.setAddress(rs.getString("address"));
				u.setPhone(rs.getString("phone"));
				u.setEmail(rs.getString("email"));
				u.setRole(rs.getLong("role"));
				u.setCreateBy(rs.getLong("create_by"));
				u.setIdchinhanh(rs.getLong("idchinhanh"));
				allUser.add(u);
			}
			connection.close();
			return allUser;
		} catch (SQLException ex) {
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return allUser;
	}
	
	public ArrayList<User> getWhereCustomer(String name, String email, String phone, int page) {

		String sql = "SELECT * FROM users where 1 = 1 ";

		if (name != "") {
			sql += " AND name like '%" + name + "%'";
		}

		if (email != "") {
			sql += " AND email like '%" + email + "%'";
		}

		if (phone != "") {
			sql += " AND phone = " + phone;
		}

		sql += " AND role = 0";
		
		sql += " ORDER BY id OFFSET " + page * Constant.SOPHANTUMOTTRANG + " ROWS FETCH NEXT "+Constant.SOPHANTUMOTTRANG+" ROWS ONLY";

		System.out.println(sql);

		ArrayList<User> allUser = new ArrayList<>();

		try {
			Connection connection = DBConnect.getConnection();

			PreparedStatement ps = connection.prepareCall(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				User u = new User();
				u.setId(rs.getLong("id"));
				u.setName(rs.getString("name"));
				u.setPassword(rs.getString("password"));
				u.setAddress(rs.getString("address"));
				u.setPhone(rs.getString("phone"));
				u.setEmail(rs.getString("email"));
				u.setRole(rs.getLong("role"));
				u.setCreateBy(rs.getLong("create_by"));
				u.setIdchinhanh(rs.getLong("idchinhanh"));
				allUser.add(u);
			}
			connection.close();
			return allUser;
		} catch (SQLException ex) {
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return allUser;
	}

	public static void main(String[] args) {
		if (new UserDAO().checkEmail("nguyenvanbay@gmail.com")) {
			System.err.println("co");
		}
	}

}
