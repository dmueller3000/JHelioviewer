package org.helioviewer.jhv.plugins;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;
import java.time.LocalDateTime;

import javax.annotation.Nullable;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.helioviewer.jhv.base.Settings;
import org.helioviewer.jhv.base.Settings.BooleanKey;
import org.helioviewer.jhv.base.downloadmanager.DownloadManager;
import org.helioviewer.jhv.base.downloadmanager.DownloadPriority;
import org.helioviewer.jhv.base.downloadmanager.HTTPRequest;
import org.helioviewer.jhv.base.math.Vector3d;
import org.helioviewer.jhv.gui.IconBank;
import org.helioviewer.jhv.gui.MainFrame;
import org.helioviewer.jhv.gui.MainPanel;
import org.helioviewer.jhv.gui.actions.ExitProgramAction;
import org.helioviewer.jhv.layers.Layers;
import org.helioviewer.jhv.layers.PluginLayer;
import org.helioviewer.jhv.opengl.RayTrace;
import org.helioviewer.jhv.plugins.hekplugin.HEKPlugin;
import org.helioviewer.jhv.plugins.pfssplugin.PfssPlugin;
import org.helioviewer.jhv.viewmodel.TimeLine;
import org.helioviewer.jhv.viewmodel.TimeLine.TimeLineListener;
import org.json.JSONObject;

public class Plugins implements TimeLineListener, MouseListener, MouseMotionListener
{
	public enum PluginIcon
	{
		REFRESH("refresh_128x128.png"),
		VISIBLE("visible_128x128.png"),
		INVISIBLE("invisible_128x128.png"),
		CANCEL("Cancel_128x128.png");
		
		private final String fname;
		PluginIcon(String _fname)
		{
            fname = _fname;
        }

        String getFilename()
        {
            return fname;
        }

	}

	/** The location of the image files relative to this folder. */
    private static final String RESOURCE_PATH = "/images/";
	
	private final Plugin[] plugins;

	public static final Plugins SINGLETON = new Plugins();
	
	private Plugins()
	{
		TimeLine.SINGLETON.addListener(this);
		
		plugins = new Plugin[]
			{
				new HEKPlugin(),
				new PfssPlugin()
			};
		
		for(Plugin p:plugins)
		{
			final PluginLayer pl=new PluginLayer(p);
			pl.setVisible(Settings.getBoolean(Settings.BooleanKey.PLUGIN_VISIBLE,pl.getName()));
			Layers.addLayer(pl);
			
			ExitProgramAction.addShutdownHook(new Runnable()
			{
				@Override
				public void run()
				{
					Settings.setBoolean(BooleanKey.PLUGIN_VISIBLE, pl.getName(), pl.isVisible());
				}
			});
		}
		
		MainFrame.SINGLETON.MAIN_PANEL.addMouseListener(this);
		MainFrame.SINGLETON.MAIN_PANEL.addMouseMotionListener(this);
	}
	
	public static void addButtonToToolbar(AbstractButton button)
	{
		MainFrame.SINGLETON.TOP_TOOL_BAR.addButton(button);
	}

	public static void addPanelToLeftControllPanel(String title, JPanel panel, boolean startExpanded)
	{
		MainFrame.SINGLETON.LEFT_PANE.add(title, panel, startExpanded);
	}

	@Override
	public void timeStampChanged(LocalDateTime current, LocalDateTime last)
	{
		for (Plugin plugin : plugins)
			plugin.timeStampChanged(current, last);
	}

	@Override
	public void dateTimesChanged(int framecount)
	{
		for (Plugin plugin : plugins)
			plugin.dateTimesChanged(framecount);
	}

	public LocalDateTime getCurrentDateTime()
	{
		return TimeLine.SINGLETON.getCurrentDateTime();
	}

	@Override
	public void mouseDragged(@Nullable MouseEvent e)
	{
		if(e==null)
			return;
		
		Vector3d hitpoint = new RayTrace().cast(e.getX(), e.getY(), MainFrame.SINGLETON.MAIN_PANEL).getHitpoint();
		for (Plugin plugin : plugins)
			plugin.mouseDragged(e, hitpoint);
	}

	@Override
	public void mouseMoved(@Nullable MouseEvent e)
	{
		if(e==null)
			return;
		
		Vector3d hitpoint = new RayTrace().cast(e.getX(), e.getY(), MainFrame.SINGLETON.MAIN_PANEL).getHitpoint();
		for (Plugin plugin : plugins)
			plugin.mouseMoved(e, hitpoint);
	}

	@Override
	public void mouseClicked(@Nullable MouseEvent e)
	{
		if(e==null)
			return;
		
		Vector3d hitpoint = new RayTrace().cast(e.getX(), e.getY(), MainFrame.SINGLETON.MAIN_PANEL).getHitpoint();
		for (Plugin plugin : plugins)
			plugin.mouseClicked(e, hitpoint);
	}

