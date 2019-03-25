package online;

import offline.RawDataFile;

public class ItemSearchTask {
	public RawDataFile datafile;
	public ItemFeature src;
	public ItemComparator.Compare comp;
	public int numResult;
	
	public ItemSearchTask(RawDataFile datafile, ItemFeature src, ItemComparator.Compare comp, int numResult) {
		this.datafile = datafile;
		this.src = src;
		this.comp = comp;
		this.numResult = numResult;
	}
}
