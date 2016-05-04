package mas.behaviours;


import java.io.IOException;
import java.util.ArrayList;

import graph.Pair;
import mas.agents.ExploAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class TreasureBehaviour extends TickerBehaviour {
	private static final long period = 10;
	
	private static final long serialVersionUID = -9129138170018876510L;
	private boolean done = false;
	private boolean etape1 = true;
	private boolean sent = false;
	private boolean chosenOne = false;
	private int profondeur;
	private int timeout = 0;
	private Pair<String,Integer> tresor;
	private ArrayList<String> enfants = new ArrayList<String>();
	private ArrayList<Pair<String, Integer>> enfantSols = new ArrayList<Pair<String, Integer>>();
	private String parent = "";
	private String maxAg = this.myAgent.getLocalName();
	

	public TreasureBehaviour(Agent agent,Pair<String,Integer> tresor,int prof, String parent){
		super(agent, period);
		this.tresor = tresor;
		this.profondeur = prof;
		this.parent = parent;
	}


	@Override
	protected void onTick() {
		if(etape1 && profondeur!=0 && timeout < profondeur*2){
			ACLMessage msg=new ACLMessage(7);
			msg.setSender(this.myAgent.getAID());
			msg.setLanguage("treasure");
			
			try {
				Pair<Pair<String,Integer>,Integer> notif = new Pair<Pair<String,Integer>,Integer>(tresor,profondeur-1);
				msg.setContentObject(notif);
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
					msg.addReceiver(new AID(result[i].getName().getLocalName(),AID.ISLOCALNAME));
					System.out.println(this.myAgent.getLocalName() + " envoie : treasure  a " + result[i].getName().getLocalName());
				}
			}
			((mas.abstractAgent)this.myAgent).sendMessage(msg);
		}
		
		ACLMessage msgAck;
		final MessageTemplate msgTemplate = MessageTemplate.MatchLanguage("ack");
		do{
			msgAck = this.myAgent.receive(msgTemplate);
			if(msgAck != null){
				enfants.add(msgAck.getSender().getLocalName());
				etape1 = false;
			}
		}while (msgAck != null);
		
		if(profondeur == 0 && !sent){
			ACLMessage msgSol=new ACLMessage(7);
			msgSol.setSender(this.myAgent.getAID());
			msgSol.setLanguage("solution");
			msgSol.setContent(Integer.toString(((ExploAgent) this.myAgent).getBackPackFreeSpace()));
			msgSol.addReceiver(new AID(parent,AID.ISLOCALNAME));
			((mas.abstractAgent)this.myAgent).sendMessage(msgSol);
			sent = true;
			System.out.println(this.myAgent.getLocalName() + " envoie : solution = " + ((ExploAgent) this.myAgent).getBackPackFreeSpace());
		}
		else{
			ACLMessage msgChild;
			final MessageTemplate msgTemplate2 = MessageTemplate.MatchLanguage("solution");
			do{
				msgChild = this.myAgent.receive(msgTemplate2);
				if(msgChild != null){
					String child = msgChild.getSender().getLocalName();
					int value = Integer.parseInt(msgChild.getContent());
					enfantSols.add(new Pair<String, Integer>(child, value));
					System.out.println(this.myAgent.getLocalName() + " recoit : solution = " + value + " de la part de " + child);
				}
			}while (msgChild != null);
		}
		
		if(timeout > 3*profondeur && (enfants.size()==enfantSols.size() || timeout > 10*profondeur) && profondeur > 0 && !parent.equals("") && !sent)
		{
			int max = ((ExploAgent) this.myAgent).getBackPackFreeSpace();
			for(Pair<String,Integer> p : enfantSols){
				if(p.getSecond()>max){
					this.maxAg = p.getFirst();
					max = p.getSecond();
				}
			}
			ACLMessage msgSol=new ACLMessage(7);
			msgSol.setSender(this.myAgent.getAID());
			msgSol.setLanguage("solution");
			msgSol.setContent(Integer.toString(max));
			msgSol.addReceiver(new AID(parent,AID.ISLOCALNAME));
			((mas.abstractAgent)this.myAgent).sendMessage(msgSol);
			sent = true;
			System.out.println(this.myAgent.getLocalName() + " envoie : solution = " + max);
		}
		
		if(timeout > 3*profondeur && (enfants.size()==enfantSols.size() || timeout > profondeur*10) && parent.equals(""))
		{
			int max = ((ExploAgent) this.myAgent).getBackPackFreeSpace();
			for(Pair<String,Integer> p : enfantSols){
				if(p.getSecond()>max){
					this.maxAg = p.getFirst();
					max = p.getSecond();
				}
			}
			if(!maxAg.equals(this.myAgent.getLocalName()))
			{
				ACLMessage msgElu=new ACLMessage(7);
				msgElu.setSender(this.myAgent.getAID());
				msgElu.setLanguage("elu");
				msgElu.setContent(Integer.toString(max));
				msgElu.addReceiver(new AID(this.maxAg,AID.ISLOCALNAME));
				((mas.abstractAgent)this.myAgent).sendMessage(msgElu);
				System.out.println(this.myAgent.getLocalName() + " envoie : elu = " + max + " a " + maxAg);
			}
			else
			{
				// Agent racine est le max
				this.chosenOne = true;
			}
			ACLMessage msgElu=new ACLMessage(7);
			msgElu.setSender(this.myAgent.getAID());
			msgElu.setLanguage("elu");
			msgElu.setContent("-1");
			for(String enf : enfants) {
				if(!enf.equals(maxAg))
				{
					msgElu.addReceiver(new AID(enf,AID.ISLOCALNAME));
					System.out.println(this.myAgent.getLocalName() + " envoie : elu = -1 a " + enf);
				}
			}
			((mas.abstractAgent)this.myAgent).sendMessage(msgElu);
			
			done = true;
		}
		
		
		if(!parent.equals(""))
		{
			ACLMessage msgFinal;
			final MessageTemplate msgTemplate2 = MessageTemplate.MatchLanguage("elu");
			msgFinal = this.myAgent.receive(msgTemplate2);
			if(msgFinal != null){
				done = true;
				int value = Integer.parseInt(msgFinal.getContent());
				System.out.println(this.myAgent.getLocalName() + " recoit : elu = " + value + " de la part de " + msgFinal.getSender().getLocalName());
				if(enfants.size()>0){
					if(value == -1){
						ACLMessage msgElu=new ACLMessage(7);
						msgElu.setSender(this.myAgent.getAID());
						msgElu.setLanguage("elu");
						msgElu.setContent("-1");
						for(String enf : enfants) {
							msgElu.addReceiver(new AID(enf,AID.ISLOCALNAME));
							System.out.println(this.myAgent.getLocalName() + " envoie : elu = -1 a " + enf);
						}
						((mas.abstractAgent)this.myAgent).sendMessage(msgElu);
					}
					else{
						if(maxAg.equals(this.myAgent.getLocalName())){
							ACLMessage msgElu=new ACLMessage(7);
							msgElu.setSender(this.myAgent.getAID());
							msgElu.setLanguage("elu");
							msgElu.setContent("-1");
							for(String enf : enfants) {
								msgElu.addReceiver(new AID(enf,AID.ISLOCALNAME));
								System.out.println(this.myAgent.getLocalName() + " envoie : elu = -1 a " + enf);
							}
							((mas.abstractAgent)this.myAgent).sendMessage(msgElu);
							this.chosenOne = true;
						}
						else{
							ACLMessage msgElu=new ACLMessage(7);
							msgElu.setSender(this.myAgent.getAID());
							msgElu.setLanguage("elu");
							msgElu.setContent("-1");
							for(String enf : enfants) {
								if(!maxAg.equals(enf))
								{
									msgElu.addReceiver(new AID(enf,AID.ISLOCALNAME));
									System.out.println(this.myAgent.getLocalName() + " envoie : elu = -1 a " + enf);
								}
							}
							((mas.abstractAgent)this.myAgent).sendMessage(msgElu);
							ACLMessage msgElu2=new ACLMessage(7);
							msgElu2.setSender(this.myAgent.getAID());
							msgElu2.setLanguage("elu");
							msgElu2.setContent(Integer.toString(value));
							msgElu2.addReceiver(new AID(maxAg,AID.ISLOCALNAME));
							((mas.abstractAgent)this.myAgent).sendMessage(msgElu2);
							System.out.println(this.myAgent.getLocalName() + " envoie : elu = " + value + " a " + maxAg);
						}
					}
				} // enfants.size() > 0
				else{ // Pas d'enfants
					if(value != -1)
					{
						this.chosenOne = true;
					}
				}
			}
		}
		
		// TODO /!\ - Vider les messages parasites, Envoyer l'elu ramasser le tresor. verifier que tous les cas sont pris en compte
		if(done)
		{
			ExploAgent ag = (ExploAgent) this.myAgent;
			ag.viderBoiteReception();
			if(chosenOne){
				ArrayList<String> path = ag.getGraph().checkPath(ag.getCurrentPosition(), this.tresor.getFirst());
				this.myAgent.addBehaviour(new GoPickTreasureBehaviour(this.myAgent, path));
			}
			else{
				if(parent.equals(""))
				{
					ArrayList<String> path = ag.getGraph().bfsToNearest(ag.getCurrentPosition());
					ag.moveTo(path.get(0));
				}
				ag.restartExplo();
			}
			ag.getGraph().addTresorTraites(tresor.getFirst(), tresor.getSecond());
			this.stop();
		}
		
		if(timeout > 60){
			ExploAgent ag = (ExploAgent) this.myAgent;
			ag.viderBoiteReception();
			ag.restartExplo();
			this.stop();
		}
		
		timeout++;
	}
	
	
}
