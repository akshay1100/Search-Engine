 import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
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
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.Collector;

public class Indexer {

	public static Map<Integer, String> W_graph = new HashMap<Integer, String>();
	public static Map<Integer, Document> fin_doc = new HashMap<Integer, Document>();
	public static Map<Integer, String> W_graphPR = new HashMap<Integer, String>();
	
	public static void main(String[] args) {

		String d_Path = "CrawlDocs";
		String u_path = "NewUrl.txt";
		String PR_doc_path = "docPageRank";
		//String filePath = "";
		String query_s = "grand slam";
		String index_d = "./luceneIndexDir/Index_New";
		String index_PR = "./luceneIndexDir/Index_PR";

		Indexer lIndexer = new Indexer();
		//lIndexer.createWebMap(u_path);
		//lIndexer.assignPageRank(PR_doc_path);
		//lIndexer.readDirectoryLemma(d_Path, index_d,0);
	//	lIndexer.readDirectoryLemma(d_Path, index_PR,1);
		lIndexer.search_wihoutPR(query_s, index_d);
		System.out.println("\n\nPage Rank Results:");
		lIndexer.search_WithPR(query_s, index_PR);
		
	}

	public void create_wmap(String path) {
		

		BufferedReader br = null;
		String inp_line = "";

		try {
			br = new BufferedReader(new FileReader(path));
			while ((inp_line = br.readLine()) != null) {
				String[] node_val = inp_line.split("\t");
				W_graph.put(Integer.parseInt(node_val[0]), node_val[2]);
			}
		//	System.out.println("Web map\n\n");
		//System.out.println(WGraph);
		} catch (Exception ex) {
       System.out.println("Problem with  web map creation");
		}
		try {
			br.close();
		} catch (IOException e1) {
			System.out.println("io exception");
			e1.printStackTrace();
		}
		//System.out.println(WGraph);
	}
	
	public void P_Ranker(String path){
		BufferedReader br = null;
		String inputLine = "";

		try {
			System.out.println(path);
			br = new BufferedReader(new FileReader(path));
			System.out.println("ad");
			while ((inputLine = br.readLine()) != null) {
				String[] node_val = inputLine.split(":");
				W_graphPR.put(Integer.parseInt(node_val[0]), node_val[1]);
				//System.out.println("Web pagerankdd\n\n");
				//System.out.println(W_graphPR);
			}
		} catch (Exception e1) {
       System.out.println("Exception occured while creating web map11");
		}
		
	}
	
	public void index_process(String path, String indexDir, int flag) {
		String docId = "";
		Path ind_path = FileSystems.getDefault().getPath(indexDir);
		try {
			File folder = new File(path);
			Directory dir = FSDirectory.open(ind_path);
			IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
			IndexWriter obj_writer = new IndexWriter(dir, config);
			if (folder != null) {
				File[] fileList = folder.listFiles();
			//	System.out.println(folder);
			//	System.out.println(fileList);
				for (File file : fileList) {
					if (file.isFile()) {
						String filename = file.getPath();
						docId = filename.substring(file.getParent().length() + "Doc".length() + 1, filename.length() - 4);
						
						System.out.println("Indexing doc: " + docId + " ");
						if(flag == 0)
						{
						Indexing_withoutPR(file.getPath(), docId, obj_writer);
						}
						else
						{
						Indexing_withPR(file.getPath(), docId, obj_writer);
						}
					}
				}
			}
			obj_writer.close();
		} catch (Exception e1) {
			System.out.println("Exception occured while reading files:" + e1.getMessage() + "\n");
			e1.printStackTrace();
		}
		}

