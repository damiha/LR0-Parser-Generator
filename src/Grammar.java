import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public String toString(){
        return String.format("Terminals: %s\nNon-terminals: %s\nProductions: %s", terminals, nonTerminals, productions.stream().map(Object::toString).collect(Collectors.joining("\n")));
    }

    public int getProdId(Item item){

        int i = 0;
        for(Production production : productions){

            if(production.lHs.equals(item.lHs) && production.rHs.equals(item.rHs)){
                return i;
            }
            i++;
        }

        throw new RuntimeException("Production not found");
    }
}
