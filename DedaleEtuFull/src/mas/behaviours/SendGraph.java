package mas.behaviours;

import java.io.IOException;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;


public class SendGraph extends TickerBehaviour{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2058134622078521998L;

	/**
	 * An agent tries to contact its friend and to give him its current position
	 * @param myagent the agent who posses the behaviour
	 *  
	 */
	public SendGraph (final Agent myagent) {
		super(myagent, 10);
		//super(myagent);
	}

	@Override
	public void onTick() {
		//ExploAgent ag  = (ExploAgent) this.myAgent;
		//ag.restartExplo();
		
		String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();

		ACLMessage msg=new ACLMessage(7);
		msg.setSender(this.myAgent.getAID());
		msg.setLanguage("graph");
		if (myPosition!=""){
			//System.out.println("Agent "+this.myAgent.getLocalName()+ " is trying to reach its friends");
			try {
				msg.setContentObject(((mas.agents.ExploAgent)this.myAgent).getGraphSerial());
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for(int i=0;i<result.length;i++){
				if(result[i].getName().getLocalName()!=myAgent.getLocalName()){
					msg.addReceiver(new AID(result[i].getName().getLocalName(),AID.ISLOCALNAME));
				}
			}

			((mas.abstractAgent)this.myAgent).sendMessage(msg);

		}

	}

}