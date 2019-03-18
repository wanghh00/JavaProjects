package hello;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.Test;

public class HelloLuceneTests {

	@Test
	public void givenSearchQueryWhenFetchedDocumentThenCorrect() {
		InMemoryLuceneIndex inMemoryLuceneIndex = new InMemoryLuceneIndex(new RAMDirectory(), new StandardAnalyzer());
		inMemoryLuceneIndex.indexDocument("Hello world", "Some hello world");

		List<Document> documents = inMemoryLuceneIndex.searchIndex("body", "world");

		assertEquals("Hello world", documents.get(0).get("title"));
	}

	@Test
	public void givenTermQueryWhenFetchedDocumentThenCorrect() {
		InMemoryLuceneIndex inMemoryLuceneIndex = new InMemoryLuceneIndex(new RAMDirectory(), new StandardAnalyzer());
		inMemoryLuceneIndex.indexDocument("activity", "running in track");
		inMemoryLuceneIndex.indexDocument("activity", "Cars are running on road");

		Term term = new Term("body", "running");
		Query query = new TermQuery(term);

		List<Document> documents = inMemoryLuceneIndex.searchIndex(query);
		assertEquals(2, documents.size());
	}

	@Test
	public void givenPrefixQueryWhenFetchedDocumentThenCorrect() {
		InMemoryLuceneIndex inMemoryLuceneIndex = new InMemoryLuceneIndex(new RAMDirectory(), new StandardAnalyzer());
		inMemoryLuceneIndex.indexDocument("article", "Lucene introduction");
		inMemoryLuceneIndex.indexDocument("article", "Introduction to Lucene");

		Term term = new Term("body", "intro");
		Query query = new PrefixQuery(term);

		List<Document> documents = inMemoryLuceneIndex.searchIndex(query);
		assertEquals(2, documents.size());
	}

	@Test
	public void givenWildcardQueryWhenFetchedDocumentThenCorrect() {
		InMemoryLuceneIndex inMemoryLuceneIndex = new InMemoryLuceneIndex(new RAMDirectory(), new StandardAnalyzer());
		inMemoryLuceneIndex.indexDocument("article", "Lucene introduction");
		inMemoryLuceneIndex.indexDocument("article", "Introduction to Lucene");

		Term term = new Term("body", "intro*");
		Query query = new WildcardQuery(term);

		List<Document> documents = inMemoryLuceneIndex.searchIndex(query);
		assertEquals(2, documents.size());
	}

	@Test
	public void givenPhraseQueryWhenFetchedDocumentThenCorrect() {
		InMemoryLuceneIndex inMemoryLuceneIndex = new InMemoryLuceneIndex(new RAMDirectory(), new StandardAnalyzer());
		inMemoryLuceneIndex.indexDocument("quotes", "A rose by any other name would smell as sweet.");
		inMemoryLuceneIndex.indexDocument("article", "Lucene introduction");
		inMemoryLuceneIndex.indexDocument("article", "Introduction to Lucene");

		Query query = new PhraseQuery(1, "body", new BytesRef("smell"), new BytesRef("sweet"));

		List<Document> documents = inMemoryLuceneIndex.searchIndex(query);
		assertEquals(1, documents.size());
	}

	@Test
	public void givenFuzzyQueryWhenFetchedDocumentThenCorrect() {
		InMemoryLuceneIndex inMemoryLuceneIndex = new InMemoryLuceneIndex(new RAMDirectory(), new StandardAnalyzer());
		inMemoryLuceneIndex.indexDocument("article", "Halloween Festival");
		inMemoryLuceneIndex.indexDocument("decoration", "Decorations for Halloween");

		Term term = new Term("body", "hallowen");
		Query query = new FuzzyQuery(term);

		List<Document> documents = inMemoryLuceneIndex.searchIndex(query);
		assertEquals(2, documents.size());
	}

	@Test
	public void givenBooleanQueryWhenFetchedDocumentThenCorrect() {
		InMemoryLuceneIndex inMemoryLuceneIndex = new InMemoryLuceneIndex(new RAMDirectory(), new StandardAnalyzer());
		inMemoryLuceneIndex.indexDocument("Destination", "Las Vegas singapore car");
		inMemoryLuceneIndex.indexDocument("Commutes in singapore", "Bus Car Bikes");

		Term term1 = new Term("body", "singapore");
		Term term2 = new Term("body", "car");

		TermQuery query1 = new TermQuery(term1);
		TermQuery query2 = new TermQuery(term2);

		BooleanQuery booleanQuery = new BooleanQuery.Builder().add(query1, BooleanClause.Occur.MUST)
				.add(query2, BooleanClause.Occur.MUST).build();

		List<Document> documents = inMemoryLuceneIndex.searchIndex(booleanQuery);
		assertEquals(1, documents.size());
	}

	@Test
	public void givenSortFieldWhenSortedThenCorrect() {
		InMemoryLuceneIndex inMemoryLuceneIndex = new InMemoryLuceneIndex(new RAMDirectory(), new StandardAnalyzer());
		inMemoryLuceneIndex.indexDocument("Ganges", "River in India");
		inMemoryLuceneIndex.indexDocument("Mekong", "This river flows in south Asia");
		inMemoryLuceneIndex.indexDocument("Amazon", "Rain forest river");
		inMemoryLuceneIndex.indexDocument("Rhine", "Belongs to Europe");
		inMemoryLuceneIndex.indexDocument("Nile", "Longest River");

		Term term = new Term("body", "river");
		Query query = new WildcardQuery(term);

		SortField sortField = new SortField("title", SortField.Type.STRING_VAL, false);
		Sort sortByTitle = new Sort(sortField);

		List<Document> documents = inMemoryLuceneIndex.searchIndex(query, sortByTitle);
		assertEquals(4, documents.size());
		assertEquals("Amazon", documents.get(0).getField("title").stringValue());
	}

}
