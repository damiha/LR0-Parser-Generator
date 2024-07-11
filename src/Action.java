public class Action {

    static class Reduce extends Action{

        int prodId;

        public Reduce(int prodId){
            this.prodId = prodId;
        }

        public String toString(){
            return String.format("r%d", prodId);
        }
    }

    static class Shift extends Action{
        int nextStateId;

        public Shift(int nextStateId){
            this.nextStateId = nextStateId;
        }

        public String toString(){
            return String.format("s%d", nextStateId);
        }
    }

    static class Accept extends Action{

        public String toString(){
            return "acc";
        }
    }
}
