
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigurationSet {

    Set<Item> items;
    int stateId = -1;

    public ConfigurationSet(Set<Item> items){
        this.items = new HashSet<>(items);
    }

    public ConfigurationSet(int stateId, Set<Item> items){
        this(items);
        this.stateId = stateId;
    }

    public String toString(){
        StringBuilder res = new StringBuilder(String.format("State I_%d\n", stateId));
        for(Item item : items){
            res.append(String.format("%s\n", item.toString()));
        }
        return res.toString();
    }

    public boolean isEmpty(){
        return items.isEmpty();
    }

    @Override
    public int hashCode(){
        return this.items.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        //System.out.println("Comparison");
        //System.out.println(this);
        //System.out.println(o);
        boolean result = (o instanceof ConfigurationSet other) && (items.equals(other.items));
        //System.out.println("Result: ");
        //System.out.println(result);
        return result;
    }
}
