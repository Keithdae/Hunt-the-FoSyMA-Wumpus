package mas.behaviours;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import mas.agents.ExploAgent;

public class WaitForSignalBehaviour extends TickerBehaviour {

	
	private static final long serialVersionUID = 6817451231945706417L;
	
	private int tickTimeout = 0;

	public WaitForSignalBehaviour(Agent a) {
		super(a, ((mas.agents.ExploAgent) a).getPeriod());
	}

	@Override
	protected void onTick() {
		tickTimeout++;
		mas.agents.ExploAgent agent = (mas.agents.ExploAgent)this.myAgent;
		ACLMessage msg;
		
		final MessageTemplate msgTemplate = MessageTemplate.MatchLanguage("signal");
		msg = this.myAgent.receive(msgTemplate);
		if(msg != null){
			String signal = msg.getContent();
			if(signal.equals("go"))
			{
				int fb = agent.getFormerBehaviour();
				if(fb == ExploAgent.COOP_WALK)
				{
					agent.restartExplo();
					this.stop();
				}
				else if(fb == ExploAgent.GO_PICK_TREASURE)
				{
					agent.restartGoPick();
					this.stop();
				}
			}
		}
		if(tickTimeout>50)
		{
			int fb = agent.getFormerBehaviour();
			if(fb == ExploAgent.COOP_WALK)
			{
				agent.restartExplo();
				this.stop();
			}
			else if(fb == ExploAgent.GO_PICK_TREASURE)
			{
				agent.restartGoPick();
				this.stop();
			}
		}
	}

}
