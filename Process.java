//process class, has burst,arrival time,process name,
//and burst copy which is a copy of the original burst time
public class Process {
	private int burst;
	private int time;
	private char procName;
	private int burstCopy;
	
	Process next;
	
	Process(){}
	Process(int a, int b, char c, int d){
		this.burst = a;
		this.time = b;
		this.procName = c;
		this.burstCopy = d;
	}
	
	void decrementTime(){
		this.time--;
	}
	
	public int getBurstCopy(){
		return this.burstCopy;
	}
	
	void decrementBurst(){
		this.burst--;
	}
	
	void printProc(){
		System.out.println("Burst:" + burst + " Time:" + time + "Process Name:" + procName );
	}
	
	public int getTime(){
		return this.time;
	}
	
	public char getName(){
		return this.procName;
	}
	
	public int getBurst(){
		return this.burst;
	}
	
	public void decBurst(){
		this.burst--;
	}
	
	public void decTime(){
		this.time--;
	}
	public String toString(){
		String a = "Burst:" + burst + " Time:" + time + "Process Name:" + procName;
		return a;
	}
	
	public Process getNext(){
		return this.next;
	}
	
	public void setNext(Process a){
		this.next = a;
	}
}
