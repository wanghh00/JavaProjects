package hello;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.DocStats;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class FileIndex {
	static final String INDEX_DIR = "/tmp/luceneidx";

	private DocStats index(Path indexDir, Path dataDir) throws IOException {
		StandardAnalyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		Directory path = FSDirectory.open(indexDir);

		AppIndexWriter.createIndexWriter(path, config);
		IndexWriter writer = AppIndexWriter.getInstance();

		return writer.getDocStats();

	}

	private void indexDirectory(IndexWriter writer, Path dataDir) {

	}

//	private void indexFileWithIndexWriter(Path file, String suffix) throws IOException {
//		File f = 
//		if (f.isHidden() || f.isDirectory() || !f.canRead() || !f.exists()) {
//			return;
//		}
//		if (suffix != null && !f.getName().endsWith(suffix)) {
//			return;
//		}
//		System.out.println("Indexing file:... " + f.getCanonicalPath());
//		Document doc = new Document();
//		doc.add(new Field("contents", new FileReader(f)));
//		doc.add(new Field("filename", f.getCanonicalPath(), Field.Store.YES, Field.Index.ANALYZED));
//		indexWriter.addDocument(doc);
//	}

	private static Document path2Doc(Path file) {
		Document doc = null;
		File f = file.toFile();
		if (f.isHidden() || f.isDirectory() || !f.canRead() || !f.exists()) {
			return doc;
		}
		
//		if (suffix != null && !f.getName().endsWith(suffix)) {
//			return null;
//		}
		
		try {			
			doc = new Document();
			Field contentField = new TextField("contents", new FileReader(f));
			doc.add(contentField);
			doc.add(new TextField("filename", f.getCanonicalPath(), Field.Store.YES));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}

	public static void indexFile(Path file) {
		try {
			System.out.println("Indexing file:... " + file.toFile().getCanonicalPath());
			Document doc = path2Doc(file);
			if (doc != null) {
				AppIndexWriter.getInstance().addDocument(doc);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		
		StandardAnalyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		
		
		Path indexpath = Paths.get(INDEX_DIR);
		Directory indexDir;
		try {
			indexDir = FSDirectory.open(indexpath);
			AppIndexWriter.createIndexWriter(indexDir, config);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try (Stream<Path> paths = Files.walk(Paths.get("/Users/hongwang/Documents/Work"))) {
			paths.filter(Files::isRegularFile).forEach(path -> FileIndex.indexFile(path));

		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			AppIndexWriter.getInstance().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
