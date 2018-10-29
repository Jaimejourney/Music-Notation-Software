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
    public static Music.Sys.Fmt SYSFMT = null;
    public static ArrayList<Music.Sys> SYSTEMS = new ArrayList<Music.Sys>();
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
                    SYSFMT = new Music.Sys.Fmt();
                    SYSTEMS.clear();
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
                if(y > SYSTEMS.get(SYSTEMS.size()-1).yBot()+15){return 100;}
                else{return UC.noBid;}
            }

            @Override
            public void act(Gesture gesture) {
                int y = gesture.vs.midy();
                if(SYSTEMS.size() == 1){
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
                ndx = SYSTEMS.size();
                SYSTEMS.add(this);
                makeStaffMatchSysFmt();
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
            public static class Fmt extends ArrayList<Staff.Fmt> {
                public int MAXH = UC.defaultStaffLineSpace;
                public int height(){
                    Staff.Fmt last = get(size()-1);
                    return last.dy+last.height();
                }

                public void addNewStaff(int y){
                    new Staff.Fmt(y-PAGE.top);
                    for(Sys s: SYSTEMS){
                        s.makeStaffMatchSysFmt();

                    }
                }

                public void showAt(Graphics g,int y){
                    for(Staff.Fmt sf:this){
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
                addReaction(new Reaction("S-S") {//creates a bar
                    public int bid(Gesture g) {
                        int x = g.vs.midx(), y1 = g.vs.loy(), y2 = g.vs.hiy();
                        if(x < PAGE.left || x > PAGE.right+UC.barToMarginSnap){return UC.noBid;}
                        int dt = Math.abs(y1-Staff.this.yTop());
                        int db = Math.abs(y2-Staff.this.yBot());
                        if(dt > 15 || db > 15){return UC.noBid;}
                        return dt+db+20;
                    }
                    public void act(Gesture g) {
                        new Bar(Staff.this.sys,g.vs.midx());
                    }
                });
                addReaction(new Reaction("S-S") {//toggle barContinues
                    public int bid(Gesture g) {
                        if (Staff.this.sys.ndx != 0) {
                            return UC.noBid;
                        }
                        int y1 = g.vs.loy(), y2 = g.vs.hiy();
                        if (Staff.this.ndx == SYSFMT.size() - 1) {
                            return UC.noBid;
                        }
                        if (Math.abs(y1 - Staff.this.yBot()) > 20) {return UC.noBid;}
                        Staff nextStaff = Staff.this.sys.staffs.get(Staff.this.ndx + 1);
                        if (Math.abs(y2 - nextStaff.yTop()) > 20) {return UC.noBid;}
                        return 10;
                    }

                    public void act(Gesture g) {
                        SYSFMT.get(Staff.this.ndx).toggleBarContinues();
                    }
                });
            }

            public void show(Graphics g) {

            }
            public int yTop(){
                return sys.yTop()+SYSFMT.get(ndx).dy;
            }

            public int yBot(){
                return yTop()+SYSFMT.get(ndx).height();
            }

            public static class Fmt {
                public int nLines = 5;
                public int H = UC.defaultStaffLineSpace;
                public int dy = 0;
                public boolean barContinues = false;

                public void toggleBarContinues(){
                    barContinues = !barContinues;
                }

                public Fmt(int dy) {
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
            public Sys sys;
            public int x;
            public static int barType;
            public static int LEFT = 4,RIGHT = 8;


            public Bar(Sys sys,int x) {
                super("BACK");
                this.sys = sys;
                this.x = x;
                if(Math.abs(x-PAGE.right)<UC.barToMarginSnap){this.x = PAGE.right;}
                this.barType = 0;
                addReaction(new Reaction("S-S") {//cycling the barType
                    public int bid(Gesture g) {
                        int x = g.vs.midx(), y1 = g.vs.loy(), y2 = g.vs.hiy();
                        if(Math.abs(x - Bar.this.x)>UC.barToMarginSnap){return UC.noBid;}
                        if(y1<Bar.this.sys.yTop()-20){return UC.noBid;}
                        if(y2>Bar.this.sys.yBot()+20){return UC.noBid;}
                        return Math.abs(x-Bar.this.x);
                    }

                    public void act(Gesture g) {
                        Bar.this.cycleType();
                    }
                });
            }

            public void cycleType(){
                barType++;
                if(barType > 2){barType = 0;}
            }
            public void toggleLeft(){barType=barType^LEFT;}
            public void toggleRight(){barType=barType^RIGHT;}

            public void show(Graphics g) {
                int yTop = sys.yTop(),y1 = 0,y2 = 0;
                boolean justSawBreak = true;
                for(Staff.Fmt sf:SYSFMT){
                    int top = yTop + sf.dy;
                    int bot = top + sf.height();
                    if(justSawBreak){y1 = top;}
                    justSawBreak = !sf.barContinues;
                    if(justSawBreak){drawLines(g,x,y1,bot);}
                    if(barType > 3){drawDots(g,x,top);}
                }
            }

            public static void wings(Graphics g,int x,int y1,int y2,int dx,int dy){
                g.drawLine(x,y1,x+dx,y1-dy);
                g.drawLine(x,y2,x+dx,y2+dy);
            }

            public static void fatBar(Graphics g,int x,int y1,int y2,int dx){
                g.fillRect(x,y1,dx,y2-y1);
            }

            public static void thinBar(Graphics g,int x,int y1,int y2){
                g.drawLine(x,y1,x,y2);
            }

            public static void drawDots(Graphics g,int x,int top){
                int H = SYSFMT.MAXH;
                if((barType & LEFT) != 0){
                    g.fillOval(x-3*H,top+11*H/4,H/2,H/2);
                    g.fillOval(x-3*H,top+19*H/4,H/2,H/2);
                }
                if((barType & RIGHT) != 0){
                    g.fillOval(x+3*H,top+11*H/4,H/2,H/2);
                    g.fillOval(x+3*H,top+19*H/4,H/2,H/2);
                }
            }

            public void drawLines(Graphics g,int x,int y1,int y2){
                int H = SYSFMT.MAXH;
                if(barType == 0){thinBar(g,x,y1,y2);}
                if(barType == 1){thinBar(g,x,y1,y2);thinBar(g,x-H,y1,y2);}
                if(barType == 2){fatBar(g,x-H,y1,y2,H);thinBar(g,x-2*H,y1,y2);}
                if(barType > 4){
                    fatBar(g,x-H,y1,y2,H);
                    if((barType & LEFT) != 0){
                        thinBar(g,x-2*H,y1,y2);
                        wings(g,x-2*H,y1,y2,-H,H);
                    }
                    if((barType & RIGHT) != 0){
                        thinBar(g,x+H,y1,y2);
                        wings(g,x+H,y1,y2,H,H);
                    }
                }
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
