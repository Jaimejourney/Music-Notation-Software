package marlin.sandbox;


import marlin.Reaction.Gesture;
import marlin.Reaction.Ink;
import marlin.Reaction.Layer;
import marlin.Reaction.Reaction;
import marlin.graphicsLib.G;
import marlin.graphicsLib.I;
import marlin.graphicsLib.UC;
import marlin.graphicsLib.Window;
import marlin.music.APP;
import marlin.music.Beam;
import marlin.music.Sys;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Music2 extends Window implements I.MusicApp {
    static {
        new Layer("BACK");
        new Layer("NOTE");
        new Layer("FORE");
    }

    public static I.Page PAGE = new M2Page();
    public static ArrayList<I.Page> PAGES = new ArrayList<>();
    static { PAGES.add(PAGE); }

    public static Sys.Fmt SYSFMT = null;
    public static ArrayList<Sys> SYSTEMS = new ArrayList<>();

    public ArrayList<I.Page> pages() { return PAGES; }

    public Sys.Fmt sysfmt(I.Page page) { return SYSFMT; }

    public ArrayList<Sys> systems(I.Page page) { return SYSTEMS; }


    public Music2() {
        super("Music2", UC.screenWeight, UC.screenHeight);
        APP.get = this;
        Reaction.initialActions = new I.Act() {
            public void act(Gesture gesture) {
                SYSFMT = null;
            }
        };
        Reaction.initialReactions.addReaction(new Reaction("E-E") {
            public int bid(Gesture g) {
                if (SYSFMT == null) {
                    return 0;
                }
                int y = g.vs.midy();
                if (y > PAGE.top() + SYSFMT.height() + 15) { // 15 or 10
                    return 100;
                } else {
                    return UC.noBid;
                }
            }

            public void act(Gesture g) {
                int y = g.vs.midy();
                if (SYSFMT == null) {
                    ((M2Page) PAGE).top = y;
                    SYSFMT = new Sys.Fmt();
                    SYSTEMS.clear();
                    new Sys(PAGE);
                }
                SYSFMT.addNewStaff(y, PAGE);
            }
        });
        Reaction.initialReactions.addReaction(new Reaction("E-W") {
            @Override
            public int bid(Gesture g) {
                if (SYSFMT == null) {
                    return UC.noBid;
                }
                int y = g.vs.midy();
                if (y > SYSTEMS.get(SYSTEMS.size() - 1).yBot() + 15) {
                    return 100;
                }
                return UC.noBid;
            }

            @Override
            public void act(Gesture g) {
                int y = g.vs.midy();
                if (SYSTEMS.size() == 1) {
                    PAGE.sysfmt().sysGap = y - (PAGE.top() + SYSFMT.height());
                }
                new Sys(PAGE);
            }
        });
    }

    static int[] xPoly = {100, 200, 200, 100};
    static int[] yPoly = {50, 70, 80, 60};
    static Polygon poly = new Polygon(xPoly, yPoly, 4);

    public void brace(int y1,int y2,int x,int h){
        //draw the brace
        G.poly.reset();
        int yh = 2*h;
        int yM = (y1+y2)/2;
        G.pSpline(x,y1+yh,x,y1,x+h,y1,4); //start from the top
        G.pSpline(x + h + h,y1,x+h,y1,x+h,y1+yh,4);
        G.pSpline(x + h,yM - yh,x+h,yM,x,yM,4);
        G.pSpline(x,yM,x+h,yM,x+h,yM+yh,4);
        G.pSpline(x+h,y2-yh,x+h,y2,x+h+h,y2,4);
        G.pSpline(x+h,y2,x,y2,x,y2-yh,4);
        G.pSpline(x,yM+yh,x,yM,x-h,yM,4);//mid
        G.pSpline(x-h,yM,x,yM,x,yM-yh,4);
    }

    public void tie(int x1,int x2,int y,int h,int b){//draw the tie between notes
        G.poly.reset();
        int xm = (x1+x2)/2;
        G.pSpline(x1,y,xm,y+b+h,x2,y,4);
        G.pSpline(x2,y,xm,y+b,x1,y,4);
    }

    @Override
    public void paintComponent(Graphics g) {
        G.fillBackground(g, Color.WHITE);
        g.setColor(Color.BLACK);
        Ink.BUFFER.show(g);
        Layer.ALL.show(g);
        int h = 8, x1 = 100, x2 = 200;
        Beam.setMasterBeam(x1, G.rnd(50) + 100, x2, G.rnd(50) + 100);
        Beam.drawBeamStack(g, 0, 1, x1, x2, h);
        g.setColor(Color.RED);
        Beam.drawBeamStack(g, 1, 3, x1 + 20, x2 - 20, h);
//    Glyph.CLEF_G.showAt(g, h, 100, PAGE.top + 4 * h);
//    Glyph.HEAD_Q.showAt(g, h, 180, PAGE.top + 4 * h); // xStem == 200?
//    g.drawRect(180, PAGE.top + 3 * h, 243 * h / 100, 2 * h);


        //TEST
       tie(500,600,500,10,-20);
       g.fillPolygon(G.poly);


    }

    @Override
    public void mousePressed(MouseEvent me) {
        Gesture.AREA.dn(me.getX(), me.getY());
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        Gesture.AREA.drag(me.getX(), me.getY());
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        Gesture.AREA.up(me.getX(), me.getY());
        repaint();
    }

    public static class M2Page implements I.Page {

        public int top = 50;

        public int top() {
            return top;
        }

        public int bot() {
            return UC.screenHeight - top;
        }

        public int left() {
            return top;
        }

        public int right() {
            return UC.screenWeight - top;
        }

        public Sys.Fmt sysfmt() {
            return SYSFMT;
        }

        public ArrayList<Sys> systems() {
            return SYSTEMS;
        }
    }
}
