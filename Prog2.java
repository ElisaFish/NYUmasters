import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.lang.Math;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.lang.Object;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.Hashtable;

class DavisPutnam {

    DavisPutnam(String file, String outputFile) {
        try {
            File input = new File(file);
            Scanner reader = new Scanner(input);

            ArrayList<ArrayList<Integer>> CS = new ArrayList<ArrayList<Integer>>();

            while (!reader.hasNext("0")) {
                ArrayList<Integer> C = new ArrayList<Integer>();
                String line = reader.nextLine();
                String[] clause = line.split(" ");
                for (String p : clause) {
                    C.add(Integer.valueOf(p));
                }
                CS.add(C);
            }

            ArrayList<String> backMatter = new ArrayList<String>();
            while (reader.hasNext()) {
                backMatter.add(reader.nextLine());
            }

            reader.close();
            
            ArrayList<SimpleEntry<Integer,Boolean>> B = new ArrayList<SimpleEntry<Integer,Boolean>>();

            B = DPLL(CS, B);
            B.sort(Comparator.comparing(SimpleEntry<Integer,Boolean>::getKey));

            // Output to file
            try {
                FileWriter fileWriter = new FileWriter(outputFile);
                PrintWriter printWriter = new PrintWriter(fileWriter);
                for (SimpleEntry<Integer, Boolean> se : B) {
                    printWriter.printf("%d %b\n", se.getKey(), se.getValue());
                }
                for (String str : backMatter) {
                    printWriter.println(str);
                }
                printWriter.close();
                fileWriter.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred in writing to the output file.");
            } catch (IOException e) {
                System.out.println("An error occurred in writing to the output file.");
            } 
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred in reading the file.");
        } 
    }

    public ArrayList<SimpleEntry<Integer,Boolean>> DPLL(ArrayList<ArrayList<Integer>> CS, ArrayList<SimpleEntry<Integer,Boolean>> B) {
        Boolean flag = true;
        while (flag) {
            // If CS is empty, we're finished!
            if (CS.size() == 0) { //CS.isEmpty()) {
                return B;
            }

            //if (emptyClause in CS) return Fail;
            for (ArrayList<Integer> C : CS) {
                if (C.isEmpty()) return new ArrayList<SimpleEntry<Integer,Boolean>>(); // return Fail
            }
            
            //if (easyCaseIn(CS, B)) [CS,B] = easyCase(CS,B);
            ArrayList<ArrayList<Integer>> CSCopy = deepCopy(CS);
            ArrayList<SimpleEntry<Integer,Boolean>> BCopy = new ArrayList<SimpleEntry<Integer,Boolean>>(B);
            SimpleEntry<ArrayList<ArrayList<Integer>>, ArrayList<SimpleEntry<Integer,Boolean>>> easy = this.EasyCase(CSCopy,BCopy);
            if (easy.getValue().size() == B.size()) {
                flag = false;
            }
            else {
                CS = easy.getKey();
                B = easy.getValue();
            }  
        }

        ArrayList<ArrayList<Integer>> CSCopy = deepCopy(CS);
        ArrayList<SimpleEntry<Integer,Boolean>> BCopy = new ArrayList<SimpleEntry<Integer,Boolean>>(B); //B.clone(); //Copy(B);

        Integer P = this.atomOf(CSCopy.get(0).get(0)); //choose an unbound atom;
        SimpleEntry<ArrayList<ArrayList<Integer>>, ArrayList<SimpleEntry<Integer,Boolean>>> CSBcopy = propagate(CSCopy, BCopy, P, true);
        //[CSCopy, BCopy]
        CSCopy = CSBcopy.getKey();
        BCopy = CSBcopy.getValue();
        ArrayList<SimpleEntry<Integer,Boolean>> answer = DPLL(CSCopy, BCopy);
        if (!answer.isEmpty()) {    // != Fail
            return answer;
        }
        //[CS, B] 
        ArrayList<ArrayList<Integer>> CSCopy2 = deepCopy(CS);
        ArrayList<SimpleEntry<Integer,Boolean>> BCopy2 = new ArrayList<SimpleEntry<Integer,Boolean>>(B); //B.clone(); //Copy(B);

        SimpleEntry<ArrayList<ArrayList<Integer>>, ArrayList<SimpleEntry<Integer,Boolean>>> CSB2 = propagate(CSCopy2, BCopy2, P, false);
        CSCopy2 = CSB2.getKey();
        BCopy2 = CSB2.getValue();
        return DPLL(CSCopy2, BCopy2);
    }

