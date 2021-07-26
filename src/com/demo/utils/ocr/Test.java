package com.demo.utils.ocr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jfinal.kit.PropKit;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class Test {

	public static void main(String[] args) {
		// System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// test_name("e:/data/ocr/eeee.jpg");
		String aa = "「 赵继孟";
		System.out.println(aa.indexOf(" "));
		System.out.println(aa.substring(aa.indexOf(" "), aa.length()));

	}

	public static String test(String path) {
		File imageFile = PictureManage.setMatImage(path, 60);
		Tesseract instance = Tesseract.getInstance();
		instance.setDatapath(PropKit.get("octdata"));

		instance.setLanguage("eng");
		try {
			String result = instance.doOCR(imageFile);
			System.out.println(result + "==" + filterUnNumber(result));
			return filterUnNumber(result);
		} catch (TesseractException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String test_name(String path) {
		File imageFile = PictureManage.setMatImage(path, 65);
		Tesseract instance = Tesseract.getInstance();
		instance.setDatapath(PropKit.get("octdata"));
		instance.setLanguage("chi_sim");
		try {
			String result = instance.doOCR(new File(path));
			result = result.replace("\n", "");
			int i = result.indexOf(" ");
			if (i >= 0 && i < 4) {
				result = result.substring(i, result.length());
			}
			return result.trim();
		} catch (TesseractException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String filterUnNumber(String str) {
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	public static void writeToFile(String content, String url) {

		try {
			File file = new File(url);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
			fw.close();
			System.out.println("test1 done!");

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private final String LANG_OPTION = "-l";
	private final String EOL = System.getProperty("line.separator");
	/**
	 * 文件位置我防止在，项目同一路径
	 */
	private String tessPath = new File("tesseract").getAbsolutePath();

	/**
	 * @param imageFile
	 *            传入的图像文件
	 * @param imageFormat
	 *            传入的图像格式
	 * @return 识别后的字符串
	 */
	public String recognizeText(File imageFile) throws Exception {
		/**
		 * 设置输出文件的保存的文件目录
		 */
		File outputFile = new File(imageFile.getParentFile(), "output");

		StringBuffer strB = new StringBuffer();
		List<String> cmd = new ArrayList<String>();

		String os = System.getProperty("os.name");
		if (os.toLowerCase().startsWith("win")) {
			cmd.add("tesseract");
		} else {
			cmd.add("tesseract");
		}
		// cmd.add(tessPath + "\\tesseract");
		cmd.add(imageFile.getName());
		cmd.add(outputFile.getName());
		// cmd.add(LANG_OPTION);
		// cmd.add("chi_sim");
		cmd.add("digits");
		// cmd.add("eng");
		// cmd.add("-psm 7");
		ProcessBuilder pb = new ProcessBuilder();

		/**
		 * Sets this process builder's working directory.
		 */
		pb.directory(imageFile.getParentFile());
		// cmd.set(1, imageFile.getName());
		pb.command(cmd);
		pb.redirectErrorStream(true);
		Process process = pb.start();

		// Process process = pb.command("ipconfig").start();
		// System.out.println(System.getenv().get("Path"));
		// Process process = pb.command("D:\\Program Files
		// (x86)\\Tesseract-OCR\\tesseract.exe",imageFile.getName(),outputFile.getName(),LANG_OPTION,"eng").start();

		// tesseract.exe 1.jpg 1 -l chi_sim
		// Runtime.getRuntime().exec("tesseract.exe 1.jpg 1 -l chi_sim");
		/**
		 * the exit value of the process. By convention, 0 indicates normal
		 * termination.
		 */
		// System.out.println(cmd.toString());
		int w = process.waitFor();
		if (w == 0)// 0代表正常退出
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile.getAbsolutePath() + ".txt"), "UTF-8"));
			String str;

			while ((str = in.readLine()) != null) {
				strB.append(str).append(EOL);
			}
			in.close();
		} else {
			String msg;
			switch (w) {
			case 1:
				msg = "Errors accessing files. There may be spaces in your image's filename.";
				break;
			case 29:
				msg = "Cannot recognize the image or its selected region.";
				break;
			case 31:
				msg = "Unsupported image format.";
				break;
			default:
				msg = "Errors occurred.";
			}
			throw new RuntimeException(msg);
		}
		new File(outputFile.getAbsolutePath() + ".txt").delete();
		return strB.toString().replaceAll("\\s*", "");
	}

	/*
	 * public static void main(String[] args) throws Exception {
	 * Tesseract("d:\\2.png"); Tesseract("/2.png"); Tesseract("/3.png");
	 * Tesseract("/4.png"); Tesseract("/5.png"); Tesseract("/6.png"); }
	 */
	private static void Tesseract(String fileString) throws Exception {
		// String filePath =
		// Test.class.getResource(fileString).getFile().toString();
		// processImg(filePath);
		File file = new File(fileString);
		String recognizeText = new Test().recognizeText(file);
		System.out.println(recognizeText);
	}
}