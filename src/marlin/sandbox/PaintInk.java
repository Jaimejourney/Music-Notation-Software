package marlin.sandbox;

import marlin.graphicsLib.G;
import marlin.graphicsLib.UC;
import marlin.graphicsLib.Window;
import marlin.reaction.Ink;
import marlin.reaction.Shape;

import java.awt.*;
import java.awt.event.MouseEvent;

public class PaintInk extends Window {
    public static Ink.List inkList = new Ink.List();
    public static Shape.Prototype.List pList = new Shape.Prototype.List();
    public static String recognized = "";

    public PaintInk() {
        super("PaintInk", UC.screenWeight,UC.screenHeight);
    }

    public void paintComponent(Graphics g){
        G.fillBackground(g,Color.white);
        g.setColor(Color.red);
//        g.drawString("Points :" + Ink.BUFFER.n,600,30);
//        G.VS vs = new G.VS(100,100,100,100);
//        G.V.T.set(Ink.BUFFER.bbox,vs);
//        G.PL pl = new G.PL(25);
//        Ink.BUFFER.subSample(pl);
//        pl.transForm();
//        pl.draw(g);
//        g.fillRect(100,100,100,100);
        pList.show(g);
//        inkList.show(g);//鼠标释放后是否仍显示线条
        Ink.BUFFER.show(g);
        g.drawString(recognized,700,40);
//        if(Ink.BUFFER.n>0){
//            Ink.Norm norm = new Ink.Norm();
//            norm.drawAt(g,new G.VS(500,30,100,100));
//            norm.drawAt(g,new G.VS(50,200,200,200));
//        }
    }

    public void mousePressed(MouseEvent me){
        int x = me.getX();
        int y = me.getY();
        Ink.BUFFER.dn(x,y);
        repaint();
    }

    public void mouseDragged(MouseEvent me){
        int x = me.getX();
        int y = me.getY();
        Ink.BUFFER.drag(x,y);
        repaint();
    }

    public void mouseReleased(MouseEvent me){
//      inkList.add(new Ink());
        Ink ink = new Ink();
        Shape s = Shape.recognize(ink);
        recognized = "Recognized:" + ((s == null) ? "UNKNOWN" : s.name);
//        inkList.add(ink);
//        Shape.Prototype proto;
//        if(pList.bestDist(ink.norm) < UC.noMathchDist) {
//            Shape.Prototype.List.bestMatch.blend(ink.norm);
//            proto = Shape.Prototype.List.bestMatch;
//        }
//        else{
//            proto = new Shape.Prototype();
//            pList.add(proto);
//        }
//        ink.norm = proto;
        repaint();
    }
}
