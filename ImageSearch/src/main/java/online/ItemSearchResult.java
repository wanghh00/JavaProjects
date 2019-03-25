package online;

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
}
