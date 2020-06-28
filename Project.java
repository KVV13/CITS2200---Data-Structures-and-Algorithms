import java.util.*;

/**
 * This program is designed to create graphs and manipulate them to find certain properties.
 * Such properties include distance between vertices, graph centres, strongly connected components and hamiltonian paths.
 *
 * @author (Kushil Vaghjiani)
 * @version (Final Submission Version)
 */
public class Project implements CITS2200Project
{
    private int count = 0; //keeps track of integer to assign string
    private HashMap<String,Integer> vertices = new HashMap<String,Integer>(); // string assigned to integer
    private HashMap<Integer,String> urls = new HashMap<Integer,String>(); //allows so quick conversion from integer to string
    private HashMap<Integer,LinkedList<Integer>> graph = new HashMap<Integer,LinkedList<Integer>>(); //graph
    private HashMap<Integer,LinkedList<Integer>> reversed = new HashMap<Integer,LinkedList<Integer>>(); //reversed graph
    
    /**
     * Adds an edge to the Wikipedia page graph. If the pages do not
     * already exist in the graph, they will be added to the graph.
     * 
     * @param urlFrom the URL which has a link to urlTo.
     * @param urlTo the URL which urlFrom has a link to.
     */
    public void addEdge(String urlFrom, String urlTo){ 
        if(urlFrom.equals(urlTo)){return;}//Doesn't allow urlFrom to refer to itself

        boolean containsFrom = vertices.containsKey(urlFrom);
        boolean containsTo = vertices.containsKey(urlTo);

        if(containsFrom && containsTo){
            if(!((graph.get(vertices.get(urlFrom))).contains(vertices.get(urlTo)))){ //checks if edge already exists
                (graph.get(vertices.get(urlFrom))).add(vertices.get(urlTo));
                (reversed.get(vertices.get(urlTo))).add(vertices.get(urlFrom));
            }
            return;
        }
        if(!(containsFrom) && !(containsTo)){ //if vertices aren't graph at all
            vertices.put(urlFrom,count);
            urls.put(count,urlFrom);
            count++;
            vertices.put(urlTo,count);
            urls.put(count,urlTo);
            count++;

            graph.put(vertices.get(urlFrom),new LinkedList<Integer>());
            graph.put(vertices.get(urlTo),new LinkedList<Integer>());

            reversed.put(vertices.get(urlFrom),new LinkedList<Integer>());
            reversed.put(vertices.get(urlTo),new LinkedList<Integer>());

            (graph.get(vertices.get(urlFrom))).add(vertices.get(urlTo));
            (reversed.get(vertices.get(urlTo))).add(vertices.get(urlFrom));

            return;
        }
        if(!(containsFrom) && containsTo){ //if urlFrom isn't in graph
            vertices.put(urlFrom,count);
            urls.put(count,urlFrom);
            count++;

            graph.put(vertices.get(urlFrom),new LinkedList<Integer>());
            reversed.put(vertices.get(urlFrom),new LinkedList<Integer>());

            (graph.get(vertices.get(urlFrom))).add(vertices.get(urlTo));
            (reversed.get(vertices.get(urlTo))).add(vertices.get(urlFrom));

            return;
        }
        if(containsFrom && !(containsTo)){ //if urlTo isn't in graph
            vertices.put(urlTo,count);
            urls.put(count,urlTo);
            count++;

            graph.put(vertices.get(urlTo),new LinkedList<Integer>());
            reversed.put(vertices.get(urlTo),new LinkedList<Integer>());

            (graph.get(vertices.get(urlFrom))).add(vertices.get(urlTo));
            (reversed.get(vertices.get(urlTo))).add(vertices.get(urlFrom));

            return;
        }
    }

    /**
     * Finds the shorest path in number of links between two pages.
     * If there is no path, returns -1.
     * 
     * @param urlFrom the URL where the path should start.
     * @param urlTo the URL where the path should end.
     * @return the length of the shorest path in number of links followed.
     */
    public int getShortestPath(String urlFrom, String urlTo){
        if(!(vertices.containsKey(urlFrom)) || !(vertices.containsKey(urlTo))){return(-1);} //if elements don't exist in graph
        if(urlFrom.equals(urlTo)){return(0);} //distance to same vertex equals 0

        Project.Breadth Shortpath = new Project.Breadth();
        Shortpath.BFS(urlFrom);
        int[] dist = Shortpath.dist;

        if(dist[vertices.get(urlTo)]==0){dist[vertices.get(urlTo)]=-1;} //zero values mean path doesn't exist

        return dist[vertices.get(urlTo)];
    }

