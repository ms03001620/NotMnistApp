package com.example.mark.loadtensorflowmodeltest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * Android Bitmap Object to .bmp image (Windows BMP v3 24bit) file util class
 * 
 * ref : http://en.wikipedia.org/wiki/BMP_file_format
 * 
 * @author ultrakain ( ultrasonic@gmail.com )
 * @since 2012-09-27
 *
 *
 *
 * String sdcardBmpPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sample_text.bmp";
Bitmap testBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_text);
AndroidBmpUtil bmpUtil = new AndroidBmpUtil();
boolean isSaveResult = bmpUtil.save(testBitmap, sdcardBmpPath);
 */
public class AndroidBmpUtil {
	
	private final int BMP_WIDTH_OF_TIMES = 4;
	private final int BYTE_PER_PIXEL = 3;

	/**
	 * Android Bitmap Object to Window's v3 24bit Bmp Format File
	 * @param orgBitmap
	 * @param filePath
	 * @return file saved result
	 */
	public boolean save(Bitmap orgBitmap, String filePath){
		
		if(orgBitmap == null){
			return false;
		}

		if(filePath == null){
			return false;
		}

		boolean isSaveSuccess = true;

		//image size
		int width = orgBitmap.getWidth();
		int height = orgBitmap.getHeight();

		//image dummy data size
		//reason : bmp file's width equals 4's multiple
		int dummySize = 0;
		byte[] dummyBytesPerRow = null;
		boolean hasDummy = false;
		if(isBmpWidth4Times(width)){
			hasDummy = true;
			dummySize = BMP_WIDTH_OF_TIMES - (width % BMP_WIDTH_OF_TIMES);
			dummyBytesPerRow = new byte[dummySize * BYTE_PER_PIXEL];
			for(int i = 0; i < dummyBytesPerRow.length; i++){
				dummyBytesPerRow[i] = (byte)0xFF;
			}
		}
 
		int[] pixels = new int[width * height];
		int imageSize = pixels.length * BYTE_PER_PIXEL + (height * dummySize * BYTE_PER_PIXEL);
		int imageDataOffset = 0x36;//位图文件从文件头开始偏移54个字节就是位图数据了，这其实说的是24或32位图的情况
		int fileSize = imageSize + imageDataOffset;

		//Android Bitmap Image Data
		orgBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

		//ByteArrayOutputStream baos = new ByteArrayOutputStream(fileSize);
		ByteBuffer buffer = ByteBuffer.allocate(fileSize);

		try {
			/**
			 * BITMAP FILE HEADER Write Start 1-2  ：424dh = 'BM',表示这是Windows支持的位图格式。有很多声称开头两个字节必须为'BM'才是位图文件
			 **/
			buffer.put((byte)0x42);//bftype 文件格式
			buffer.put((byte)0x4D);

			//size
			buffer.put(writeInt(fileSize));//bfsize 位图文件大小

			//reserved
			buffer.put(writeShort((short)0));//保留位
			buffer.put(writeShort((short)0));//保留位默认位0
		
			//image data start offset
			buffer.put(writeInt(imageDataOffset));//bfOffsetBits这个值说明头到数据段的距离。
		
			/** BITMAP FILE HEADER Write End */

			//*******************************************
		
			/** BITMAP INFO HEADER Write Start */
			//size
			buffer.put(writeInt(0x28));//biSize 00000028h = 40,这就是说我这个位图信息头的大小为40个字节
		
			//width, height
			buffer.put(writeInt(width));
			buffer.put(writeInt(height));
		
			//planes 该值总为1
			buffer.put(writeShort((short)1));
		
			//bit count bfBitCount 比特数／每像素 1，2，4，8，24，34。这里目标图是24位所以位24
			buffer.put(writeShort((short)24));
		
			//bit compression 图像压缩类型。0为不压缩
			buffer.put(writeInt(0));
		
			//image data size
			buffer.put(writeInt(imageSize));
		
			//horizontal resolution in pixels per meter
			buffer.put(writeInt(0));
		
			//vertical resolution in pixels per meter (unreliable)
			buffer.put(writeInt(0));
		
			//使用所有调色板
			buffer.put(writeInt(0));
		
			//影响索引条目数量0为都重要
			buffer.put(writeInt(0));

			/** BITMAP INFO HEADER Write End */
 
			int row = height;
			int col = width;
			int startPosition = 0;
			int endPosition = 0;
 
			while( row > 0 ){
 	
				startPosition = (row - 1) * col;
				endPosition = row * col;
 		
				for(int i = startPosition; i < endPosition; i++ ){
					buffer.put(write24BitForPixcel(pixels[i]));
  	
					if(hasDummy){
						if(isBitmapWidthLastPixcel(width, i)){
							buffer.put(dummyBytesPerRow);
						}  			
					}
				}
				row--;
			}
 
			FileOutputStream fos = new FileOutputStream(filePath);
			fos.write(buffer.array());
			fos.close();
	
		} catch (IOException e1) {
			e1.printStackTrace();
			isSaveSuccess = false;
		}
		finally{
	
		}

		return isSaveSuccess;
	}

	/**
	 * Is last pixel in Android Bitmap width  
	 * @param width
	 * @param i
	 * @return
	 */
	private boolean isBitmapWidthLastPixcel(int width, int i) {
		return i > 0 && (i % (width - 1)) == 0;
	}

	/**
	 * BMP file is a multiples of 4?
	 * @param width
	 * @return
	 */
	private boolean isBmpWidth4Times(int width) {
		return width % BMP_WIDTH_OF_TIMES > 0;
	}
	
	/**
	 * Write integer to little-endian 
	 * @param value
	 * @return
	 * @throws IOException
	 */
	private byte[] writeInt(int value) throws IOException {
		byte[] b = new byte[4];
 	
		b[0] = (byte)(value & 0x000000FF);
		b[1] = (byte)((value & 0x0000FF00) >> 8);
		b[2] = (byte)((value & 0x00FF0000) >> 16);
		b[3] = (byte)((value & 0xFF000000) >> 24);
  
		return b;
	}
 
	/**32位位图图像的格式为：Blue, Green, Red, Alpha
	 * Write integer pixel to little-endian byte array
	 * @param value
	 * @return
	 * @throws IOException
	 */
	private byte[] write24BitForPixcel(int value) throws IOException {
		byte[] b = new byte[3];
 		//11111111
		b[0] = (byte)(value & 0x000000FF);
		b[1] = (byte)((value & 0x0000FF00) >> 8);
		b[2] = (byte)((value & 0x00FF0000) >> 16);
  
		return b;
	}

	/**
	 * Write short to little-endian byte array
	 * @param value
	 * @return
	 * @throws IOException
	 */
	private byte[] writeShort(short value) throws IOException {
		byte[] b = new byte[2];
 	
		b[0] = (byte)(value & 0x00FF);
		b[1] = (byte)((value & 0xFF00) >> 8);
		
		return b;
	}

	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

}
