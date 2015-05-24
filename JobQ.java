import java.util.*;

public class JobQ {

	Process[] theJobQ;
	final int qSize;
	private int front,last = 0;
	private int numItems = 0;
	
	JobQ(int size){
		theJobQ = new Process[size];
		qSize = size;
	}
	public void addJobInOrder(Process a){
		int i;
		
		//first process, add to the head
		if(numItems == 0){
			theJobQ[last] = a;
			last++;
			numItems++;
		}
		else{
			//if not first process move processes forward
			//to make room based on arrival time
			for(i = numItems - 1; i >= 0; i--){
				if(a.getTime() < theJobQ[i].getTime()){
					theJobQ[i+1] = theJobQ[i];
				} else break;
			}
			
			theJobQ[i+1] = a;
			
			last++;
			numItems++;
		}
	}
	
	public void print(){
		for(int i = 0; i < qSize; i++){
			if(theJobQ[i] != null)
			System.out.println("Time:  " +theJobQ[i].getTime() + " Name: " + theJobQ[i].getName() +
					" Burst: " + theJobQ[i].getBurst());
		}
	}
	
}
