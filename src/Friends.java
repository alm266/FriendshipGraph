/*
 * 
 * CS112 Friendship Graph Algorithm Assignment
 * 
 * Teerapad Jindachomthong
 * Andrew Marshall
 * 
 *
 * */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
//import java.io.IOException;

class Neighbor{
	public int vertexNum;
	public Neighbor next;
	public Neighbor(int vnum, Neighbor nbr){
		this.vertexNum=vnum;
		next=nbr;
	}
	
}

class Vertex{
	String name;
	String schoolname;
	Neighbor adjList;
		public Vertex(String name, String schoolname, Neighbor neighbors){
			this.name=name;
			this.schoolname=schoolname;
			this.adjList=neighbors;	
		}
}

public class Friends {

	static Vertex[] adjLists;
	static boolean[] visited;
	static boolean[] endpoint;
	static int[] dfsNum;
	static int[] backNum;
	static boolean[] connectors;
	
	public static void main(String[] args) throws Exception {
		//User Interface
		String filename;
		int choice;
		Scanner input= new Scanner(System.in);
		
		System.out.print("Enter the filename: ");
		filename= input.nextLine();
		System.out.println();
		Graph(filename);
		//printGraph();
		
		boolean run=true;
		
		while(run){
			System.out.println("Please enter a number: ");
			System.out.println("(1) Shortest Intro Chain");
			System.out.println("(2) Cliques at School");
			System.out.println("(3) Connectors");
			System.out.println("(4) Quit");
			choice= input.nextInt();
	
			switch(choice){
				case 1:
					System.out.println("Enter the name of the person who wants an introduction: ");
					String name1=input.next();
					System.out.println("Enter the name of the person who is being introduced: ");
					String name2=input.next();
					shortestPath(name1,name2);
					continue;
				case 2:
					System.out.println("Enter the name of the school: ");
					String schoolname=input.next();
					cliques(schoolname);
					continue;
				case 3:
					connectors();
					continue;
				case 4:
					System.out.println("Goodbye!");
					run=false;
					break;
				default:
					System.out.println("Invalid choice number");
					continue;
			}
		}
		
	}
	
	public static void Graph(String filename)throws FileNotFoundException{ 
		
		Scanner sc= new Scanner(new File(filename)); //throws exception
		adjLists = new Vertex[sc.nextInt()];
		sc.nextLine();
		//Reads vertices
		for(int i=0; i<adjLists.length;i++){
			String vertexdata=sc.nextLine();
			//System.out.print(vertexdata+": ");
			//Splits up vertex data into name, school
			String vname=null, vschool=null;
			char schoolexists='n';
			for(int j=0, endsubstring=0;j<vertexdata.length();j++){
				if(vertexdata.charAt(j)=='|'){
					endsubstring=j;
					if(vname==null){
						vname=vertexdata.substring(0,endsubstring);
						schoolexists=vertexdata.charAt(j+1);
						if(vname!=null && schoolexists=='y'){
							vschool=vertexdata.substring(j+3,vertexdata.length());
						}				
					}
				}		
			}
			
			//Inserts beginning vertex			
			adjLists[i]= new Vertex(vname, vschool,null);
			//System.out.println(vname);
		}
		
		//Inserts Edges
		while(sc.hasNext()){
			
			//reads vertex names and translates to vertex numbers
			
			//Splits up relationship data
			String edgedata=sc.nextLine();
			String v1name=null, v2name=null;
			
			for(int j=0, endsubstring=0;j<edgedata.length();j++){
				if(edgedata.charAt(j)=='|'){
					endsubstring=j;
					v1name=edgedata.substring(0,endsubstring);
					v2name=edgedata.substring(j+1,edgedata.length());
				}		
			}
			
			
			int v1= indexForName(v1name); 
			int v2= indexForName(v2name); 
	
			if(v1==-1 || v2==-1){
				System.out.println("Improperly formatted txt file");
				System.out.println("Now exiting program");
				System.exit(0);
			}
			
			//add v2 to the front of v1's linked list and v1 to the front of v2's linked list
			adjLists[v1].adjList= new Neighbor(v2, adjLists[v1].adjList);
			adjLists[v2].adjList= new Neighbor(v1, adjLists[v2].adjList);
		}
		
	}	
	
	public static int indexForName(String name){
		for(int v=0; v<adjLists.length; v++){
			if(adjLists[v].name.equals(name)){
				return v;
			}		
		}
		return -1;
	}
	
	
	//Used to check graph
	public static void printGraph(){
		for(int v=0; v<adjLists.length;v++){
			System.out.print(adjLists[v].name+"("+adjLists[v].schoolname+")");
			for(Neighbor nbr=adjLists[v].adjList; nbr!=null; nbr=nbr.next){
				System.out.print(" ---> "+adjLists[nbr.vertexNum].name);	
			}
			System.out.println();
			System.out.println();
		}
	}
	
