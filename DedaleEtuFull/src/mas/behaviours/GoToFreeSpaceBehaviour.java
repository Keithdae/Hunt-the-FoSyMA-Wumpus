package mas.behaviours;

import java.util.ArrayList;
import java.util.List;

import env.Attribute;
import env.Couple;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class GoToFreeSpaceBehaviour extends TickerBehaviour {

	private ArrayList<String> path;
	
	public GoToFreeSpaceBehaviour(Agent a, ArrayList<String> p) {
		super(a, ((mas.agents.ExploAgent) a).getPeriod());
		path = p;
	}

	private static final long serialVersionUID = -3772298727160635396L;

	@Override
	protected void onTick() {
		mas.agents.ExploAgent agent = (mas.agents.ExploAgent)this.myAgent;
		
		boolean avance = false;
		boolean done = false;
		String curNode = agent.getCurrentPosition();
		if(!path.isEmpty()){
			avance = agent.moveTo(path.get(0));
			System.out.println(agent.getLocalName() + " MY PATH TO FREE SPACE IS : " + path);
		}
		else {
			this.stop();
			done = true;
			agent.restartWaitForSignal();
		}
		
		if(!done)
		{
			if(avance){
				agent.resetEchecs();
				path.remove(0);
				agent.getGraphStream().getNode(curNode).removeAttribute("ui.color");
				agent.getGraphStream().getNode(((mas.abstractAgent)this.myAgent).getCurrentPosition()).addAttribute("ui.color",1);
			}
			else{
				agent.incEchecs();
				agent.setBlockNode(path.get(0));
				agent.setBlock(true);
			}
			
			if(agent.getEchecs() > 10 && !avance)
			{
				List<Couple<String,List<Attribute>>> lobs=agent.observe();
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
						agent.getGraphStream().getNode(agent.getCurrentPosition()).addAttribute("ui.color",1);
					}
				}
			}
		}
		
		
	}

}
