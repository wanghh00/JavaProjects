package services;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class AppIndexReader {
	private static IndexReader reader;
	static final String INDEX_DIR = "/tmp/luceneidx";

	private AppIndexReader() {

	}

	public static IndexReader getIndexReader() {
		if (reader == null) {
			synchronized (AppIndexReader.class) {
				if (reader == null) {
					Directory indexDir;
					try {
						indexDir = FSDirectory.open(Paths.get(INDEX_DIR));
						reader = DirectoryReader.open(indexDir);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return reader;
	}

	public static void main(String[] args) {
		Analyzer analyzer = new StandardAnalyzer();
		String querystr = "feedhome";

		Query q;
		try {
			q = new QueryParser("contents", analyzer).parse(querystr);

			int hitsPerPage = 10;
			IndexSearcher searcher = new IndexSearcher(getIndexReader());
			TopDocs docs = searcher.search(q, hitsPerPage);
			ScoreDoc[] hits = docs.scoreDocs;

			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				System.out.println((i + 1) + ". " + d.get("filename"));
			}

		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
