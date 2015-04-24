import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class Main {
	public static class CA {
		int rule, width, height;
		boolean[][] array;
		boolean[][][] stateMap;

		CA(int rule, int width, int height) {
			this.rule = rule;
			this.width = width;
			this.height = height;
			array = new boolean[height][width];
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					array[i][j] = false;
				}
			}
			array[0][width / 2] = true;
			stateMap = new boolean[2][2][2];
			for (int i = 0; i < 8; i++) {
				stateMap[getBitInt(i, 2)][getBitInt(i, 1)][getBitInt(i, 0)] = getBitBool(
						rule, i);
			}
		}

		int getBitInt(int num, int bit) {
			return (num >> bit) & 1;
		}

		boolean getBitBool(int num, int bit) {
			return getBitInt(num, bit) == 1;
		}

		public void go() {
			for (int i = 1; i < height; i++) {
				for (int j = 0; j < width; j++) {
					boolean[] arr = new boolean[3];
					for (int pos = -1; pos <= 1; pos++) {
						arr[pos + 1] = array[i - 1][(j + pos + width) % width];
					}
					array[i][j] = getState(arr);
				}
			}
		}

		public boolean getState(boolean[] arr) {
			return stateMap[arr[0] ? 1 : 0][arr[1] ? 1 : 0][arr[2] ? 1 : 0];
		}

		public boolean[][] getArray() {
			return array;
		}

	}

	public static void main(String[] Args) throws IOException {
		Scanner inp = new Scanner(System.in);
		System.out.println("enter 5 numbers : ");
		System.out.println("rule number , width and height of cells , vertical and horizontal scales");
		int rule = inp.nextInt();
		int width = inp.nextInt();
		int height = inp.nextInt() ;
		int scalerv = inp.nextInt();
		int scalerh = inp.nextInt();
		CA ca = new CA(rule, width, height);
		ca.go();
		boolean[][] arr = ca.getArray();
		byte[] bytes = new byte[width * height * 3 * scalerv * scalerh];

		int nowon = 0;
		for (int i = 0; i < height; i++) {
			for (int t = 0; t < scalerv; t++) {
				for (int j = 0; j < width; j++) {
					byte val = arr[i][j] ? ((byte) 0xff) : (byte) 0x00;
					for (int tt = 0; tt < scalerh; tt++) {
						for (int k = 0; k < 3; k++) {
							bytes[nowon] = val;
							nowon++;
						}
						// System.out.print((arr[i][j]?1:0) + " ");
					}
					// System.out.println();
				}
			}
		}

		// bytes = scale(bytes, height, width, scaler);
		writeToImage(width*scalerh, height*scalerv, bytes);
	}

	public static void writeToImage(int width, int height, byte[] aByteArray)
			throws IOException {

		DataBuffer buffer = new DataBufferByte(aByteArray, aByteArray.length);

		// 3 bytes per pixel: red, green, blue
		WritableRaster raster = Raster.createInterleavedRaster(buffer, width,
				height, 3 * width, 3, new int[] { 0, 1, 2 }, (Point) null);
		ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault()
				.getColorSpace(), false, true, Transparency.OPAQUE,
				DataBuffer.TYPE_BYTE);
		BufferedImage image = new BufferedImage(cm, raster, true, null);

		ImageIO.write(image, "png", new File("image.png"));
	}
}
