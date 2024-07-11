import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Item {

    NonTerminal lHs;
    List<Symbol> rHs;

    // 0 = dot is BEFORE the first symbol
    int dotPosition;

    public Item(NonTerminal lHs, List<Symbol> rHs, int dotPosition){
        this.lHs = lHs;
        this.rHs = rHs;
        this.dotPosition = dotPosition;
    }

    public String toString(){

        List<String> rhsStrings = new java.util.ArrayList<>(rHs.stream().map(Objects::toString).toList());
        rhsStrings.add(dotPosition, "#");
        return String.format("%s -> %s", lHs.toString(), rhsStrings.stream().collect(Collectors.joining("")));
    }

    public NonTerminal nonTerminalAfterDot(){
        return (NonTerminal) rHs.get(dotPosition);
    }

    public Symbol symbolAfterDot(){
        return dotPosition < rHs.size() ? rHs.get(dotPosition) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return dotPosition == item.dotPosition &&
                Objects.equals(lHs, item.lHs) &&
                Objects.equals(rHs, item.rHs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lHs, rHs, dotPosition);
    }
}
