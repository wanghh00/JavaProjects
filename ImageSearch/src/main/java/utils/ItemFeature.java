package utils;

public class ItemFeature {
	public final static int EMBEDDING_SIZE = 4096;
	
	public long itemId;
	public int category;
	public byte[] embedding;
	
	public ItemFeature() {
		embedding = new byte[EMBEDDING_SIZE];
	}

}
