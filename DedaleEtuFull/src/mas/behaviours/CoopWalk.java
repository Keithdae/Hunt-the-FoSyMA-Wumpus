package mas.behaviours;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import env.Attribute;
import env.Couple;
import graph.Graphe;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class CoopWalk extends TickerBehaviour {

	public CoopWalk(final mas.abstractAgent myagent) {
		super(myagent,100);
		// TODO Auto-generated constructor stub
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void onTick() {
		// TODO Auto-generated method stub
		mas.agents.DummyExploAgent agent = (mas.agents.DummyExploAgent)this.myAgent;
		ACLMessage msg;
		do{
			final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			msg = this.myAgent.receive(msgTemplate);
			if(msg != null){
				//Union des listes des noeuds explorés
				try {
					Graphe grapheRecu = (Graphe)msg.getContentObject();
					agent.getGraph().merge(grapheRecu);
				} catch(UnreadableException e){
					e.printStackTrace();
				}
			}
		}while(msg != null);
		
		//Listes des noeuds voisins atteignables
		List<Couple<String,List<Attribute>>> lobs=((mas.abstractAgent)this.myAgent).observe();
		System.out.println(this.myAgent.getLocalName()+" -- list of observables: "+lobs);
		System.out.println(this.myAgent.getLocalName()+" -- list of known nodes before: "+agent.getGraph().getConnus());
		//System.out.println(this.myAgent.getLocalName()+" -- list of explored nodes: "+agent.getListeExplores());
		// On extrait les noeuds observes, sans leurs attributs
		Collection<String> listeExplorables = new ArrayList<String>();
		for(Couple<String,List<Attribute>> noeud: lobs){
			listeExplorables.add(noeud.getLeft());
		}
		//Difference des listes explorables et explorés
		String curNode ="";
		try{
			curNode = (String)listeExplorables.toArray()[0];
			agent.getGraph().addNodeExpl(curNode);
			listeExplorables.removeAll(agent.getListeExplores());
			//System.out.println(this.myAgent.getLocalName()+" -- list of explorables: "+listeExplorables);
		}catch(NullPointerException e){
			e.printStackTrace();
		}
		// Ajout des noeuds decouverts avec leur arete
				
		if(listeExplorables.size() > 0)
		{
			for(int i=0; i<listeExplorables.size();i++)
			{
				String newNode = (String)listeExplorables.toArray()[i];
				agent.getGraph().addNodeConnu(newNode);
				agent.getGraph().addEdge(curNode, (String)listeExplorables.toArray()[i]);
			}
		}
		
		
		//Little pause to allow you to follow what is going on
		/*try {
			System.out.println("Press a key to allow the agent "+this.myAgent.getLocalName() +" to execute its next move");
			System.out.println(this.myAgent.getLocalName()+" -- list of explored nodes: "+agent.getListeExplores());
			System.out.println(this.myAgent.getLocalName()+" -- list of known nodes after: "+agent.getGraph().getConnus());
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		
		
		
		// On se deplace vers un noeud connu a distance 1
		int i = 0;
		boolean avance = false;
		while(!avance && i < listeExplorables.size()){
			//essayer d'avancer
			avance = agent.moveTo((String)listeExplorables.toArray()[i]);
			if(avance){
				agent.resetEchecs();
				//agent.getGraph().addNodeExpl(((mas.abstractAgent)this.myAgent).getCurrentPosition());
				//agent.getGraph().getNode(curNode).removeAttribute("ui.color");
				//agent.getGraph().getNode(((mas.abstractAgent)this.myAgent).getCurrentPosition()).addAttribute("ui.color",1);
			}
			else
				System.out.println("Failure to move");
			i++;
		}
		
		// Sinon on se deplace vers le noeud "connu" le proche
		if(!avance)
		{
			agent.setPath(agent.getGraph().bfsToNearest(agent.getCurrentPosition()));
			if(!agent.getPath().isEmpty()){
				avance = agent.moveTo(agent.getPath().get(0));
				System.out.println("MY PATH IS : " + agent.getPath());
			}
			if(avance){
				agent.resetEchecs();
				agent.getPath().remove(0);
			}
			else{
				agent.incEchecs();
			}
		}
		
		// Si on a toujours pas avance et le nombre d'echecs a atteint un seuil, on essaye de se deplacer aleatoirement
		if(!avance && agent.getEchecs()==12){
			while(lobs.size() != 0 && !avance)
			{
				int j = agent.getRandom(lobs.size());
				avance = agent.moveTo(lobs.get(j).getLeft()); 
				if(!avance){
					lobs.remove(j);
				}
				else{
					agent.resetEchecs();
				}
			}
		}
		
		if(agent.getGraph().isExplored())
		{
			System.out.println("Fin parcours");
			//((mas.abstractAgent)this.myAgent).doDelete();
			System.out.println("Noeuds explores a la fin : " + agent.getListeExplores());
			this.stop();
		}
		agent.getGraphStream().miseAJour(agent.getGraph());
	}

}
