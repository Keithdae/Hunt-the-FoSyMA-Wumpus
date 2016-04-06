package mas.behaviours;

import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.behaviours.SimpleBehaviour;

public class MigrationBehaviour extends SimpleBehaviour {

	private static final long serialVersionUID = -1297266419276493338L;

	public MigrationBehaviour() {
		// TODO Auto-generated constructor stub
	}

	public MigrationBehaviour(Agent a) {
		super(a);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub
		ContainerID cID= new ContainerID();
		cID.setName("MyDistantContainer0");
		cID.setPort("8888");
		cID.setAddress("132.227.112.239");
	
		
		this.myAgent.doMove(cID);
	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return true;
	}

}
