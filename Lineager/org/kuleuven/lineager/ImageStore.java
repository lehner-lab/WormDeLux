package org.kuleuven.lineager;

import ij.ImagePlus;

import java.util.Collection;
import java.util.Map;

import org.kuleuven.collections.SortedListMap;
import org.kuleuven.storecaching.StoreMapCaching;
import org.kuleuven.utilities.ImageUtilities;

public class ImageStore extends StoreMapCaching<TimeAndPlanePair, ImagePlus> {

	private SortedListMap<Integer, SortedListMap<Integer, String>> map;
	private String directory;

	public ImageStore(String directory, SortedListMap<Integer, SortedListMap<Integer, String>> map) {
		this.map = map;
		this.directory = directory;
	}

	@Override
	public int size() {
		if (map != null && map.size() > 0) {
			SortedListMap<Integer, String> first = map.iterator().next();
			return map.size() * first.size();
		}
		return 0;
	}

	@Override
	protected ImagePlus getEntryFromStoreWithID(TimeAndPlanePair id) {
		if (map.containsKey(id.time)) {
			SortedListMap<Integer, String> time = map.get(id.time);
			if (time.containsKey(id.plane)) {
				return ImageUtilities.openImage(directory + time.get(id.plane));
			}
		}
		return null;
	}

	@Override
	protected Map<TimeAndPlanePair, ImagePlus> getEntriesFromStoreWithIDs(
			Collection<TimeAndPlanePair> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setEntryInStore(TimeAndPlanePair id, ImagePlus value) {
		// TODO Auto-generated method stub

	}

}
