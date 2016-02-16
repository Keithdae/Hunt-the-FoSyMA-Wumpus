package graph;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import org.graphstream.graph.implementations.Graphs;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.graph.implementations.SingleNode;



public class GraphStreamSerial extends SingleGraph implements Serializable{
	
	private static final long serialVersionUID = 4667604546699445450L;

	public GraphStreamSerial(String id) {
		super(id);
	}

	public void merge(GraphStreamSerial g)
	{
		String[] expl = (String[]) getExplores().toArray();
		Graphs.mergeIn(this, g);
		for(int i=0; i<expl.length; i++)
		{
			this.getNode(expl[i]).setAttribute("ui.class", "explored");
		}
	}
	
	public Collection<String> getExplores(){
		Object[] nodes = this.getNodeSet().toArray();
		Collection<String> res = new HashSet<String>();
		int i = this.getNodeCount();
		for(int j=0;j<i;j++)
		{
			SingleNode t = (SingleNode) nodes[j];
			if(t.getAttribute("ui.class") == "explored")
			{
				res.add(t.getId());
			}
		}

		return res;
	}
	
	public Collection<String> getConnus(){
		Object[] nodes = this.getNodeSet().toArray();
		Collection<String> res = new HashSet<String>();
		int i = this.getNodeCount();
		for(int j=0;j<i;j++)
		{
			SingleNode t = (SingleNode) nodes[j];
			if(t.getAttribute("ui.class") == "known")
			{
				res.add(t.getId());
			}
		}

		return res;
	}

	public boolean isExplored() {
		boolean res = true;
		Object[] nodes = this.getNodeSet().toArray();
		int i = this.getNodeCount();
		for(int j=0;res && j<i;j++)
		{
			SingleNode t = (SingleNode) nodes[j];
			if(t.getAttribute("ui.class") == "known")
			{
				res = false;
			}
		}
		
		return res;
	}

	public void addNodeExpl(String node) {
		this.addNodeSafe(node);
		this.getNode(node).setAttribute("ui.class", "explored");
	}
	
	
	public void addNodeSafe(String node)
	{
		if(this.getNode(node) == null)
		{
			this.addNode(node);
			this.getNode(node).addAttribute("ui.class", "known");
		}
	}

	public void addEdgeSafe(String edgeName, String node1, String node2) {
		if(this.getEdge(edgeName) == null)
		{
			this.addEdge(edgeName, node1, node2);
		}
	}
}
