package mas.agents;

import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.ParallelBehaviour;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import graph.Graphe;
import mas.abstractAgent;
import mas.behaviours.CoopWalk;
import mas.behaviours.MigrationBehaviour;
import mas.behaviours.SendGraph;
import mas.behaviours.TreasureBehaviour;
import mas.agents.interactions.protocols.deployMe.*;

public class MovingIndiana extends abstractAgent {
	
	private static final long serialVersionUID = -5686331366676803589L;
	

	protected void beforeMove(){//Automatically called before doMove()
		super.beforeMove();
		//voyeur.close();
		System.out.println("I migrate");
	}
	
	protected void afterMove(){//Automatically called after doMove()
		super.afterMove();
		
		
		// Initialize graphStream
		/*graph.miseAJour(graphe);
		graph.addAttribute("ui.stylesheet", styleSheet);
		graph.addAttribute("ui.quality");
		graph.addAttribute("ui.antialias");
		voyeur = graph.display();*/
		
		
		// Behaviours
		FSMBehaviour fsm = new FSMBehaviour(this);
		ParallelBehaviour pb = new ParallelBehaviour();
		pb.addSubBehaviour(new SendGraph(this));
		pb.addSubBehaviour(new CoopWalk(this));
		fsm.registerFirstState(pb, "Exploration");
		fsm.registerState(new TreasureBehaviour(this), "Treasure");
		fsm.registerTransition("Exploration", "Treasure", 0);
		fsm.registerTransition("Treasure", "Exploration", 0);
		addBehaviour(fsm);

		addBehaviour(new R1_deployMe("GK", this));
		
		
		System.out.println("I migrated");
	}
	

	//private GraphStreamSerial graph = new GraphStreamSerial(this.getLocalName());
	private Graphe graphe = new Graphe();
	private Random rng = new Random();
	private int echecs = 0;
	private ArrayList<String> path = new ArrayList<String>();


	protected String styleSheet =
	        "node.known {" +
	        "	fill-color: red, cyan;" +
	        "	fill-mode: dyn-plain;" +
	        "}" +
	        "node.explored {" +
	        "	fill-color: blue, cyan;" +
	        "	fill-mode: dyn-plain;" +
	        "}" +
	        "node.treasure {" +
	        "	fill-color: yellow, cyan;" +
	        "	fill-mode: dyn-plain;" +
	        "}";

	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1) set the agent attributes 
	 *	 		2) add the behaviours
	 *          
	 */
	protected void setup(){

		super.setup();

		
		
		//graphe.addNodeExpl(this.getCurrentPosition());
	

		//Add the behaviours
		
		
		addBehaviour(new MigrationBehaviour(this));
		addBehaviour(new R1_deployMe("GK", this));
		addBehaviour(new R1_managerAnswer("GK", this));
		
		
		
		// Initialize graphStream
		/*graph.addAttribute("ui.stylesheet", styleSheet);
		graph.addAttribute("ui.quality");
	    graph.addAttribute("ui.antialias");
		voyeur = graph.display();*/
		
		/*
		// Register on DF
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID()); // getAID est l'AID de l'agent qui veut s'enregistrer
		ServiceDescription sd  = new ServiceDescription();
		sd.setType( "explorer" ); // il faut donner des noms aux services qu'on propose (ici explorer)
		sd.setName(getLocalName() );
		dfd.addServices(sd);
		        
		try {  
		      DFService.register(this, dfd );  
		}
		catch (FIPAException fe) { fe.printStackTrace(); }*/
		

		System.out.println("the agent "+this.getLocalName()+ " is started");

	}

	/**
	 * This method is automatically called after doDelete()
	 */
	protected void takeDown(){

	}
	
	public Collection<String> getListeExplores() {
		return graphe.getExplores();
	}
	
	public Collection<String> getListeConnus() {
		return graphe.getConnus();
	}
	
	/*public Serializable getListeExploresSerial() {
		return (Serializable) graph.getExplores();
	}*/
	
	/*public GraphStreamSerial getGraphStream(){
		return this.graph;
	}*/
	
	public int getRandom(int max)
	{
		return rng.nextInt(max);
	}
	
	public Serializable getGraphSerial()
	{
		return (Serializable) graphe;
	}
	
	public Graphe getGraph()
	{
		return graphe;
	}

	public int getEchecs() {
		return echecs;
	}

	public void resetEchecs() {
		this.echecs = 0;
	}
	
	public void incEchecs() {
		this.echecs++;
	}

	public ArrayList<String> getPath() {
		return path;
	}

	public void setPath(ArrayList<String> path) {
		this.path = path;
	}
}
