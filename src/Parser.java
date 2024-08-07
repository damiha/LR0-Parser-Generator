import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Parser {

    Action[][] actionTable;
    int[][] gotoTable;
    Map<Terminal, Integer> terminalToIdx;
    Map<NonTerminal, Integer> nonTerminalToIdx;

    List<Integer> stateStack;
    List<ASTNode> nodeStack;

    Grammar grammar;

    boolean printDebug = false;

    public Parser(ParserTable parserTable){
        this.actionTable = parserTable.actionTable;
        this.gotoTable = parserTable.gotoTable;
        this.terminalToIdx = parserTable.terminalToIdx;
        this.nonTerminalToIdx = parserTable.nonTerminalToIdx;
        this.grammar = parserTable.grammar;
    }

    private void log(String message){
        if(printDebug){
            System.out.printf("[DEBUG]: %s\n", message);
        }
    }

    // input is always terminal symbols
    public ASTNode parse(List<Terminal> input){

        stateStack = new ArrayList<>();
        nodeStack = new ArrayList<>();

        // push the initial state on the stack
        stateStack.add(0);

        int i = 0;

        while(true){
            Terminal t = input.get(i);

            int tIdx = terminalToIdx.get(t);

            int state = stateStack.getLast();

            Action a = actionTable[state][tIdx];

            if(a == null){
                throw new RuntimeException("Error during parsing");
            }

            else if(a instanceof Action.Shift shift){
                int nextState = shift.nextStateId;

                log(String.format("Changing to state %d\n", nextState));

                stateStack.add(nextState);

                nodeStack.add(new ASTNode.Leaf(t));

                // get new input
                i++;
            }
            else if(a instanceof Action.Reduce reduce){
                Production reduceWith = grammar.productions.get(reduce.prodId);

                log(String.format("Reducing with production %s\n", reduceWith.toString()));

                int nonTerminalIdx = nonTerminalToIdx.get(reduceWith.lHs);

                // pop as many states as the production rule has children
                int nToPop = reduceWith.rHs.size();

                List<ASTNode> nodesPopped = new ArrayList<>();

                for(int j = 0; j < nToPop; j++) {
                    stateStack.removeLast();

                    // move over in the right order
                    if(!nodeStack.isEmpty()) {
                        nodesPopped.addFirst(nodeStack.removeLast());
                    }
                }

                ASTNode node = new ASTNode.Inner(reduceWith, nodesPopped);
                nodeStack.addLast(node);

                // we can only look into gotoTable AFTER we have popped the stack
                state = stateStack.getLast();

                int newState = gotoTable[state][nonTerminalIdx];
                stateStack.add(newState);

                log(String.format("Changing to state %d\n", newState));
            }
            else if(a instanceof Action.Accept){
                log("Parsing successful");
                return nodeStack.getLast();
            }
        }
    }
}
