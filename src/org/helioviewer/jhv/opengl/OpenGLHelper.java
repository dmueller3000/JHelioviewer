package org.helioviewer.jhv.opengl;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.helioviewer.jhv.base.ImageRegion;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLContext;

public class OpenGLHelper {
	public static GLContext glContext;
	private int textureID;
	private int textureWidth = 0;
	private int textureHeight = 0;
	
	private float scaleFactorHeight;
	private float scaleFactorWidth;

	public static int nextPowerOfTwo(int input) {
		int output = 1;
		while (output < input) {
			output <<= 1;
		}
		return output;
	}
	
	public int createTextureID(){
		GL2 gl = GLContext.getCurrentGL().getGL2();
        int tmp[] = new int[1];
		gl.glGenTextures(1, tmp, 0);
		this.textureID = tmp[0];
		return tmp[0];
	}
	
	public static int[] createTextureIDs(int countTexture){
		GL2 gl = GLContext.getCurrentGL().getGL2();
		int tmp[] = new int[countTexture];
		gl.glGenTextures(countTexture, tmp, 0);
		return tmp;
	}
	
	public void bindBufferedImageToGLTexture(final BufferedImage bufferedImage){
				int width2 = nextPowerOfTwo(bufferedImage.getWidth());
				int height2 = nextPowerOfTwo(bufferedImage.getHeight());
				
				if (textureHeight != height2 && textureWidth != width2){
					createTexture(bufferedImage, width2, height2);
				}

				this.textureHeight = height2;
				this.textureWidth = width2;

				updateTexture(bufferedImage,0,0);
	}
	
	public static void bindByteBufferToGLTexture(ImageRegion region, final ByteBuffer byteBuffer, final Rectangle imageSize){
				int width2 = nextPowerOfTwo(imageSize.width);
				int height2 = nextPowerOfTwo(imageSize.height);
				
				if (region.textureHeight != height2 && region.textureWidth != width2){
					OpenGLHelper.createTexture(region, width2, height2);
				}

				OpenGLHelper.updateTexture(region, byteBuffer, imageSize.width, imageSize.height);
				region.setTextureScaleFactor(width2 / (float)imageSize.width, height2 / (float)imageSize.height);
	}
	
	public float getScaleFactorWidth(){
		return scaleFactorWidth;
	}
	
	public float getScaleFactorHeight(){
		return scaleFactorHeight;
	}
	
	public void bindBufferedImageToGLTexture(final BufferedImage bufferedImage, final int width, final int height){
				int width2 = nextPowerOfTwo(width);
				int height2 = nextPowerOfTwo(height);
				
				if (textureHeight != height2 && textureWidth != width2){
					createTexture(bufferedImage, width2, height2);
				}

				this.textureHeight = height2;
				this.textureWidth = width2;

				updateTexture(bufferedImage, 0, 0);		
	}
	
	public void bindBufferedImageToGLTexture(final BufferedImage bufferedImage, int xOffset, int yOffset, final int width, final int height){
		int width2 = nextPowerOfTwo(width);
		int height2 = nextPowerOfTwo(height);

		if (textureHeight != height2 && textureWidth != width2){
			createTexture(bufferedImage, width2, height2);			
		}
		this.textureHeight = height2;
		this.textureWidth = width2;
		this.scaleFactorWidth = bufferedImage.getWidth() / (float)width2;
		this.scaleFactorHeight = bufferedImage.getHeight() / (float)height2;
		updateTexture(bufferedImage, xOffset, yOffset);		
	}	
	

