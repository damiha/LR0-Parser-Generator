import java.sql.SQLOutput;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        NonTerminal E = new NonTerminal("E");
        NonTerminal T = new NonTerminal("T");
        Terminal id = new Terminal("id");
        Terminal plus = new Terminal("+");
        Terminal leftParen = new Terminal("(");
        Terminal rightParen = new Terminal(")");

        List<Production> productions = List.of(
                new Production(E, List.of(E, plus, T)),
                new Production(E, List.of(T)),
                new Production(T, List.of(leftParen, E, rightParen)),
                new Production(T, List.of(id))
        );

        Grammar grammar = new Grammar(
                E, productions, List.of(id, plus, leftParen, rightParen), List.of(E, T)
        );

        System.out.println(grammar);

        LRZeroTableGenerator tableGenerator = new LRZeroTableGenerator();

        ParserTable parserTable = tableGenerator.createActionAndGotoTable(grammar);

        System.out.println("Table generated from grammar: ");
        System.out.println(parserTable);

        Parser parser = new Parser(parserTable);

        List<Terminal> input = List.of(
                id, plus, leftParen, id, rightParen, plus, leftParen, leftParen, id, rightParen, rightParen, new Terminal("$")
        );

        ASTNode node = parser.parse(input);

        System.out.printf("Yield: %s\n", node.yield());

        Interpreter interpreter = new Interpreter();

        System.out.printf("Result: %s\n", interpreter.interpret(node));
    }
}