	@Override
	public void mousePressed(@Nullable MouseEvent e)
	{
		if(e==null)
			return;
		
		Vector3d hitpoint = new RayTrace().cast(e.getX(), e.getY(), MainFrame.SINGLETON.MAIN_PANEL).getHitpoint();
		for (Plugin plugin : plugins)
			plugin.mousePressed(e, hitpoint);
	}

	@Override
	public void mouseReleased(@Nullable MouseEvent e)
	{
		if(e==null)
			return;
		
		Vector3d hitpoint = new RayTrace().cast(e.getX(), e.getY(), MainFrame.SINGLETON.MAIN_PANEL).getHitpoint();
		for (Plugin plugin : plugins)
			plugin.mouseReleased(e, hitpoint);
	}

	@Override
	public void mouseEntered(@Nullable MouseEvent e)
	{
		if(e==null)
			return;
		
		Vector3d hitpoint = new RayTrace().cast(e.getX(), e.getY(), MainFrame.SINGLETON.MAIN_PANEL).getHitpoint();
		for (Plugin plugin : plugins)
			plugin.mouseEntered(e, hitpoint);
	}

	@Override
	public void mouseExited(@Nullable MouseEvent e)
	{
		if(e==null)
			return;
		
		Vector3d hitpoint = new RayTrace().cast(e.getX(), e.getY(), MainFrame.SINGLETON.MAIN_PANEL).getHitpoint();
		for (Plugin plugin : plugins)
			plugin.mouseExited(e, hitpoint);
	}

	public static void setCursor(Cursor cursor)
	{
		MainFrame.SINGLETON.MAIN_PANEL.setCursor(cursor);
	}

	public static Cursor getCursor()
	{
		return MainFrame.SINGLETON.MAIN_PANEL.getCursor();
	}

	public static Point mainPanelGetLocationOnScreen()
	{
		return MainFrame.SINGLETON.MAIN_PANEL.getLocationOnScreen();
	}

	public static Dimension mainPanelGetSize()
	{
		return MainFrame.SINGLETON.MAIN_PANEL.getSize();
	}

	public static @Nullable LocalDateTime getStartDateTime()
	{
		return TimeLine.SINGLETON.getFirstDateTime();
	}

	public static @Nullable LocalDateTime getEndDateTime()
	{
		return TimeLine.SINGLETON.getLastDateTime();
	}

	public static Dimension getMainPanelSize() 
	{
		return MainFrame.SINGLETON.MAIN_PANEL.getSize();
	}

	public static double getViewPortSize()
	{
		return MainFrame.SINGLETON.MAIN_PANEL.getTranslationCurrent().z * Math.tan(MainPanel.FOV / 2) * 2;
	}

	public static void repaintMainPanel()
	{
		MainFrame.SINGLETON.MAIN_PANEL.repaint();
	}

	public static HTTPRequest generateAndStartHTPPRequest(String uri, DownloadPriority priority)
	{
		HTTPRequest httpRequest = new HTTPRequest(uri, priority);
		DownloadManager.addRequest(httpRequest);
		return httpRequest;
	}

	public void storeConfiguration(JSONObject jsonPlugins)
	{
		//FIXME: save layer visibility state, expanded/collapsed panel state, ...
		
		for (Plugin plugin : plugins)
			plugin.storeConfiguration(jsonPlugins);
	}

	public void restoreConfiguration(JSONObject jsonPlugins)
	{
		for (Plugin plugin : plugins)
			plugin.restoreConfiguration(jsonPlugins);
	}
	
	/*public static void setPanelOpenCloseState(Component component, boolean open)
	{
		if (open)
			MainFrame.SINGLETON.LEFT_PANE.expand(component);
		else
			MainFrame.SINGLETON.LEFT_PANE.collapse(component);

		MainFrame.SINGLETON.LEFT_PANE.revalidate();
		component.repaint();
	}*/
	
	public static ImageIcon getIcon(PluginIcon icon, int width, int height)
	{
        URL imgURL = IconBank.class.getResource(RESOURCE_PATH + icon.getFilename());
        ImageIcon imageIcon = new ImageIcon(imgURL);
        Image image = imageIcon.getImage();
        image = image.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
        imageIcon.setImage(image);
        return imageIcon;	
    }
	
	public static void removePanelOnLeftControllPanel(JPanel jPanel)
	{
		MainFrame.SINGLETON.LEFT_PANE.remove(jPanel);
	}

	public static void repaintLayerPanel()
	{
		MainFrame.SINGLETON.LAYER_PANEL.updateData();
	}

	@Override
	public void isPlayingChanged(boolean _isPlaying)
	{
	}
}
