import java.util.ArrayList;
import java.util.List;

public class Grammar {

    NonTerminal startSymbol;

    List<Production> productions;

    List<Terminal> terminals;

    List<NonTerminal> nonTerminals;

    public Grammar(NonTerminal startSymbol, List<Production> productions, List<Terminal> terminals, List<NonTerminal> nonTerminals){
        this.startSymbol = startSymbol;
        this.productions = new ArrayList<>(productions);
        this.terminals = new ArrayList<>(terminals);
        this.nonTerminals = new ArrayList<>(nonTerminals);
    }

    public List<Symbol> getSymbols(){
        List<Symbol> symbols = new ArrayList<>();
        symbols.addAll(terminals);
        symbols.addAll(nonTerminals);
        return symbols;
    }
}
