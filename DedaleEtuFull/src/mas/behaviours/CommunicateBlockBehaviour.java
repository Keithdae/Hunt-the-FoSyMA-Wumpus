package mas.behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.agents.ExploAgent;

import java.io.IOException;
import java.util.ArrayList;

import graph.BlockMessage;

public class CommunicateBlockBehaviour extends TickerBehaviour {

	public CommunicateBlockBehaviour(Agent a) {
		super(a, ((mas.agents.ExploAgent) a).getPeriod());
	}

	private static final long serialVersionUID = -1341735220936583574L;

	@Override
	protected void onTick() {
		mas.agents.ExploAgent agent = (mas.agents.ExploAgent)this.myAgent;
		
		BlockMessage blockRecu = null;
		
		ACLMessage msg;
		do{
			final MessageTemplate msgTemplate = MessageTemplate.MatchLanguage("block");
			msg = this.myAgent.receive(msgTemplate);
			if(msg != null){
				try {
					blockRecu = (BlockMessage) msg.getContentObject();
					if(blockRecu.getTarget().equals(agent.getCurrentPosition())) // Si on le bloque
					{
						if(agent.getPriorityLevel() <= blockRecu.getPriority()) // Si on est moins prioritaire
						{
							if(blockRecu.getPath().size() > 1) // Si l'agent bloque dispose d'un chemin a parcourir
							{
								String freeSpace = agent.getGraph().findFreeSpace(blockRecu.getPath());
								if(!freeSpace.equals("")) // Si on peut laisser passer l'autre agent
								{
									// Chemin vers l'espace libre
									ArrayList<String> path = agent.getGraph().checkPath(agent.getCurrentPosition(), freeSpace);
									// On cherche le noeud voisin de l'espace ou passe celui qu'on a bloque 
									ArrayList<String> neigh = agent.getGraph().voisins(freeSpace);
									neigh.retainAll(blockRecu.getPath());
									String signalNode = "";
									if(neigh.size() > 0 && blockRecu.getPath().contains(neigh.get(0)))
									{
										signalNode = neigh.get(0);
									}
									// On envoie le noeud a l'agent que l'on bloque
									ACLMessage msgNodeSignal = new ACLMessage(7);
									msgNodeSignal.setSender(this.myAgent.getAID());
									msgNodeSignal.setLanguage("nodesignal");
									msgNodeSignal.setContent(signalNode);
									msgNodeSignal.addReceiver(msg.getSender());
									// On se rend vers l'espace libre
									agent.restartGoToFreeSpace(path);
									switch(blockRecu.getPriority()) {
									case ExploAgent.UNBLOCK_TREASURE_PRIO:
										agent.setPriorityToUnblockTreasure();
										break;
									case ExploAgent.TREASURE_PRIO:
										agent.setPriorityToUnblockTreasure();
										break;
									case ExploAgent.UNBLOCK_PRIO:
										agent.setPriorityToUnblock();
										break;
									case ExploAgent.NO_PRIO:
										agent.setPriorityToUnblock();
										break;
									default:
										System.out.println("INCONSISTENT PRIORITY LEVEL RECEIVED");
									}
									agent.setBlock(false);
									this.stop();
								}
								else // On ne connait pas de moyen pour laisser passer l'agent, c'est � lui de bouger
								{
									// On s'assure d'envoyer un message block a cet agent
									agent.setBlock(true);
									agent.setBlockNode(blockRecu.getPath().get(0));
									agent.setSentBlock(false);
									// On triche sur notre priorite
									agent.setPriorityToUnblockTreasure();
								}
							}
							else // Si il n'a pas de chemin, on cherche un autre moyen de resolution
							{
								System.out.println("Ca n'aurait pas du arriver, oops.");
							}
						}
						else // Cet agent est moins prioritaire, c'est a lui de me laisser passer
						{
							// On s'assure d'envoyer un message block a cet agent
							agent.setBlock(true);
							agent.setBlockNode(blockRecu.getPath().get(0));
							agent.setSentBlock(false);
						}
					}
				} catch(UnreadableException e){
					e.printStackTrace();
				}
			}
		}while(msg != null);
		
		
		do{
			final MessageTemplate msgTemplate = MessageTemplate.MatchLanguage("nodesignal");
			msg = this.myAgent.receive(msgTemplate);
			if(msg != null){
				String nodeSig = msg.getContent();
				agent.setNodeSignal(nodeSig);
				agent.setAgentToSignal(msg.getSender());
			}
		}while(msg != null);
		
		if(agent.getBlocked() && !agent.getSentBlock()) // Si l'agent est bloque, on essaye de communiquer avec l'agent concerne
		{
			@SuppressWarnings("unchecked")
			ArrayList<String> path = (ArrayList<String>) agent.getPath().clone();
			path.add(0, agent.getCurrentPosition());
			BlockMessage bm = new BlockMessage(agent.getBlockNode(), path, agent.getPriorityLevel());
			ACLMessage msgBlock = new ACLMessage(7);
			msgBlock.setSender(this.myAgent.getAID());
			msgBlock.setLanguage("block");
			try {
				msgBlock.setContentObject(bm);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd  = new ServiceDescription();
			sd.setType("explorer"); /* le même nom de service que celui qu'on a déclaré*/
			dfd.addServices(sd);

			DFAgentDescription[] result = null;
			try {
				result = DFService.search((mas.abstractAgent)this.myAgent, dfd);
			} catch (FIPAException e) {
				e.printStackTrace();
			}

			for(int i=0;i<result.length;i++){
				if(result[i].getName().getLocalName()!=myAgent.getLocalName()){
					msgBlock.addReceiver(new AID(result[i].getName().getLocalName(),AID.ISLOCALNAME));
					System.out.println(this.myAgent.getLocalName() + " envoie : treasure  a " + result[i].getName().getLocalName());
				}
			}
			((mas.abstractAgent)this.myAgent).sendMessage(msgBlock);
			agent.setSentBlock(true);
		}
	}

}
