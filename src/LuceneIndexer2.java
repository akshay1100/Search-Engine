 import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.uci.ics.crawler4j.crawler.exceptions.ParseException;

import org.apache.lucene.search.Collector;
import org.apache.lucene.search.FuzzyQuery;

public class LuceneIndexer2 {

	public static Map<Integer, String> WGraph = new HashMap<Integer, String>();
	public static Map<Integer, String> WGraphPageRank = new HashMap<Integer, String>();
	public static Map<Integer, Document> resultDoc = new HashMap<Integer, Document>();
	static LuceneIndexer2 lIndexer1;
	
	public static void main(String[] args) throws IOException {

		String dirPath = "CrawlDocs";
		String urlPath = "NewUrl.txt";
		String docPageRankPath = "../docPageRank";
		String filePath = "";
		//String dirPath = "F://code//informationretrieval//Assignment1//IRDataTest";
		String queryString = "tennis";
		String indexDir = "./luceneIndexDir/Index_New";
		String indexDirPR = "./luceneIndexDir/Index_PR";
		
		lIndexer1 = new LuceneIndexer2();
		//lIndexer.createWebMap(urlPath);
		//lIndexer.assignPageRank(docPageRankPath);
		//lIndexer.readDirectoryLemma(dirPath, indexDir);
		lIndexer1.searchIndex(queryString, indexDir);
		lIndexer1.searchUsingRelevance(queryString, indexDir);
		System.out.println("\n\nPage Rank Results:");
		//lIndexer.searchIndexWithPageRank(queryString, indexDirPR);
		
	}

