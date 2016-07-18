import java.util.ArrayList;
import java.util.List;

public class WNode {

	public Integer nodeId;
	public Integer incomingLink;
	public Integer outgoingLink;
	public String nodeurl;
	public Double pageRank;
	public Double prevPageRank;
	public List<WNode> adjList;
	public List<WNode> incomingList;
	public WNode(){}
	public WNode(Integer nodeId, String nodeurl){
		this.nodeId = nodeId;
		this.nodeurl = nodeurl;
	}
	public WNode(String nodeurl){
		this.nodeurl = nodeurl;
	}
	
	public WNode(Integer nodeId){
		this.nodeId= nodeId;
		this.incomingLink = 0;
		this.outgoingLink = 0;
	    this.pageRank = 0.1;
	    this.incomingList = new ArrayList<WNode>();
		adjList = new ArrayList<WNode>();
	}
}
