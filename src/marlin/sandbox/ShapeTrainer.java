package marlin.sandbox;

import marlin.graphicsLib.G;
import marlin.graphicsLib.UC;
import marlin.graphicsLib.Window;
import marlin.reaction.Ink;
import marlin.reaction.Shape;
import java.awt.Graphics;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class ShapeTrainer extends Window {
    public static final String ILLEGAL = "This name is illegal";
    public static final String KNOWN = "This name is known";
    public static final String UNKNOWN = "This name is unknown";
    public static String currentName = "Hello";
    public static String currentStatus = ILLEGAL;
    public static Shape.Prototype.List pList = null;
    public ShapeTrainer() {
        super("ShapeTrainer", UC.screenWeight,UC.screenHeight);
    }

    public static void setStatus(){
        currentStatus = (currentName.equals("") || currentName.equals("DOT")) ? ILLEGAL : UNKNOWN;
        if(currentStatus == UNKNOWN){
            if(Shape.DB.containsKey(currentName)){
                currentStatus = KNOWN;
                pList = Shape.DB.get(currentName).prototypes;
            }else{
                pList = null;
            }
        }
    }



    public void paintComponent(Graphics g){
        G.fillBackground(g, Color.white);
        g.setColor(Color.black);
        g.drawString(currentName,600,30);
        g.drawString(currentStatus,600,60);
        Ink.BUFFER.show(g);
        if(pList != null) pList.show(g);
    }

    public void keyTyped(KeyEvent ke){
        char c = ke.getKeyChar();
        System.out.println("Type" + c);
        currentName = c == ' ' ? "" : currentName +c;
        if(c == 10 || c == 13){
            currentName = "";
            Shape.saveDB();
        }
        setStatus();
        repaint();
    }

    public void mousePressed(MouseEvent me){
        Ink.BUFFER.dn(me.getX(),me.getY());
    }
    public void mouseDragged(MouseEvent me){
        Ink.BUFFER.drag(me.getX(),me.getY());
    }
    public void mouseReleased(MouseEvent me){
        if(currentStatus != ILLEGAL){
            Ink ink = new Ink();
            Shape.Prototype proto;
            if(pList == null){
                Shape s = new Shape(currentName);
                Shape.DB.put(currentName,s);
                pList = s.prototypes;
            }
            if(pList.bestDist(ink.norm) < UC.noMathchDist){
                proto = Shape.Prototype.List.bestMatch;
                proto.blend(ink.norm);
            }else{
                proto = new Shape.Prototype();
                pList.add(proto);
            }
            setStatus();
        }
        repaint();
    }
}
