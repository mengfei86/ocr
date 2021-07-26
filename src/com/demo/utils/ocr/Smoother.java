package com.demo.utils.ocr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.googlecode.javacpp.FloatPointer;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_highgui;
import com.googlecode.javacv.cpp.opencv_imgproc;
import com.googlecode.javacv.cpp.opencv_objdetect;

public class Smoother {
	static String XML_FILE = "e:\\haarcascade_frontalface_alt2.xml";

	public static void smooth(String filename) {
		opencv_core.IplImage image = opencv_highgui.cvLoadImage(filename);

		if (image != null) {
			opencv_imgproc.cvSmooth(image, image, 2, 3);

			opencv_highgui.cvSaveImage(filename, image);

			opencv_core.cvReleaseImage(image);
		}
	}

	public static void padding(String filename) {
		int top = 10;
		int left = 10;

		int borderType = 0;

		opencv_core.CvMat src = opencv_highgui.cvLoadImageM(filename);

		opencv_core.CvMat dst = opencv_core.cvCreateMat(src.rows() + left, src.cols() + top, opencv_core.CV_8UC3);

		opencv_core.CvScalar value = new opencv_core.CvScalar(0.0D, 0.0D, 0.0D, 0.0D);

		opencv_core.CvPoint point = opencv_core.cvPoint(left / 2, top / 2);

		opencv_imgproc.cvCopyMakeBorder(src, dst, point, borderType, value);

		opencv_highgui.cvSaveImage("f:\\aaa.jpg", dst);
	}

	public static void pyramid_up(String filename) {
		opencv_core.CvMat src = opencv_highgui.cvLoadImageM(filename);

		opencv_core.CvMat dst = opencv_core.cvCreateMat(src.rows() * 2, src.cols() * 2, src.type());

		opencv_imgproc.cvPyrUp(src, dst, 7);

		opencv_highgui.cvSaveImage("D:\\IBM\\pyramid_up.JPG", dst);
	}

	public static void pyramid_down(String filename) {
		opencv_core.CvMat src = opencv_highgui.cvLoadImageM(filename);

		opencv_core.CvMat dst = opencv_core.cvCreateMat(src.rows() / 2, src.cols() / 2, src.type());

		opencv_imgproc.cvPyrDown(src, dst, 7);

		opencv_highgui.cvSaveImage("D:\\IBM\\pyramid_down.JPG", dst);
	}

	public static void morphology_Dilation(String filename, int dilation_elem) {
		opencv_core.CvMat src = opencv_highgui.cvLoadImageM(filename);

		opencv_core.CvMat dilation_dst = src;

		int dilation_type = 0;

		if (dilation_elem == 0) {
			dilation_type = 0;
		} else if (dilation_elem == 1) {
			dilation_type = 1;
		} else if (dilation_elem == 2) {
			dilation_type = 2;
		}

		opencv_imgproc.IplConvKernel kernel = opencv_imgproc.cvCreateStructuringElementEx(3, 3, 1, 1, dilation_type, null);

		opencv_imgproc.cvDilate(src, dilation_dst, kernel, 1);

		opencv_imgproc.cvReleaseStructuringElement(kernel);

		opencv_highgui.cvSaveImage("D:\\IBM\\morphology_Dilation_" + dilation_type + ".JPG", dilation_dst);
	}

	public static void morphology_Erosion(String filename, int dilation_elem) {
		opencv_core.CvMat src = opencv_highgui.cvLoadImageM(filename);

		opencv_core.CvMat erosion_dst = src;

		int dilation_type = 0;

		if (dilation_elem == 0) {
			dilation_type = 0;
		} else if (dilation_elem == 1) {
			dilation_type = 1;
		} else if (dilation_elem == 2) {
			dilation_type = 2;
		}

		opencv_imgproc.IplConvKernel kernel = opencv_imgproc.cvCreateStructuringElementEx(3, 3, 1, 1, dilation_type, null);

		opencv_imgproc.cvErode(src, erosion_dst, kernel, 1);

		opencv_imgproc.cvReleaseStructuringElement(kernel);

		opencv_highgui.cvSaveImage("e:\\IBM\\morphology_Erosion_" + dilation_type + ".JPG", erosion_dst);
	}

	public static void Thresholding(String filename, int type) {
		int threshold_value = 0;

		int threshold_type = type;

		int max_BINARY_value = 255;

		opencv_core.IplImage src = opencv_highgui.cvLoadImage(filename);

		opencv_core.IplImage pGrayImg = gray(src);

		opencv_core.IplImage dst = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 1);

