package graph;

import java.io.Serializable;
import java.util.ArrayList;

public class BlockMessage implements Serializable {

	private static final long serialVersionUID = -306846734056890294L;
	
	private String targetNode;
	private ArrayList<String> path;
	private int priorityLevel;
	
	public BlockMessage(String t, ArrayList<String> p, int prio)
	{
		this.targetNode = t;
		this.path = p;
		this.priorityLevel = prio;
	}
	
	public String getTarget() {
		return targetNode;
	}
	
	public ArrayList<String> getPath() {
		return path;
	}
	
	public int getPriority() {
		return priorityLevel;
	}
	
}
