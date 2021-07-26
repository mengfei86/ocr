package com.demo.utils.ocr;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.jfinal.kit.PropKit;

public class PictureManage {
	private org.opencv.core.Mat image;

	// private JLabel jLabelImage;
	public PictureManage(String fileName) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		this.image = Highgui.imread(fileName);
	}

	/**
	 * 图片画质处理
	 * 
	 * @param image
	 * @return
	 */
	public static File setMatImage(String filen, int a) {
		Mat image = Highgui.imread(filen);
		Mat loadeMatImage = new Mat();
		// 灰度处理
		Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2GRAY);
		// 二值化处理
		Mat binaryMat = new Mat(image.height(), image.width(), CvType.CV_8UC1);
		Imgproc.threshold(image, binaryMat, a, 300, Imgproc.THRESH_BINARY);

		// 图像腐蚀
		Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(500, 500));
		Imgproc.erode(binaryMat, image, element);
		// loadeMatImage = image;
		loadeMatImage = binaryMat;
		Image img = toBufferedImage(loadeMatImage);
		int index = filen.lastIndexOf("\\");
		String suffix = index != -1 ? filen.substring(index + 1) : ".png";
		String destFile = PropKit.get("imgurl") + "do/do_" + suffix;
		saveImage(img, filen);
		return new File(filen);
	}

	/**
	 * Mat转image
	 * 
	 * @param matrix
	 * @return
	 */
	private static Image toBufferedImage(Mat matrix) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (matrix.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
		byte[] buffer = new byte[bufferSize];
		matrix.get(0, 0, buffer);
		BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
		final byte[] targetPxiels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(buffer, 0, targetPxiels, 0, buffer.length);
		return image;
	}

	/***
	 * 将Image变量保存成图片
	 * 
	 * @param im
	 * @param fileName
	 */
	public static void saveImage(Image im, String fileName) {
		int w = im.getWidth(null);
		int h = im.getHeight(null);
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = bi.getGraphics();
		g.drawImage(im, 0, 0, null);
		try {
			ImageIO.write(bi, "jpg", new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 图片处理
	 * 
	 * @param args
	 */
	public void imshow() {

		// // 添加原图
		// Image originalImage = toBufferedImage(image);
		// saveImage(originalImage, "yuantu.jpg");
		// // jLabelImage.setIcon(new ImageIcon(originalImage));
		// // 添加处理图
		// Mat mat1 = setMatImage(image, 30);
		// Image newImage = toBufferedImage(mat1);
		// saveImage(newImage, "xintu.jpg");
	}

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat image = Highgui.imread("e:\\data\\sfz\\sfz2.jpg");
		Mat loadeMatImage = new Mat();
		// 灰度处理
		Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2GRAY);
		// 二值化处理
		Mat binaryMat = new Mat(image.height(), image.width(), CvType.CV_8UC1);
		Imgproc.threshold(image, binaryMat, 35, 300, Imgproc.THRESH_BINARY);

		// 图像腐蚀
		Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(500, 500));
		Imgproc.erode(binaryMat, image, element);
		// loadeMatImage = image;
		loadeMatImage = binaryMat;

		// for (int y = 0; y < loadeMatImage.height(); y++) {
		// for (int x = 0; x < loadeMatImage.width(); x++) {
		// // 得到该行像素点的值
		// double[] data = loadeMatImage.get(y, x);
		// for (int i1 = 0; i1 < data.length; i1++) {
		// data[i1] = 255;// 像素点都改为白色
		// }
		// loadeMatImage.put(x, y, data);
		// }
		// }
		Image newImage = toBufferedImage(loadeMatImage);
		saveImage(newImage, "xintu11.jpg");
	}
}