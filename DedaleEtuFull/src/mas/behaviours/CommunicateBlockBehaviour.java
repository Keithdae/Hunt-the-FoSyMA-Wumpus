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
							String freeSpace = agent.getGraph().findFreeSpace(blockRecu.getPath());
							if(!freeSpace.equals(""))
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
			BlockMessage bm = new BlockMessage(agent.getBlockNode(), agent.getPath(), agent.getPriorityLevel());
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
