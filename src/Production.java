import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Production {

    NonTerminal lHs;
    List<Symbol> rHs;

    public Production(NonTerminal lHs, List<Symbol> rHs){
        this.lHs = lHs;
        this.rHs = rHs;
    }

    public String toString(){

        String rHsString = rHs.stream().map(Objects::toString).collect(Collectors.joining(""));

        return String.format("%s -> %s", lHs.toString(), rHsString);
    }
}