	public ArrayList<String> search_wihoutPR(String queryString, String indexDir) {
		ArrayList<String> url1 = new ArrayList<String>();
		Path indexDirPath = FileSystems.getDefault().getPath(indexDir);

		try {
			IndexSearcher obj_search = new IndexSearcher(DirectoryReader.open(FSDirectory.open(indexDirPath)));
			QueryParser obj_parse = new QueryParser("content", new StandardAnalyzer());
			Query obj_query = obj_parse.parse(queryString);
			TopDocs obj_topd = obj_search.search(obj_query, 10);
			ScoreDoc[] hits = obj_topd.scoreDocs;
			for(int i =0;i<10;i++){
				Document obj_doc = obj_search.doc(hits[i].doc);
			    System.out.println(obj_doc.get("id") + " :" + obj_doc.get("url") );
			    //System.out.println(d.get("content"));
			}
		
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("Problem while searching normal index:" + e1.getMessage());
		}
		return url1;
	}
	public ArrayList<String> search_WithPR(String queryString, String indexDir) {
		Path indexDirPath = FileSystems.getDefault().getPath(indexDir);
		ArrayList<String> url2 = new ArrayList<String>();
		try {
			IndexSearcher obj_search = new IndexSearcher(DirectoryReader.open(FSDirectory.open(indexDirPath)));
			QueryParser obj_parse = new QueryParser("content", new StandardAnalyzer());
			Query obj_query = obj_parse.parse(queryString);
			TopDocs obj_topd = obj_search.search(obj_query, 10);
			ScoreDoc[] hits = obj_topd.scoreDocs;
			for(int i =0;i<10;i++){
				Document d = obj_search.doc(hits[i].doc);
				fin_doc.put(i, d);
			    //System.out.println(d.get("id") + "-->" + d.get("url") + "-->" +d.get("pagerank"));
			}
			
			Map<Integer, Document> rankedPage = sortby_PR(fin_doc);
			for(int i =0;i<10;i++){
				Document obj_doc = rankedPage.get(i);
				System.out.println(obj_doc.get("id") + ":" + obj_doc.get("url") + " : " +obj_doc.get("pagerank"));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("Problem while searching index with PR:" + e1.getMessage());
		}
		return url2;
	}

	public Map<Integer, Document> sortby_PR(Map<Integer, Document> resultDoc) {

		List<Map.Entry<Integer, Document>> Rank_list = new LinkedList<Map.Entry<Integer, Document>>(resultDoc.entrySet());

		Collections.sort(Rank_list, new Comparator<Map.Entry<Integer, Document>>() {
			public int compare(Map.Entry<Integer, Document> d1, Map.Entry<Integer, Document> d2) {
				Double obj1 = Double.parseDouble(d1.getValue().get("pagerank")); 
				Double obj2 = Double.parseDouble(d2.getValue().get("pagerank"));
				return (obj1).compareTo(obj2) * (-1);
			}
		});
		int c = 0;
		Map<Integer, Document> Doc_list = new LinkedHashMap<Integer, Document>();
		for(Entry<Integer, Document> entry: Rank_list){
			Doc_list.put(c, entry.getValue());
		    c++;
		}
		return Doc_list;
	}
	
	public void Indexing_withoutPR(String path, String id, IndexWriter obj_write) {

		BufferedReader br = null;
		String inp_line = "";
		String Text = "";
		try {
			br = new BufferedReader(new FileReader(path));
			while ((inp_line = br.readLine()) != null) {
				Text += inp_line;
			}

			Document obj_doc = new Document();
			obj_doc.add(new StringField("id", id, Field.Store.YES));
			obj_doc.add(new StringField("url", W_graph.get(Integer.parseInt(id)), Field.Store.YES));
			obj_doc.add(new TextField("content", Text, Field.Store.YES));
		
			obj_write.addDocument(obj_doc);
		} catch (Exception e1) {
			e1.printStackTrace();

			System.out.println(
					"Problem in accessing tokens!!");
		}
		
		
		
	}
	
	public void Indexing_withPR(String path, String id, IndexWriter obj_write) {

		System.out.println("in creating page rank");
		BufferedReader br = null;
		String inp_line = "";
		String Text = "";
		try {
			br = new BufferedReader(new FileReader(path));
			while ((inp_line = br.readLine()) != null) {
				Text += inp_line;
			}

			Document ibj_doc = new Document();
			ibj_doc.add(new StringField("id", id, Field.Store.YES));
			ibj_doc.add(new StringField("url", W_graph.get(Integer.parseInt(id)), Field.Store.YES));
			ibj_doc.add(new StringField("pagerank", W_graphPR.get(Integer.parseInt(id)), Field.Store.YES));
			ibj_doc.add(new TextField("content", Text, Field.Store.YES));
		
			obj_write.addDocument(ibj_doc);
			
		} catch (Exception ex) {
			ex.printStackTrace();

			System.out.println(
					"Problem in accessing tokens!! with PR" );
		}
	}
}
