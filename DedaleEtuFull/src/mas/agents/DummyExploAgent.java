package mas.agents;


import java.io.Serializable;
import java.util.Collection;
import java.util.Random;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import env.Environment;
import graph.Graphe;
import graph.Pair;
import mas.abstractAgent;
import mas.behaviours.*;


public class DummyExploAgent extends abstractAgent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1784844593772918359L;

	private Graphe graph = new Graphe();
	private Graph affichage = new SingleGraph(this.getLocalName());
	private Random rng = new Random();

	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1) set the agent attributes 
	 *	 		2) add the behaviours
	 *          
	 */
	protected void setup(){

		super.setup();

		//get the parameters given into the object[]. In the current case, the environment where the agent will evolve
		final Object[] args = getArguments();
		if(args[0]!=null){

			deployAgent((Environment) args[0]);

		}else{
			System.err.println("Malfunction during parameter's loading of agent"+ this.getClass().getName());
			System.exit(-1);
		}
	

		//Add the behaviours
		//addBehaviour(new RandomWalkBehaviour(this));
		addBehaviour(new SayHello(this));
		addBehaviour(new CoopWalk(this));

		this.affichage.display();
		

		System.out.println("the agent "+this.getLocalName()+ " is started");

	}

	/**
	 * This method is automatically called after doDelete()
	 */
	protected void takeDown(){

	}
	
	public Collection<String> getListeExplores() {
		return graph.getExplores();
	}
	
	public Collection<String> getListeConnus() {
		return graph.getConnus();
	}
	
	public Collection<Pair<String,String>> getListeAretes() {
		return graph.getAretes();
	}
	
	public Serializable getListeExploresSerial() {
		return (Serializable) graph.getExplores();
	}
	
	public void addToExplores(String node){
		graph.getExplores().add(node);
	}
	
	public int getRandom(int max)
	{
		return rng.nextInt(max);
	}
	
	public Serializable getGraphSerial()
	{
		return (Serializable) graph;
	}
	
	public Graphe getGraph()
	{
		return graph;
	}
	
	
}
