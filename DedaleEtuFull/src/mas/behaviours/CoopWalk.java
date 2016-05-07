package mas.behaviours;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import env.Attribute;
import env.Couple;
import graph.Graphe;
import graph.Pair;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class CoopWalk extends TickerBehaviour {
	
	private boolean mustStop = false;

	public CoopWalk(final mas.abstractAgent myagent) {
		super(myagent,((mas.agents.ExploAgent)myagent).getPeriod());
		// TODO Auto-generated constructor stub
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void onTick() {
		// TODO Auto-generated method stub
		mas.agents.ExploAgent agent = (mas.agents.ExploAgent)this.myAgent;
		ACLMessage msg;
		do{
			final MessageTemplate msgTemplate = MessageTemplate.MatchLanguage("graph");
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
		
		//il y a-t-il des trésors autour?
		final MessageTemplate msgTemplate = MessageTemplate.MatchLanguage("treasure");
		msg = this.myAgent.receive(msgTemplate);
		if(msg != null) {
			try {
				@SuppressWarnings("unchecked")
				Pair<Pair<String,Integer>,Integer> infotresor = (Pair<Pair<String,Integer>,Integer>) msg.getContentObject();
				if(!agent.getGraph().checkPath(agent.getCurrentPosition(), infotresor.getFirst().getFirst()).isEmpty() && agent.getBackPackFreeSpace() > 0)
				{
					agent.restartTreasuring(infotresor.getFirst(), infotresor.getSecond(), msg.getSender().getLocalName());
					ACLMessage ack=new ACLMessage(7);
					ack.setSender(this.myAgent.getAID());
					ack.setLanguage("ack");
					ack.setContent("ack");
					ack.addReceiver(msg.getSender());
					((mas.abstractAgent)this.myAgent).sendMessage(ack);
					this.stop();
					mustStop = true;
				}
			} catch(UnreadableException e){
				e.printStackTrace();
			}
		}
		
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
		// Localisation des tresors
		if(lobs.get(0).getRight().contains(Attribute.TREASURE)){
			agent.getGraph().addTresor(lobs.get(0).getLeft(),(Integer)lobs.get(0).getRight().get(0).getValue());
			Pair<String,Integer> tresor = new Pair<String,Integer>(lobs.get(0).getLeft(),(Integer)lobs.get(0).getRight().get(0).getValue());
			if(agent.getBackPackFreeSpace() > 0 && !agent.getGraph().estTraite(tresor))
			{
				agent.restartTreasuring(tresor, 3, "");
				this.stop();
				mustStop = true;
			}
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
		
		agent.getGraphStream().miseAJour(agent.getGraph());
		
		// Si on a fini l'exploration
		if(agent.getGraph().isExplored())
		{
			System.out.println("Fin parcours");
			//((mas.abstractAgent)this.myAgent).doDelete();
			System.out.println("Noeuds explores a la fin : " + agent.getListeExplores());
			System.out.println("Tresors trouves : " + agent.getGraph().getTresors());
			System.out.println("Mon espace libre : " + agent.getBackPackFreeSpace());
			System.out.println("Ma quantite de tresor ramassee : " + agent.getTreasurePicked());
			this.stop();
			mustStop = true;
		}
		
		if(!mustStop) // Si le Behaviour n'est pas fini, on continue a explorer
		{
			// Un agent nous a laisse passer et attend un signal depuis un noeud adjacent pour pouvoir repartir
			if(!agent.getNodeSignal().equals("") && agent.getCurrentPosition().equals(agent.getNodeSignal())
					&& agent.getAgentToSignal() != null)
			{
				ACLMessage msgSignal = new ACLMessage(7);
				msgSignal.setSender(this.myAgent.getAID());
				msgSignal.setLanguage("signal");
				msgSignal.setContent("go");
				msgSignal.addReceiver(agent.getAgentToSignal());
				((mas.abstractAgent)this.myAgent).sendMessage(msgSignal);
				agent.setNodeSignal("");
				agent.setAgentToSignal(null);
			}
			
			// On se deplace vers un noeud connu a distance 1
			int i = 0;
			boolean avance = false;
			while(!avance && i < listeExplorables.size()){
				//essayer d'avancer
				avance = agent.moveTo((String)listeExplorables.toArray()[i]);
				if(avance){
					agent.resetEchecs();
					//agent.getGraph().addNodeExpl(((mas.abstractAgent)this.myAgent).getCurrentPosition());
					agent.getGraphStream().getNode(curNode).removeAttribute("ui.color");
					agent.getGraphStream().getNode(((mas.abstractAgent)this.myAgent).getCurrentPosition()).addAttribute("ui.color",1);
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
					agent.getGraphStream().getNode(curNode).removeAttribute("ui.color");
					agent.getGraphStream().getNode(((mas.abstractAgent)this.myAgent).getCurrentPosition()).addAttribute("ui.color",1);
				}
				else{
					agent.incEchecs();
					agent.setBlockNode(agent.getPath().get(0));
					agent.setBlock(true);
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
						agent.getGraphStream().getNode(curNode).removeAttribute("ui.color");
						agent.getGraphStream().getNode(((mas.abstractAgent)this.myAgent).getCurrentPosition()).addAttribute("ui.color",1);
					}
				}
			}	
		}
	} // OnTick	
	
	
}
