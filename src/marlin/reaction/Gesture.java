package marlin.reaction;

import marlin.graphicsLib.G;
import marlin.graphicsLib.I;

public class Gesture {
    public Shape shape;
    public G.VS vs;
    private Gesture(Shape shape,G.VS vs){
        this.shape = shape;
        this.vs = vs;
    }
    public static Gesture getNew(Ink ink){
        Shape s = Shape.recognize(ink);
        return s== null ? null:new Gesture(s,ink.vs);
    }

    public static I.Area AREA = new I.Area(){
        public boolean hit(int x,int y){
            return true;
        }

        @Override
        public void dn(int x, int y) {
            Ink.BUFFER.dn(x,y);
        }

        @Override
        public void drag(int x, int y) {
            Ink.BUFFER.drag(x,y);
        }

        @Override
        public void up(int x, int y) {
            Ink.BUFFER.add(x,y);
            Ink ink = new Ink();
            Gesture gesture = Gesture.getNew(ink);
            Ink.BUFFER.clear();
            if(gesture != null){
                Reaction r = Reaction.best(gesture);
                if(r!=null){
                    r.act(gesture);
                }
            }
        }

    };
}
