package graph;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;


public class Graphe implements Serializable {
	
	private static final long serialVersionUID = -2601570650176863215L;
	
	// Params
	private Collection<String> explores = new HashSet<String>();
	private Collection<String> connus = new HashSet<String>();
	private Collection<Pair<String,String>> aretes = new HashSet<Pair<String,String>>();
	
	
	// Getters
	public Collection<String> getExplores() {
		return explores;
	}
	public Collection<String> getConnus() {
		return connus;
	}
	public Collection<Pair<String, String>> getAretes() {
		return aretes;
	}	
	
	
	public void addNodeExpl(String noeud){
		explores.add(noeud);
		connus.remove(noeud);
	}
	
	public void addNodeConnu(String noeud){
		connus.add(noeud);
	}
	
	public void addEdge(String depart, String arrivee){
		aretes.add(new Pair<String,String>(depart,arrivee));
	}
	
	public void merge(Graphe g){
		this.explores.addAll(g.getExplores());
		this.connus.addAll(g.getConnus());
		this.aretes.addAll(g.getAretes());
		Collection<String> temp = connus;
		temp.retainAll(explores);
		connus.removeAll(temp);
	}
	
	public boolean isExplored(){
		return connus.isEmpty();
	}
}
