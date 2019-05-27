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

import org.apache.catalina.connector.Request;

import connect.DBConnect;
import constant.Constant;
import model.Bill;
import model.BillDetail;

/**
 *
 * @author NVB
 */
public class BillDAO implements BillInterface {

	// add bill.
	public boolean insertBill(Bill b) {
		Connection connection = DBConnect.getConnection();
		String sql = "INSERT INTO bills(customer, address, phone, sumMoney, status, create_by, create_at, idchinhanh) VALUES(?,?,?,?,?,?,?,?)";

		Long id = (long) 0;

		try {
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setString(1, b.getCustomer());
			ps.setString(2, b.getAddress());
			ps.setString(3, b.getPhone());
			ps.setDouble(4, b.getSumMoney());
			ps.setLong(5, b.getStatus());
			ps.setLong(6, b.getCreateBy());
			ps.setTimestamp(7, b.getCreateAt());
			ps.setLong(8, b.getIdchinhanh());
			ps.executeUpdate();
			connection.close();
			
			id = this.getLastId();

			for (BillDetail billDetail : b.getListBillDetail()) {
				billDetail.setBill(new Bill(id));
				new BillDetailDAO().insertBillDetail(billDetail);
			}

			return true;
		} catch (SQLException ex) {
			Logger.getLogger(BillDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	// edit Bill.
	public boolean editBill(Bill b) {
		Connection connection = DBConnect.getConnection();
		String sql = "UPDATE bills set status = ? WHERE id = ?";
		try {
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setLong(1, b.getStatus());
			ps.setLong(2, b.getId());
			ps.executeUpdate();
			connection.close();
			return true;
		} catch (SQLException ex) {
			Logger.getLogger(BillDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	public Bill getBillById(long id) {
		try {
			Connection connection = DBConnect.getConnection();
			String sql = "SELECT * FROM bills WHERE id = ?";
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			Bill b = new Bill();

			while (rs.next()) {

				b.setId(rs.getLong("id"));
				b.setAddress(rs.getString("address"));
				b.setCustomer(rs.getString("customer"));
				b.setPhone(rs.getString("phone"));
				b.setSumMoney(rs.getDouble("sumMoney"));
				b.setStatus(rs.getLong("status"));
				b.setCreateAt(rs.getTimestamp("create_at"));
				b.setIdchinhanh(rs.getLong("idchinhanh"));

			}

			ArrayList<BillDetail> listBillDetail = new BillDetailDAO().getBillDetailByIdBill(id);
			b.setListBillDetail(listBillDetail);

			connection.close();

			return b;
		} catch (SQLException ex) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public long getLastId() {
		long id = 0;
			Connection connection = DBConnect.getConnection();
			String sql = "SELECT MAX(id) AS lastid FROM bills";
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
	
	
	public ArrayList<Bill> getAll(int page) {

		ArrayList<Bill> listBill = new ArrayList<>();

		try {
			Connection connection = DBConnect.getConnection();
			String sql = "SELECT * FROM bills ORDER BY status ASC OFFSET " + page * Constant.SOPHANTUMOTTRANG + " ROWS FETCH NEXT "+Constant.SOPHANTUMOTTRANG+" ROWS ONLY";
			PreparedStatement ps = connection.prepareCall(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Bill b = new Bill();
				b.setId(rs.getLong("id"));
				b.setAddress(rs.getString("address"));
				b.setCustomer(rs.getString("customer"));
				b.setPhone(rs.getString("phone"));
				b.setSumMoney(rs.getDouble("sumMoney"));
				b.setStatus(rs.getLong("status"));
				b.setCreateAt(rs.getTimestamp("create_at"));
				b.setIdchinhanh(rs.getLong("idchinhanh"));
				listBill.add(b);
			}
			
			connection.close();
			int i = 0;
	
			for (Bill bill : listBill) {
				long idtemp = 0;
				ArrayList<BillDetail> listBillDetail = new BillDetailDAO().getBillDetailByIdBill(idtemp);
				bill.setListBillDetail(listBillDetail);
				listBill.set(i, bill);
				i++;
			}

			return listBill;
		} catch (SQLException ex) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return listBill;
	}

	public ArrayList<Bill> getWhere(String name, String address, String phone, String sumFrom, String sumTo, String createFrom, String createTo, int page) {
		ArrayList<Bill> listBill = new ArrayList<>();

		try {
			Connection connection = DBConnect.getConnection();
			String sql = "SELECT * FROM bills Where 1 = 1 ";
			
			if(name != "") {
				sql += " AND customer like '%" + name + "%'";
			}
			
			if(address != "") {
				sql += " AND address like '%" + address + "%'";
			}
			
			if(phone != "") {
				sql += " AND phone like '%" + phone + "%'";
			}
			
			if(sumFrom != "" && sumTo != "") {
				sql += " AND sumMoney BETWEEN "+ sumFrom +" AND " + sumTo +" ";
			}
			
			if(createFrom != "" && createTo != "") {
				sql += " AND create_at BETWEEN '"+ createFrom +"' AND '" + createTo +"' ";
			}


			sql += " ORDER BY status ASC OFFSET " + page * Constant.SOPHANTUMOTTRANG + " ROWS FETCH NEXT "+Constant.SOPHANTUMOTTRANG+" ROWS ONLY";
			
			PreparedStatement ps = connection.prepareCall(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Bill b = new Bill();
				b.setId(rs.getLong("id"));
				b.setAddress(rs.getString("address"));
				b.setCustomer(rs.getString("customer"));
				b.setPhone(rs.getString("phone"));
				b.setSumMoney(rs.getDouble("sumMoney"));
				b.setStatus(rs.getLong("status"));
				b.setCreateAt(rs.getTimestamp("create_at"));
				b.setIdchinhanh(rs.getLong("idchinhanh"));
				listBill.add(b);

			}
			
			System.out.println(listBill.size());

			connection.close();

			return listBill;
		} catch (SQLException ex) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return listBill;
	}

	@Override
	public ArrayList<Bill> geBySQL(String sql) {
		ArrayList<Bill> listBill = new ArrayList<>();

		try {
			Connection connection = DBConnect.getConnection();
			PreparedStatement ps = connection.prepareCall(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Bill b = new Bill();
				b.setId(rs.getLong("id"));
				b.setAddress(rs.getString("address"));
				b.setCustomer(rs.getString("customer"));
				b.setPhone(rs.getString("phone"));
				b.setSumMoney(rs.getDouble("sumMoney"));
				b.setStatus(rs.getLong("status"));
				b.setCreateAt(rs.getTimestamp("create_at"));
				b.setIdchinhanh(rs.getLong("idchinhanh"));
				listBill.add(b);
			}
			
			connection.close();
			int i = 0;
	
			for (Bill bill : listBill) {
				long idtemp = 0;
				ArrayList<BillDetail> listBillDetail = new BillDetailDAO().getBillDetailByIdBill(idtemp);
				bill.setListBillDetail(listBillDetail);
				listBill.set(i, bill);
				i++;
			}

			return listBill;
		} catch (SQLException ex) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return listBill;
	}

	@Override
	public long countByStatus(long status) {
		long soluong = 0;
		try {
			Connection connection = DBConnect.getConnection();
			String sql = "SELECT COUNT(*) as soluong FROM bills WHERE status = ?";
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setLong(1, status);
			ResultSet rs = ps.executeQuery();
			
			
			
			while (rs.next()) {
				soluong = rs.getLong("soluong");
			}

			connection.close();

			return soluong;
		} catch (SQLException ex) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return soluong;
	}
	

}
