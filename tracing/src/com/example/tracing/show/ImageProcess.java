package com.example.tracing.show;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class ImageProcess {

	boolean debug = false;

	private Bitmap bitmap;

	private int w;
	private int h;
	private int picsize = w * h;

	private int threshold = 128;
	private int[] GaussLaplacian = { -2, -4, -4, -4, -2, -4, 0, 8, 0, -4, -4,
			8, 24, 8, -4, -4, 0, 8, 0, -4, -2, -4, -4, -4, -2 };

	public ImageProcess(Bitmap source) {

		setSource(source);
	}

	public void setSource(Bitmap source) {

		bitmap = source;
		w = source.getWidth();
		h = source.getHeight();
		picsize = w * h;
	}

	private int[] image2pixels(Bitmap image) {
		int ai[] = new int[picsize];
		// System.out.println(width*height+"  "+picsize);
		image.getPixels(ai, 0, w, 0, 0, w, h);

		boolean flag = false;
		int k1 = 0;
		do {
			if (k1 >= 16)
				break;
			int i = (ai[k1] & 0xff0000) >> 16;
			int k = (ai[k1] & 0xff00) >> 8;
			int i1 = ai[k1] & 0xff;
			if (i != k || k != i1) {
				flag = true;
				break;
			}
			k1++;
		} while (true);
		if (flag) {
			for (int l1 = 0; l1 < picsize; l1++) {
				int j = (ai[l1] & 0xff0000) >> 16;
				int l = (ai[l1] & 0xff00) >> 8;
				int j1 = ai[l1] & 0xff;
				ai[l1] = (int) (0.29799999999999999D * (double) j
						+ 0.58599999999999997D * (double) l + 0.113D * (double) j1);
				//if (ai[l1]<0) System.out.println("sad");
			}

		} else {
			for (int i2 = 0; i2 < picsize; i2++)
				ai[i2] = ai[i2] & 0xff;

		}
		return ai;
	}

	public Bitmap pixelsGrayImage(int[] data) {

		int alpha = 0xFF << 24;
		for (int i = 0; i < picsize; i++)
			/*
			 * if (data[i] > threshold) data[i] = -1; else data[i] = 0xff000000;
			 */
			data[i] = alpha | (data[i] << 16) | (data[i] << 8) | data[i];
		Bitmap m = Bitmap.createBitmap(data, w, h, Config.ARGB_8888);

		return m;
	}

	public Bitmap pixel2Image() {
		int[] data = image2pixels(bitmap);
		for (int i = 0; i < picsize; i++)
			if (data[i] > threshold)
				data[i] = -1;
			else
				data[i] = 0xff000000;
		Bitmap m = Bitmap.createBitmap(data, w, h, Config.ARGB_8888);

		return m;

	}

	public Bitmap getAbstract() {
		int[] data = image2pixels(bitmap);
		int[] dest = Convolution(data, GaussLaplacian);
		Bitmap m = pixelsGrayImage(dest);
		
		
		return m;
	}

	// kernel : do convolution to data
	// m length to kernel
	public int[] Convolution(int data[], int[] kernel) {
		int[] dest = new int[picsize];
		int length = kernel.length;
		int m = (int) Math.sqrt(length);
		int gap = m / 2;
		int add = 0;
		for (int i = gap; i < w - gap; i++)
			for (int j = gap; j < h - gap; j++) {
				int now = i + j * w;
				for (int a = -gap; a <= gap; a++)
					for (int b = -gap; b <= gap; b++) {
						add = data[(i + a) + (j + b) * w]
								* kernel[(gap + a) + (gap + b) * m];
						dest[now] = dest[now] + add;
					}
				if (dest[now]<0) dest[now] = 0;
				if (dest[now]>255) dest[now] = 255;
				//System.out.println(dest[now]);
				

			}

		return dest;
	}

}

