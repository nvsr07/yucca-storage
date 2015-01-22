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
	
	public static void main(String[] arg){
		
		
		ImageProcessor processor = new ImageProcessor();
		String imageBase64 ="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";
		String path ="D:/";
		
		try {
			processor.doProcessOdata(imageBase64, path,"baseImg.png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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

		String image = imageBase64;

		convertImage(savePath, fileName, image);
		
		mergeImages(savePath, fileName);
		System.out.println("Done");
		
	}
	
	public void doProcessStream(String imageBase64,String path,String fileName) throws IOException{
		// TODO Auto-generated method stub
//		String appPath = getServletContext().getRealPath("");
		// constructs path of the directory to save uploaded file
		String savePath = path;

		// creates the save directory if it does not exists
		File fileSaveDir = new File(savePath);
		if (!fileSaveDir.exists()) {
			fileSaveDir.mkdir();
		}

		String image = imageBase64;

		convertImage(savePath, fileName, image);
		System.out.println("Done");
//		mergeImages(savePath, imageFileName);
		
	}
	
	private void convertImage(String savePath, String imageFileName, String imageBase64) throws IOException {

		byte[] bytearray = Base64.decodeBase64(imageBase64.getBytes());

		BufferedImage imag = ImageIO.read(new ByteArrayInputStream(bytearray));
		ImageIO.write(imag, "png", new File(savePath, imageFileName));
	}

	private boolean mergeImages(String savePath, String baseImageName) throws IOException {
		File path = new File(savePath);

		// load source images
		BufferedImage image = ImageIO.read(new File(path, baseImageName));
		BufferedImage overlay = ImageIO.read(ImageProcessor.class.getClassLoader().getResourceAsStream("conf/icons/odataOverlay.png"));

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
		return ImageIO.write(combined, "PNG", new File(path, baseImageName + "-odata.png"));
	}
	


}
