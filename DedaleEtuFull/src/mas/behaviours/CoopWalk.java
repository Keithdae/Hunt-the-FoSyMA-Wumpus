package mas.behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import env.Attribute;
import env.Environment.Couple;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class CoopWalk extends TickerBehaviour {

	public CoopWalk(final mas.abstractAgent myagent) {
		super(myagent,1000);
		// TODO Auto-generated constructor stub
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void onTick() {
		// TODO Auto-generated method stub
		ACLMessage msg;
		do{
			final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			msg = this.myAgent.receive(msgTemplate);
			if(msg != null){
				//Union des listes des noeuds explorés
				try {
					Collection<String> listeRecue = (Collection<String>)msg.getContentObject();
					((mas.agents.DummyExploAgent)this.myAgent).getListeExplores().addAll(listeRecue);
				} catch(UnreadableException e){
					e.printStackTrace();
				}
			}
		}while(msg != null);
		
		//Listes des noeuds voisins atteignables
		List<Couple<String,List<Attribute>>> lobs=((mas.abstractAgent)this.myAgent).observe();
		System.out.println(this.myAgent.getLocalName()+" -- list of observables: "+lobs);

		//Little pause to allow you to follow what is going on
		try {
			System.out.println("Press a key to allow the agent "+this.myAgent.getLocalName() +" to execute its next move");
			System.out.println(this.myAgent.getLocalName()+" -- list of explored nodes: "+((mas.agents.DummyExploAgent)this.myAgent).getListeExplores());
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Collection<String> listeExplorables = new ArrayList<String>();
		for(Couple<String,List<Attribute>> noeud: lobs){
			listeExplorables.add(noeud.getLeft());
		}
		//Difference des listes explorables et explorés
		try{
			listeExplorables.removeAll( ((mas.agents.DummyExploAgent)this.myAgent).getListeExplores());
		}catch(NullPointerException e){
			//e.printStackTrace();
		}
		int i = 0;
		boolean avance = false;
		while(!avance && i < listeExplorables.size()){
			//essayer d'avancer
			avance = ((mas.agents.DummyExploAgent)this.myAgent).moveTo((String)listeExplorables.toArray()[i]);
			if(avance){
				((mas.agents.DummyExploAgent)this.myAgent).addToExplores(((mas.abstractAgent)this.myAgent).getCurrentPosition());
			}
			i++;
		}
		
		if(!avance)
		{
			while(lobs.size() != 0 && !avance)
			{
				int j = ((mas.agents.DummyExploAgent)this.myAgent).getRandom(lobs.size());
				avance = ((mas.agents.DummyExploAgent)this.myAgent).moveTo(lobs.get(j).getLeft()); 
				if(!avance){
					lobs.remove(j);
				}
			}
		}
	}

}
