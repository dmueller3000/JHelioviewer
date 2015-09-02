package org.helioviewer.jhv.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.helioviewer.jhv.gui.MainFrame;

public class SetCameraRotationInteractionAction extends AbstractAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1199107637545677948L;
	
	public SetCameraRotationInteractionAction() {
		super("Rotate");
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		MainFrame.MAIN_PANEL.activateRotationInteraction();
		MainFrame.OVERVIEW_PANEL.activateRotationInteraction();
	}

}