    private class Breadth{
        int numv = vertices.size(); // number of vertices
        int[] dist= new int[numv]; // must be reset
        int farvertex;
        HashSet<Integer> visited;// = new HashSet<Integer>();

        public void BFS(String urlFrom){
            LinkedList<Integer> Q = new LinkedList<Integer>(); //queue
            visited = new HashSet<Integer>(); //tracks visited vertices
            int w; //for dequeing
            int inlist; // for list iterator
            boolean[] colour=new boolean[numv];
            dist= new int[numv]; 
            farvertex=0;

            colour[vertices.get(urlFrom)]=true;
            Q.add(vertices.get(urlFrom));
            while(!(Q.peekFirst()==null)){
                w=Q.poll();
                ListIterator<Integer> iter = (graph.get(w)).listIterator(0);
                int sizeconnect = (graph.get(w)).size();
                for(int i=0;i<sizeconnect;i++){
                    inlist=iter.next();
                    if(colour[inlist]==false){
                        dist[inlist]=dist[w]+1;
                        visited.add(inlist);
                        if(dist[inlist]>farvertex){farvertex=dist[inlist];}
                        colour[inlist]=true;
                        Q.add(inlist);

                    }
                }
            }
        }
    }

    /**
     * Finds all the centers of the page graph. The order of pages
     * in the output does not matter. Any order is correct as long as
     * all the centers are in the array, and no pages that aren't centers
     * are in the array.
     * 
     * If null is returned, a single vertex cannot reach all other vertices.
     * 
     * @return an array containing all the URLs that correspond to pages that are centers.
     */
    public String[] getCenters(){
        LinkedList<Integer> Q = new LinkedList<Integer>(); //queue
        LinkedList<String> potcentre = new LinkedList<String>(); //holds centres
        int numv = vertices.size();
        int overallfar=0;
        int counter=0;

        Project.Breadth centres = new Project.Breadth();
        for(int j=0;j<numv;j++){
            centres.BFS(urls.get(j));
            int distmin=1;
            int farvertex = centres.farvertex;
            HashSet<Integer> visited = centres.visited;

            if(!(centres.visited.size()==(numv-1))){ //not all vertices were visited
                distmin=0;
                counter++;
            }
            if(farvertex!=0 && distmin!=0){
                if(overallfar==0){overallfar=farvertex;}
                if(farvertex==overallfar){potcentre.add(urls.get(j));}
                if(farvertex<overallfar){
                    potcentre.clear();
                    overallfar=farvertex;
                    potcentre.add(urls.get(j));
                }
            }
        }
        if(counter==numv){return null;}
        String[] centre = potcentre.toArray(new String[0]);
        return centre;
    }

    /**
     * Finds all the strongly connected components of the page graph.
     * Every strongly connected component can be represented as an array 
     * containing the page URLs in the component. The return value is thus an array
     * of strongly connected components. The order of elements in these arrays
     * does not matter. Any output that contains all the strongly connected
     * components is considered correct.
     * 
     * @return an array containing every strongly connected component.
     */
    public String[][] getStronglyConnectedComponents(){
        if((vertices.size())==0){return null;} //will not run if there aren't any components in graph
        LinkedList<String> SCC;
        LinkedList<String[]> LinkedSCCs = new LinkedList<String[]>(); //Holds list of arrays
        String[] parents;
        int numvertex=vertices.size();
        boolean[] visit = new boolean[numvertex];
        int popped;

        Project.Depth time = new Project.Depth();
        for(int i=0; i<numvertex; i++){//makes sure DFS is done for all vertices
            if(time.tovisit.containsKey(i)){time.DFS(graph,i);}
        }
        Stack<Integer> stack=time.stack; //stack used for reverse DFS
        Project.Depth reversedtimes = new Project.Depth();
        while(!(stack.empty())){ //captures all SCCs even if graphs are disconnected
            popped=stack.pop();
            if(visit[popped]==false){
                SCC = reversedtimes.DFS(reversed,popped);
                SCC.add(urls.get(popped));
                parents = SCC.toArray(new String[0]);
                LinkedSCCs.add(parents);     

                visit=reversedtimes.travel; //tracks visited vertices on reverse
                reversedtimes.parent = new LinkedList<String>(); //resets reversedtimes List
            }     
        }
        String[][] SCCs = LinkedSCCs.toArray(new String[0][0]); //for jagged array
        return SCCs;
    }
    
