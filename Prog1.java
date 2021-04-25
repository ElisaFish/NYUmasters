// Iterative Deepening & Hill Climbing

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.lang.Math;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.lang.Object;


class BestVertexCover {
    int budget;
    HashMap<Character, Integer> vertices;
    int numVertices;
    ArrayList<Character> vertexList;
    ArrayList<ArrayList<Character>> edges;
    int numEdges;
    ArrayList<ArrayList<Character>> states;
    int numStates;

    // Read input and create a Best Vertex Cover problem object
    public SimpleEntry<Character, Integer> read_BVC(String file, String method) {
        int randomRestarts = -1;

        try {
            File input = new File(file);
            Scanner reader = new Scanner(input);

            // Read in budget, verbose/compact flag, randomRestarts if Hill Climbing
            this.budget = reader.nextInt();
            Character vc = reader.next().charAt(0);
            if (method == "HC") {
                randomRestarts = reader.nextInt();
            }

            // Read in Vertices and Costs
            this.vertices = new HashMap<Character, Integer>();
            this.vertexList = new ArrayList<Character>();

            Character edge1 = null;
            do {
                Character vertex = reader.next().charAt(0);
                try {
                    int cost = reader.nextInt();
                    this.vertices.put(vertex, cost);
                    boolean a = vertexList.add(vertex);
                } catch (NoSuchElementException e) {
                    edge1 = vertex;
                    break;
                }
            } while (true);
            this.numVertices = vertices.size();

            // Read in Edges
            edges = new ArrayList<ArrayList<Character>>();
            ArrayList<Character> newEdge = new ArrayList<Character>();
            if (edge1 != null) {
                Character edge2 = reader.next().charAt(0);
                newEdge.add(edge1);
                newEdge.add(edge2);
                boolean a = edges.add(newEdge);
            }   
            while (reader.hasNext()) {
                Character v1 = reader.next().charAt(0);
                Character v2 = reader.next().charAt(0);
                newEdge = new ArrayList<Character>();
                newEdge.add(v1);
                newEdge.add(v2);
                boolean a = edges.add(newEdge);
            }
            this.numEdges = this.edges.size();

            // Return
            if (method == "ID") {
                return new SimpleEntry<Character, Integer>(vc, this.numVertices - 1);
            }
            else if (method == "HC" && randomRestarts != -1) {
                // Create States
                this.states = new ArrayList<ArrayList<Character>>();
                this.generateStates(new ArrayList<Character>(){});
                this.numStates = this.states.size();
                return new SimpleEntry<Character,Integer>(vc, randomRestarts);
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred in reading the file.");
            return null;
        }
        return null;
    }

    // Does this state meet the goal?
    // Returns 1, cost, error if goal is met
    // Returns -1, cose, error if goal is not met
    public Integer[] goal(ArrayList<Character> state) {
        // If all edges are covered within the budget, return True. Else, return False
        // If the error = 0, all the edges are covered within the budget
        // Calculate Cost
        ArrayList<Integer> costError = this.costError(state);
        Integer totalCost = costError.get(0);
        Integer totalError = costError.get(1);
        Integer answer = 0;
        if (totalError == 0) {
            answer = 1;
        }
        else {
            answer = -1;
        }
        Integer[] result = new Integer[]{answer, totalCost, totalError};
        return result;
    }

    // Calculates and returns cost of input state
    public Integer cost(ArrayList<Character> state) {
        Integer totalCost = 0;
        for (Character vertex : state) {
            totalCost += vertices.get(vertex);
        }
        return totalCost;
    }

    // Calculates and returns cost and error of input state
    public ArrayList<Integer> costError(ArrayList<Character> state) {
        Integer totalCost = this.cost(state);
        Integer totalError = Math.max(0, totalCost - this.budget);

        // All edges covered?
        // If Edge is not covered, add cost of lowest node to error
        //int edgeCount = 0;
        for (ArrayList<Character> edge : edges) {
            if (!state.contains(edge.get(0)) && !state.contains(edge.get(1))) {
                totalError += Math.min(vertices.get(edge.get(0)), vertices.get(edge.get(1)));
            }
            /*
            if (state.contains(edge.get(0)) || state.contains(edge.get(1))) {
                edgeCount++;
            }
            else {
                totalError += Math.min(vertices.get(edge.get(0)), vertices.get(edge.get(1)));
            }
            */
        }

        ArrayList<Integer> result = new ArrayList<Integer>();
        result.add(totalCost);
        result.add(totalError);
        return result;
    }

    // Generate list of successors to input state
    // For Iterative Deepening problem
    // To generate a list of states for the Hill Climbing problem
    public ArrayList<ArrayList<Character>> successors(ArrayList<Character> state) {
        ArrayList<ArrayList<Character>> succs = new ArrayList<ArrayList<Character>>();
        int length = state.size();

        int indexLastVertex = -1;
        if (length > 0) {
            Character lastVertex = state.get(length-1);
            indexLastVertex = this.vertexList.indexOf(lastVertex);
        }
        
        // for vertices > last vertex in state
        for (int i=indexLastVertex+1; i<this.numVertices; i++) {
            ArrayList<Character> newState = new ArrayList<Character>(state);
            newState.add(this.vertexList.get(i));
            newState.sort(null);
            boolean a = succs.add(newState);
        }
        return succs;
    }

    // Generate all possible states so the Hill Climbing algorithm can choose a random starting state
    public void generateStates(ArrayList<Character> state) {
        this.states.add(state);
        ArrayList<ArrayList<Character>> succs = this.successors(state);
        succs.forEach(successor -> {this.generateStates(successor);});
    }

}

class IterativeDeepening {
    BestVertexCover BVC;
    int depth_max;
    int numVertices;
    Character vc;

