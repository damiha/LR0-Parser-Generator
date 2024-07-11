import java.util.List;
import java.util.stream.Collectors;

public abstract class ASTNode {

    abstract String yield();

    static class Inner extends ASTNode{

        Production head;

        // in general, can be n-ary
        List<ASTNode> children;

        public Inner(Production head, List<ASTNode> children) {
            this.head = head;
            this.children = children;
        }

        public String yield(){
            return children.stream().map(ASTNode::yield).collect(Collectors.joining(""));
        }
    }

    static class Leaf extends ASTNode{

        Terminal terminal;

        public Leaf(Terminal terminal) {
            this.terminal = terminal;
        }

        public String yield(){
            return terminal.toString();
        }
    }
}
