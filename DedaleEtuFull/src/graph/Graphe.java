package graph;

import java.io.Serializable;
import java.util.ArrayList;
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
		Collection<String> temp = new HashSet<String>(connus);
		temp.retainAll(explores);
		connus.removeAll(temp);
	}
	
	public boolean isExplored(){
		return connus.isEmpty();
	}
	
	
	public String toString(){
		String res = "Expl = ";
		res += explores;
		res += "\n Known = " + connus;
		//res += "\n Edges = " + aretes;
		return res;
	}
	
	public ArrayList<String> bfsToNearest(String start){
		ArrayList<String> res = new ArrayList<String>();
		ArrayList<String> file = new ArrayList<String>();
		ArrayList<String> markus = new ArrayList<String>();
		ArrayList<Pair<String,String>> perus = new ArrayList<Pair<String,String>>();
		file.add(start);
		markus.add(start);
		String ncn = "";
		boolean trouve = false;
		while(!file.isEmpty() && !trouve){
			String nodus = file.remove(0);
			ArrayList<String> voisins = voisins(nodus);
			for(int i=0;i<voisins.size() && !trouve;i++){
				ncn = voisins.get(i);
				perus.add(new Pair<String, String>(ncn,nodus));
				if(connus.contains(ncn)){
					trouve = true;
				}
				if(!markus.contains(ncn)){
					file.add(ncn);
					markus.add(ncn);
				}
			}
		}
		//BACKTRACK
		if(trouve){
			while(ncn != start){
				res.add(0,ncn);
				trouve = false;
				int i=0;
				while(!trouve){
					if(perus.get(i).getFirst() == ncn){
						ncn = perus.get(i).getSecond();
						trouve=true; 
					}
					i++;
				}
			}
		}
		return res; //renvoie le chemin (suite de noeuds, start non-inclus) de start vers le noeud "connu" le plus proche
	}
	

	public ArrayList<String> voisins(String nodus){
		ArrayList<String> res = new ArrayList<String>();
		for(int i=0;i<aretes.size();i++){
			for (Pair<String,String> edgeus : aretes) {
				if(edgeus.getFirst() == nodus)
					res.add(edgeus.getSecond());
				else if(edgeus.getSecond() == nodus)
					res.add(edgeus.getFirst());
					
			}
		}
		return res;
	}
}