	public void createWebMap(String filePath) {

		BufferedReader br = null;
		String inputLine = "";

		try {
			br = new BufferedReader(new FileReader(filePath));
			while ((inputLine = br.readLine()) != null) {
				String[] nodeVal = inputLine.split("\t");
				WGraph.put(Integer.parseInt(nodeVal[0]), nodeVal[2]);
			}
			System.out.println("Web map\n\n");
		System.out.println(WGraph);
		} catch (Exception ex) {
       System.out.println("Exception occured while creating web map");
		}
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void assignPageRank(String filePath){
		BufferedReader br = null;
		String inputLine = "";

		try {
			System.out.println(filePath);
			br = new BufferedReader(new FileReader(filePath));
			System.out.println("ad");
			while ((inputLine = br.readLine()) != null) {
				String[] nodeVal = inputLine.split(":");
				WGraphPageRank.put(Integer.parseInt(nodeVal[0]), nodeVal[1]);
				System.out.println("Web map\n\n");
				System.out.println(WGraphPageRank);
			}
		} catch (Exception ex) {
       System.out.println("Exception occured while creating web map11");
		}
		
	}
	
	public void readDirectoryLemma(String dirPath, String indexDir) {
		String docId = "";
		Path indexDirPath = FileSystems.getDefault().getPath(indexDir);
		try {
			File folder = new File(dirPath);
			Directory dir = FSDirectory.open(indexDirPath);
			IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
			IndexWriter indexWriter = new IndexWriter(dir, config);
			if (folder != null) {
				File[] fileList = folder.listFiles();
			//	System.out.println(folder);
			//	System.out.println(fileList);
				for (File file : fileList) {
					if (file.isFile()) {
						//docId = file.getPath().substring(17, file.getPath().length() - 4);
						String filename = file.getPath();
						//docId = filename.substring(file.getParent().length() + "Cranfield".length() + 1, filename.length());
						docId = filename.substring(file.getParent().length() + "Doc".length() + 1, filename.length() - 4);
						
						System.out.println("Processing Doc#" + docId + "......");
						createIndex(file.getPath(), docId, indexWriter);
					}
				}
			}
			indexWriter.close();
		} catch (Exception ex) {
			System.out.println("Exception occured while reading files:" + ex.getMessage() + "\n");
			ex.printStackTrace();
		}
		//System.out.println(indexWriter.maxDoc());
	}

	public void searchIndex(String queryString, String indexDir) {
		Path indexDirPath = FileSystems.getDefault().getPath(indexDir);

		try {
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(indexDirPath)));
			QueryParser parser = new QueryParser("content", new StandardAnalyzer());
			Query query = parser.parse(queryString);
			TopDocs collector = searcher.search(query, 10);
			ScoreDoc[] hits = collector.scoreDocs;
			for(int i =0;i<3;i++){
				Document d = searcher.doc(hits[i].doc);
			    System.out.println(d.get("id") + "-->" + d.get("url") + "  -->" +d.get("pagerank"));
			}
		
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Exception occured while searching index:" + ex.getMessage());
		}
	}
	
	private void searchUsingRelevance(String queryString, String indexDir)
		      throws IOException, ParseException{
		
		Path indexDirPath = FileSystems.getDefault().getPath(indexDir);
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(indexDirPath)));
		      long startTime = System.currentTimeMillis();
		      //create a term to search file name
		      Term term = new Term("content", queryString);
		      //create the term query object
		      Query query = new FuzzyQuery(term);
		      lIndexer1.setDefaultFieldSortScoring(true, false);
		      //do the search
		      TopDocs hits = searcher.search1(query,Sort.RELEVANCE);
		      long endTime = System.currentTimeMillis();

		      System.out.println(hits.totalHits +
		         " documents found. Time :" + (endTime - startTime) + "ms");
		      for(ScoreDoc scoreDoc : hits.scoreDocs) {
		         Document doc = searcher.getDocument(scoreDoc);
		         System.out.print("Score: "+ scoreDoc.score + " ");
		         System.out.println("File: "+ doc.get(LuceneConstants.FILE_PATH));
		      }
		      searcher.close();
		   }
	public TopDocs search1(Query query,Sort sort) 
		      throws IOException, ParseException{
		String indexDir = "./luceneIndexDir/Index_New";
		 Path indexDirPath3 = FileSystems.getDefault().getPath(indexDir);
			IndexSearcher indexSearcher12=new IndexSearcher(DirectoryReader.open(FSDirectory.open(indexDirPath3)));
		      return indexSearcher12.search(query, 
		         10,sort);
		   }

	
	
	 public void setDefaultFieldSortScoring(boolean doTrackScores, 
		      boolean doMaxScores){
		 String indexDir = "./luceneIndexDir/Index_New";
		 Path indexDirPath3 = FileSystems.getDefault().getPath(indexDir);
			IndexSearcher indexSearcher12=new IndexSearcher(DirectoryReader.open(FSDirectory.open(indexDirPath3)));
		indexSearcher12.set
			indexSearcher12.setDefaultFieldSortScoring(
		         doTrackScores,doMaxScores);
		   }
	
	
	public void searchIndexWithPageRank(String queryString, String indexDir) {
		Path indexDirPath = FileSystems.getDefault().getPath(indexDir);

		try {
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(indexDirPath)));
			QueryParser parser = new QueryParser("content", new StandardAnalyzer());
			Query query = parser.parse(queryString);
			TopDocs collector = searcher.search(query, 10);
			ScoreDoc[] hits = collector.scoreDocs;
			for(int i =0;i<10;i++){
				Document d = searcher.doc(hits[i].doc);
				resultDoc.put(i, d);
			    //System.out.println(d.get("id") + "-->" + d.get("url") + "-->" +d.get("pagerank"));
			}
			
			Map<Integer, Document> rankedPage = rankByPageRank(resultDoc);
			for(int i =0;i<10;i++){
				Document d = rankedPage.get(i);
				System.out.println(d.get("id") + "-->" + d.get("url") + "  -->" +d.get("pagerank"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Exception occured while searching index:" + ex.getMessage());
		}
	}

	public Map<Integer, Document> rankByPageRank(Map<Integer, Document> resultDoc) {

		List<Map.Entry<Integer, Document>> docRankList = new LinkedList<Map.Entry<Integer, Document>>(resultDoc.entrySet());

		Collections.sort(docRankList, new Comparator<Map.Entry<Integer, Document>>() {
			public int compare(Map.Entry<Integer, Document> doc1, Map.Entry<Integer, Document> doc2) {
				Double d1 = Double.parseDouble(doc1.getValue().get("pagerank")); /// docMap.get(w1.getKey()).docLen;
				Double d2 = Double.parseDouble(doc2.getValue().get("pagerank")); /// docMap.get(w2.getKey()).docLen;
				return (d1).compareTo(d2) * (-1);
			}
		});
		int count = 0;
		Map<Integer, Document> topDocList = new LinkedHashMap<Integer, Document>();
		for(Entry<Integer, Document> entry: docRankList){
			topDocList.put(count, entry.getValue());
		    count++;
		}
		return topDocList;
	}
	
	public void createIndex(String filePath, String docId, IndexWriter indexWriter) {

		BufferedReader br = null;
		String inputLine = "";
		String docText = "";
		try {
			br = new BufferedReader(new FileReader(filePath));
			while ((inputLine = br.readLine()) != null) {
				docText += inputLine;
			}

			Document doc = new Document();
			doc.add(new StringField("id", docId, Field.Store.YES));
			doc.add(new StringField("url", WGraph.get(Integer.parseInt(docId)), Field.Store.YES));
			doc.add(new TextField("content", docText, Field.Store.YES));
		
			indexWriter.addDocument(doc);
		} catch (Exception ex) {
			ex.printStackTrace();

			System.out.println(
					"Exception occured in retrieving tokens!!" + ex.getMessage() + "\n\n" + ex.getStackTrace());
		}
		
		
		
	}
	
	public void createIndexWithPageRank(String filePath, String docId, IndexWriter indexWriter) {

		BufferedReader br = null;
		String inputLine = "";
		String docText = "";
		try {
			br = new BufferedReader(new FileReader(filePath));
			while ((inputLine = br.readLine()) != null) {
				docText += inputLine;
			}

			Document doc = new Document();
			doc.add(new StringField("id", docId, Field.Store.YES));
			doc.add(new StringField("url", WGraph.get(Integer.parseInt(docId)), Field.Store.YES));
			doc.add(new StringField("pagerank", WGraphPageRank.get(Integer.parseInt(docId)), Field.Store.YES));
			doc.add(new TextField("content", docText, Field.Store.YES));
		
			indexWriter.addDocument(doc);
			
		} catch (Exception ex) {
			ex.printStackTrace();

			System.out.println(
					"Exception occured in retrieving tokens!!" + ex.getMessage() + "\n\n" + ex.getStackTrace());
		}
	}
}
