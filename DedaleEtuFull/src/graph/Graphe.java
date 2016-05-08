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
	private Collection<Pair<String,Integer>> tresors = new HashSet<Pair<String,Integer>>();
	private Collection<Pair<String,Integer>> tresorsTraites = new HashSet<Pair<String,Integer>>();
	
	
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
	public void addTresor(String noeud, Integer attribute){
		boolean found = false;
		Pair<String,Integer> tp = new Pair<String,Integer>("",0);
		for(Pair<String,Integer> t : tresors)
		{
			if(t.getFirst().equals(noeud) && attribute < t.getSecond())
			{
				found = true;
				tp = t;
			}
		}
		if(found)
		{
			tresors.remove(tp);			
		}
		this.tresors.add(new Pair<String,Integer>(noeud, attribute));
	}
	public void addTresorTraites(String noeud, Integer attribute){
		boolean found = false;
		Pair<String,Integer> tp = new Pair<String,Integer>("",0);
		for(Pair<String,Integer> t : tresorsTraites)
		{
			if(t.getFirst().equals(noeud) && attribute < t.getSecond())
			{
				found = true;
				tp = t;
			}
		}
		if(found)
		{
			tresorsTraites.remove(tp);			
		}
		this.tresorsTraites.add(new Pair<String,Integer>(noeud, attribute));
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
		// Fusion des tresors
		Collection<Pair<String,Integer>> tres = g.getTresors();
		for(Pair<String,Integer> t1:tres)
		{
			this.addTresor(t1.getFirst(), t1.getSecond());
		}
		// Fusion des tresors traites
		Collection<Pair<String,Integer>> trestrait = g.getTresorsTraites();
		for(Pair<String,Integer> t1:trestrait)
		{
			this.addTresorTraites(t1.getFirst(), t1.getSecond());
		}
		
		Collection<String> temp = new HashSet<String>(connus);
		temp.retainAll(explores);
		connus.removeAll(temp);
	}
	
	private Collection<Pair<String, Integer>> getTresorsTraites() {
		return this.tresorsTraites;
	}
	public boolean isExplored(){
		return connus.isEmpty();
	}
	
	public boolean estTraite(Pair<String, Integer> tre){
		boolean res = false;
		for(Pair<String,Integer> t:this.tresorsTraites)
		{
			if(tre.equals(t))
				res = true;
		}
		return res;
	}
	
	public Collection<Pair<String,Integer>> getTresors(){
		return this.tresors;
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
		else
		{
			Collection<String> temp = new HashSet<String>(explores);
			temp.removeAll(markus);
			if(!temp.isEmpty()){
				System.out.println("INCONSISTENCY : " + temp);
				System.out.println("EDGES : " + aretes);
				System.out.println("START : "+ start);
				System.out.println("VOISINS : "+ voisins(start));
				System.out.println("MARKUS : " + markus);
			}
		}
		return res; //renvoie le chemin (suite de noeuds, start non-inclus) de start vers le noeud "connu" le plus proche
	}
	

	public ArrayList<String> voisins(String nodus){
		ArrayList<String> res = new ArrayList<String>();
		for (Pair<String,String> edgeus : aretes) {
			if(edgeus.getFirst().equals(nodus))
				res.add(edgeus.getSecond());
			else if(edgeus.getSecond().equals(nodus))
				res.add(edgeus.getFirst());					
		}
		return res;
	}
	
	public ArrayList<String> checkPath(String start, String goal){
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
				if(ncn.equals(goal)){
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
		return res;
	}
	
	public String findFreeSpace(ArrayList<String> path)
	{
		String res = "";
		boolean found = false;
		
		// Element 0 est la position de l'agent bloquant
		for(int i=1; i<path.size() && !found; i++)
		{
			String curNode = path.get(i);
			ArrayList<String> neigh = voisins(curNode);
			neigh.removeAll(path); 
			if(!neigh.isEmpty()) // Il y a un noeud qui n'est pas sur le chemin de l'autre agent
			{
				res = neigh.get(0);
				found = true;
			}
		}
		
		return res;
	}
	
	public void removeTreasure(String node) {
		boolean found = false;
		Pair<String,Integer> tp = new Pair<String,Integer>("",0);
		for(Pair<String,Integer> t : tresors)
		{
			if(t.getFirst().equals(node))
			{
				found = true;
				tp = t;
			}
		}
		if(found)
		{
			tresors.remove(tp);			
		}
	}
	
	public boolean isTreasureNode(String node)
	{
		boolean found = false;
		for(Pair<String,Integer> t : tresors)
		{
			if(t.getFirst().equals(node))
			{
				found = true;
			}
		}
		return found;
	}
	
	public String getMaxTreasure(String start)
	{
		String res = "";
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
				if(tresors.contains(ncn)){
					trouve = true;
					res = ncn;
				}
				if(!markus.contains(ncn)){
					file.add(ncn);
					markus.add(ncn);
				}
			}
		}
		return res;
	}
	
}
