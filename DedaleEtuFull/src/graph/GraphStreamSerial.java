package graph;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import org.graphstream.graph.Edge;
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
	
	public HashSet<Pair<String,String>> getEdges(){
		Object[] edg = this.getEdgeSet().toArray();
		HashSet<Pair<String,String>> res = new HashSet<Pair<String,String>>();
		
		for(int i=0;i<edg.length;i++)
		{
			Edge e = (Edge) edg[i];
			res.add(new Pair<String,String>(e.getNode0().getId(), e.getNode1().getId()));
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
	
	public void addNodeTreasure(String node){
		this.getNode(node).setAttribute("treasure", "true");
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
		if((this.getEdge(edgeName) == null) && (this.getEdge(node2+"|"+node1) == null))
		{
			this.addEdge(edgeName, node1, node2);
		}
	}
	
	public void miseAJour(Graphe gus){
		HashSet<String> expl = new HashSet<String>(gus.getExplores());
		HashSet<String> known = new HashSet<String>(gus.getConnus());
		HashSet<Pair<String,String>> edgeus = new HashSet<Pair<String,String>>(gus.getAretes());
		expl.removeAll(this.getExplores());
		known.removeAll(this.getConnus());
		edgeus.removeAll(this.getEdges());
		for(String s : expl){
			this.addNodeExpl(s);
		}
		for(String s : known){
			this.addNodeSafe(s);
		}
		for(Pair<String,String> p : edgeus){
			this.addEdgeSafe(p.getFirst()+"|"+p.getSecond(), p.getFirst(), p.getSecond());
		}
		for(Pair<String,Integer> t: gus.getTresors()){
			this.addNodeTreasure(t.getFirst());
			this.getNode(t.getFirst()).addAttribute("ui.class","treasure");
		}
	}
	
}


