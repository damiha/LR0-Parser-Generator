public class Terminal extends Symbol{

    String name;

    public Terminal(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }

    public boolean equals(Object o){
        return o instanceof Terminal nT && nT.name.equals(name);
    }
}
