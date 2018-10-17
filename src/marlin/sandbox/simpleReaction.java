package marlin.sandbox;

import marlin.graphicsLib.G;
import marlin.graphicsLib.UC;
import marlin.graphicsLib.Window;
import marlin.reaction.*;

import java.awt.*;
import java.awt.event.MouseEvent;


public class simpleReaction extends Window {
    static{
        new Layer("BACK");
        new Layer("FORE");
    }
    public simpleReaction() {
        super("simpleReaction", UC.screenWeight,UC.screenHeight);
        Reaction.initialReactions.addReaction(new Reaction("SW-SW"){
            public int bid(Gesture gesture){return 0;}
            public void act(Gesture gesture){
               new Box(gesture.vs);
            }
        });
    }

    public void paintComponent(Graphics g){
        G.fillBackground(g,Color.white);
        g.setColor(Color.blue);
        Ink.BUFFER.show(g);
        Layer.ALL.show(g);
    }

    public void mousePressed(MouseEvent me){
        Gesture.AREA.dn(me.getX(),me.getY());
    }
    public void mouseDragged(MouseEvent me){
        Gesture.AREA.drag(me.getX(),me.getY());
    }
    public void mouseReleased(MouseEvent me){
        Gesture.AREA.up(me.getX(),me.getY());
    }
    public static class Box extends Mass {
        public G.VS vs;
        public Color c = G.rndColor();
        public Box(G.VS vs){
            super("BACK");
            this.vs = vs;
        }

        public void show(Graphics g){
            vs.fill(g,c);
        }
    }
}