	/*
	 * Input: Name of person who wants the intro, and the name of the other person
	 * Result: The shortest chain of people in the graph starting at the first and ending at the second.
	 * Output: Print the chain of people in the shortest path, for example:
	 * If there is no way to get from the first person to the second person, 
	 * then the output should be a message to this effect. 
	 */
	public static void shortestPath(String name1, String name2){
		//find the corresponding vertex nums
		int v1=-1, v2=-1;
		v1=indexForName(name1);
		v2=indexForName(name2);
		
		if(v1==-1){
			System.out.println(name1+" not found");
			System.exit(0);
		}
		if(v2==-1){
			System.out.println(name2+" not found");
			System.exit(0);
		}
		
		
		//two shortest paths??
		
		//breadth first search, storing the path
		boolean[] visited= new boolean[adjLists.length];
		int[] prev= new int[adjLists.length];
		
		for(int v=0;v<adjLists.length;v++){
			visited[v]=false;
			prev[v]=-1;
		}
		
		//empty queue
		Queue<Integer> q= new LinkedList<Integer>();
		
		visited[v1]=true;
		q.add(v1);
		
		while(!q.isEmpty()){
			int i=q.remove();
			for(Neighbor nbr=adjLists[i].adjList; nbr!=null; nbr=nbr.next){
				//System.out.println("checking neighbor "+adjLists[nbr.vertexNum].name);
				if(visited[nbr.vertexNum]==false){
					visited[nbr.vertexNum]=true;
					prev[nbr.vertexNum]=i;
					q.add(nbr.vertexNum);
				}
				if(nbr.vertexNum==v2){//name2 found
					int temp=v2;
					Stack<Vertex> s= new Stack<Vertex>();
					
					//stores the names into a stack (fixes backwards order)
					while(prev[temp]!=v1){
						s.push(adjLists[prev[temp]]);
						temp=prev[temp];
					}
					s.push(adjLists[v1]);
					
					//prints out the stack
					while(!s.isEmpty()){
						String printName=s.pop().name;
						System.out.print(printName+" ---> ");
					}
					System.out.println(adjLists[v2].name);
					System.out.println();
					
					return;
				}
				
			}
		}
		
		if(prev[v2]==-1){
			System.out.println(name2+" cannot be introduced to "+name1);
		}

				
		
	}
	
	public static void cliques(String schoolname){
		boolean[] seen = new boolean[adjLists.length];
		ArrayList<Vertex> studVerts = new ArrayList<Vertex>();
		ArrayList<ArrayList<Vertex>> cliques = new ArrayList<ArrayList<Vertex>>();
		
		//make all visited values false
		for(int i=0; i<seen.length;i++){
			seen[i] = false;
		}
		
		//store all student that go to named school
		for(int i=0; i<adjLists.length;i++){
			String s = adjLists[i].name;
			if(s.equals(schoolname) && !seen[i]){
				studVerts.add(adjLists[i]);
				seen[i]=true;
			}
		}
		
		//check who is connected with who and number cliques appropriately
		for(int i=0; i<studVerts.size(); i++){
			Neighbor tmpNbr = studVerts.get(i).adjList;
			ArrayList<Vertex> tmpC = new ArrayList<Vertex>();
			while(tmpNbr!=null){
				if(adjLists[tmpNbr.vertexNum].schoolname.equals(schoolname)){
					tmpC.add(adjLists[tmpNbr.vertexNum]);
					continue;
				}else{
					if(!tmpC.isEmpty()){
						cliques.add(tmpC);
						tmpC = new ArrayList<Vertex>();
					}
					continue;
				}
				
				tmpNbr = tmpNbr.next;
			}
			if(!tmpC.isEmpty())
				cliques.add(tmpC);
		}
		
		//print cliques
		for(int i=0;i<cliques.size();i++){
			printSubGraph(cliques.get(i));
		}
	}
	
	public static void printSubGraph(ArrayList<Vertex> sub){
		for(int v=0; v<sub.size();v++){
			System.out.print(adjLists[v].name+"("+sub.get(v).schoolname+")");
			for(int i=0; i<sub.size();i++){
				System.out.print(" ---> "+sub.get(i).name);	
			}
			System.out.println();
			System.out.println();
		}
	}
	
	public static void connectors(){
		
		//initialize visited and ending points
		boolean[] visited =  new boolean[adjLists.length];
		boolean[] endpoint= new boolean[adjLists.length];
		for(int v=0; v<adjLists.length;v++){ 
			visited[v]=false;
			
			int count=0;
			for(Neighbor nbr=adjLists[v].adjList; nbr!=null;nbr=nbr.next){
				count++;
				if(count>1){
					break;
				}
			}
			if(count==1){
				endpoint[v]=true;
			}
			else{
				endpoint[v]=false;
			}
			//endpoints are vertices with only one neighbor and thus cannot be a connector
			
		}
		
		
		
		for(int v=0; v<visited.length;v++){
			if(!visited[v]){
				dfs(v,visited);
			}
		}
		
		
	}
	
	
	public static void dfs(int v, boolean[]visited){
		visited[v]=true;
		System.out.println("Visiting "+adjLists[v].name);
		for(Neighbor nbr=adjLists[v].adjList; nbr!=null; nbr=nbr.next){
			if(!visited[nbr.vertexNum]){
				dfs(nbr.vertexNum,visited);
			}else{
				if(visited[nbr.next.vertexNum])
					backNum[v] = Math.min(backNum[v],dfsNum[v+1]);
			}
		}
		
	}
	
	
}
