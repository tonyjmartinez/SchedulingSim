import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
public class Driver {
	public static void main(String[] argv) throws Exception{
		
		Scanner input = null;
		Scanner lineCounter = null;
        String buf, buf2;
        int lineCount = 0;
        
        try
        {
            input = new Scanner(Paths.get("C:/Users/ajosephmartinez/workspace/SchedSimv4/src/prog2files.txt"));
            lineCounter = new Scanner(Paths.get("C:/Users/ajosephmartinez/workspace/SchedSimv4/src/prog2files.txt"));
        }
        catch(Exception e)     
        {
            System.out.println("Error opening file!");
        }
        
        //line counter to tell me how many processes there will be
        while(lineCounter.hasNextLine()){
        	lineCount++;
        	lineCounter.nextLine();
        }

        //create a new job queue of the size line count
        JobQ mainJobQ = new JobQ(lineCount);
     
        //splits each line of the input file into tokens, creates processes of those tokens
        //adds these into the job queue in order of arrival time
        while(input.hasNext())  
        {
            if ( input != null ){
            	buf = input.nextLine();
            	String[] tokens = buf.split(" |,");
            	Process foo = new Process(Integer.parseInt(tokens[1]),Integer.parseInt(tokens[2]),tokens[0].charAt(0),Integer.parseInt(tokens[1]));
            	mainJobQ.addJobInOrder(foo);
            }
        } 
        
        //create linked lists for each of the schedulers
        LinkedList sjf = new LinkedList();
        LinkedList robin = new LinkedList();
        LinkedList srtf = new LinkedList();
        
        //feed the job queue into each list
        for(int i = 0; i < lineCount; i++){
        	robin.add(mainJobQ.theJobQ[i]); 
        }
        
        for(int i = 0; i < lineCount; i++){
        	sjf.add(mainJobQ.theJobQ[i]); 
        }

        for(int i = 0; i < lineCount; i++){
        	srtf.add(mainJobQ.theJobQ[i]); 
        }


        
        
        
        //run the schedulers then print out their stats
        printRobin(robin,3,1);
        printStats(robin.getHead());
        
        printSJF(sjf,'x',0);
        printStats(sjf.getHead());

        printSRTF(srtf,'x',0,0);
        printStats(srtf.getHead());
        
        input.close();
	}
	
//prints the stats for each scheduler, I get the turnaround time by taking the absolute value
//of the time variable of each process which ends up being negative at the end
//I had to make sure I only decremented time when a process still had burst left
//so that the end value would be the negative of the real turnaround time
public static void printStats(Process head){
	float waitAvg;
	float turnAvg;
	int count = 0;
	float waitSum = 0;
	float turnSum = 0;
	
	Process curPtr = head;
	
	System.out.println("Process ID\t\tTurnaround Time\t\tWaiting Time");
	while(curPtr != null){
		count++;
		int foo = Math.abs(curPtr.getTime()) - Math.abs(curPtr.getBurstCopy());
		int foo2 = Math.abs(curPtr.getTime());
		turnSum += foo2;
		waitSum += foo;
		System.out.println(curPtr.getName() + "\t\t\t" + foo2 + "\t\t\t" + foo);
		curPtr = curPtr.getNext();
	}
	
	turnAvg = turnSum/count;
	waitAvg = waitSum/count;
	
	System.out.printf("Average\t\t\t" + turnSum + '/' + count + " = %.2f\t\t" + 
	" " + waitSum + '/' + count + " = %.2f\n", turnAvg,waitAvg);
	System.out.println();
	
}


//wrote print functions for each scheduler to print out the header, the first 0, and complete
//to go along with the printout each scheduler function does
public static void printSRTF(LinkedList srtf, char curJob, int theTime, int curBurst){
	System.out.println("Shortest Remaing Time First Scheduling");
	System.out.print("0");
	SRTFsched(srtf,curJob,theTime,curBurst);
	System.out.print(" Complete\n");
}
public static void printRobin(LinkedList robin, int quantum, int theTime){
	System.out.println("Round Robin Scheduling");
	System.out.print("0");
	RobinScheduler(robin,quantum,theTime);
	System.out.print(" Complete\n");
}

public static void printSJF(LinkedList sjf, char curJob, int theTime){
	System.out.println("Shortest Job First Scheduling");
	System.out.print("0");
	SJFsched(sjf,curJob,theTime);
	System.out.print(" Complete\n");
}

//Robin Scheduler, moves forward until we are at a process that needs work, checks if
//quantum is 0 and if so moves that process to the appropriate spot
//finally decrements time on the appropriate processes
public static void RobinScheduler(LinkedList robin, int quantum, int theTime){
	Process curPtr = robin.getHead();
	Process prevPtr = curPtr;
	//move forward until we are at a process that has burst time left
	while (curPtr != null && curPtr.getBurst() <= 0){
		prevPtr = curPtr;
		curPtr = curPtr.getNext();
	}
	if(curPtr != null){
		//decrement time for processes that have arrived, if they
		//are done reset quantum and print process terminated
		if(curPtr.getTime() <= 0){
			curPtr.decBurst();
			if (curPtr.getBurst() == 0) {
				curPtr.decTime();
				quantum = 4;
				System.out.print(" " + curPtr.getName() + " Process Terminated\n" + theTime);
			}
		}

		//check if quantum expired, if so move the process back to after the most
		//recently arrived process
		quantum--;
		if(quantum == 0){
			System.out.print(" " + curPtr.getName() + " Quantum Expired\n" + theTime);
			Process tempPtr = robin.getHead();
			while(tempPtr.getNext() != null && tempPtr.getNext().getTime() <= 0){
				tempPtr = tempPtr.getNext();
			}
			if (curPtr != tempPtr){
				if (curPtr == robin.getHead()){
					robin.removeFirst();
				}
				else{
					prevPtr.setNext(curPtr.getNext());
				}
				curPtr.setNext(tempPtr.getNext());
				tempPtr.setNext(curPtr);
			}
			quantum = 3;
		}
		
		//decrements time on appropriate processes
		Process curPtr2 = robin.getHead();
		while(curPtr2 != null){
			if(curPtr2.getBurst() > 0) curPtr2.decTime();
			curPtr2 = curPtr2.getNext();
		}
		
		//increment time and run again with new list, quantum, and time
		theTime++;
		RobinScheduler(robin,quantum,theTime);
	}
}

//shortest job first scheduler. takes the linked list, a char called curjob which tells it which
//job is currently running, and a time variable as parameters. Moves forward in the list until
//we get to the current job, continues working on the current job until it's finished,
//chooses a new job by finding the smallest burst time
public static void SJFsched(LinkedList sjf, char curJob, int theTime){
	if(sjf != null){
	Process curPtr = sjf.getHead();
	int minVal = 999;//set to this value so we know if it's the first run
	if(curPtr != null){
		//move forward until current job
		while(curPtr != null && curPtr.getName() != curJob){
			curPtr = curPtr.getNext();
		}
		Process curPtr2 = sjf.getHead();
		if (curJob == 'x'){//'x' is the value we initially give the function, so this is the case of 1st run
			while(curPtr2 != null){
				if (curPtr2.getTime() <= 0 && curPtr2.getBurst() < minVal && curPtr2.getBurst() > 0){
					minVal = curPtr2.getBurst();
					curJob = curPtr2.getName();
				}
				curPtr2 = curPtr2.getNext();
			}
			if(minVal > 0 && minVal != 999){
				SJFsched(sjf,curJob,theTime);
			}
			else SJFsched(null,curJob,theTime);
		}
		//process has terminated, so print out and find next process to run
		if (curPtr != null && curPtr.getBurst() == 0){
			System.out.print(" " + curPtr.getName() + " Process Terminated\n" + theTime);
			while(curPtr2 != null){
				//runs through list and finds process with smallest burst
				if (curPtr2.getTime() <= 0 && curPtr2.getBurst() < minVal && curPtr2.getBurst() > 0){
					minVal = curPtr2.getBurst();
					curJob = curPtr2.getName();
				}
				curPtr2 = curPtr2.getNext();
			}
			if(minVal > 0 && minVal != 999){
				SJFsched(sjf,curJob,theTime);
			}
			else SJFsched(null,curJob,theTime);
		}
		else{
			//otherwise do work by decrementing burst and time
			if (curPtr != null){
				curPtr.decBurst();
				if(curPtr.getBurst() == 0) curPtr.decTime();
				theTime++;
				while(curPtr2 != null){
					if(curPtr2.getBurst() > 0){
						curPtr2.decTime();
					}
					curPtr2 = curPtr2.getNext();
				}
				SJFsched(sjf,curJob,theTime);
			}
		}
	}
	}
}

//Shortest Remaining Time First Scheduler, takes similar parameters to
//SJF, but also has a current burst int to keep track of the burst of 
//the current job(curJob) so we can preempt if necessary
public static void SRTFsched(LinkedList srtf, char curJob, int theTime, int curBurst){
	if(srtf != null){
	char curJob2 = curJob;
	Process curPtr = srtf.getHead();
	int minVal = 999;
	Process curPtr2 = curPtr;
	if(curPtr != null){
			while(curPtr != null){
				//for processes that have arrived, find smallest burst value
				if (curPtr.getTime() <= 0 && curPtr.getBurst() < minVal && curPtr.getBurst() > 0){
					minVal = curPtr.getBurst();
					curJob2 = curPtr.getName();
					curPtr2 = curPtr;
				}
				curPtr = curPtr.getNext();
			}
			if(minVal > 0 && minVal != 999){
				curPtr2.decBurst();
				theTime++;
				//case where we found a new process different from the process we
				//started the function with, which means it got preempted
				if (curPtr2.getName() != curJob && curBurst > 0){
					System.out.print(" " + curJob + " Process preempted by process with shorter burst time\n" +
				(theTime -1));
				}
				//if the burst hit 0, the process terminated
				if(curPtr2.getBurst() == 0){
					System.out.print(" " + curJob2 + " Process Terminated\n" + theTime);
					curPtr2.decTime();
				}
				//store burst of current process for next run
				curBurst = curPtr2.getBurst();
				curPtr2 = srtf.getHead();
				//for all the processes that still have work to do, decrement time so we 
				//get closer to them running
				while(curPtr2 != null){
					if(curPtr2.getBurst() > 0){
						curPtr2.decTime();
					}
					curPtr2 = curPtr2.getNext();
				}
				//run again with new parameters
				SRTFsched(srtf,curJob2,theTime,curBurst);
			}
			//termination, gives the function a null value
			else SRTFsched(null,curJob2,theTime,curBurst);
	}
	}
}
}
















