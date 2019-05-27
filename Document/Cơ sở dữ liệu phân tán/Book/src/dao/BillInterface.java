package dao;

import java.util.ArrayList;

import model.Bill;

public interface BillInterface {
	public boolean insertBill(Bill b);
	public boolean editBill(Bill b);
	public ArrayList<Bill> getAll(int page);
	public Bill getBillById(long id);
	public ArrayList<Bill> getWhere(String name, String address, String phone, String sumFrom, String sumTo, String createFrom, String createTo, int page);
	public ArrayList<Bill> geBySQL(String sql);
	public long countByStatus(long type);
	
	
}
