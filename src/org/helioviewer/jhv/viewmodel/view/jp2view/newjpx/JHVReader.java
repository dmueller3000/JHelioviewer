package org.helioviewer.jhv.viewmodel.view.jp2view.newjpx;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.concurrent.FutureTask;

public interface JHVReader {
	public FutureTask<JHVCachable> getData(LocalDateTime[] frameDateTimes);
	
	
}