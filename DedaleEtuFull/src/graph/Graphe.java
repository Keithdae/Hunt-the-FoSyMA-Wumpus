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
		this.tresors.add(new Pair<String,Integer>(noeud, attribute));
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
			boolean found = false;
			for(Pair<String,Integer> t2:this.tresors)
			{
				// Si a une position donnee la quantite de tresor a diminuee, on met a jour
				if(t1.getFirst() == t2.getFirst()) 
				{
					found = true;
					if(t1.getSecond() < t2.getSecond())
					{
						this.tresors.remove(t2);
						this.tresors.add(t1);
					}
				}
			}
			if(!found)
				this.tresors.add(t1);
		}
		
		Collection<String> temp = new HashSet<String>(connus);
		temp.retainAll(explores);
		connus.removeAll(temp);
	}
	
	public boolean isExplored(){
		return connus.isEmpty();
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
}