    private class Depth
    {
        Stack<Integer> stack = new Stack<Integer>(); //order of full discoveries
        HashSet<Integer> instack = new HashSet<Integer>(); // prevents double ups in stack
        HashMap<Integer,String> tovisit = new HashMap<Integer,String>(urls); //Help determine if DFS should be conducted
        LinkedList<String> parent= new LinkedList<String>(); //returned to SCC class
        int numvertex = vertices.size();
        boolean[] travel = new boolean[numvertex];
        int check;
        int inlist;

        public LinkedList<String> DFS(HashMap<Integer,LinkedList<Integer>> thismap,int startvertex){
            travel[startvertex]=true;
            tovisit.remove(startvertex);
            check=0;

            if((thismap.get(startvertex)).size()==0 && !(instack.contains(startvertex))){
                stack.push(startvertex);
                instack.add(startvertex);
            }

            ListIterator<Integer> iter = (thismap.get(startvertex)).listIterator(0);
            for(int i=0; i<((thismap.get(startvertex)).size()); i++){
                inlist=iter.next();    
                if(travel[inlist]==true){ //vertex inlist has no out pointing edges
                    check++;
                    if(((thismap.get(startvertex)).size())==check){
                        stack.push(startvertex);
                        instack.add(startvertex);
                    }
                }
                if(travel[inlist]==false){
                    parent.add(urls.get(inlist));
                    DFS(thismap,inlist); //recursive call
                    if(!(instack.contains(startvertex))){
                        stack.push(startvertex);
                        instack.add(startvertex);
                    }
                }
            }
            return parent; //visited vertices from input vertex
        }
    }
    
    /**
     * Finds a Hamiltonian path in the page graph. There may be many
     * possible Hamiltonian paths. Any of these paths is a correct output.
     * This method should never be called on a graph with more than 20
     * vertices. If there is no Hamiltonian path, this method will
     * return an empty array. The output array should contain the URLs of pages
     * in a Hamiltonian path. The order matters, as the elements of the
     * array represent this path in sequence. So the element [0] is the start
     * of the path, and [1] is the next page, and so on.
     * 
     * @return a Hamiltonian path of the page graph.
     */
    public String[] getHamiltonianPath(){
        int numvertex = vertices.size();
        boolean[][] dp = new boolean[numvertex][1<<numvertex];
        HashMap<Integer,Integer> HAM = new HashMap<Integer,Integer>();
        HashSet<Integer> seen = new HashSet<Integer>(); //checks if vertex is in path
        int binary = 1;
        int last = numvertex-1;//for building path
        String[] path = new String[numvertex];

        for(int i=0; i<numvertex; i++){
            dp[i][1<<i]=true;
            HAM.put(i,binary);
            binary=binary*2;
        }
        binary-=1; //total sum of binarys
        for(int i=0; i<(1<<numvertex); i++){
            for(int j=0; j<numvertex; j++){
                if((i&(1<<(j)))>=1){
                    for(int k=0; k<numvertex; k++){
                        if((i&(1<<(k)))>=1 && k!=j && dp[k][i^(1<<j)] && ((graph.get(k).contains(j))==true)){
                            dp[j][i]=true;
                            break;
                        }
                    }
                }
            }
        }
        for(int i=0; i<numvertex; i++){ //build path if exists
            if(dp[i][(1<<numvertex)-1]){ //if there exists a full subset, singly visited vertices
                path[last]=urls.get(i);
                last-=1;
                seen.add(i);
                binary-=(HAM.get(i));
                while((seen.size())!=numvertex && binary>=0 && last>=0){
                    for(int j=0; j<numvertex; j++){
                        if((dp[j][binary])==true && (seen.contains(j)==false) && graph.get(j).contains(vertices.get(path[last+1]))==true){
                            path[last]=urls.get(j);
                            last-=1;
                            seen.add(j);
                            binary-=(HAM.get(j));
                            break;
                        }
                    }
                }
                return path;
            }
        }
        return path;
    }
    
}
