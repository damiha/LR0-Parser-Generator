import java.util.List;

public class Grammar {

    NonTerminal startSymbol;

    List<Production> productions;

    List<Terminal> terminals;

    List<NonTerminal> nonTerminals;

    public Grammar(NonTerminal startSymbol, List<Production> productions, List<Terminal> terminals, List<NonTerminal> nonTerminals){
        this.startSymbol = startSymbol;
        this.productions = productions;
        this.terminals = terminals;
        this.nonTerminals = nonTerminals;
    }
}
