package dao;

import java.util.ArrayList;

import model.Image;

public interface ImageInterface {
	public void deleteById(Long id);
	public void deleteImageByIdProduct(Long idProduct);
	public void deleteListImage(ArrayList<Image> listImage);
	public boolean editImage(Image i);
	public ArrayList<Image> getAll();
	public Image getImageById(long id);
	public ArrayList<Image> getImageByProductIdAndType(long idProduct, long type);
	public boolean insertImage(Image i);
	public boolean insertListImage(ArrayList<Image> listImg);
}
