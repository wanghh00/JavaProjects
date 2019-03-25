package online;

public class ItemFeature {
	public final static int EMBEDDING_SIZE = 4096;
	
	public long itemId;
	public int category;
	public int part;
	public byte[] embedding;
	public int numOfResults = 100;
	
	public ItemFeature() {
		embedding = new byte[EMBEDDING_SIZE];
	}
	
	public ItemFeature(long itemId, int category, int part) {
		this();
		
		this.itemId = itemId;
		this.category = category;
		this.part = part;
		
	}
}
