package mas.agents;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;


import env.Environment;
import graph.GraphStreamSerial;
import graph.Graphe;
import mas.abstractAgent;
import mas.behaviours.*;


public class DummyExploAgent extends abstractAgent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1784844593772918359L;

	private GraphStreamSerial graph = new GraphStreamSerial(this.getLocalName());
	private Graphe graphe = new Graphe();
	private Random rng = new Random();
	
	private int echecs = 0;
	private ArrayList<String> path = new ArrayList<String>();


	protected String styleSheet =
	        "node.known {" +
	        "	fill-color: red;" +
	        "}" +
	        "node.explored {" +
	        "	fill-color: blue, yellow;" +
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

		//get the parameters given into the object[]. In the current case, the environment where the agent will evolve
		final Object[] args = getArguments();
		if(args[0]!=null){

			deployAgent((Environment) args[0]);

		}else{
			System.err.println("Malfunction during parameter's loading of agent"+ this.getClass().getName());
			System.exit(-1);
		}
		
		//graphe.addNodeExpl(this.getCurrentPosition());
	

		//Add the behaviours
		//addBehaviour(new RandomWalkBehaviour(this));
		addBehaviour(new SayHello(this));
		addBehaviour(new CoopWalk(this));

		graph.addAttribute("ui.stylesheet", styleSheet);
		graph.addAttribute("ui.quality");
	    graph.addAttribute("ui.antialias");
		graph.display();
		

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
	
	public GraphStreamSerial getGraphStream(){
		return this.graph;
	}
	
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
