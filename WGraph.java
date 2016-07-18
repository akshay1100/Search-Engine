import java.util.ArrayList;
import java.util.List;

public class WGraph {

	public List<WNode> nodeList;
	
	public void addNode(WNode wnode){
		if(nodeList == null){
			nodeList = new ArrayList<WNode>();
		}
		nodeList.add(wnode);
	}
	
}
