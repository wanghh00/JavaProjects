package onlinetests;

import java.util.Arrays;
import java.util.PriorityQueue;

import org.apache.log4j.Logger;
import org.junit.Test;

import online.ItemSearchResult;

public class ItemSearchResultTests {
	static final Logger LOG = Logger.getLogger(ItemSearchResultTests.class);
	
	@Test
	public void testCompare() {
		PriorityQueue<ItemSearchResult> queue = new PriorityQueue<ItemSearchResult>(3);
		ItemSearchResult[] list = { new ItemSearchResult(0.1f), new ItemSearchResult(0.21f), new ItemSearchResult(0.41f),
				new ItemSearchResult(1.1f), new ItemSearchResult(2.1f), new ItemSearchResult(3.1f) };
		
		for (ItemSearchResult one : list) {
			queue.offer(one);
			if (queue.size() > 3) {
				queue.poll();
			}
		}
		Object[] outlist = queue.toArray();
		Arrays.sort(outlist);
				
		for (Object ret : outlist) {
			System.out.println(((ItemSearchResult)ret).sim);
		}
		
	}
}
