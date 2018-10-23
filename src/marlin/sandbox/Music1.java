package marlin.sandbox;

import marlin.Reaction.*;
import marlin.graphicsLib.G;
import marlin.graphicsLib.I;
import marlin.graphicsLib.UC;
import marlin.graphicsLib.Window;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Music1 extends Window {

    static{
        new Layer("BACK");
        new Layer("NOTE");
        new Layer("FORE");
    }

    public static Music.Page PAGE = new Music.Page();
    public static Music.Sys.fmt SYSFMT = null;
    public static ArrayList<Music.Sys> SYSTEM = new ArrayList<Music.Sys>();
    public Music1() {
        super("Music1", UC.screenWeight,UC.screenHeight);
        Reaction.initialActions  = new I.Act(){
            public void act(Gesture gesture) {SYSFMT = null;}
        };
        Reaction.initialReactions.addReaction(new Reaction("E-E") {
            @Override
            public int bid(Gesture g) {
                if(SYSFMT == null){return 0;}
                int y = g.vs.midy();
                if(y > PAGE.top + SYSFMT.height() + 15){return 100;}
                else{return UC.noBid;}
            }

            @Override
            public void act(Gesture g) {
                int y = g.vs.midy();
                if(SYSFMT == null){
                    PAGE.top = y;
                    SYSFMT = new Music.Sys.fmt();
                    SYSFMT.clear();
                    new Music.Sys();
                }
                SYSFMT.addNewStaff(y);
            }
        });
        Reaction.initialReactions.addReaction(new Reaction("E-W") {
            @Override
            public int bid(Gesture gesture) {
                if(SYSFMT == null){return UC.noBid;}
                int y = gesture.vs.midy();
                if(y > SYSTEM.get(SYSTEM.size()-1).yBot()+15){return 100;}
                else{return UC.noBid;}
            }

            @Override
            public void act(Gesture gesture) {
                int y = gesture.vs.midy();
                if(SYSTEM.size() == 1){
                    PAGE.SysGap = y - (PAGE.top+SYSFMT.height());
                }
                new Music.Sys();
            }
        });
    }

    public void paintComponent(Graphics g){
        G.fillBackground(g,Color.white);
        g.setColor(Color.black);
        Ink.BUFFER.show(g);
        Layer.ALL.show(g);
    }


    public void mousePressed(MouseEvent me){Gesture.AREA.dn(me.getX(),me.getY());repaint(); }
    public void mouseDragged(MouseEvent me){Gesture.AREA.drag(me.getX(),me.getY());repaint(); }
    public void mouseReleased(MouseEvent me){Gesture.AREA.up(me.getX(),me.getY());repaint(); }





    public static class Music{
        //------------- Music -------------
        public static class Sys extends Mass {
            public ArrayList<Staff> staffs = new ArrayList<>();
            public int ndx;
            public Sys() {
                super("BACK");
                ndx = SYSTEM.size();
                SYSTEM.add(this);
            }

            public int yTop(){
                return PAGE.top+ndx*(SYSFMT.height()+PAGE.SysGap);
            }

            public int yBot(){
                return yTop()+SYSFMT.height();
            }

            @Override
            public void show(Graphics g) {
                SYSFMT.showAt(g,yTop());
                g.drawLine(PAGE.left,yTop(),PAGE.left,yBot());
            }

            public void makeStaffMatchSysFmt(){
                while(staffs.size() < SYSFMT.size()){
                    new Staff(this);
                }
            }
            public static class fmt extends ArrayList<Staff.fmt> {
                public int height(){
                    Staff.fmt last = get(size()-1);
                    return last.dy+last.height();
                }

                public void addNewStaff(int y){
                    new Staff.fmt(y-PAGE.top);
                    for(Sys s: SYSTEM){
                        s.makeStaffMatchSysFmt();

                    }
                }

                public void showAt(Graphics g,int y){
                    for(Staff.fmt sf:this){
                        sf.showAt(g,y+sf.dy);
                    }
                }
            }
        }

        public static class Staff extends Mass{
            public Sys sys;
            public int ndx;

            public Staff(Sys sys) {
                super("BACK");
                this.sys = sys;
                this.ndx = sys.staffs.size();
                sys.staffs.add(this);
            }

            @Override
            public void show(Graphics g) {

            }

            public static class fmt{
                public int nLines = 5;
                public int H = UC.defaultStaffLineSpace;
                public int dy = 0;

                public fmt(int dy) {
                    this.dy = dy;
                    SYSFMT.add(this);
                }
                public int height(){
                    return H*2*(nLines-1);
                }

                public void showAt(Graphics g,int y){
                    for (int i = 0; i < nLines; i++) {
                        int yy = y+2*i*H;
                        g.drawLine(PAGE.left,yy,PAGE.right,yy);
                    }
                }
            }
        }

        public static class Bar extends Mass{

            public Bar() {
                super("BACK");
            }

            @Override
            public void show(Graphics g) {

            }
        }

        public static class Page{
            public static int N = 50;
            public int top = N;
            public int left = N;
            public int bot = UC.screenHeight-N;
            public int right = UC.screenWeight-N;
            public int SysGap = 0;
        }
    }
}
