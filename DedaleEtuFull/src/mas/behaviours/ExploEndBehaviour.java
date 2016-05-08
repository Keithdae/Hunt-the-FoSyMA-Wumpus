package mas.behaviours;

import java.util.List;

import env.Attribute;
import env.Couple;
import graph.Graphe;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class ExploEndBehaviour extends TickerBehaviour {

	private static final long serialVersionUID = -926015720881355370L;

	private boolean done = false;
	
	public ExploEndBehaviour(Agent a) {
		super(a, ((mas.agents.ExploAgent)a).getPeriod());
	}

	@Override
	protected void onTick() {
		mas.agents.ExploAgent agent = (mas.agents.ExploAgent)this.myAgent;
		ACLMessage msg;
		do{
			final MessageTemplate msgTemplate = MessageTemplate.MatchLanguage("graph");
			msg = agent.receive(msgTemplate);
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
		
		
		// Exploration finie et plus d'espace, cet agent n'a plus de r�le particulier
		if(agent.backpackFull() && !done){
			System.out.println(agent.getLocalName() + " can't pick anything more, already has : " + agent.getTreasurePicked());
			done = true;
			agent.getPath().clear();
		}
		
		// On va remplir notre espace restant
		if(!done)
		{
			System.out.println(agent.getLocalName() + " is trying to pick some additional treasure, its free space : " + agent.getBackPackFreeSpace());
			
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
			
			// On se dirige vers un tr�sor
			if(!agent.getPath().isEmpty())
			{
				boolean avance;
				String curNode = agent.getCurrentPosition();
				avance = agent.moveTo(agent.getPath().get(0));
				if(avance){
					agent.resetEchecs();
					agent.getPath().remove(0);
					agent.getGraphStream().getNode(curNode).removeAttribute("ui.color");
					agent.getGraphStream().getNode(agent.getCurrentPosition()).addAttribute("ui.color",1);
				}
				else{
					agent.incEchecs();
					agent.setBlockNode(agent.getPath().get(0));
					agent.setBlock(true);
				}
			}
			else // Si on est sur un tr�sor => on ramasse / sinon on se dirige vers un autre tr�sor
			{
				List<Couple<String,List<Attribute>>> lobs=((mas.abstractAgent)this.myAgent).observe();
				
				if(lobs.get(0).getRight().contains(Attribute.TREASURE)){
					int v = (Integer)lobs.get(0).getRight().get(0).getValue(); // valeur du tresor
					int p = agent.betterPickUp();
					System.out.println("I picked : " + p);
					if(v == p) // On a tout pris
					{
						// On retire le tresor retenu dans le graphe mais entierement ramasse en realite
						if(agent.getGraph().isTreasureNode(agent.getCurrentPosition()))
						{
							agent.getGraph().removeTreasure(agent.getCurrentPosition());
						}
					}
				}
				else {
					// On retire le tresor retenu dans le graphe mais entierement ramasse en realite
					if(agent.getGraph().isTreasureNode(agent.getCurrentPosition()))
					{
						agent.getGraph().removeTreasure(agent.getCurrentPosition());
					}
				}
				
				agent.setPath(agent.getGraph().checkPath(agent.getCurrentPosition(), agent.getGraph().getMaxTreasure(agent.getCurrentPosition()))); // TODO
			}
		}
	}

}
