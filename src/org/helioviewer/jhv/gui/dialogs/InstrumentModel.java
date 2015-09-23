package org.helioviewer.jhv.gui.dialogs;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.SwingUtilities;

import org.helioviewer.jhv.Settings;
import org.helioviewer.jhv.base.AlphanumComparator;
import org.helioviewer.jhv.base.downloadmanager.AbstractDownloadRequest;
import org.helioviewer.jhv.base.downloadmanager.DownloadPriority;
import org.helioviewer.jhv.base.downloadmanager.HTTPRequest;
import org.helioviewer.jhv.base.downloadmanager.UltimateDownloadManager;
import org.helioviewer.jhv.layers.Layers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//FIXME: morph into general data object, not dialog-specific 
public class InstrumentModel
{
	//FIXME: move to JHVGlobals
	private static final String URL_DATASOURCE = "http://api.helioviewer.org/v2/getDataSources/?";
	private static TreeMap<String, Observatory> observatories = new TreeMap<String, InstrumentModel.Observatory>(new AlphanumComparator()); 
	private static final ArrayList<Runnable> updateListeners = new ArrayList<Runnable>(); 
	
	public static void addUpdateListener(Runnable _listener)
	{
		updateListeners.add(_listener);
	}
	
	static
	{
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				final HTTPRequest httpRequest = new HTTPRequest(URL_DATASOURCE, DownloadPriority.HIGH, AbstractDownloadRequest.INFINITE_TIMEOUT);
				UltimateDownloadManager.addRequest(httpRequest);

				
				//FIXME: add error handling, in case download fails
				try
				{
					final String json = httpRequest.getDataAsString();
					
					SwingUtilities.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							try
							{
								addObservatories(new JSONObject(json));
								
								if (Boolean.parseBoolean(Settings.getProperty("startup.loadmovie")))
								{
									Filter instrument = observatories.get("SDO").filters.get("AIA").filters.get("171");
									LocalDateTime start = instrument.end.minusDays(1);
									Layers.addLayer(instrument.sourceId, start, instrument.end, 1728, "AIA 171");
								}
								
								for(Runnable ul:updateListeners)
									ul.run();
							}
							catch (JSONException e)
							{
								e.printStackTrace();
							}
						}
					});
				}
				catch (InterruptedException | IOException _e)
				{
					//FIXME: add proper error handling here, should retry etc.
					_e.printStackTrace();
				}
			}
		}, "MODEL_LOAD");
		thread.setDaemon(true);
		thread.start();
	}

	public static Collection<Observatory> getObservatories()
	{
		return observatories.values();
	}

	private static void addObservatories(JSONObject _observatories)
	{
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = _observatories.sortedKeys();
		while (iterator.hasNext())
		{
			String observatoryName = iterator.next();
			Observatory observatory = new Observatory(observatoryName);
			try
			{
				JSONObject jsonObservatory = _observatories.getJSONObject(observatoryName);
				addFilter(jsonObservatory, observatory);
			}
			catch (JSONException _e)
			{
				_e.printStackTrace();
			}
			observatories.put(observatoryName, observatory);
		}
	}
	
	private static void addFilter(JSONObject jsonFilter, Observatory observatory)
	{
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = jsonFilter.sortedKeys();
		while (iterator.hasNext())
		{
			String filterName = iterator.next();
			try
			{
				JSONObject jsonFilter1 = jsonFilter.getJSONObject(filterName);
				Filter detector1 = new Filter(filterName);
				observatory.addFilter(filterName, detector1);
				addFilter(jsonFilter1, detector1, observatory);
			}
			catch (Exception e)
			{
				addUILabel(jsonFilter, observatory);
				return;
			}
		}
	}
	
	private static void addFilter(JSONObject jsonFilter, Filter filter, Observatory observatory)
	{
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = jsonFilter.sortedKeys();
		while (iterator.hasNext())
		{
			String filterName = iterator.next();
			try
			{
				JSONObject jsonFilter1 = jsonFilter.getJSONObject(filterName);
				Filter detector1 = new Filter(filterName);
				filter.addFilter(filterName, detector1);
				addFilter(jsonFilter1, detector1, observatory);
			}
			catch (Exception e)
			{
				addUILabel(jsonFilter, observatory);
				addData(jsonFilter, filter);
				return;
			}
		}
	}

	private static void addUILabel(JSONObject jsonObject, Observatory observatory)
	{
		JSONArray uiLabels;
		try
		{
			ArrayList<String> uiLabel = new ArrayList<String>();
			uiLabels = (JSONArray) jsonObject.get("uiLabels");
			for (int i = 1; i < uiLabels.length(); i++)
			{
				JSONObject obj = (JSONObject) uiLabels.get(i);
				uiLabel.add(obj.getString("label"));
				observatory.uiLabels = uiLabel;
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	private static void addData(JSONObject jsonObject, Filter filter)
	{
		try
		{
			DateTimeFormatter reader = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String end = jsonObject.getString("end");
			String start = jsonObject.getString("start");
			if (start != null && end != null && end != "null" && start != "null")
			{
				LocalDateTime endDateTime = LocalDateTime.parse(end, reader);
				LocalDateTime startDateTime = LocalDateTime.parse(start, reader);
				filter.start = startDateTime;
				filter.end = endDateTime;
			}
			//filter.layeringOrder = jsonObject.getInt("layeringOrder");
			filter.nickname = (String) jsonObject.getString("nickname");
			filter.sourceId = jsonObject.getInt("sourceId");
		}
		catch (JSONException e)
		{
		}
	}

	static class Observatory
	{
		private TreeMap<String, Filter> filters = new TreeMap<String, Filter>(new AlphanumComparator());
		private String name;
		private ArrayList<String> uiLabels;

		private Observatory(String name) {
			this.name = name;
		}

		private void addFilter(String name, Filter filter) {
			filters.put(name, filter);
		}

		public Collection<Filter> getInstruments() {
			return this.filters.values();
		}

		public ArrayList<String> getUiLabels() {
			return uiLabels;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	static class Filter
	{
		private TreeMap<String, Filter> filters = new TreeMap<String, InstrumentModel.Filter>(new AlphanumComparator());
		private String name;
		
		private LocalDateTime start;
		private LocalDateTime end;
		private String nickname;
		int sourceId;

		private Filter(String name) {
			this.name = name;
		}

		private void addFilter(String name, Filter filter) {
			filters.put(name, filter);
		}

		public Collection<Filter> getFilters() {
			return this.filters.values();
		}

		@Override
		public String toString() {
			return this.name;
		}
		
		public LocalDateTime getStart(){
			return start;
		}
		
		public LocalDateTime getEnd(){
			return end;
		}
		
		public String getNickname(){
			return this.nickname;
		}
	}
}