    public SimpleEntry<ArrayList<ArrayList<Integer>>, ArrayList<SimpleEntry<Integer,Boolean>>> propagate(ArrayList<ArrayList<Integer>> CS, ArrayList<SimpleEntry<Integer,Boolean>> B, Integer A, Boolean V) {
        boolean bool = B.add( new SimpleEntry<Integer,Boolean>(A,V));
        Integer Aprime = A;
        if (V == true) {
            Aprime = Math.abs(A); 
        }
        else {
            Aprime = 0 - A;
        }
        Integer notA = 0 - Aprime;

        ArrayList<ArrayList<Integer>> toRemove = new ArrayList<ArrayList<Integer>>();
        for (ArrayList<Integer> C : CS) {
            if (C.contains(Aprime)) {
                toRemove.add(C); //delete C from CS;
            }
            else if (C.contains(notA)) {
                C.remove(notA); //delete A from C;
            }
        }
        CS.removeAll(toRemove);
        return new SimpleEntry<ArrayList<ArrayList<Integer>>, ArrayList<SimpleEntry<Integer,Boolean>>>(CS, B);
    }

    public SimpleEntry<ArrayList<ArrayList<Integer>>, ArrayList<SimpleEntry<Integer,Boolean>>> EasyCase(ArrayList<ArrayList<Integer>> CS, ArrayList<SimpleEntry<Integer,Boolean>> B) {
        //Literal List
        ArrayList<Integer> literals = new ArrayList<Integer>();

        
        for (ArrayList<Integer> C : CS) {
            // Singleton Clause
            if (C.size() == 1) {
                Integer L = C.get(0);
                return this.propagate(CS, B, atomOf(L), valueOf(L));
            }
            // Pure Literal
            for (Integer L : C) {
                if (!literals.contains(L)) {
                    literals.add(L);
                }   
            }  
        }

        // Pure Literal
        for (Integer L : literals) {
            if (!literals.contains(0-L)) {
                return this.propagate(CS, B, atomOf(L), valueOf(L));
            }
        }

        return new SimpleEntry<ArrayList<ArrayList<Integer>>, ArrayList<SimpleEntry<Integer,Boolean>>>(CS, B);
    }

    public Integer atomOf(Integer L) {
        return Math.abs(L);
    }