		opencv_imgproc.cvThreshold(pGrayImg, dst, threshold_value, max_BINARY_value, threshold_type);

		opencv_highgui.cvSaveImage("D:\\IBM\\morphology_Thresholding_" + type + ".JPG", dst);

		opencv_core.cvReleaseImage(src);

		opencv_core.cvReleaseImage(dst);

		opencv_core.cvReleaseImage(pGrayImg);
	}

	public static opencv_core.IplImage gray(opencv_core.IplImage src) {
		opencv_core.IplImage pImg = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 3);

		opencv_highgui.cvConvertImage(src, pImg, 2);

		opencv_core.IplImage pGrayImg = opencv_core.cvCreateImage(opencv_core.cvGetSize(pImg), 8, 1);

		opencv_imgproc.cvCvtColor(pImg, pGrayImg, 7);

		opencv_core.cvReleaseImage(pImg);

		opencv_highgui.cvSaveImage("D:\\IBM\\gray.jpg", pGrayImg);

		return pGrayImg;
	}

	public static void sobel(String filename) {
		opencv_core.CvMat grad = null;

		int scale = 1;

		int delta = 0;

		int ddepth = 3;

		opencv_core.CvMat src = opencv_highgui.cvLoadImageM(filename);

		opencv_imgproc.cvSmooth(src, src, 2, 3);

		opencv_core.CvMat src_gray = gray(src.asIplImage()).asCvMat();

		opencv_core.CvMat grad_x = null;
		opencv_core.CvMat grad_y = null;

		opencv_core.CvMat abs_grad_x = null;
		opencv_core.CvMat abs_grad_y = null;

		grad_x = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 1).asCvMat();

		opencv_imgproc.cvSobel(src_gray, grad_x, 1, 0, 3);

		abs_grad_x = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 1).asCvMat();

		opencv_core.cvConvertScaleAbs(grad_x, abs_grad_x, 1.0D, 0.0D);

		grad_y = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 1).asCvMat();

		abs_grad_y = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 1).asCvMat();

		opencv_imgproc.cvSobel(src_gray, grad_y, 0, 1, 3);

		opencv_core.cvConvertScaleAbs(grad_y, abs_grad_y, 1.0D, 0.0D);

		grad = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 1).asCvMat();

		opencv_core.cvAddWeighted(abs_grad_x, 0.5D, abs_grad_y, 0.5D, 0.0D, grad);

		opencv_highgui.cvSaveImage("D:\\IBM\\sobel.jpg", grad);
	}

	public static void laplacian(String filename) {
		opencv_core.CvMat src = opencv_highgui.cvLoadImageM(filename);

		opencv_imgproc.cvSmooth(src, src, 2, 3);

		opencv_core.CvMat src_gray = gray(src.asIplImage()).asCvMat();

		opencv_core.CvMat dst = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 1).asCvMat();

		opencv_core.CvMat abs_dst = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 1).asCvMat();

		opencv_imgproc.cvLaplace(src_gray, dst, 3);

		opencv_core.cvConvertScaleAbs(dst, abs_dst, 1.0D, 0.0D);

		opencv_highgui.cvWaitKey(0);
	}

	public static void canny(String filename) {
		opencv_core.CvMat src = opencv_highgui.cvLoadImageM(filename);

		opencv_core.CvMat src_gray = gray(src.asIplImage()).asCvMat();

		opencv_core.CvMat detected_edges = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 1).asCvMat();

		opencv_imgproc.cvSmooth(src_gray, detected_edges, 2, 3);

		opencv_imgproc.cvCanny(detected_edges, detected_edges, 90.0D, 270.0D, 3);

		opencv_core.CvMat dst = opencv_core.cvCreateMat(src.rows(), src.cols(), src.type());

		opencv_core.cvSet(dst, opencv_core.cvScalar(0.0D, 0.0D, 0.0D, 0.0D), null);

		opencv_core.cvCopy(src, dst, detected_edges);

		opencv_highgui.cvSaveImage("D:\\IBM\\canny.jpg", dst);
	}

	public static void standardHoughLine(String filename) {
		opencv_core.CvMat src = opencv_highgui.cvLoadImageM(filename, 0);

		opencv_core.CvMat detected_edges = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 1).asCvMat();

		opencv_core.CvMat color_dst = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 3).asCvMat();

		opencv_imgproc.cvCanny(src, detected_edges, 50.0D, 200.0D, 3);

		opencv_imgproc.cvCvtColor(detected_edges, color_dst, 8);

		opencv_core.CvMemStorage storage = opencv_core.cvCreateMemStorage(0);

		opencv_core.CvSeq lines = opencv_imgproc.cvHoughLines2(detected_edges, storage, 0, 1.0D, 0.0174532925199433D, 150, 0.0D, 0.0D);

		for (int i = 0; i < lines.total(); i++) {
			FloatPointer line = new FloatPointer(opencv_core.cvGetSeqElem(lines, i));

			float rho = line.get(0);

			float theta = line.get(1);

			System.out.println(rho + "::" + theta);

			float a = (float) Math.cos(theta);
			float b = (float) Math.sin(theta);

			float x0 = a * rho;
			float y0 = b * rho;

			opencv_core.CvPoint pt1 = new opencv_core.CvPoint(new int[] { Math.round(x0 + 1000.0F * -b), Math.round(y0 + 1000.0F * a) });

			opencv_core.CvPoint pt2 = new opencv_core.CvPoint(new int[] { Math.round(x0 - 1000.0F * -b), Math.round(y0 - 1000.0F * a) });

			opencv_core.cvLine(color_dst, pt1, pt2, opencv_core.CV_RGB(0.0D, 0.0D, 255.0D), 1, 16, 0);
		}

		opencv_highgui.cvNamedWindow("Hough");

		opencv_highgui.cvShowImage("Hough", color_dst);

		opencv_highgui.cvWaitKey();
	}

	public static void houghLine(String filename) {
		opencv_core.CvMat src = opencv_highgui.cvLoadImageM(filename, 0);

		opencv_core.CvMat detected_edges = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 1).asCvMat();

		opencv_core.CvMat color_dst = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 3).asCvMat();

		opencv_imgproc.cvCanny(src, detected_edges, 50.0D, 200.0D, 3);

		opencv_imgproc.cvCvtColor(detected_edges, color_dst, 8);

		opencv_core.CvMemStorage storage = opencv_core.cvCreateMemStorage(0);

		opencv_core.CvSeq lines = opencv_imgproc.cvHoughLines2(detected_edges, storage, 1, 1.0D, 0.0174532925199433D, 150, 50.0D, 10.0D);

		for (int i = 0; i < lines.total(); i++) {
			opencv_core.CvPoint line = new opencv_core.CvPoint(opencv_core.cvGetSeqElem(lines, i));

			opencv_core.cvLine(color_dst, new opencv_core.CvPoint(line.position(0)), new opencv_core.CvPoint(line.position(1)),
					opencv_core.CV_RGB(0.0D, 255.0D, 0.0D), 1, 16, 0);
		}

		opencv_highgui.cvNamedWindow("Hough");

		opencv_highgui.cvShowImage("Hough", color_dst);

		opencv_highgui.cvWaitKey();
	}

	public static void houghCircle(String filename) {
		opencv_core.CvMat src = opencv_highgui.cvLoadImageM(filename);

		opencv_core.CvMat src_gray = gray(src.asIplImage()).asCvMat();

		opencv_imgproc.cvSmooth(src_gray, src_gray, 2, 3);

		opencv_core.CvMemStorage storage = opencv_core.cvCreateMemStorage(0);

		opencv_core.CvSeq circles = opencv_imgproc.cvHoughCircles(src_gray, storage, 3, 1.0D, src.rows() / 8, 200.0D, 100.0D, 0, 0);

		for (int i = 0; i < circles.total(); i++) {
			FloatPointer seq = new FloatPointer(opencv_core.cvGetSeqElem(circles, i));

			System.out.println(seq.get(0) + "," + seq.get(1) + "," + seq.get(2));

			opencv_core.CvPoint center = new opencv_core.CvPoint(new int[] { Math.round(seq.get(0)), Math.round(seq.get(1)) });

			int radius = Math.round(seq.get(2));

			opencv_core.cvCircle(src, center, 3, opencv_core.CV_RGB(0.0D, 255.0D, 0.0D), -1, 8, 0);

			opencv_core.cvCircle(src, center, radius, opencv_core.CV_RGB(255.0D, 0.0D, 0.0D), 3, 8, 0);
		}

		opencv_highgui.cvNamedWindow("Hough Circle Transform Demo", 1);

		opencv_highgui.cvShowImage("Hough Circle Transform Demo", src);

		opencv_highgui.cvWaitKey(0);
	}

	public static void histogramEqualization(String filename) {
		opencv_core.CvMat src = opencv_highgui.cvLoadImageM(filename);

		opencv_core.CvMat src_gray = gray(src.asIplImage()).asCvMat();

		opencv_core.CvMat dst = opencv_core.cvCreateMat(src.rows(), src.cols(), opencv_core.CV_8UC1);

		opencv_imgproc.cvEqualizeHist(src_gray, dst);

		opencv_highgui.cvNamedWindow("source_window", 1);

		opencv_highgui.cvNamedWindow("equalized_window", 1);

		opencv_highgui.cvShowImage("source_window", src);

		opencv_highgui.cvShowImage("equalized_window", dst);

		opencv_highgui.cvWaitKey(0);
	}

	public static void histogramCalculationFor1(String filename) {
		opencv_core.CvMat src = opencv_highgui.cvLoadImageM(filename);

		opencv_core.CvMat redImage = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 1).asCvMat();

		opencv_core.CvMat greenImage = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 1).asCvMat();

		opencv_core.CvMat blueImage = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 1).asCvMat();

		opencv_core.cvSplit(src, blueImage, greenImage, redImage, null);

		opencv_core.IplImage[] b_planes = { blueImage.asIplImage() };

		opencv_core.IplImage[] g_planes = { greenImage.asIplImage() };

		opencv_core.IplImage[] r_planes = { redImage.asIplImage() };

		int histSize = 256;

		float[] range = { 0.0F, 255.0F };

		float[][] histRange = { range };

		int[] hist_size = { histSize };

		opencv_imgproc.CvHistogram b_hist = opencv_imgproc.cvCreateHist(1, hist_size, 0, histRange, 1);

		opencv_imgproc.cvCalcHist(b_planes, b_hist, 0, null);

		opencv_imgproc.CvHistogram g_hist = opencv_imgproc.cvCreateHist(1, hist_size, 0, histRange, 1);

		opencv_imgproc.cvCalcHist(g_planes, g_hist, 0, null);

		opencv_imgproc.CvHistogram r_hist = opencv_imgproc.cvCreateHist(1, hist_size, 0, histRange, 1);

		opencv_imgproc.cvCalcHist(r_planes, r_hist, 0, null);

		int hist_w = 512;
		int hist_h = 400;

		int bin_w = Math.round(hist_w / histSize);

		opencv_core.CvMat histImage = opencv_core.cvCreateMat(hist_h, hist_w, opencv_core.CV_8UC3);

		opencv_core.cvSet(histImage, opencv_core.CV_RGB(0.0D, 0.0D, 0.0D), null);

		opencv_core.cvNormalize(b_hist.mat(), b_hist.mat(), 1.0D, histImage.rows(), 32, null);

		opencv_core.cvNormalize(g_hist.mat(), g_hist.mat(), 1.0D, histImage.rows(), 32, null);

		opencv_core.cvNormalize(r_hist.mat(), r_hist.mat(), 1.0D, histImage.rows(), 32, null);

		for (int i = 1; i < histSize; i++) {
			opencv_core.cvLine(histImage,
					opencv_core.cvPoint(bin_w * (i - 1), hist_h - (int) Math.round(opencv_core.cvGetReal1D(b_hist.bins(), i - 1))),
					opencv_core.cvPoint(bin_w * i, hist_h - (int) Math.round(opencv_core.cvGetReal1D(b_hist.mat(), i))),
					opencv_core.CV_RGB(255.0D, 0.0D, 0.0D), 2, 8, 0);

			opencv_core.cvLine(histImage,
					opencv_core.cvPoint(bin_w * (i - 1), hist_h - (int) Math.round(opencv_core.cvGetReal1D(g_hist.bins(), i - 1))),
					opencv_core.cvPoint(bin_w * i, hist_h - (int) Math.round(opencv_core.cvGetReal1D(g_hist.bins(), i))),
					opencv_core.CV_RGB(0.0D, 255.0D, 0.0D), 2, 8, 0);

			opencv_core.cvLine(histImage,
					opencv_core.cvPoint(bin_w * (i - 1), hist_h - (int) Math.round(opencv_core.cvGetReal1D(r_hist.bins(), i - 1))),
					opencv_core.cvPoint(bin_w * i, hist_h - (int) Math.round(opencv_core.cvGetReal1D(r_hist.bins(), i))),
					opencv_core.CV_RGB(0.0D, 0.0D, 255.0D), 2, 8, 0);
		}

		opencv_highgui.cvNamedWindow("calcHist Demo", 1);

		opencv_highgui.cvShowImage("calcHist Demo", histImage);

		opencv_highgui.cvWaitKey(0);
	}

	public static void histogramCalculationFor0(String filename) {
		opencv_core.CvMat src = opencv_highgui.cvLoadImageM(filename);

		opencv_core.CvMat redImage = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 1).asCvMat();

		opencv_core.CvMat greenImage = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 1).asCvMat();

		opencv_core.CvMat blueImage = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 1).asCvMat();

		opencv_core.cvSplit(src, blueImage, greenImage, redImage, null);

		opencv_core.IplImage[] b_planes = { blueImage.asIplImage() };

		opencv_core.IplImage[] g_planes = { greenImage.asIplImage() };

		opencv_core.IplImage[] r_planes = { redImage.asIplImage() };

		int histSize = 3;

		float[] range = { 0.0F, 100.0F, 101.0F, 200.0F, 201.0F, 255.0F };

		float[][] histRange = { range };

		int[] hist_size = { histSize };

		opencv_imgproc.CvHistogram b_hist = opencv_imgproc.cvCreateHist(1, hist_size, 0, histRange, 0);

		opencv_imgproc.cvCalcHist(b_planes, b_hist, 0, null);

		opencv_imgproc.CvHistogram g_hist = opencv_imgproc.cvCreateHist(1, hist_size, 0, histRange, 0);

		opencv_imgproc.cvCalcHist(g_planes, g_hist, 0, null);

		opencv_imgproc.CvHistogram r_hist = opencv_imgproc.cvCreateHist(1, hist_size, 0, histRange, 0);

		opencv_imgproc.cvCalcHist(r_planes, r_hist, 0, null);

		int hist_w = 512;
		int hist_h = 400;

		int bin_w = Math.round(hist_w / histSize);

		opencv_core.CvMat histImage = opencv_core.cvCreateMat(hist_h, hist_w, opencv_core.CV_8UC3);

		opencv_core.cvSet(histImage, opencv_core.CV_RGB(0.0D, 0.0D, 0.0D), null);

		opencv_core.cvNormalize(b_hist.mat(), b_hist.mat(), 1.0D, histImage.rows(), 32, null);

		opencv_core.cvNormalize(g_hist.mat(), g_hist.mat(), 1.0D, histImage.rows(), 32, null);

		opencv_core.cvNormalize(r_hist.mat(), r_hist.mat(), 1.0D, histImage.rows(), 32, null);

		for (int i = 1; i < histSize; i++) {
			opencv_core.cvLine(histImage,
					opencv_core.cvPoint(bin_w * (i - 1), hist_h - (int) Math.round(opencv_core.cvGetReal1D(b_hist.bins(), i - 1))),
					opencv_core.cvPoint(bin_w * i, hist_h - (int) Math.round(opencv_core.cvGetReal1D(b_hist.mat(), i))),
					opencv_core.CV_RGB(255.0D, 0.0D, 0.0D), 2, 8, 0);

			opencv_core.cvLine(histImage,
					opencv_core.cvPoint(bin_w * (i - 1), hist_h - (int) Math.round(opencv_core.cvGetReal1D(g_hist.bins(), i - 1))),
					opencv_core.cvPoint(bin_w * i, hist_h - (int) Math.round(opencv_core.cvGetReal1D(g_hist.bins(), i))),
					opencv_core.CV_RGB(0.0D, 255.0D, 0.0D), 2, 8, 0);

			opencv_core.cvLine(histImage,
					opencv_core.cvPoint(bin_w * (i - 1), hist_h - (int) Math.round(opencv_core.cvGetReal1D(r_hist.bins(), i - 1))),
					opencv_core.cvPoint(bin_w * i, hist_h - (int) Math.round(opencv_core.cvGetReal1D(r_hist.bins(), i))),
					opencv_core.CV_RGB(0.0D, 0.0D, 255.0D), 2, 8, 0);
		}

		opencv_highgui.cvNamedWindow("calcHist Demo", 1);

		opencv_highgui.cvShowImage("calcHist Demo", histImage);

		opencv_highgui.cvWaitKey(0);
	}

	public static void histogramComparison(String src, String test1, String test2) {
		opencv_core.CvMat src_base = opencv_highgui.cvLoadImageM(src);

		opencv_core.CvMat src_test1 = opencv_highgui.cvLoadImageM(test1);

		opencv_core.CvMat src_test2 = opencv_highgui.cvLoadImageM(test2);

		System.out.println("坐标起始点:" + src_base.asIplImage().origin());

		opencv_core.CvMat src_half = opencv_core.cvCreateMatHeader(src_test2.rows() / 2, src_test2.cols(), src_test2.type());

		opencv_core.CvRect rect = opencv_core.cvRect(0, src_base.rows() / 2, src_base.cols(), src_base.rows() / 2);

		opencv_core.cvGetSubRect(src_base, src_half, rect);

		opencv_core.CvMat hsv_base = opencv_core.cvCreateImage(opencv_core.cvGetSize(src_base), 8, 3).asCvMat();

		opencv_core.CvMat hsv_test1 = opencv_core.cvCreateImage(opencv_core.cvGetSize(src_test1), 8, 3).asCvMat();

		opencv_core.CvMat hsv_test2 = opencv_core.cvCreateImage(opencv_core.cvGetSize(src_test2), 8, 3).asCvMat();

		opencv_core.CvMat hsv_half_down = opencv_core.cvCreateImage(opencv_core.cvGetSize(src_half), 8, 3).asCvMat();

		opencv_imgproc.cvCvtColor(src_base, hsv_base, 40);

		opencv_imgproc.cvCvtColor(src_test1, hsv_test1, 40);

		opencv_imgproc.cvCvtColor(src_test2, hsv_test2, 40);

		opencv_imgproc.cvCvtColor(src_half, hsv_half_down, 40);

		opencv_core.IplImage hsv_base_h_plane = opencv_core.cvCreateImage(opencv_core.cvGetSize(src_base), 8, 1);

		opencv_core.IplImage hsv_base_s_plane = opencv_core.cvCreateImage(opencv_core.cvGetSize(src_base), 8, 1);

		opencv_core.cvSplit(hsv_base, hsv_base_h_plane, hsv_base_s_plane, null, null);

		opencv_core.IplImage[] hsv_base_array = { hsv_base_h_plane, hsv_base_s_plane };

		opencv_core.IplImage hsv_test1_h_plane = opencv_core.cvCreateImage(opencv_core.cvGetSize(src_test1), 8, 1);

		opencv_core.IplImage hsv_test1_s_plane = opencv_core.cvCreateImage(opencv_core.cvGetSize(src_test1), 8, 1);

		opencv_core.cvSplit(hsv_test1, hsv_test1_h_plane, hsv_test1_s_plane, null, null);

		opencv_core.IplImage[] hsv_test1_array = { hsv_test1_h_plane, hsv_test1_s_plane };

		opencv_core.IplImage hsv_test2_h_plane = opencv_core.cvCreateImage(opencv_core.cvGetSize(src_test2), 8, 1);

		opencv_core.IplImage hsv_test2_s_plane = opencv_core.cvCreateImage(opencv_core.cvGetSize(src_test2), 8, 1);

		opencv_core.cvSplit(hsv_test2, hsv_test2_h_plane, hsv_test2_s_plane, null, null);

		opencv_core.IplImage[] hsv_test2_array = { hsv_test2_h_plane, hsv_test2_s_plane };

		opencv_core.IplImage hsv_half_down_h_plane = opencv_core.cvCreateImage(opencv_core.cvGetSize(hsv_half_down), 8, 1);

		opencv_core.IplImage hsv_half_down_s_plane = opencv_core.cvCreateImage(opencv_core.cvGetSize(hsv_half_down), 8, 1);

		opencv_core.cvSplit(hsv_half_down, hsv_half_down_h_plane, hsv_half_down_s_plane, null, null);

		opencv_core.IplImage[] hsv_half_down_array = { hsv_half_down_h_plane, hsv_half_down_s_plane };

		int h_bins = 50;
		int s_bins = 60;

		int[] histSize = { h_bins, s_bins };

		float[] h_ranges = { 0.0F, 256.0F };

		float[] s_ranges = { 0.0F, 180.0F };

		float[][] ranges = { h_ranges, s_ranges };

		opencv_imgproc.CvHistogram hist_base = opencv_imgproc.cvCreateHist(2, histSize, 0, ranges, 1);

		opencv_imgproc.cvCalcHist(hsv_base_array, hist_base, 0, null);

		opencv_core.cvNormalize(hist_base.mat(), hist_base.mat(), 0.0D, 1.0D, 32, null);

		opencv_imgproc.CvHistogram hist_half_down = opencv_imgproc.cvCreateHist(2, histSize, 0, ranges, 1);

		opencv_imgproc.cvCalcHist(hsv_half_down_array, hist_half_down, 0, null);

		opencv_core.cvNormalize(hist_half_down.mat(), hist_half_down.mat(), 0.0D, 1.0D, 32, null);

		opencv_imgproc.CvHistogram hist_test1 = opencv_imgproc.cvCreateHist(2, histSize, 0, ranges, 1);

		opencv_imgproc.cvCalcHist(hsv_test1_array, hist_test1, 0, null);

		opencv_core.cvNormalize(hist_test1.mat(), hist_test1.mat(), 0.0D, 1.0D, 32, null);

		opencv_imgproc.CvHistogram hist_test2 = opencv_imgproc.cvCreateHist(2, histSize, 0, ranges, 1);

		opencv_imgproc.cvCalcHist(hsv_test2_array, hist_test2, 0, null);

		opencv_core.cvNormalize(hist_test2.mat(), hist_test2.mat(), 0.0D, 1.0D, 32, null);

		for (int i = 0; i < 4; i++) {
			int compare_method = i;

			double base_base = opencv_imgproc.cvCompareHist(hist_base, hist_base, compare_method);

			double base_half = opencv_imgproc.cvCompareHist(hist_base, hist_half_down, compare_method);

			double base_test1 = opencv_imgproc.cvCompareHist(hist_base, hist_test1, compare_method);

			double base_test2 = opencv_imgproc.cvCompareHist(hist_base, hist_test2, compare_method);

			System.out.println(" Method [%d] Perfect, Base-Half, Base-Test(1), Base-Test(2) : %f, %f, %f, %f \n" + i + "," + base_base + ","
					+ base_half + "," + base_test1 + "," + base_test2);
		}
	}

	public static void backProjection(String filename, int bins) {
		opencv_core.IplImage target = opencv_highgui.cvLoadImage(filename, 1);

		opencv_core.IplImage target_hsv = opencv_core.cvCreateImage(opencv_core.cvGetSize(target), 8, 3);

		opencv_core.IplImage target_hue = opencv_core.cvCreateImage(opencv_core.cvGetSize(target), 8, 1);

		opencv_imgproc.cvCvtColor(target, target_hsv, 40);

		opencv_core.cvSplit(target_hsv, target_hue, null, null, null);

		if (bins < 2) {
			bins = 2;
		}
		int[] hist_size = { bins };

		float[][] ranges = { { 0.0F, 180.0F } };

		opencv_imgproc.CvHistogram hist = opencv_imgproc.cvCreateHist(1, hist_size, 0, ranges, 1);

		opencv_core.IplImage[] target_hues = { target_hue };

		opencv_imgproc.cvCalcHist(target_hues, hist, 0, null);

		opencv_core.cvNormalize(hist.mat(), hist.mat(), 0.0D, 255.0D, 32, null);

		opencv_core.IplImage result = opencv_core.cvCreateImage(opencv_core.cvGetSize(target), 8, 1);

		opencv_imgproc.cvCalcBackProject(target_hues, result, hist);

		opencv_highgui.cvShowImage("BackProj", result);

		opencv_highgui.cvWaitKey(0);
	}

	public static void templateMatching(String src, String template, int match_method) {
		opencv_core.CvMat img = opencv_highgui.cvLoadImageM(src, 1);

		opencv_core.CvMat templ = opencv_highgui.cvLoadImageM(template, 1);

		int result_cols = img.cols() - templ.cols() + 1;

		int result_rows = img.rows() - templ.rows() + 1;

		opencv_core.CvMat result = opencv_core.cvCreateMat(result_cols, result_rows, opencv_core.CV_32FC1);

		opencv_imgproc.cvMatchTemplate(img, templ, result, 0);

		opencv_core.cvNormalize(result, result, 0.0D, 1.0D, 32, null);

		double[] minVal = new double[5];
		double[] maxVal = new double[5];
		opencv_core.CvPoint minLoc = new opencv_core.CvPoint();

		opencv_core.CvPoint maxLoc = new opencv_core.CvPoint();

		opencv_core.CvPoint matchLoc = new opencv_core.CvPoint();

		opencv_core.cvMinMaxLoc(result, minVal, maxVal, minLoc, maxLoc, null);

		if ((match_method == 0) || (match_method == 1)) {
			matchLoc = minLoc;
		} else {
			matchLoc = maxLoc;
		}

		opencv_core.CvMat img_display = opencv_core.cvCreateMat(img.cols(), img.rows(), img.type());

		opencv_core.cvCopy(img, img_display, null);

		opencv_core.cvRectangle(img_display, matchLoc,
				new opencv_core.CvPoint(new int[] { matchLoc.x() + templ.cols(), matchLoc.y() + templ.rows() }),
				new opencv_core.CvScalar(0.0D, 0.0D, 0.0D, 0.0D), 2, 8, 0);

		opencv_core.cvRectangle(result, matchLoc, new opencv_core.CvPoint(new int[] { matchLoc.x() + templ.cols(), matchLoc.y() + templ.rows() }),
				new opencv_core.CvScalar(0.0D, 0.0D, 0.0D, 0.0D), 2, 8, 0);

		opencv_highgui.cvShowImage("image_window", img_display);

		opencv_highgui.cvShowImage("result_window", result);
	}

	public static void main(String[] strings) {
		gray(opencv_highgui.cvLoadImageM("e:\\e.png").asIplImage()).asCvMat();

		opencv_core.IplImage img = opencv_highgui.cvLoadImage("e:\\data\\sbk\\sbk2.jpg");
		detect(img);
	}

	public static int[] detect_t(String file) {
		opencv_core.IplImage src = opencv_highgui.cvLoadImage(file);
		opencv_objdetect.CvHaarClassifierCascade cascade = new opencv_objdetect.CvHaarClassifierCascade(opencv_core.cvLoad(XML_FILE));
		opencv_core.CvMemStorage storage = opencv_core.CvMemStorage.create();
		opencv_core.CvSeq sign = opencv_objdetect.cvHaarDetectObjects(src, cascade, storage, 1.2D, 2, 1);

		opencv_core.cvClearMemStorage(storage);
		int[] rectPosition = new int[4];
		int total_Faces = sign.total();
		if (total_Faces == 0)
			return rectPosition;
		opencv_core.CvRect rect = new opencv_core.CvRect(opencv_core.cvGetSeqElem(sign, 0));

		rectPosition[0] = rect.x();
		rectPosition[1] = rect.y();
		rectPosition[2] = rect.width();
		rectPosition[3] = rect.height();
		return rectPosition;
	}

	public static void detect(opencv_core.IplImage src) {
		opencv_objdetect.CvHaarClassifierCascade cascade = new opencv_objdetect.CvHaarClassifierCascade(opencv_core.cvLoad(XML_FILE));
		opencv_core.CvMemStorage storage = opencv_core.CvMemStorage.create();
		opencv_core.CvSeq sign = opencv_objdetect.cvHaarDetectObjects(src, cascade, storage, 1.2D, 2, 1);

		opencv_core.cvClearMemStorage(storage);

		int total_Faces = sign.total();
		if (total_Faces == 0)
			return;
		for (int i = 0; i < total_Faces; i++) {
			opencv_core.CvRect r = new opencv_core.CvRect(opencv_core.cvGetSeqElem(sign, i));
			opencv_core.cvRectangle(src, opencv_core.cvPoint(r.x(), r.y()), opencv_core.cvPoint(r.width() + r.x(), r.height() + r.y()),
					opencv_core.CvScalar.RED, 2, 16, 0);

			System.out.println(r.x() + "--" + r.y() + "--" + r.width() + "--" + r.height());
			cut("e:\\data\\sbk\\sbk2.jpg", "e:\\" + i + "_ccc.jpg", r.x(), r.y(), r.width(), r.height());
		}
		int[] a = { 33, 85 };

		opencv_core.CvMat src1 = opencv_highgui.cvLoadImageM("e:\\data\\sbk\\sbk2.jpg");
		opencv_core.CvMat dst = opencv_core.cvCreateMat(src1.rows() * 2, src1.cols() * 2, src1.type());
		opencv_core.IplImage aaa = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 3);
		opencv_core.IplImage bbb = opencv_core.cvCreateImage(opencv_core.cvGetSize(src), 8, 3);
		opencv_core.cvCopy(src, aaa, bbb);

		opencv_imgproc.cvPyrUp(src1, dst, 7);
		opencv_highgui.cvSaveImage("a.jpg", aaa);

		opencv_highgui.cvShowImage("Result", src);
		opencv_highgui.cvWaitKey(0);
	}

	public static void cut(String srcPath, String NsrcPath, int startX, int startY, int width, int height) {
		File file = new File(srcPath);
		try {
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
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

	public BufferedImage SplitImage(BufferedImage img, int x, int y, int width, int height) {
		if ((x + width >= img.getWidth()) || (y + height >= img.getHeight())) {
			return null;
		}
		BufferedImage newImg = new BufferedImage(width, height, 2);
		for (int i = x; i < x + width; i++) {
			for (int j = y; j < y + height; j++) {
				newImg.setRGB(i - x, j - y, img.getRGB(i, j));
			}
		}
		return newImg;
	}
}