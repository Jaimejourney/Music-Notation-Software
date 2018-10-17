package marlin.reaction;

import marlin.graphicsLib.I;
import marlin.graphicsLib.UC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Reaction implements I.React{
    public Shape shape;
    public static List initialReactions  =  new List();
    private static Map byShapeMap = new Map();
    public Reaction(String shapeName){
        this.shape = Shape.DB.get(shapeName);
        if(this.shape == null){
            System.out.println("Shape not in DB:"+ shapeName);
        }
    }

    public static Reaction best(Gesture gesture){
        return byShapeMap.getList(gesture.shape).lowBid(gesture);
    }

    public static class List extends ArrayList<Reaction>{
        public void addReaction(Reaction r){
            this.add(r);
            byShapeMap.addReaction(r);
        }

        public void removeReaction(Reaction r){
            this.remove(r);
            byShapeMap.removeReaction(r);
        }

        public void clearAll(){
            for(Reaction r: this){
                byShapeMap.removeReaction(r);
            }
            this.clear();
        }

        public Reaction lowBid(Gesture gesture){
            Reaction res = null;
            int bestSoFar = UC.noBid;
            for(Reaction r:this){
                int b = r.bid(gesture);
                if(b<bestSoFar){
                    bestSoFar = b;
                    res = r;
                }
            }
            return res;
        }
    }
    public static class Map extends HashMap<Shape,List>{
        public List getList(Shape shape){
            List res = get(shape);
            if(res == null){
                res = new List();
                put(shape,res);
            }
            return res;
        }
        public void addReaction(Reaction r){
            byShapeMap.getList(r.shape).add(r);
        }
        public void removeReaction(Reaction r){
            byShapeMap.getList(r.shape).remove(r);
        }
    }
}
