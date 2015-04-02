package org.csi.yucca.storage.datamanagementapi.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

public class ImageProcessor {



	public void doProcessOdata(String imageBase64,String path,String fileName) throws IOException{
		// TODO Auto-generated method stub
		//		String appPath = getServletContext().getRealPath("");
		// constructs path of the directory to save uploaded file
		String savePath = path;

		// creates the save directory if it does not exists
		File fileSaveDir = new File(savePath);
		if (!fileSaveDir.exists()) {
			fileSaveDir.mkdir();
		}

		String imageBase64Clean =null;

		if(imageBase64!=null){		
			String[] imageBase64Array = imageBase64.split(",");

			if(imageBase64Array.length>1){
				imageBase64Clean= imageBase64Array[1];
			}else{
				imageBase64Clean= imageBase64Array[0];
			}
		}
		convertImage(savePath, fileName, imageBase64Clean);

		mergeImages(savePath, fileName);
		System.out.println("Done");
	}

	public void doProcessStream(String imageBase64,String path,String fileName) throws IOException{
		//		String appPath = getServletContext().getRealPath("");
		// constructs path of the directory to save uploaded file
		String savePath = path;

		// creates the save directory if it does not exists
		File fileSaveDir = new File(savePath);
		if (!fileSaveDir.exists()) {
			fileSaveDir.mkdir();
		}

		String imageBase64Clean =null;
		if(imageBase64!=null){
			String[] imageBase64Array = imageBase64.split(",");

			if(imageBase64Array.length>1){
				imageBase64Clean= imageBase64Array[1];
			}else{
				imageBase64Clean= imageBase64Array[0];
			}
		}
		convertImage(savePath, fileName, imageBase64Clean);
		System.out.println("Done");
		//		mergeImages(savePath, imageFileName);

	}

	private void convertImage(String savePath, String imageFileName, String imageBase64) throws IOException {
		BufferedImage imag=null;

		if(imageBase64!=null){
			byte[] bytearray = Base64.decodeBase64(imageBase64.getBytes());
			imag = ImageIO.read(new ByteArrayInputStream(bytearray));
		}
		if(imageBase64==null || imag==null){
			imag = ImageIO.read(ImageProcessor.class.getClassLoader().getResourceAsStream(Constants.DEFAULT_IMAGE));
		}
		
		BufferedImage combined = new BufferedImage(Constants.DEFAULT_IMAGE_WIDTH, Constants.DEFAULT_IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Image imageScaled = imag.getScaledInstance(Constants.DEFAULT_IMAGE_WIDTH, Constants.DEFAULT_IMAGE_HEIGHT, Image.SCALE_DEFAULT);
		// paint both images, preserving the alpha channels
		Graphics g = combined.getGraphics();
		g.drawImage(imageScaled, 0, 0, null);

		ImageIO.write(combined, "png", new File(savePath, imageFileName));
	}

	private boolean mergeImages(String savePath, String baseImageName) throws IOException {
		File path = new File(savePath);

		// load source images
		BufferedImage image = ImageIO.read(new File(path, baseImageName));
		BufferedImage overlay = ImageIO.read(ImageProcessor.class.getClassLoader().getResourceAsStream(Constants.DEFAULT_ODATA_IMAGE));

		// create the new image, canvas size is the max. of both image sizes
		int w = Math.min(image.getWidth(), overlay.getWidth());
		int h = Math.min(image.getHeight(), overlay.getHeight());

		Image imageScaled = image.getScaledInstance(w, h, Image.SCALE_DEFAULT);
		Image overlayScaled = overlay.getScaledInstance(w, h, Image.SCALE_DEFAULT);

		BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		// paint both images, preserving the alpha channels
		Graphics g = combined.getGraphics();
		g.drawImage(imageScaled, 0, 0, null);
		g.drawImage(overlayScaled, 0, 0, null);

		// Save as new image
		return ImageIO.write(combined, "png", new File(path, baseImageName));
	}
}
