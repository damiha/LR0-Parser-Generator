import java.util.List;

public class NonTerminal extends Symbol{

    String name;

    public NonTerminal(String name){
       this.name = name;
    }

    public String toString(){
        return name;
    }
}
