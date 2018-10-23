package marlin.sandbox;

import marlin.graphicsLib.I;
import marlin.Reaction.*;
import marlin.graphicsLib.UC;
import marlin.graphicsLib.G;
import marlin.graphicsLib.Window;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Random;

public class simpleReaction extends Window {
    static {
        new Layer("BACK");
        new Layer("FORE");
    }
    public static int SEED = 1234;
    public simpleReaction() {
        super("SimpleReaction", UC.screenWeight,UC.screenHeight);
        Reaction.initialReactions.addReaction(new Reaction("SW-SW"){
            @Override
            public int bid(Gesture gesture) { return 0;}
            @Override
            public void act(Gesture gesture) {new Box(gesture.vs);}
        });
        G.RND = new Random(SEED);
        Reaction.initialActions = new I.Act() {
            public void act(Gesture gesture) {
                G.RND = new Random(SEED);
            }
        };
    }

    public void paintComponent(Graphics g){
        G.fillBackground(g, Color.WHITE);
        g.setColor(Color.BLUE);
        Ink.BUFFER.show(g);
        Layer.ALL.show(g);
    }

    public void mousePressed(MouseEvent me){ Gesture.AREA.dn(me.getX(),me.getY());repaint(); }
    public void mouseDragged(MouseEvent me){ Gesture.AREA.drag(me.getX(),me.getY());repaint(); }
    public void mouseReleased(MouseEvent me){ Gesture.AREA.up(me.getX(),me.getY());repaint(); }

    public static class Box extends Mass{
        public G.VS vs;
        public Color c = G.rndColor();
        public Box(G.VS vs){
            super("BACK");
            this.vs = vs;
            addReaction(new Reaction("S-S") {
                @Override
                public int bid(Gesture gesture) {
                    int x = gesture.vs.midx();
                    int y = gesture.vs.loy();
                    if (Box.this.vs.hit(x, y)) {
                        return Math.abs(x-Box.this.vs.midx());
                    } else {
                        return UC.noBid;
                    }
                }

                @Override
                public void act(Gesture gesture) {
                    Box.this.delete();
                }
            });

            addReaction(new Reaction("DOT") {
                @Override
                public int bid(Gesture gesture) {
                    int x = gesture.vs.midx();
                    int y = gesture.vs.loy();
                    if (Box.this.vs.hit(x, y)) {
                        return Math.abs(x-Box.this.vs.midx());
                    } else {
                        return UC.noBid;
                    }
                }

                @Override
                public void act(Gesture gesture) {
                    c = G.rndColor();
                }
            });
        }

        @Override
        public void show(Graphics g) {
            vs.fill(g,c);
        }
    }

}
