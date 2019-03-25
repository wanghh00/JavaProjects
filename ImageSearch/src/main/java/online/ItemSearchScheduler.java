package online;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.apache.log4j.Logger;

import offline.RawDataFile;

public class ItemSearchScheduler implements AutoCloseable {
	static final Logger LOG = Logger.getLogger(ItemSearchScheduler.class);
	
	private List<List<RawDataFile>> cat2dataList = new ArrayList<List<RawDataFile>>();
	
	public ItemSearchScheduler() {
		for (int cat = 0; cat < 4; cat++) {
			List<RawDataFile> lst = new ArrayList<RawDataFile>();
			
			for (int part=0; part < 3; part++) {
				RawDataFile datafile = new RawDataFile(part, cat);
				datafile.enableMmapMode();
				lst.add(datafile);
			}
			cat2dataList.add(lst);
		}
	}
	
	public ItemFeature getItemFeature(long itemId) {
		Map<String, Object> ret = MySqlSearcher.getItemMeta(itemId);
		int category = (int)ret.get("category");
		int part = (int)ret.get("part");
		
		ItemFeature feature = new ItemFeature((long) ret.get("itemid"), category, part);
		RawDataFile datafile = cat2dataList.get(category).get(part);
		datafile.getItemFeature((long)ret.get("offset"), feature);
		
		return feature;
	}
	
	public List<ItemSearchTask> genSearchTasks(ItemFeature feature) {
		List<ItemSearchTask> searchTasks = new ArrayList<ItemSearchTask>();
		ItemComparator.Compare comp = ItemComparator.HammingDist.getInstance();
		
		for (RawDataFile datafile : cat2dataList.get(feature.category)) {
			searchTasks.add(new ItemSearchTask(datafile, feature, comp, feature.numOfResults));
		}
		return searchTasks;
		
	}
	
	public static PriorityQueue<ItemSearchResult> mapItemSearchTask(ItemSearchTask task) {
		long start = System.currentTimeMillis();
		ItemFeature feature = new ItemFeature();
		
		PriorityQueue<ItemSearchResult> queue = new PriorityQueue<ItemSearchResult>(task.numResult + 1);
		
		while (task.datafile.hasNext()) {
			task.datafile.nextItemFeature(feature);
			float tmp = task.comp.similarity(task.src.embedding, feature.embedding);
			ItemSearchResult ret = new ItemSearchResult(feature.itemId, tmp);
			
			queue.offer(ret);
			if (queue.size() > task.numResult) {
				queue.poll();
			}
		}
		task.datafile.reset();
		
		LOG.info(Thread.currentThread().getName() + " Running time: " + (System.currentTimeMillis() - start));
		int x = 0;
		for (Object one : queue.toArray()) {
			ItemSearchResult result = (ItemSearchResult) one;
			LOG.info(String.format("%s %s %s %s", Thread.currentThread().getName(), x, result.itemId, result.sim));
			
			x++;
			if (x == 5) break;
		}
		
		return queue;
	}
	
	public static PriorityQueue<ItemSearchResult> reduceItemSearchTask(PriorityQueue<ItemSearchResult> q1,
			PriorityQueue<ItemSearchResult> q2) {
		int limit = q1.size();
		for (ItemSearchResult one : q2) {
			q1.offer(one);
			if (q1.size() > limit)
				q1.poll();
		}
		return q1;
	}
	
	@Override
	public void close() throws Exception {
		LOG.info("Close all RawDataFiles...");
		
		for (List<RawDataFile> lst : cat2dataList) {
			for (RawDataFile datafile : lst) {
				datafile.close();
			}
		}
	}
}