    IterativeDeepening(String file) {
        this.BVC = new BestVertexCover();
        SimpleEntry<Character, Integer> vcDepth = BVC.read_BVC(file, "ID");
        this.vc = vcDepth.getKey();
        this.depth_max = vcDepth.getValue();
        IDS();
    }

    // Core Depth First Search
    // If the algorithm finds a state that meets the goal, return the state and its cost
    // If not, return the state and -1
    public SimpleEntry<ArrayList<Character>, Integer> DFS(ArrayList<Character> s, int depth) {
        Integer[] result = this.BVC.goal(s);
        Integer success = result[0];
        Integer cost = result[1];

        // If the input state is above budget, return false -- no need to check for successors if all costs are non-negative
        if (cost > this.BVC.budget) {
            return new SimpleEntry<ArrayList<Character>, Integer>(s, -1);
        }

        // Verbose mode only
        if (this.vc == 'V') {
            System.out.println(s.toString() + " Cost=" + cost + '.');
        }

        // Two base cases for recursion: a goal is found, or state s has no successors

        // If input state is the goal:
        if (success == 1) {
            return new SimpleEntry<ArrayList<Character>, Integer>(s, cost);
        }

        // If input state is not the goal, create a list of successors and run each through DFS
        if (depth > 0) {
            ArrayList<ArrayList<Character>> succs = this.BVC.successors(s);
            for (ArrayList<Character> c : succs) {
                SimpleEntry<ArrayList<Character>, Integer> ans = DFS(c, depth-1);
                if (ans.getValue() != -1) {
                    return ans;
                }  
            }
        }
        
        // If none of the successors of c work, then there is no goal in subtree of s
        return new SimpleEntry<ArrayList<Character>, Integer>(s, -1);
    }

    // Iterative Deepening Search
    public void IDS() {
        for (int depth=1; depth <= this.depth_max; depth++) {
            //Verbose mode only
            if (vc == 'V') {
                System.out.println("Depth = " + depth);
            }

            // DFS
            SimpleEntry<ArrayList<Character>, Integer> ans = this.DFS(new ArrayList<Character>(){}, depth);
            ArrayList<Character> solution = ans.getKey();
            Integer cost = ans.getValue();

            // Verbose mode only
            if (vc == 'V') {
                System.out.println();
            }

            // Return if successful
            if (this.BVC.goal(solution)[0] == 1) {
                if (vc == 'V') {
                    System.out.println("Found solution " + solution + " Cost=" + cost);
                }
                else {
                    System.out.println("Found solution " + solution);
                }
                return;
            }
        }
        // If no solution found:
        System.out.println("No solution found.");
        return;
        // return Fail
    }
}


class HillClimbing {
    BestVertexCover BVC;
    Character vc;
    Integer randomRestarts;

    // Creates BestVertexCover object and begins Hill Climbing algorithm with a specified start state
    // Used for testing
    HillClimbing(String file, ArrayList<Character> startState) {
        this.BVC = new BestVertexCover();
        SimpleEntry<Character, Integer> vcRR = this.BVC.read_BVC(file, "HC");
        this.vc = vcRR.getKey();

        System.out.println("Start state: " + startState);

        if (HC(startState)) {
            return;
        }
        System.out.println("No solution found.");
        System.out.println();
    }
        
    // Creates a BestVertexCover object and starts Hill Climbing algorithm with random start states for the number of random restarts in file
    HillClimbing(String file) {
        this.BVC = new BestVertexCover();
        SimpleEntry<Character, Integer> vcRR = this.BVC.read_BVC(file, "HC");
        this.vc = vcRR.getKey();
        this.randomRestarts = vcRR.getValue();

        for (int i=0; i < this.randomRestarts; i++) {
            // Randomly choose start state
            Random rand = new Random();
            int indexStartState = rand.nextInt(this.BVC.numStates);
            if (vc == 'V') {
                System.out.println("Randomly chosen start state: " + this.BVC.states.get(indexStartState));
            }

            // Run HillClimbing algorithm
            if (HC(this.BVC.states.get(indexStartState))) {
                break;
            }
            System.out.println("No solution found.");
            System.out.println();
        }    
    }