	private void createTexture(BufferedImage bufferedImage, int width, int height){
		GL2 gl = GLContext.getCurrentGL().getGL2();
		
		int internalFormat = GL2.GL_RGB;
		int inputFormat = GL2.GL_RGB;
		int inputType = GL2.GL_UNSIGNED_BYTE;
		int bpp = 3;
		switch (bufferedImage.getType()) {
		case BufferedImage.TYPE_INT_ARGB:
		case BufferedImage.TYPE_4BYTE_ABGR:
			inputFormat = GL2.GL_RGBA;
			internalFormat = GL2.GL_RGBA;
			bpp = 4;
			break;
			
		case BufferedImage.TYPE_3BYTE_BGR:
		case BufferedImage.TYPE_INT_BGR:			
			inputFormat = GL2.GL_RGB;
			internalFormat = GL2.GL_RGB;
			break;
		case BufferedImage.TYPE_INT_RGB:
			inputFormat = GL2.GL_RGB;
			internalFormat = GL2.GL_RGB;
			break;

		default:
			throw new NotImplementedException();
		}
				
		gl.glEnable(GL2.GL_TEXTURE_2D);			
		gl.glBindTexture(GL2.GL_TEXTURE_2D, textureID);
		
		ByteBuffer b = ByteBuffer.allocate(width*height*bpp);
		b.limit(width*height*bpp);
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, internalFormat, width,
				height, 0, inputFormat, inputType, b);
	}
	
	private void updateTexture(BufferedImage bufferedImage, int xOffset, int yOffset){
		GL2 gl = GLContext.getCurrentGL().getGL2();
		boolean alpha = false;
		boolean switchChannel = false;
		int inputFormat = GL2.GL_RGB;
		int inputType = GL2.GL_UNSIGNED_BYTE;
		switch (bufferedImage.getType()) {
		case BufferedImage.TYPE_4BYTE_ABGR:
			switchChannel = true;
		case BufferedImage.TYPE_INT_ARGB:
			inputFormat = GL2.GL_RGBA;
			alpha = true;			
			break;
			
		case BufferedImage.TYPE_3BYTE_BGR:
		case BufferedImage.TYPE_INT_BGR:			
			switchChannel = true;
			inputFormat = GL2.GL_RGB;
			break;
		case BufferedImage.TYPE_INT_RGB:
			inputFormat = GL2.GL_RGB;
			break;

		default:
			throw new NotImplementedException();
		}
		
		ByteBuffer buffer = readPixels(bufferedImage, alpha, switchChannel);
		
		gl.glEnable(GL2.GL_TEXTURE_2D);			
		gl.glBindTexture(GL2.GL_TEXTURE_2D, this.textureID);
		
		gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, xOffset, yOffset, bufferedImage.getWidth(), bufferedImage.getHeight(),
				inputFormat, inputType, buffer);

		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
				GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
				GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S,
				GL2.GL_CLAMP);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T,
				GL2.GL_CLAMP);
	}
		
	private static void createTexture(ImageRegion imageRegion, int width, int height){
		GL2 gl = GLContext.getCurrentGL().getGL2();
		ByteBuffer b = ByteBuffer.allocate(width * height);
		b.limit(width * height);
		gl.glEnable(GL2.GL_TEXTURE_2D);			
		gl.glBindTexture(GL2.GL_TEXTURE_2D, imageRegion.getTextureID());
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL2.GL_LUMINANCE8, width,
				height, 0, GL2.GL_LUMINANCE, GL2.GL_UNSIGNED_BYTE, b);

		imageRegion.textureWidth = width;
		imageRegion.textureHeight = height;
		
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
				GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
				GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S,
				GL2.GL_CLAMP_TO_BORDER);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T,
				GL2.GL_CLAMP_TO_BORDER);
	}
	
	private static void updateTexture(ImageRegion imageRegion, ByteBuffer byteBuffer, int width, int height){
		GL2 gl = GLContext.getCurrentGL().getGL2();
		
		gl.glEnable(GL2.GL_TEXTURE_2D);			
		
		gl.glPixelStorei(GL2.GL_UNPACK_SKIP_PIXELS, 0);
		gl.glPixelStorei(GL2.GL_UNPACK_SKIP_ROWS, 0);
		gl.glPixelStorei(GL2.GL_UNPACK_ROW_LENGTH, 0);
		gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 8 >> 3);

		gl.glBindTexture(GL2.GL_TEXTURE_2D, imageRegion.getTextureID());
		
		gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0, width, height,
				GL2.GL_ABGR_EXT, GL2.GL_UNSIGNED_BYTE, byteBuffer);

		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
				GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
				GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S,
				GL2.GL_CLAMP);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T,
				GL2.GL_CLAMP);
	}

	private static ByteBuffer readPixels(BufferedImage image, boolean storeAlphaChannel, boolean switchRandBChannel) {
		int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        int factor = storeAlphaChannel ? 4 : 3;
        ByteBuffer buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * factor); //4 for RGBA, 3 for RGB
        
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> (switchRandBChannel?16:0)) & 0xFF));     
                buffer.put((byte) ((pixel >> 8) & 0xFF));      
                buffer.put((byte) ((pixel >> (switchRandBChannel?0:16)) & 0xFF));
                if (storeAlphaChannel){
                	buffer.put((byte) ((pixel >> 24) & 0xFF));
                }
            }
        }
        buffer.flip(); 
        
	    return buffer;
	}
}