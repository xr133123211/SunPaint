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
	private double sigma = 3.0;

	private int pThrHigh, pThrLow;
	private double dRatHigh = 0.4, dRatLow = 0.9;

	private int[] GaussLaplacian = { -2, -4, -4, -4, -2, -4, 0, 8, 0, -4, -4,
			8, 24, 8, -4, -4, 0, 8, 0, -4, -2, -4, -4, -4, -2 };

	private double[] gx = new double[picsize];
	private double[] gy = new double[picsize];
	private int[] mag = new int[picsize];
	private int[] pNSRst = new int[picsize];

	public ImageProcess(Bitmap source) {

		setSource(source);
	}

	public void setSource(Bitmap source) {

		bitmap = source;
		w = source.getWidth();
		h = source.getHeight();
		picsize = w * h;

		gx = new double[picsize];
		gy = new double[picsize];
		mag = new int[picsize];
		pNSRst = new int[picsize];
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
				// if (ai[l1]<0) System.out.println("sad");
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

	public Bitmap getCanny() {
		int[] data = image2pixels(bitmap);

		data = GaussianSmooth(data, sigma);
		System.out.println(data.length + " " + gx.length);
		grand(data);
		NonmaxSuppress();
		Hysteresis(data);

		Bitmap m = pixelsGrayImage(data);

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
				if (dest[now] < 0)
					dest[now] = 0;
				if (dest[now] > 255)
					dest[now] = 255;
				// System.out.println(dest[now]);

			}

		return dest;
	}

	public void grand(int[] data) {
		int k;
		double dSqt1, dSqt2;

		for (int i = 1; i < w - 1; i++)
			for (int j = 1; j < h - 1; j++) {
				k = i + j * w;
				gy[k] = data[k + 1] - data[k - 1];
			}
		for (int j = 1; j < h - 1; j++)
			for (int i = 1; i < w - 1; i++) {
				k = i + j * w;
				gx[k] = data[k + w] - data[k - w];
			}
		for (int j = 0; j < h; j++)
			for (int i = 0; i < w; i++) {
				k = i + j * w;
				dSqt1 = gx[k] * gx[k];
				dSqt2 = gy[k] * gy[k];
				mag[k] = (int) (Math.sqrt(dSqt1 + dSqt2) + 0.5);
			}

	}

	// ���������
	public void NonmaxSuppress() {

		double grandx, grandy;
		int pos;
		int dtmp, dtmp1, dtmp2;
		int g1, g2, g3, g4;
		double weight;

		for (int x = 0; x < w; x++) {
			pNSRst[x] = 0;
			pNSRst[(h - 1) * w + x] = 0;

		}

		for (int y = 0; y < h; y++) {
			pNSRst[y * w] = 0;
			pNSRst[y * w + w - 1] = 0;
		}

		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++) {
				pos = i + w * j;
				if (mag[pos] == 0) {
					pNSRst[pos] = 0;
				} else {
					dtmp = mag[pos];
					grandx = gx[pos];
					grandy = gy[pos];
					// ���������y������x������˵����������������y����
					if (Math.abs(grandy) > Math.abs(grandx)) {
						// �����ֵ����
						weight = Math.abs(grandx) / Math.abs(grandy);

						g2 = mag[pos - w];
						g4 = mag[pos + w];

						// ���x,y�����������ķ�����ͬ
						// C Ϊ��ǰ���أ���g1-g4 ��λ�ù�ϵΪ��
						// g1 g2
						// C
						// g4 g3
						if (grandx * grandy > 0) {
							g1 = mag[pos - w - 1];
							g3 = mag[pos + w + 1];
						}

						// ���x,y��������ķ����������෴
						// C�ǵ�ǰ���أ���g1-g4�Ĺ�ϵΪ��
						// g2 g1
						// C
						// g3 g4
						else {
							g1 = mag[pos - w + 1];
							g3 = mag[pos + w - 1];
						}
					}

					// ���������x������y������˵�������ķ���������x����
					else {
						// ��ֵ����
						weight = Math.abs(grandy) / Math.abs(grandx);

						g2 = mag[pos + 1];
						g4 = mag[pos - 1];

						// ���x,y��������ķ�����������ͬ
						// ��ǰ����C�� g1-g4�Ĺ�ϵΪ
						// g3
						// g4 C g2
						// g1
						if (grandx * grandy > 0) {
							g1 = mag[pos + w + 1];
							g3 = mag[pos - w - 1];
						}

						// ���x,y�����������ķ����෴
						// C��g1-g4�Ĺ�ϵΪ
						// g1
						// g4 C g2
						// g3
						else {
							g1 = mag[pos - w + 1];
							g3 = mag[pos + w - 1];
						}
					}

					// ���� g1-g4 ���ݶȽ��в�ֵ
					{
						dtmp1 = (int) (weight * g1 + (1 - weight) * g2);
						dtmp2 = (int) (weight * g3 + (1 - weight) * g4);

						// ��ǰ���ص��ݶ��Ǿֲ������ֵ
						// �õ�����Ǳ߽��
						if (dtmp >= dtmp1 && dtmp >= dtmp2) {
							pNSRst[pos] = 128;
						} else {
							// �������Ǳ߽��
							pNSRst[pos] = 0;
						}
					}
				}

			}

	}

	public void estimateThreshold(int[] data) {
		int[] nHist = new int[256];

		// ���ܱ߽���
		int nEdgeNum;

		// ����ݶ���
		int nMaxMag;

		int nHighCount;

		nMaxMag = 0;

		int pos;

		// ��ʼ��
		for (int k = 0; k < 256; k++) {
			nHist[k] = 0;
		}
		// ͳ��ֱ��ͼ,����ֱ��ͼ������ֵ
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				pos = y * w + x;
				if (data[w] == 128) {
					nHist[mag[pos]]++;
				}
			}
		}

		nEdgeNum = nHist[0];
		nMaxMag = 0;

		for (int k = 1; k < 256; k++) {
			if (nHist[k] != 0) {
				nMaxMag = k;
			}

			// �ݶ�Ϊ0�ĵ��ǲ�����Ϊ�߽���
			// ����non-maximum suppression���ж�������
			nEdgeNum += nHist[k];

		}

		// �ݶȱȸ���ֵ*pThrHigh С�����ص�����Ŀ
		nHighCount = (int) (dRatHigh * nEdgeNum + 0.5);

		int k = 1;
		nEdgeNum = nHist[1];

		// �������ֵ
		while ((k < (nMaxMag - 1)) && (nEdgeNum < nHighCount)) {
			k++;
			nEdgeNum += nHist[k];
		}

		pThrHigh = k;

		// ����ֵ
		pThrLow = (int) ((pThrHigh) * dRatLow + 0.5);
	}

	public void Hysteresis(int[] data) {

		int pos;
		// ����TraceEdge ������Ҫ�ĵ���ֵ���Լ�Hysteresis����ʹ�õĸ���ֵ
		estimateThreshold(data);

		// Ѱ�Ҵ���dThrHigh�ĵ㣬��Щ�����������߽�㣬
		// Ȼ����TraceEdge�������ٸõ��Ӧ�ı߽�
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				pos = y * w + x;

				// ����������ǿ��ܵı߽�㣬�����ݶȴ��ڸ���ֵ��
				// ��������Ϊһ���߽�����
				if ((data[pos] == 128) && (mag[pos] >= pThrHigh)) {
					// ���øõ�Ϊ�߽��
					data[pos] = 255;
					TraceEdge(y, x, pThrLow, data);
				}

			}
		}

		// �������Ѿ�������Ϊ�߽��
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				pos = y * w + x;

				if (data[pos] != 255) {
					data[pos] = 0;
				}
			}
		}
	}

	private void TraceEdge(int y, int x, int pThrLow2, int[] pResult) {
		// TODO Auto-generated method stub
		int[] xNum = { 1, 1, 0, -1, -1, -1, 0, 1 };
		int[] yNum = { 0, 1, 1, 1, 0, -1, -1, -1 };

		int xx, yy;

		for (int k = 0; k < 8; k++) {
			yy = y + yNum[k];
			xx = x + xNum[k];

			if (pResult[yy * w + xx] == 128 && mag[yy * w + xx] >= pThrLow) {
				// �õ���Ϊ�߽��
				pResult[yy * w + xx] = 255;

				// �Ըõ�Ϊ�����ٽ��и���
				TraceEdge(yy, xx, pThrLow, pResult);
			}
		}
	}

	// ��˹�˲���
	/*
	 * * double sigma - ��˹�����ı�׼�� * double **pdKernel - ָ���˹���������ָ�� int *
	 * pnWindowSize - ���ݵĳ���
	 */
	double[] MakeGauss(double sigma, int pnWindowSize) {
		// ѭ�����Ʊ���
		int i;
		// ��������ĵ�
		int nCenter;

		// �����ĳһ�㵽���ĵ�ľ���
		double dDis;

		double PI = 3.14159;
		// �м����
		double dValue;
		double dSum;
		dSum = 0;
		// ���鳤�ȣ����ݸ����۵�֪ʶ��ѡȡ[-3*sigma, 3*sigma]���ڵ����ݡ�
		// ��Щ���ݻḲ�Ǿ��󲿷ֵ��˲�ϵ��
		pnWindowSize = (int) (1 + 2 * Math.ceil(3 * sigma));
		// ����
		nCenter = (pnWindowSize) / 2;
		// �����ڴ�
		double[] pdKernel = new double[pnWindowSize];
		for (i = 0; i < pnWindowSize; i++) {
			dDis = (double) (i - nCenter);
			dValue = Math.exp(-(1 / 2) * dDis * dDis / (sigma * sigma))
					/ (Math.sqrt(2 * PI) * sigma);
			(pdKernel)[i] = dValue;
			dSum += dValue;
		}
		// ��һ��
		for (i = 0; i < (pnWindowSize); i++) {
			(pdKernel)[i] /= dSum;
		}
		return pdKernel;
	}

	// ��˹�˲�

	public int[] GaussianSmooth(int[] pUnchImg, double sigma) {
		// ѭ�����Ʊ���
		int y;
		int x;
		int i;
		// ��˹�˲��������鳤��
		int nWindowSize = (int) (1 + 2 * Math.ceil(3 * sigma));
		// ���ڳ��ȵ�1/2
		int nHalfLen;
		// һά��˹�����˲���
		double[] pdKernel;
		// ��˹ϵ����ͼ�����ݵĵ��
		double dDotMul;
		// ��˹�˲�ϵ�����ܺ�
		double dWeightSum;
		// �м����
		double[] pdTmp;
		// �����ڴ�
		int[] pUnchSmthdImg = new int[picsize];
		pdTmp = new double[picsize];

		// ����һά��˹�����˲���

		pdKernel = MakeGauss(sigma, nWindowSize);
		// MakeGauss���ش��ڵĳ��ȣ����ô˱������㴰�ڵİ볤
		nHalfLen = nWindowSize / 2;
		// x��������˲�
		for (y = 0; y < h; y++) {
			for (x = 0; x < w; x++) {
				dDotMul = 0;
				dWeightSum = 0;
				for (i = (-nHalfLen); i <= nHalfLen; i++) {
					// �ж��Ƿ���ͼ���ڲ�
					if ((i + x) >= 0 && (i + x) < w) {

						dDotMul += pUnchImg[y * w + (i + x)]
								* pdKernel[nHalfLen + i];
						dWeightSum += pdKernel[nHalfLen + i];
					}
				}
				pdTmp[y * w + x] = dDotMul / dWeightSum;
			}
		}
		// y��������˲�
		for (x = 0; x < w; x++) {
			for (y = 0; y < h; y++) {
				dDotMul = 0;
				dWeightSum = 0;
				for (i = (-nHalfLen); i <= nHalfLen; i++) {
					// �ж��Ƿ���ͼ���ڲ�
					if ((i + y) >= 0 && (i + y) < h) {
						dDotMul += pdTmp[(y + i) * w + x]
								* pdKernel[nHalfLen + i];
						dWeightSum += pdKernel[nHalfLen + i];
					}
				}
				pUnchSmthdImg[y * w + x] = (int) (dDotMul / dWeightSum);
			}
		}
		return pUnchSmthdImg;

	}

}
