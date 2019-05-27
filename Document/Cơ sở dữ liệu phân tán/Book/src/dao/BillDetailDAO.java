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
import model.Bill;
import model.BillDetail;
import model.Product;

/**
 *
 * @author NVB
 */
public class BillDetailDAO implements BillDetailInterface {

	// add billdetail
	public boolean insertBillDetail(BillDetail b) {
		Connection connection = DBConnect.getConnection();
		String sql = "INSERT INTO billdetails(id_bill, id_product, count, price) VALUES(?,?,?,?)";
		try {
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setLong(1, b.getBill().getId());
			ps.setLong(2, b.getProduct().getId());
			ps.setLong(3, b.getCount());
			ps.setDouble(4, b.getPrice());

			ps.executeUpdate();
			connection.close();
			return true;
		} catch (SQLException ex) {
			Logger.getLogger(BillDetailDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	// get bill by id.
	public BillDetail getBillDetailById(long id) {
		try {
			Connection connection = DBConnect.getConnection();
			String sql = "SELECT * FROM billdetails WHERE id = ?";
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			BillDetail b = new BillDetail();
			while (rs.next()) {

				b.setId(rs.getLong("id"));
				b.setCount(rs.getLong("count"));
				b.setPrice(rs.getDouble("price"));
				b.setProduct(new Product(rs.getLong("id_product")));
				b.setBill(new Bill(rs.getLong("id_bill")));
			}

			Product p = new ProductDAO().getProductById(b.getProduct().getId());
			b.setProduct(p);

			connection.close();
			return b;
		} catch (SQLException ex) {
			Logger.getLogger(BillDetailDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	// get bill by id.
	public ArrayList<BillDetail> getBillDetailByIdBill(long id) {

		ArrayList<BillDetail> listBillDetail = new ArrayList<>();

		try {
			Connection connection = DBConnect.getConnection();
			String sql = "SELECT * FROM billdetails WHERE id_bill = ?";
			PreparedStatement ps = connection.prepareCall(sql);
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				BillDetail b = new BillDetail();

				b.setId(rs.getLong("id"));
				b.setCount(rs.getLong("count"));
				b.setPrice(rs.getDouble("price"));
				b.setProduct(new Product(rs.getLong("id_product")));
				b.setBill(new Bill(rs.getLong("id_bill")));

				listBillDetail.add(b);

			}
			
			int i = 0;
			for (BillDetail billDetail : listBillDetail) {
				Product p = new ProductDAO().getProductById(billDetail.getProduct().getId());
				billDetail.setProduct(p);
				listBillDetail.set(i, billDetail);
				i++;
			}
			
			connection.close();
			return listBillDetail;
		} catch (SQLException ex) {
			Logger.getLogger(BillDetailDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return listBillDetail;
	}
	

}
