package offlinetests;

import org.apache.log4j.Logger;
import org.junit.Test;

import offline.RawDataFile;
import online.ItemComparator;
import online.ItemFeature;

public class RawDataFileTests {
	static final Logger LOG = Logger.getLogger(RawDataFileTests.class);
	
	public static void walkThroughBinFile(String path, boolean enableMmap) {
		RawDataFile datafile = new RawDataFile(path);
		if (enableMmap) datafile.enableMmapMode();
		
		ItemFeature feature = new ItemFeature();
		
		ItemFeature src = new ItemFeature();
		datafile.getItemFeature(10, src);
		datafile.reset();
		
		ItemComparator.Compare comp = new ItemComparator.HammingDist();
		
		long start = System.currentTimeMillis();
		int idx = 0;
		float max = 0.0f;
		
		while (datafile.hasNext()) {
			datafile.nextItemFeature(feature);
			float sim = comp.similarity(src.embedding, feature.embedding);
			max = sim > max ? sim : max;
			idx++;
		
			if (idx % 10000 == 0) {
				// LOG.info(String.format("%s %s %s", idx, one.get("itemid"), one.get("category")));
				LOG.info(String.format("Sim: %s Max: %s", sim, max));
			}
		}
		LOG.info("Running time: " + (System.currentTimeMillis() - start));
		datafile.close();
	}
	
	@Test
	public void testWalkThroughBinFileWithoutMMap() {
		String path = "/Users/hongwang/Downloads/cvswedb_1_0.bin";
		walkThroughBinFile(path, false);
	}
	
	@Test
	public void testWalkThroughBinFileWithMMap() {
		String path = "/Users/hongwang/Downloads/cvswedb_1_0.bin";
		walkThroughBinFile(path, true);
	}
}
