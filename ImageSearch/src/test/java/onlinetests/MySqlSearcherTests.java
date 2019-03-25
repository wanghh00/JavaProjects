package onlinetests;

import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import offline.RawDataFile;
import online.MySqlSearcher;
import utils.ByteUtils;

public class MySqlSearcherTests {
	static final Logger LOG = Logger.getLogger(MySqlSearcherTests.class);

	@Test
	public void testGetItemMeta() {
		long itemid = 302329317684L;
		Map<String, Object> ret = MySqlSearcher.getItemMeta(itemid);
		LOG.info(ret);
		
		RawDataFile file = new RawDataFile((int) ret.get("part"), (int) ret.get("category"));
		Map<String, Object> fileret = file.get((long) ret.get("offset"));
		
		byte[] embedding = (byte[]) fileret.get("embedding");
		
		LOG.info(ByteUtils.bytesToHex(embedding));
		LOG.info(fileret);
		
		file.close();
	}
}
