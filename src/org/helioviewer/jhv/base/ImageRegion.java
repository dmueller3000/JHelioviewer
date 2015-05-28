package org.helioviewer.jhv.base;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.time.LocalDateTime;

import org.helioviewer.jhv.base.math.Vector2d;
import org.helioviewer.jhv.base.math.Vector2i;
import org.helioviewer.jhv.gui.components.newComponents.MainFrame;
import org.helioviewer.jhv.layers.LayerInterface;
import org.helioviewer.jhv.opengl.OpenGLHelper;
import org.helioviewer.jhv.viewmodel.metadata.MetaData;
import org.helioviewer.jhv.viewmodel.view.opengl.MainPanel;

public class ImageRegion {
	
	// relative image coordinates
	private Rectangle2D imageData;
	// image size, which have been decoded
	private Rectangle imageSize;
	
	private float imageScaleFactor;
	private double imageZoomFactor;

	private float textureOffsetX = 0;
	private float textureOffsetY = 0;
	private float textureScaleWidth = 1;
	private float textureScaleHeight = 1;

	private LocalDateTime localDateTime;
	
	private int textureID;
	
	public int textureWidth;
	public int textureHeight;

	private float xTextureScale = 1;
	private float yTextureScale = 1;
	
	private MetaData metaData;
	
	
	public ImageRegion(LocalDateTime localDateTime) {
		imageData = new Rectangle();
		this.localDateTime = localDateTime;
	}
			
	public void setImageData(Rectangle2D imageData){
		this.imageData = imageData;
	}
	
	public Rectangle2D getImageData(){
		return this.imageData;
	}

	public boolean contains(ImageRegion imageRegion) {
		return imageSize.contains(imageRegion.imageSize);
	}

	public double getScaleFactor(){
		return this.imageScaleFactor;
	}
	
	public boolean compareScaleFactor(ImageRegion imageRegion) {
		return this.imageScaleFactor >= imageRegion.getScaleFactor();
	}

	public void calculateScaleFactor(LayerInterface layerInterface, MainPanel compenentView, MetaData metaData) {
		Vector2i resolution = metaData.getResolution();
		
		int textureMaxX = getNextInt(resolution.getX() * (imageData.getX() + imageData.getWidth()));
		int textureMaxY = getNextInt(resolution.getY() * (imageData.getY() + imageData.getHeight()));
		int textureMinX = (int)(resolution.getX() * imageData.getX());
		int textureMinY = (int)(resolution.getY() * imageData.getY());
		int textureWidth = textureMaxX - textureMinX;
		int textureHeight = textureMaxY - textureMinY;
		
		double texleCount = textureWidth * textureHeight;
		Dimension canvasSize = MainFrame.MAIN_PANEL.getCanavasSize();
		
		
		
		double z = metaData.getPhysicalImageWidth() / Math.tan(Math.toRadians(MainPanel.FOV));
		double zoom = compenentView.getTranslation().z / z;
		int screenMaxX = getNextInt(canvasSize.getWidth() * (imageData.getX() + imageData.getWidth()) /  zoom);
		int screenMaxY = getNextInt(canvasSize.getHeight() * (imageData.getY() + imageData.getHeight()) / zoom);
		int screenMinX = (int)(canvasSize.getWidth() * imageData.getX() / zoom);
		int screenMinY = (int)(canvasSize.getHeight() * imageData.getY() / zoom);
		int screenWidth = screenMaxX - screenMinX;
		int screenHeight = screenMaxY - screenMinY;
		double pixelCount = screenWidth * screenHeight;

		this.imageZoomFactor = pixelCount >= texleCount ? 1 : Math.sqrt((pixelCount / texleCount));
		this.imageScaleFactor = imageZoomFactor >= 1.0 ? 1 : nextZoomFraction(imageZoomFactor);
		
		imageSize = new Rectangle(textureMinX, textureMinY, textureWidth, textureHeight);
		
		this.calculateImageSize(resolution);
		
	}	
	
	public float getTextureOffsetX(){
		return textureOffsetX;
	}
	
	public float getTextureOffsetY(){
		return textureOffsetY;
	}
	
	public float getTextureScaleWidth(){
		return textureScaleWidth * xTextureScale;
	}
	
	public float getTextureScaleHeight(){
		return textureScaleHeight * yTextureScale;
	}
	
	private void calculateImageSize(Vector2i resolution){
		//Math.ceil(resolution);
		int textureMaxX = getNextInt(resolution.getX() * (imageData.getX() + imageData.getWidth()) * imageScaleFactor);
		int textureMaxY = getNextInt(resolution.getY() * (imageData.getY() + imageData.getHeight()) * imageScaleFactor);
		int textureMinX = (int)(resolution.getX() * imageData.getX() * imageScaleFactor);
		int textureMinY = (int)(resolution.getY() * imageData.getY() * imageScaleFactor);
		int textureWidth = textureMaxX - textureMinX;
		int textureHeight = textureMaxY - textureMinY;
		this.imageSize = new Rectangle(textureMinX, textureMinY, textureWidth, textureHeight);
		
		this.textureOffsetX = textureMinX / (float)(resolution.getX() * imageScaleFactor);
		this.textureOffsetY = textureMinY / (float)(resolution.getY() * imageScaleFactor);
		this.textureScaleWidth = imageSize.width / (float)(resolution.getX() * imageScaleFactor);
		this.textureScaleHeight = imageSize.height / (float)(resolution.getY() * imageScaleFactor);
	}
	
	public Rectangle getImageSize(){
		return imageSize;
	}
	
	private static int getNextInt(double d){
		return d % 1 == 0 ? (int) d : (int)(d - d % 1) + 1;
	}
	
	public float getZoomFactor(){
		return imageScaleFactor;
	}
	
	private static float nextZoomFraction(double zoomFactor){
		int powerOfTwo = OpenGLHelper.nextPowerOfTwo(getNextInt((1/zoomFactor)));
		powerOfTwo >>= 1;
		return 1/(float)powerOfTwo;
	}
	

	public LocalDateTime getDateTime() {
		return this.localDateTime;
	}

	public void setLocalDateTime(LocalDateTime currentDateTime) {
		this.localDateTime = currentDateTime;
	}	
	
	public int getTextureID(){
		return this.textureID;
	}
	
	public void setTextureID(int textureID){
		this.textureID = textureID;
	}

	public void setTextureScaleFactor(float xScale, float yScale) {
		this.xTextureScale = xScale;
		this.yTextureScale = yScale;
	}
	
	public void setMetaData(MetaData metaData){
		this.metaData = metaData;
	}
	
	public MetaData getMetaData(){
		return metaData;
	}
}

