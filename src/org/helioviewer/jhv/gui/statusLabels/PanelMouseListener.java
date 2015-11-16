package org.helioviewer.jhv.gui.statusLabels;

import org.helioviewer.jhv.opengl.RayTrace.Ray;

import java.awt.event.MouseEvent;

public interface PanelMouseListener
{
	void mouseExited();
	void mouseMoved(MouseEvent e, Ray ray);
}