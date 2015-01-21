package org.csi.yucca.storage.datamanagementapi.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class ImageProcessor {

	public void doProcess(String imageBase64,String path) throws IOException{
		// TODO Auto-generated method stub
//		String appPath = getServletContext().getRealPath("");
		// constructs path of the directory to save uploaded file
		String savePath = path;

		// creates the save directory if it does not exists
		File fileSaveDir = new File(savePath);
		if (!fileSaveDir.exists()) {
			fileSaveDir.mkdir();
		}

		String imageFileName = "baseImage.png";
		String image = imageBase64;

		convertImage(savePath, imageFileName, image);

		mergeImages(savePath, imageFileName);
		
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
		BufferedImage overlay = ImageIO.read(ImageProcessor.class.getClassLoader().getResourceAsStream("odataOverlay.png"));

		// create the new image, canvas size is the max. of both image sizes
		int w = Math.max(image.getWidth(), overlay.getWidth());
		int h = Math.max(image.getHeight(), overlay.getHeight());
		BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		// paint both images, preserving the alpha channels
		Graphics g = combined.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.drawImage(overlay, 0, 0, null);

		// Save as new image
		return ImageIO.write(combined, "PNG", new File(path, baseImageName + "-odata.png"));
	}
	


}
