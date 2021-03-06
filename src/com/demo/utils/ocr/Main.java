package com.demo.utils.ocr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;

public class Main {
	private static final Double avatarSpacePer = Double.valueOf(0.16D);
	private static final Double avatarPer = Double.valueOf(0.28D);
	static {
		try {
			System.out.println("Load Dll!");
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Load DLL!");
		}
	}

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		test_sfz("e:\\data\\sfz\\sfz2.jpg");
	}

	public static void test_sbk(String fileName) {
		int index = fileName.lastIndexOf("\\");
		String suffix = index != -1 ? fileName.substring(index + 1) : ".png";

		int[] rectPosition = Smoother.detect_t(fileName);
		System.out.println("x=" + rectPosition[0] + " y=" + rectPosition[1] + " width=" + rectPosition[2] + " height=" + rectPosition[3]);

		int x = rectPosition[0];
		int y = rectPosition[1];
		int w = rectPosition[2];
		int h = rectPosition[3];
		int[] imgRect = new int[2];
		try {
			imgRect = getImageWidth(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if ((x == 0) || (y == 0) || (w == 0) || (h == 0)) {
			x = imgRect[0];
			y = imgRect[1];
			w = (int) (x * avatarPer.doubleValue());
			h = w;
			System.out.println("??????????????????:" + fileName + " ??????????????????");
		}
		int cutX = x / 2 + 80;
		int cutY = (int) ((x + w / 2) * 2.6D);
		String destFile = "e:/data/ocr/idcard_" + cutY + "_" + suffix;

		int width = imgRect[0] - cutX - 35;
		cut(fileName, "e:/data/ocr/" + x + "_" + y + "_" + w + "_" + h + suffix, x, y, w, h);
		int cutHeight = (int) ((x + w / 2) * 0.5D);

		cut(fileName, destFile, cutX, cutY, width, cutHeight);
		Test.test(destFile);
	}

	public static JSONObject test_sfz(String fileName) {
		int index = fileName.lastIndexOf("\\");
		String suffix = index != -1 ? fileName.substring(index + 1) : ".png";
		int[] rectPosition = detectFace(fileName);
		System.out.println("x=" + rectPosition[0] + " y=" + rectPosition[1] + " width=" + rectPosition[2] + " height=" + rectPosition[3]);

		int x = rectPosition[0];
		int y = rectPosition[1];
		int w = rectPosition[2];
		int h = rectPosition[3];
		int[] imgRect = new int[2];
		try {
			imgRect = getImageWidth(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		boolean isgetface = true;
		if ((x == 0) || (y == 0) || (w == 0) || (h == 0)) {
			x = imgRect[0];
			y = imgRect[1];
			w = (int) (x * avatarPer.doubleValue());
			h = w;
			System.out.println("??????????????????:" + fileName + " ??????????????????");
			isgetface = false;
		}

		int cutX = x / 2;

		String destFile = PropKit.get("imgurl") + "no/no_" + suffix;
		String nameFile = PropKit.get("imgurl") + "name/name_" + suffix;

		int width = imgRect[0] - cutX - 30;

		if (isgetface) {
			cut(fileName, PropKit.get("imgurl") + "tx/tx_" + suffix, x, y, w, h);
		}
		int cutHeight = 140;
		int imgHieght = imgRect[1];
		int topSpace = (int) (imgHieght * avatarPer.doubleValue());
		int height = (int) (imgHieght * avatarSpacePer.doubleValue());
		int cutY = y + h + height;
		if (imgHieght - cutY < cutHeight - 5) {
			cutY = imgHieght - (int) (imgHieght / 3.8D) - 10;
		}
		if (imgHieght < 500) {
			cutY += 20;
		}

		y = imgRect[0];
		x = imgRect[1];
		System.out.println("xxx=" + x + "---yyyy=" + y);
		new Main().cutImage(fileName, destFile, (int) (y * 0.35D), (int) (x * 0.75D), y, (int) (x * 0.25D));

		new Main().cutImage(fileName, nameFile, (int) (y * 0.15D), (int) (x * 0.08D), (int) (y * 0.3D), (int) (x * 0.15D));
		String no = Test.test(destFile);
		String name = Test.test_name(nameFile);
		JSONObject b = new JSONObject();
		b.put("name", name);
		b.put("no", no);
		return b;
	}

	public static void cut(String srcPath, String NsrcPath, int startX, int startY, int width, int height) {
		File file = new File(srcPath);
		try {
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			BufferedImage bufImg = ImageIO.read(file);
			bufImg = bufImg.getSubimage(startX, startY, width, height);
			ImageIO.write(bufImg, "jpg", new File(NsrcPath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ???????????????????????????
	 * 
	 * @param fileName
	 * @param destFile
	 */
	public static void convertBackWhiteImage(String fileName, String destFile) {
		OutputStream output = null;
		String suffix = null;
		String types = Arrays.toString(ImageIO.getReaderFormatNames()).replace("]", ",");
		// ??????????????????
		if (fileName.indexOf(".") > -1) {
			suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
		} // ??????????????????????????????????????????????????????????????????
		if (suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase() + ",") < 0) {
			return;
		}
		try {
			BufferedImage sourceImg = replaceWithWhiteColor(ImageIO.read(new FileInputStream(fileName)));
			output = new FileOutputStream(destFile);
			ImageIO.write(sourceImg, suffix, output);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (output != null)
				try {
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param bi
	 * @return
	 */
	public static BufferedImage replaceWithWhiteColor(BufferedImage bi) {

		int[] rgb = new int[3];

		int width = bi.getWidth();

		int height = bi.getHeight();

		int minx = bi.getMinX();

		int miny = bi.getMinY();

		/**
		 * 
		 * ???????????????????????????????????????????????????????????????????????????????????????????????????????????? ????????????????????????????????????????????????
		 * 
		 */

		int hitCount = 0;

		for (int i = minx; i < width - 1; i++) {

			for (int j = miny; j < height; j++) {

				/**
				 * 
				 * ?????????????????????i,j)??????RGB??????
				 * 
				 */

				int pixel = bi.getRGB(i, j);

				int pixelNext = bi.getRGB(i + 1, j);

				/**
				 * 
				 * ??????????????????????????? r g b?????????
				 * 
				 */

				rgb[0] = (pixel & 0xff0000) >> 16;

				rgb[1] = (pixel & 0xff00) >> 8;

				rgb[2] = (pixel & 0xff);

				/**
				 * 
				 * ???????????????????????????????????????????????????????????????????????????rgb??????????????????????????????
				 * 
				 */

				// ?????????????????????RGB?????????????????????15?????????????????????????????????

				// ????????????????????????????????????73???78?????????????????????100?????????RGB??????????????????????????????????????????????????????

				if ((Math.abs(rgb[0] - rgb[1]) < 15)

						&& (Math.abs(rgb[0] - rgb[2]) < 15)

						&& (Math.abs(rgb[1] - rgb[2]) < 15) &&

						(((rgb[0] > 73) && (rgb[0] < 78)) || (rgb[0] > 100))) {

					// ??????????????????,0xffffff?????????

					bi.setRGB(i, j, 0xffffff);

				}

			}

		}

		return bi;

	}

	public static int[] getImageWidth(String fileName) throws FileNotFoundException, IOException {
		File picture = new File(fileName);
		BufferedImage sourceImg = ImageIO.read(new FileInputStream(picture));
		int[] imgRect = new int[2];
		imgRect[0] = sourceImg.getWidth();
		imgRect[1] = sourceImg.getHeight();
		return imgRect;
	}

	public static int[] detectFace(String fileName) {
		int[] rectPosition = new int[4];
		CascadeClassifier faceDetector = new CascadeClassifier("e:\\lbpcascade_frontalface.xml");
		Mat image = Highgui.imread(fileName);
		MatOfRect faceDetections = new MatOfRect();
		Size minSize = new Size(120, 120);

		Size maxSize = new Size(250, 250);
		faceDetector.detectMultiScale(image, faceDetections, 1.1f, 4, 0, minSize, maxSize);

		System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));
		for (Rect rect : faceDetections.toArray()) {
			System.out.println(rect.toString());
			rectPosition[0] = rect.x;
			rectPosition[1] = rect.y;
			rectPosition[2] = rect.width;
			rectPosition[3] = rect.height;
		}

		/*
		 * String filename = "d:/data/avatar.jpg";
		 * System.out.println(String.format("Writing %s", filename));
		 * Imgcodecs.imwrite(filename, image);
		 */
		return rectPosition;
	}

	/**
	 * <p>
	 * Title: cutImage
	 * </p>
	 * <p>
	 * Description: ?????????????????????size??????????????????
	 * </p>
	 * 
	 * @param srcImg
	 *            ?????????
	 * @param output
	 *            ???????????????
	 * @param rect
	 *            ????????????????????????????????????
	 */
	public void cutImage(File srcImg, File destImg, java.awt.Rectangle rect) {
		if (srcImg.exists()) {
			java.io.FileInputStream fis = null;
			ImageInputStream iis = null;
			OutputStream output = null;
			try {
				fis = new FileInputStream(srcImg);
				// ImageIO ????????????????????? : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG,
				// JPEG, WBMP, GIF, gif]
				String types = Arrays.toString(ImageIO.getReaderFormatNames()).replace("]", ",");
				String suffix = null;
				// ??????????????????
				if (srcImg.getName().indexOf(".") > -1) {
					suffix = srcImg.getName().substring(srcImg.getName().lastIndexOf(".") + 1);
				} // ??????????????????????????????????????????????????????????????????
				if (suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase() + ",") < 0) {
					return;
				}
				// ???FileInputStream ?????????ImageInputStream
				iis = ImageIO.createImageInputStream(fis);
				// ???????????????????????????????????????ImageReader
				ImageReader reader = ImageIO.getImageReaders(new FileImageInputStream(srcImg)).next(); // ImageIO.getImageReadersBySuffix(suffix).next();
				reader.setInput(iis, true);
				ImageReadParam param = reader.getDefaultReadParam();
				param.setSourceRegion(rect);
				BufferedImage bi = reader.read(0, param);
				output = new FileOutputStream(destImg);
				ImageIO.write(bi, suffix, output);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fis != null)
						fis.close();
					if (iis != null)
						iis.close();
					if (output != null)
						output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
		}
	}

	public void cutImage(String srcImg, String destImg, int x, int y, int width, int height) {
		cutImage(new File(srcImg), new File(destImg), new java.awt.Rectangle(x, y, width, height));
	}
}