

//a standard linked list
public class LinkedList {
	private Process first;
	private int count;
	
	public LinkedList(){
		first = null;
		count = 0;
	}
	
	public void print(){
		Process foo = first;
		while(foo != null){
			foo.printProc();
			foo = foo.getNext();
		}
	}
	
	public void add(Process a){
		Process temp = new Process(a.getBurst(),a.getTime(),a.getName(),a.getBurstCopy());
		Process curPtr = first;
		if(first == null){
			this.first = temp;
			this.first.setNext(null);
		}
		else{
			while(curPtr.getNext() != null){
				curPtr = curPtr.getNext();
			}
		
			curPtr.setNext(temp);
			count++;
		}
	}
	public Process getHead(){
		return first;
	}
	
	public void removeFirst(){
		first = first.next;
	}
}
