import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParserTest {

    public static void main(String[] args) {

        // grammar from Stanford Bottom Up Parsing 0

        // augmented grammar
        NonTerminal Ep = new NonTerminal("E'");

        NonTerminal E = new NonTerminal("E");
        NonTerminal T = new NonTerminal("T");
        Terminal id = new Terminal("id");
        Terminal plus = new Terminal("+");
        Terminal leftParen = new Terminal("(");
        Terminal rightParen = new Terminal(")");
        Terminal eof = new Terminal("$");

        List<Production> productions = List.of(
                new Production(Ep, List.of(E)), // for augmented grammar (table generation)
                new Production(E, List.of(E, plus, T)),
                new Production(E, List.of(T)),
                new Production(T, List.of(leftParen, E, rightParen)),
                new Production(T, List.of(id))
        );

        System.out.println(productions);

        Grammar grammar = new Grammar(
                E, productions, List.of(id, plus, leftParen, rightParen), List.of(E, T)
        );

        // actions tell the parser what to do when a TERMINAL is read in

        // first index = what state are you in
        // second index = what token was read in

        // we have 5 terminals
        // id, +, (, ), $ (end of input)

        // and 9 states (will come from the canonical automaton)
        Action[][] actionTable = {
                {new Action.Shift(4), null, new Action.Shift(3), null, null},  // Row 0
                {null, new Action.Shift(5), null, null, new Action.Accept()},  // Row 1
                {new Action.Reduce(2), new Action.Reduce(2), new Action.Reduce(2), new Action.Reduce(2), new Action.Reduce(2)},  // Row 2
                {new Action.Shift(4), null, new Action.Shift(3), null, null},  // Row 3
                {new Action.Reduce(4), new Action.Reduce(4), new Action.Reduce(4), new Action.Reduce(4), new Action.Reduce(4)},  // Row 4
                {new Action.Shift(4), null, new Action.Shift(3), null, null},  // Row 5
                {null, new Action.Shift(5), null, new Action.Shift(7), null},  // Row 6
                {new Action.Reduce(3), new Action.Reduce(3), new Action.Reduce(3), new Action.Reduce(3), new Action.Reduce(3)},  // Row 7
                {new Action.Reduce(1), new Action.Reduce(1), new Action.Reduce(1), new Action.Reduce(1), new Action.Reduce(1)}   // Row 8
        };

        int[][] goToTable = {
                {1, 2},
                {-1, -1},
                {-1, -1},
                {6, 2},
                {-1, -1},
                {-1, 8},
                {-1, -1},
                {-1, -1},
                {-1, -1}
        };

        Map<Terminal, Integer> terminalToIdx = new HashMap<>();
        terminalToIdx.put(id, 0);
        terminalToIdx.put(plus, 1);
        terminalToIdx.put(leftParen, 2);
        terminalToIdx.put(rightParen, 3);
        terminalToIdx.put(eof, 4);

        Map<NonTerminal, Integer> nonTerminalToIdx = new HashMap<>();
        nonTerminalToIdx.put(E, 0);
        nonTerminalToIdx.put(T, 1);

        Parser parser = new Parser(actionTable, goToTable, terminalToIdx, nonTerminalToIdx, grammar);

        List<Terminal> input = List.of(
                id, plus, leftParen, id, rightParen, plus, leftParen, leftParen, id, rightParen, rightParen, eof
        );

        ASTNode node = parser.parse(input);

        System.out.println("Yield: ");
        System.out.println(node.yield());

        System.out.println("Result");
        Interpreter interpreter = new Interpreter();

        System.out.println(interpreter.interpret(node));
    }
}
