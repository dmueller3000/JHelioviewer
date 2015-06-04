package org.helioviewer.jhv.plugins.sdocutoutplugin;

import java.net.URI;
import java.net.URL;

import org.helioviewer.jhv.plugins.plugin.NewPlugin;
import org.helioviewer.jhv.plugins.plugin.UltimatePluginInterface;

public class SDOCutOutPlugin3D extends NewPlugin {

	/**
     * Sets up the visual sub components and the visual part of the component
     * itself.
     **/
    public SDOCutOutToggleButton sdoCutOutToggleButton;
    
    private URI pluginLocation;
    
    
    /**
     * Default constructor
     */
    public SDOCutOutPlugin3D() {
    	UltimatePluginInterface.addButtonToToolbar(new SDOCutOutToggleButton());
    }
    
    public URI getLocation() {
    	return this.pluginLocation;
    }
	
	public void installPlugin() {
		if (sdoCutOutToggleButton == null)
			sdoCutOutToggleButton = new SDOCutOutToggleButton();
		
    }

    public void uninstallPlugin() {
    	if (sdoCutOutToggleButton != null)
    		sdoCutOutToggleButton.removeButton();
    }
    
    public String getName() {
        return "SDOCutOut Plugin";
    }

    public String getAboutLicenseText() {
    	return null;
    }
	
	public static URL getResourceUrl(String name) {
		return SDOCutOutPlugin3D.class.getResource(name);
	}
	
}
