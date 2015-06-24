package org.helioviewer.jhv.layers;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import org.helioviewer.jhv.base.ImageRegion;
import org.helioviewer.jhv.base.math.Vector2d;
import org.helioviewer.jhv.gui.opengl.MainPanel;
import org.helioviewer.jhv.opengl.raytrace.RayTrace;
import org.helioviewer.jhv.viewmodel.metadata.MetaData;

public class LayerRayTrace{
	private RayTrace rayTrace;
	
	private static final int MAX_X_POINTS = 11;
	private static final int MAX_Y_POINTS = 11;
	
	private LayerInterface layer;
	
	public LayerRayTrace(LayerInterface layer) {
		this.layer = layer;
		rayTrace = new RayTrace();
		/*contentPanel.setBackground(Color.BLACK);
		frame.setContentPane(contentPanel);
		frame.setBounds(50, 50, 640, 480);
		frame.setVisible(true);
		*/
	}
	
	public ImageRegion getCurrentRegion(MainPanel mainPanel, MetaData metaData){
		return getCurrentRegion(mainPanel, metaData, mainPanel.getCanavasSize());
	}
	
	public ImageRegion getCurrentRegion(MainPanel mainPanel, MetaData metaData, Dimension size){
		/*if (!(compenentView instanceof OverViewPanel)){
			contentPanel.removeAll();
			contentPanel.setLayout(null);
		}*/
		double partOfWidth = mainPanel.getWidth() / (double)(MAX_X_POINTS-1);
		double partOfHeight = mainPanel.getHeight() / (double)(MAX_Y_POINTS-1);
		
		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
		
		for (int i = 0; i < MAX_X_POINTS; i++){
			for (int j = 0; j < MAX_Y_POINTS; j++){
				Vector2d imagePoint = rayTrace.castTexturepos((int)(i * partOfWidth), (int)(j * partOfHeight), metaData, mainPanel);
				
				if (imagePoint != null){
					/*JPanel panel = null;
					if (!(compenentView instanceof OverViewPanel)){

				panel = new JPanel();
				panel.setBackground(Color.YELLOW);
					}*/
				minX = Math.min(minX, imagePoint.x);
				maxX = Math.max(maxX, imagePoint.x);
				minY = Math.min(minY, imagePoint.y);
				maxY = Math.max(maxY, imagePoint.y);
				
				//if (!(compenentView instanceof OverViewPanel)){
				//panel.setBounds((int) (imagePoint.x * contentPanel.getWidth()) - 3,(int) (imagePoint.y * contentPanel.getHeight()) - 3, 5, 5);
				//contentPanel.add(panel);}
				}
			}
		}
		//frame.repaint();
		
		
		Rectangle2D rectangle = new Rectangle2D.Double(minX, minY, maxX-minX, maxY-minY);
		ImageRegion imageRegion = new ImageRegion(layer.getTime());
		imageRegion.setImageData(rectangle);
		imageRegion.calculateScaleFactor(layer, mainPanel, metaData, size);
		return imageRegion;
		//frame.repaint();
		//frame.setVisible(true);
	}	
}