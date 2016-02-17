package graph;

import java.io.Serializable;

public class Pair<T1, T2> implements Serializable{

	private static final long serialVersionUID = 7505507600319811364L;
	
	final private T1 first;
	final private T2 second;
	

    public Pair(T1 first, T2 second) {
    	this.first = first;
    	this.second = second;
    }

    public int hashCode() {
    	int hashFirst = first != null ? first.hashCode() : 0;
    	int hashSecond = second != null ? second.hashCode() : 0;

    	return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

    public boolean equals(Object other) {
    	if (other instanceof Pair) {
    		@SuppressWarnings("rawtypes")
			Pair otherPair = (Pair) other;
    		return 
    		((  this.first == otherPair.first ||
    			( this.first != null && otherPair.first != null &&
    			  this.first.equals(otherPair.first))) &&
    		 (	this.second == otherPair.second ||
    			( this.second != null && otherPair.second != null &&
    			  this.second.equals(otherPair.second))) );
    	}

    	return false;
    }

    public String toString()
    { 
           return "(" + first + ", " + second + ")"; 
    }

    public T1 getFirst() {
    	return first;
    }


    public T2 getSecond() {
    	return second;
    }

}
