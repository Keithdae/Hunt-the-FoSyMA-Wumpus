package mas.behaviours;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;

public class GoPickTreasureBehaviour extends TickerBehaviour {

	private static final long serialVersionUID = -7769587262621071102L;
	
	private ArrayList<String> path;

	public GoPickTreasureBehaviour(Agent a, ArrayList<String> path) {
		super(a,((mas.agents.ExploAgent)a).getPeriod());
		this.path = path;
	}

	@Override
	protected void onTick() {
		mas.agents.ExploAgent agent = (mas.agents.ExploAgent)this.myAgent;
		boolean moved = false;
		if(!path.isEmpty())
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
			
			moved = agent.moveTo(path.get(0));
			if(!moved)
			{
				// Interblocage
				agent.incEchecs();
				agent.setBlockNode(path.get(0));
				agent.setBlock(true);
			}
			else
			{
				path.remove(0);
				agent.resetEchecs();
			}
		}
		else
		{
			int value = agent.betterPickUp();
			System.out.println("I picked : " + value);
			agent.setPriorityToNone();
			agent.setTreasureGoal("");
			agent.restartExplo();
			this.stop();
		}
	}

}
