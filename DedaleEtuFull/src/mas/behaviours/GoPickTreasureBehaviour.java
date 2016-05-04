package mas.behaviours;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
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
			moved = agent.moveTo(path.get(0));
			if(!moved)
			{
				// Interblocage
			}
			else
			{
				path.remove(0);
			}
		}
		else
		{
			int value = agent.pick();
			System.out.println("I picked : " + value);
			agent.restartExplo();
			this.stop();
		}
	}

}
