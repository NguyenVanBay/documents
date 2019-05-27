package dao;

import java.util.ArrayList;

import model.BillDetail;

public interface BillDetailInterface {
	public BillDetail getBillDetailById(long id);
	public ArrayList<BillDetail> getBillDetailByIdBill(long id);
	public boolean insertBillDetail(BillDetail b);
}
