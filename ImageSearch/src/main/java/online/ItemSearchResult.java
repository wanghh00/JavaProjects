package online;

import java.util.Arrays;
import java.util.PriorityQueue;

public class ItemSearchResult implements Comparable<ItemSearchResult> {
	public float sim;
	public long itemId;
	
	public ItemSearchResult() {
		sim = 0.0f;
	}
	
	public ItemSearchResult(long itemId, float sim) {
		this.itemId = itemId;
		this.sim = sim;
	}
	
	public ItemSearchResult(float sim) {
		this.sim = sim;
	}
	
	@Override
	public int compareTo(ItemSearchResult o) {
		return sim < o.sim ? -1 : 1;
	}
	
	public static void main(String[] args) {
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
		
//		queue.add(new ItemSearchResult(0.1f));
//		queue.add(new ItemSearchResult(0.2f));
//		queue.add(new ItemSearchResult(0.4f));
//		queue.add(new ItemSearchResult(1.1f));
//		queue.add(new ItemSearchResult(0.34f));
//		queue.add(new ItemSearchResult(0.11f));
//		queue.add(new ItemSearchResult(2.1f));
//		queue.add(new ItemSearchResult(4.1f));
				
		for (Object ret : outlist) {
			System.out.println(((ItemSearchResult)ret).sim);
		}
		
	}


}
