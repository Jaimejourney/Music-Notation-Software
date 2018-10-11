package marlin.reaction;

import marlin.graphicsLib.G;
import marlin.graphicsLib.I;
import marlin.graphicsLib.UC;

import java.awt.*;
import java.io.*;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Shape implements Serializable {
    public String name;
    public Prototype.List prototypes = new Prototype.List();
    public Shape(String name){this.name = name;}

    public static HashMap<String,Shape> DB = loadDB();
    public static Shape DOT = DB.get("DOT");
    public static Collection<Shape> LIST = DB.values();

    public static HashMap<String,Shape> loadDB(){
        HashMap<String,Shape> res = new HashMap<>();
        res.put("DOT",new Shape("DOT"));
//        res.put("EEE",new Shape("E"));
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(UC.shapeDBFileName));
            res = (HashMap<String, Shape>) ois.readObject();
            System.out.println("Loaded Shape DB.");
        } catch (Exception e) {
            System.out.println("Failed load Shape DB.");
            System.out.println(e);
        }
        return res;
    }

    public static void saveDB(){
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(UC.shapeDBFileName));
            oos.writeObject(DB);
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public static Shape recognize(Ink ink){
        if(ink.vs.size.x < UC.dotThreshold  && ink.vs.size.y < UC.dotThreshold){
            return DOT;
        }
        Shape bestMatched = null;
        int bestSoFar = UC.noMathchDist;
        for(Shape s : LIST){
            int d = s.prototypes.bestDist(ink.norm);
            if(d<bestSoFar) bestSoFar = d; bestMatched = s;
        }

        return bestMatched;
    }

    public static class Prototype extends Ink.Norm implements Serializable{
        public int nBlends = 1;//running average
        public void blend(Ink.Norm norm){
            for (int i = 0; i < N; i++) {
                points[i].blend(norm.points[i],nBlends);
            }
            nBlends++;
        }
        public static class List extends ArrayList<Prototype> implements I.Show,Serializable{
            public static Prototype bestMatch;
            public int bestDist(Ink.Norm norm){
                bestMatch = null;
                int bestSoFar = UC.noMathchDist;
                for(Prototype p:this){
                    int d = p.dist(norm);
                    if(d<bestSoFar) bestSoFar=d; bestMatch=p;
                }
                return bestSoFar;
            }
            private static int m = 10,w = 60;
            private static G.VS showBOX = new G.VS(m,m,w,w);
            @Override
            public void show(Graphics g) {
                g.setColor(Color.black);
                for (int i = 0; i < size(); i++) {
                    int x = (i+1)*m + i*w;
                    showBOX.set(x,m,w,w);
                    Prototype p = get(i);
                    p.drawAt(g,showBOX);
                    g.drawString(""+p.nBlends,x,m);
                }
            }
        }
    }
}
