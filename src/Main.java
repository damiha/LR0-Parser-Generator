import java.util.List;

public class Main {
    public static void main(String[] args) {

        NonTerminal E = new NonTerminal("E");
        NonTerminal T = new NonTerminal("T");
        Terminal id = new Terminal("id");
        Terminal plus = new Terminal("+");
        Terminal leftParen = new Terminal("(");
        Terminal rightParen = new Terminal(")");
        Terminal eof = new Terminal("$");

        List<Production> productions = List.of(
                new Production(E, List.of(E, plus, T)),
                new Production(E, List.of(T)),
                new Production(T, List.of(leftParen, E, rightParen)),
                new Production(T, List.of(id))
        );

        System.out.println(productions);

        Grammar grammar = new Grammar(
                E, productions, List.of(id, plus, leftParen, rightParen), List.of(E, T)
        );

        LRZeroTableGenerator tableGenerator = new LRZeroTableGenerator();

        tableGenerator.createActionAndGotoTable(grammar);
    }
}