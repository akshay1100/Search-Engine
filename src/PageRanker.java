import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.algorithms.scoring.PageRank;


class WGraph {

	public List<WNode> n_list;
	
	public void addNode(WNode wnode){
		if(n_list == null){
			n_list = new ArrayList<WNode>();
		}
		n_list.add(wnode);
	}
	
}

class WNode {

	public Integer n_id;
	public Integer in_link;
	public Integer out_link;
	public String n_url;
	public Double p_rank;
	public Double prev_pr;
	public List<WNode> adj_list;
	public List<WNode> in_list;
	public WNode(){}
	public WNode(Integer n_Id, String n_url){
		this.n_id = n_Id;
		this.n_url = n_url;
	}
	public WNode(String n_url){
		this.n_url = n_url;
	}
	
	public WNode(Integer nodeId){
		this.n_id= nodeId;
		this.in_link = 0;
		this.out_link = 0;
	    this.p_rank = 0.1;
	    this.in_list = new ArrayList<WNode>();
		adj_list = new ArrayList<WNode>();
	}
}

public class PageRanker {

	Map<String, WNode> WG1 = new HashMap<String, WNode>();
	Map<Integer, WNode> WG2 = new HashMap<Integer, WNode>();
	Map<Integer, String> Url_map = new HashMap<Integer, String>();
	static DirectedGraph<Integer, String> w_graph = new DirectedSparseGraph<Integer, String>();
	static double alpha_v = 0.16;

	public static void main(String[] args) {
		String input_p = "NewUrl.txt";
		String docPR_p = "./docPageRank";
		PageRanker obj_pr = new PageRanker();
		
		 obj_pr.Web_map(input_p);
		obj_pr.D_Graph2(input_p);
		PageRank<Integer, String> PR_score = new PageRank<Integer, String>(w_graph,
				alpha_v);	
		PR_score.evaluate();
		
		obj_pr.save_W_Graph(PR_score, docPR_p);
		
		

	}

	public void Web_map(String filePath) {

		BufferedReader br = null;
		String inputLine = "";

		try {
			br = new BufferedReader(new FileReader(filePath));
			while ((inputLine = br.readLine()) != null) {
				String[] node_val = inputLine.split("\t");
				WNode obj_wn = new WNode(Integer.parseInt(node_val[0]), node_val[2]);
				WG1.put(node_val[2], obj_wn);
			//	System.out.println("webmap");
			//	System.out.println(WGraph);
				
			}
		} catch (Exception ex) {
System.out.println("problem in web map");
		}
	}

	
	
	public void print_WG(){
		for (Entry<Integer, WNode> g_entry : WG2.entrySet()) {
			System.out.print(g_entry.getValue().n_id + ":" + g_entry.getValue().p_rank + ":"+ g_entry.getValue().in_link + ":"+ g_entry.getValue().adj_list.size() + "-->");
			for (WNode adjNode : g_entry.getValue().in_list) {
				System.out.print(adjNode.n_id + ",");
			}
			System.out.println();
		}
	}
	
	public void compute_PR(){
		
		double[] current_PR = new double[WG2.size()];
			for (Entry<Integer, WNode> g_entry : WG2.entrySet()) {
			double sum = 0;
			for (WNode adjNode : g_entry.getValue().in_list) {
			  sum += adjNode.p_rank / adjNode.adj_list.size(); 
			}
			current_PR[g_entry.getKey()] = 0.15 + 0.85 * sum;
			System.out.println(g_entry.getKey() + "-->" + g_entry.getValue().p_rank);
			
		}
}
	
	
	public void Web_graph(String path) {

		BufferedReader br = null;
		String inp_line = "";
		try {
			br = new BufferedReader(new FileReader(path));
			while ((inp_line = br.readLine()) != null) {
				String[] node_val = inp_line.split("\t");
				WNode obj_p_node = null;
				WNode obj_w_node = null;

				if (!WG1.containsKey(node_val[2])) {
					obj_w_node = new WNode(Integer.parseInt(node_val[0]), node_val[2]);
				} else {
					obj_w_node = WG1.get(node_val[2]);
					obj_w_node.n_id = Integer.parseInt(node_val[0]);
				}

				if (!node_val[3].equals("null")) {
					if (!WG1.containsKey(node_val[3])) {
						obj_p_node = new WNode(node_val[3]);
					} else {
						obj_p_node = WG1.get(node_val[3]);
					}
					if (obj_p_node.adj_list == null) {
						obj_p_node.adj_list = new ArrayList<WNode>();
					}
					obj_p_node.adj_list.add(obj_w_node);
					WG1.put(node_val[3], obj_p_node);
				}

				WG1.put(node_val[2], obj_w_node);
				w_graph.addVertex(Integer.parseInt(node_val[0]));

				Url_map.put(Integer.parseInt(node_val[0]), node_val[2]);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Exception occured while creating graph: " + ex.getMessage());
		}
	}

	public void D_Graph() {

		for (Entry<String, WNode> graphEntry : WG1.entrySet()) {
		if (graphEntry.getValue().adj_list != null) {
		for (WNode adjNode : graphEntry.getValue().adj_list) {
		w_graph.addEdge(graphEntry.getValue().n_url + "->" + adjNode.n_url,
		graphEntry.getValue().n_id, adjNode.n_id);}}}}

	public void D_Graph2(String filePath) {

		BufferedReader br = null;
		String inputLine = "";
		try {
		br = new BufferedReader(new FileReader(filePath));
		while ((inputLine = br.readLine()) != null) {
		String[] nodeVal = inputLine.split("\t");
		if (!nodeVal[4].equals("null")) {
		w_graph.addEdge(nodeVal[3] + "-->" + nodeVal[0],
		Integer.parseInt(nodeVal[3]), Integer.parseInt(nodeVal[0]));
		} else {
		w_graph.addVertex(Integer.parseInt(nodeVal[0]));
		}
		}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("problem in graph:" + ex.getMessage());
		}
	//	System.out.println("wss34564ebDGraph");
	//	System.out.println(webDGraph);
		
	}


	public void print_WG(PageRank<Integer, String> pR) {
		for (Entry<String, WNode> graphEntry : WG1.entrySet()) {
			System.out.print(graphEntry.getKey() + "{" + graphEntry.getValue().n_id + "}->");
			if (graphEntry.getValue().adj_list != null) {
				for (WNode adjNode : graphEntry.getValue().adj_list) {
					System.out.print(adjNode.n_id + ":: ");
				}
			}
			System.out.println();

		}

		for (Integer v : w_graph.getVertices()) {
			double scr = pR.getVertexScore(v);
			System.out.println("node: " + v + ": = " + scr);
		}

		for (String e : w_graph.getEdges()) {
			System.out.println(e);
		}

	}

	public void save_W_Graph(PageRank<Integer, String> pR, String docPRPath) {
		FileWriter writerOpFile = null;
		try {
			writerOpFile = new FileWriter(docPRPath);

			for (Entry<String, WNode> graphEntry : WG1.entrySet()) {
				int nodeId = graphEntry.getValue().n_id;
				writerOpFile.write(nodeId + ":" + pR.getVertexScore(nodeId) + ":" + graphEntry.getValue());
				writerOpFile.write("\n");
			}

			writerOpFile.close();
			System.out.println("Done, file Creation properly");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(" occured while writing file:" + ex.getMessage());
		}

	}
}
