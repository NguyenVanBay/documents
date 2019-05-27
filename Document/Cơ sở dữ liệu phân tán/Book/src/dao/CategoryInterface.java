package dao;

import java.sql.SQLException;
import java.util.ArrayList;

import model.Category;

public interface CategoryInterface {
	public void deleteById(Long id) throws SQLException ;
	public boolean editCategory(Category c);
	public ArrayList<Category> getAll();
	public ArrayList<Category> getAllPage(int page);
	public Category getCategoryById(long id);
	public ArrayList<Category> getWhere(String name,int page);
	public boolean insertCategory(Category c);
}
