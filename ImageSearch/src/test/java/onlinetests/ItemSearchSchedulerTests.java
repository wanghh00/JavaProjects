package onlinetests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

import org.apache.log4j.Logger;

import offline.RawDataFile;
import online.ItemComparator;
import online.ItemFeature;
import online.ItemSearchResult;
import online.ItemSearchScheduler;
import online.ItemSearchTask;

public class ItemSearchSchedulerTests {
	static final Logger LOG = Logger.getLogger(ItemSearchSchedulerTests.class);
	
	
	public void testSearchInParallel() {
		String path = "/Users/hongwang/Downloads/cvswedb_0_0.bin";
		String path1 = "/Users/hongwang/Downloads/cvswedb_1_0.bin";
		String path2 = "/Users/hongwang/Downloads/cvswedb_2_0.bin";

		RawDataFile datafile = new RawDataFile(path);
		datafile.enableMmapMode();
		RawDataFile datafile1 = new RawDataFile(path1);
		datafile1.enableMmapMode();
		RawDataFile datafile2 = new RawDataFile(path2);
		datafile2.enableMmapMode();

		ItemFeature src = new ItemFeature();
		datafile.getItemFeature(1001, src);

		ItemComparator.Compare comp = ItemComparator.HammingDist.getInstance();

		List<ItemSearchTask> searchTasks = new ArrayList<ItemSearchTask>();
		searchTasks.add(new ItemSearchTask(datafile, src, comp, 100));
		searchTasks.add(new ItemSearchTask(datafile1, src, comp, 100));
		searchTasks.add(new ItemSearchTask(datafile2, src, comp, 100));

		Optional<PriorityQueue<ItemSearchResult>> results = searchTasks.parallelStream()
				.map(task -> ItemSearchScheduler.mapItemSearchTask(task))
				.reduce((ret1, ret2) -> ItemSearchScheduler.reduceItemSearchTask(ret1, ret2));
		
		Object[] outlist = results.get().toArray();
		Arrays.sort(outlist);

		for (Object ret : outlist) {
			System.out.println(((ItemSearchResult) ret).sim);
		}

		// ForkJoinPool threadPool = new ForkJoinPool(4);
	}

}
