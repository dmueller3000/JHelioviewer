package org.helioviewer.jhv.viewmodel.view.jp2view.newjpx;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedDeque;

import kdu_jni.KduException;

import org.helioviewer.jhv.layers.CacheableImageData;

public class Cache {
	private static ConcurrentLinkedDeque<CacheableImageData> ramCache;

	static {
		ramCache = new ConcurrentLinkedDeque<CacheableImageData>();
	}

	public static void addCacheElement(CacheableImageData cacheableImageData) {
		ramCache.add(cacheableImageData);
	}

	public static CacheableImageData getCacheElement(int id,
			LocalDateTime currentDate) {
		for (CacheableImageData cacheableImageData : ramCache) {
			if (cacheableImageData.contains(id, currentDate)) {
				return cacheableImageData;

			}
		}
		return null;

	}
}