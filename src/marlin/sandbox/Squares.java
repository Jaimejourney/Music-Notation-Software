package marlin.sandbox;

import marlin.graphicsLib.G;
import marlin.graphicsLib.I;
import marlin.graphicsLib.UC;
import marlin.graphicsLib.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Squares extends Window implements ActionListener {
    public Squares() {
        super("Square", UC.screenWeight, UC.screenHeight);
        timer = new Timer(33,this);//this?
        timer.setInitialDelay(5000);
        timer.start();
    }
    public static Timer timer;

    public static G.VS theVS = new G.VS(100, 100, 200, 300);
    public static Color theColor = G.rndColor();
    public static Square theSquare = new Square(200, 328);
    public static Square.List theList = new Square.List();
    public static Square backGroundSquare = new Square(0,0){
        @Override
        public void dn(int x, int y){
            theList.add(new Square(x,y));
        }
        public void drag(int x, int y){
            Square s = theList.get(theList.size()-1);
            int w = Math.abs(x - s.loc.x);
            int h = Math.abs(y - s.loc.y);
            s.resize(w,h);
        }
        public void up(int x, int y){
            firstpressed.set(x,y);
        }

    };
    static{
        theList.add(backGroundSquare);
        backGroundSquare.size.set(3000,3000);
        backGroundSquare.c = Color.white;
    }//??
    public static Boolean dragging = false;
    public static G.V mousePosition = new G.V(0,0);
    public static G.V firstpressed = new G.V(0,0);//???
    public static I.Area currentArea;

    public void paintComponent(Graphics g) {
        G.fillBackground(g, Color.white);
//        g.setColor(Color.BLUE);
//        g.fillRect(100,200,300,300);
//        theVS.fill(g, theColor);
        theList.draw(g);
    }

    public void mousePressed(MouseEvent me) {
//        if(theVS.hit(me.getX(),me.getY())){
//            theColor = G.rndColor();
//        }
        int x = me.getX();
        int y = me.getY();
        firstpressed.set(x,y);
        theSquare = theList.hit(x,y);
        currentArea = theSquare;
        currentArea.dn(x,y);
        repaint();
    }

    public void mouseDragged(MouseEvent me){
        int x = me.getX(), y = me.getY();
        currentArea.drag(x,y);
        /*if(dragging == false){
        }else{
        }
        */
        repaint();
    }

    public void mouseReleased(MouseEvent me){
        int x = me.getX();
        int y = me.getY();
        currentArea.up(x,y);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        repaint();

    }

    public static class Square extends G.VS implements I.Area {
        public Color c = G.rndColor();
        public G.V dv = new G.V(G.rnd(20)-10,G.rnd(20)-10);
        public Square(int x, int y) {
            super(x, y, 100, 100);
        }
        public void moveAndBounce(){
            loc.add(dv);
            if(lox() < 0 && dv.x < 0){ dv.x = -dv.x;}
            if(loy() < 0 && dv.y < 0){ dv.y = -dv.y;}
            if(hix() > 1000 && dv.x > 0){ dv.x = -dv.x;}
            if(hiy() > 800 && dv.y > 0){ dv.y = -dv.y;}
        }
        public void draw(Graphics g) {
            this.fill(g, c);
            moveAndBounce();
        }

        public void resize(int x,int y){
            size.x = x;
            size.y = y;
        }

        @Override
        public void dn(int x, int y) {
            theSquare.dv.set(0,0);
            mousePosition.x = x - theSquare.loc.x;
            mousePosition.y = y - theSquare.loc.y;
        }

        @Override
        public void drag(int x, int y) {
            theSquare.loc.x = x - mousePosition.x;
            theSquare.loc.y = y - mousePosition.y;
        }

        @Override
        public void up(int x, int y) {
            theSquare.dv.set((x- firstpressed.x),y-firstpressed.y);
        }

        public static class List extends ArrayList<Square> {
            public void draw(Graphics g) {
                for (Square s : this) {
                    s.draw(g);
                }
            }
            public Square hit(int x , int y){
                Square result = null;
                for(Square s : this){
                    if(s.hit(x,y)){
                        result = s;
                    }
                }
                return result;
            }
        }
    }
}