    public Boolean valueOf(Integer L) {
        if (L > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public ArrayList<ArrayList<Integer>> deepCopy(ArrayList<ArrayList<Integer>> CS) {
        ArrayList<ArrayList<Integer>> CSCopy = new ArrayList<ArrayList<Integer>>();
        for (ArrayList<Integer> C : CS) {
            ArrayList<Integer> Ccopy = new ArrayList<Integer>(C);
            CSCopy.add(Ccopy);
        }
        return CSCopy;
    }
}


class TreasureNode {
    String nodeName;
    ArrayList<TreasureNode> neighbors;
    ArrayList<String> neighborsString;
    ArrayList<String> treasures;
    ArrayList<Integer> atTime;

    TreasureNode(String node) {
        this.nodeName = node;
        this.neighbors = new ArrayList<TreasureNode>();
        this.treasures = new ArrayList<String>();
        this.atTime = new ArrayList<Integer>();
    }

    public void addAtTime(Integer atom) {
        this.atTime.add(atom);
    }

    public Integer getAtTime(Integer time) {
        return this.atTime.get(time);
    }
}

class FrontEnd {

    Hashtable<String, TreasureNode> nodesDict;
    ArrayList<TreasureNode> treasureNodes;
    Hashtable<String, ArrayList<Integer>> treasuresDict;
    Hashtable<String, ArrayList<String>> nodesWithTreasure;

    FrontEnd(String file, String outputFile) {
        // Read in file
        try {
            File input = new File(file);
            Scanner reader = new Scanner(input);

            // Read in Nodes
            this.nodesDict = new Hashtable<String,TreasureNode>();
            this.treasureNodes = new ArrayList<TreasureNode>();
            String line = reader.nextLine();
            String[] nodes = line.split(" ");
            for (String node : nodes) {
                this.nodesDict.put(node, new TreasureNode(node));
            }

            // Read in Treasures
            this.treasuresDict = new Hashtable<String, ArrayList<Integer>>();
            this.nodesWithTreasure = new Hashtable<String, ArrayList<String>>();
            line = reader.nextLine();
            String[] treasures = line.split(" ");
            for (String treasure : treasures) {
                this.treasuresDict.put(treasure, new ArrayList<Integer>());
                this.nodesWithTreasure.put(treasure, new ArrayList<String>());
            }

            // Read in Steps
            line = reader.nextLine();
            int steps = Integer.parseInt(line);

            // Read in Maze Encoding
            while (reader.hasNext()) {
                line = reader.nextLine();
                String[] mazeEncoding = line.split(" ");
                treasureNodes.add(parseME(mazeEncoding));
                
            }
            
            reader.close();

            // Create Atoms
            ArrayList<SimpleEntry<Integer,String>> backMatter = new ArrayList<SimpleEntry<Integer,String>>();
            backMatter.add(new SimpleEntry<Integer,String>(0, ""));
            int i = 1;
            for (int time = 0; time <= steps; time++) {
                for (String node : nodes) {
                    String str = "At(" + node + "," + Integer.toString(time) + ")";
                    backMatter.add(new SimpleEntry<Integer,String>(i, str));
                    nodesDict.get(node).addAtTime(i);
                    i++;
                }
                
            }
            for (int time = 0; time <= steps; time++) {
                for (String treasure : treasures) {
                    String str = "Has(" + treasure + "," + Integer.toString(time) + ")";
                    backMatter.add(new SimpleEntry<Integer, String>(i, str));
                    treasuresDict.get(treasure).add(i);
                    i++;
                }
            }

            // Output to file
            try {
                FileWriter fileWriter = new FileWriter(outputFile);
                PrintWriter printWriter = new PrintWriter(fileWriter);
                
                // Form Clauses, 8 Categories
                // Category 1: for each node, for each time stamp, ~At(Node1, 0) V ~At(Node2, 0)
                for (int time = 0; time <= steps; time++) {
                    int lastNode = (time + 1) * nodes.length;
                    for (int node = 1 + time * nodes.length; node < lastNode; node++) {
                        for (int node2 = node+1; node2 <= lastNode; node2++) {
                            // Print clauses to file
                            printWriter.printf("%d %d\n", -node, -node2);
                        }
                        
                    }
                }

                // Type 2: for each node, you can only go to a neighboring node at the next time stamp
                for (int time = 0; time < steps; time++) {
                    for (TreasureNode tn : this.treasureNodes) {
                        ArrayList<Integer> clause = new ArrayList<Integer>();
                        clause.add(0-tn.getAtTime(time));
                        for (TreasureNode neighbor : tn.neighbors) {
                            Integer literal = this.nodesDict.get(neighbor.nodeName).getAtTime(time+1);
                            clause.add(literal);
                        }
                        printWriter.println(clause.toString().replace("[", "").replace(",", "").replace("]", ""));
                    }
                }

                // Type 3: If you're at a node with a treasure, you have that treasure at that time
                for (int time = 0; time <= steps; time++) {
                    for (TreasureNode tn : this.treasureNodes) {
                        ArrayList<Integer> clause = new ArrayList<Integer>();
                        clause.add(0-tn.getAtTime(time));
                        for (String treasure : tn.treasures) {
                            Integer literal = this.treasuresDict.get(treasure).get(time);
                            clause.add(literal);
                            printWriter.println(clause.toString().replace("[", "").replace(",", "").replace("]", ""));
                            clause.remove(1);
                        } 
                    }
                }

                // Type 4: If you have a treasure at a given time, you have it at all times going forward
                for (int time = 0; time < steps; time++) {
                    for (String treasure : treasures) {
                        ArrayList<Integer> clause = new ArrayList<Integer>();
                        Integer literal = this.treasuresDict.get(treasure).get(time);
                        clause.add(0-literal);
                        literal = this.treasuresDict.get(treasure).get(time + 1);
                        clause.add(literal);
                        printWriter.println(clause.toString().replace("[", "").replace(",", "").replace("]", ""));
                    }
                }

                // Type 5: If you don't have a treasure at time-1 and have it at time, at time you're at a node that supplies the treasure
                for (int time = 0; time < steps; time++) {
                    for (String treasure : treasures) {
                        ArrayList<Integer> clause = new ArrayList<Integer>();
                        Integer literal = this.treasuresDict.get(treasure).get(time);
                        clause.add(literal);
                        literal = this.treasuresDict.get(treasure).get(time + 1);
                        clause.add(0-literal);
                        // Add all At(node, time+1) where node supplies the treasure
                        for (String node : this.nodesWithTreasure.get(treasure)) {
                            literal = this.nodesDict.get(node).getAtTime(time+1);
                            clause.add(literal);
                        }
                        
                        printWriter.println(clause.toString().replace("[", "").replace(",", "").replace("]", ""));
                    }
                }

                // Category 6: Player is at START at time 0
                printWriter.println(1);

                // Category 7: At time 0, player has none of the treasures
                for (String treasure : treasures) {
                    printWriter.println(0-this.treasuresDict.get(treasure).get(0));
                }

                // Category 8: At time step, player has all treasures
                for (String treasure : treasures) {
                    printWriter.println(this.treasuresDict.get(treasure).get(steps));
                }
                
                // Print back matter
                for (SimpleEntry<Integer, String> strList : backMatter) {
                    printWriter.printf("%d %s\n", strList.getKey(), strList.getValue());
                }
                printWriter.close();
                fileWriter.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred in writing to the output file.");
            } catch (IOException e) {
                System.out.println("An error occurred in writing to the output file.");
            } 
            
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred in reading the file.");
        }
    }

    public TreasureNode parseME(String[] mazeEncoding) {
        TreasureNode tn = nodesDict.get(mazeEncoding[0]);
        int i = 1;
        if (mazeEncoding[i].equals("TREASURES")) {
            i++;
            while (!mazeEncoding[i].equals("NEXT")) {
                tn.treasures.add(mazeEncoding[i]);
                this.nodesWithTreasure.get(mazeEncoding[i]).add(mazeEncoding[0]);
                i++;
            }
        }
        if (mazeEncoding[i].equals("NEXT")) {
            i++;
            while (i < mazeEncoding.length) {
                tn.neighbors.add(nodesDict.get(mazeEncoding[i]));
                i++;
            }
        }
        return tn;
    }
}

class BackEnd {

    BackEnd(String file, String outputFile) {

        // Read in file
        try {
            File input = new File(file);
            Scanner reader = new Scanner(input);

            FileWriter fileWriter = new FileWriter(outputFile);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            int literal = reader.nextInt();
            ArrayList<Integer> trueStatements = new ArrayList<Integer>();

            // Check if there is a solution: First line "0" means there is no solution
            if (literal == 0) {
                printWriter.println("NO SOLUTION");
                printWriter.close();
                fileWriter.close();
                return;
            }
            else while (literal != 0) {
                String value = reader.next();
                if (value.equals("true")) {
                    trueStatements.add(literal);
                }
                literal = reader.nextInt();
            }

            // Find path
                // Assemble true statements
                // Find At statements, one true for each time
                // Print the Location of the true At Statements
            while (reader.hasNext()) {
                literal = reader.nextInt();
                if (trueStatements.get(0) == literal) {
                    trueStatements.remove(0);
                    String[] atom = reader.next().split("[(,]+");
                    String statement = atom[1];
                    if (atom[0].equals("Has")) {
                        break;
                    }
                    printWriter.printf("%s ", statement);
                }
                else {
                    String statement = reader.next();
                }
            }
            printWriter.close();
            fileWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred in reading the input file.");
        } catch (IOException e) {
            System.out.println("An error occurred in reading or writing the file.");
        }
    }
}

class Prog2 {
    public static void main(String args[]) {
        FrontEnd fe = new FrontEnd("input.txt", "FEout.txt");
        DavisPutnam dp = new DavisPutnam("FEout.txt", "DPout.txt");
        BackEnd be = new BackEnd("DPout.txt", "output.txt");
        
    }
}