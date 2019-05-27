package dao;

import java.util.ArrayList;

import model.Product;

public interface ProductInterface {
	public void deleteById(Long id);
	public boolean editProduct(Product p);
	public ArrayList<Product> getAll();
	public ArrayList<Product> getAllPage(int page);
	public ArrayList<Product> getProductByCategoryId(Long categoryId);
	public Product getProductById(long id);
	public ArrayList<Product> getWhere(String name, String author, String title, String priceFrom, String priceTo,
			String idCategory, String publicFrom, String publicTo, int page);
	public ArrayList<Product> getWhereInClient(String name, String author, String priceFrom, String priceTo,
			String publicFrom, String publicTo);
	public boolean insertProduct(Product p);
	public boolean updateCount(Long idProduct, Long count);
}
