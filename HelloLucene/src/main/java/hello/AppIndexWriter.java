package hello;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class AppIndexWriter {
	private static IndexWriter writer;
	
	private AppIndexWriter() {		
	}
	
	public static void createIndexWriter(Directory path, IndexWriterConfig config) throws IOException {
		if (writer == null) {
			synchronized (AppIndexWriter.class) {
				if (writer == null) {
					writer = new IndexWriter(path, config);
				}
			}
		}
	}
	
	public static IndexWriter getInstance() {
		return writer;
	}
	
	public static void main(String[] args) throws IOException {
		String indexPath = "/tmp/luceneidx";
        // 1. create the index
        Directory path = new RAMDirectory();
        
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        
		AppIndexWriter.createIndexWriter(path, config);
		AppIndexWriter.getInstance();
		
		
	}

}