    // Hill Climbing algorithm
    public Boolean HC(ArrayList<Character> S) {
        ArrayList<Integer> costError = BVC.costError(S);
        Integer cost = costError.get(0);
        Integer error = costError.get(1);

        // Verbose mode only
        if (vc == 'V') {
            System.out.println(S + " Cost=" + cost + ". Error=" + error);
        }

        while (true) {
            Integer[] goalResult = this.BVC.goal(S);
            Integer goalS = goalResult[0];
            Integer costS = goalResult[1];
            Integer errorS = goalResult[2];
            if (goalS == 1) {
                // Verbose Mode
                if (vc == 'V') {
                    System.out.println("Found solution " + S + " Cost=" + costS);
                }
                // Compact Mode
                else {
                    System.out.println("Found solution " + S);
                }
                return true;
            }
            //Create neighbors with cost and error
            ArrayList<ArrayList<Character>> neighborsList = this.neighbors(S);
            ArrayList<ArrayList<Integer>> neighCostError = new ArrayList<ArrayList<Integer>>();
            neighborsList.forEach(neighbor -> {
                // Cost
                neighCostError.add(this.BVC.costError(neighbor));
            });
            //N = neighbor of S for which error is minimal (break ties arbitrarily);
            ArrayList<Character> N = new ArrayList<Character>();
            Integer costN = 0;
            Integer errorN = Integer.MAX_VALUE;

            // Verbose mode only
            if (vc == 'V') {
                System.out.println("Neighbors");
            }

            for (int i=0; i<neighCostError.size(); i++) {
                // Verbose mode only
                if (vc == 'V') {
                    System.out.println(neighborsList.get(i) + " Cost=" + neighCostError.get(i).get(0) + ". Error=" + neighCostError.get(i).get(1));
                }

                // If error == 0, return solution
                if (neighCostError.get(i).get(1) == 0) {
                    N = neighborsList.get(i);
                    costN = neighCostError.get(i).get(0);
                    errorN = neighCostError.get(i).get(1);

                    // Verbose Mode
                    if (vc == 'V') {
                        System.out.println();
                        System.out.println("Found solution " + N + " Cost=" + costN + ". Error=" + errorN);
                    }
                    // Compact Mode
                    else {
                        System.out.println("Found solution " + N);
                    }
                    return true;
                }
                // If this neighbor is not a solution, check if it has the lowest error
                if (neighCostError.get(i).get(1) < errorN) {
                    N = neighborsList.get(i);
                    costN = neighCostError.get(i).get(0);
                    errorN = neighCostError.get(i).get(1);
                }
            }
            if (errorN >= errorS) {
                if (vc == 'V') {
                    System.out.println();
                    System.out.println("Search failed");
                    System.out.println();
                }
                return false;
            }
            S=N;

            // Verbose Mode only
            if (vc == 'V') {
                System.out.println();
                System.out.println("Move to " + N + ". Cost=" + costN + ". Error=" + errorN);
            }
        }
    }

    // Find neighbors of input state:
    // Remove a vertex one by one
    // Then add a vertex to the input state one by one
    public ArrayList<ArrayList<Character>> neighbors(ArrayList<Character> state) {
        ArrayList<ArrayList<Character>> neighbors = new ArrayList<ArrayList<Character>>();
        // Subtract vertices one by one
        for (int i = 0; i<state.size(); i++) {
            ArrayList<Character> newState = new ArrayList<Character>(state.subList(0, i));
            boolean b = newState.addAll(state.subList(i+1, state.size()));
            newState.sort(null);
            neighbors.add(newState);
        }

        // Add vertices one by one
        for (Character vertex : BVC.vertices.keySet()) {
            if (!state.contains(vertex)) {
                ArrayList<Character> newState = new ArrayList<Character>(state.subList(0, state.size()));
                boolean b = newState.add(vertex);
                newState.sort(null);
                neighbors.add(newState);
            }
        }

        return neighbors;
    }
}

// Main
class Prog1 {
    public static void main(String args[]) {
        //////////////////////// INPUT HERE /////////////////////////

        //IterativeDeepening id = new IterativeDeepening("IDinput1.txt");
        //IterativeDeepening id = new IterativeDeepening("IDEx1.txt");
        //IterativeDeepening id = new IterativeDeepening("IDex2.txt");
        //IterativeDeepening id = new IterativeDeepening("IDex3.txt");

        //HillClimbing hc = new HillClimbing("HCinput1.txt");
        //ArrayList<Character> start = new ArrayList<Character>();
        //start.add('A');
        //start.add('B');
        //start.add('C');
        //start.add('D');
        //start.add('E');
        //HillClimbing hc = new HillClimbing("HCinput1.txt", start);
        //HillClimbing hc = new HillClimbing("HCex1.txt", start);
        //HillClimbing hc = new HillClimbing("HCex2.txt", start);
        //HillClimbing hc = new HillClimbing("HCex2.txt");
    }
}