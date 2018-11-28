package marlin.sandbox;

import marlin.Reaction.Gesture;
import marlin.graphicsLib.G;
import marlin.graphicsLib.UC;
import marlin.graphicsLib.Window;

import java.awt.*;
import java.awt.event.MouseEvent;

public class Splines extends Window {

    public Splines() {
        super("splines", UC.screenWeight, UC.screenHeight);
    }

    public static Point[] points = {new Point(100,100), new Point(100,200),new Point(300,300)};
    public static int cPoint = 0;

    public void paintComponent(Graphics g){
        G.fillBackground(g,Color.white);
        g.setColor(Color.red);
        G.poly.reset();
        G.pSpline(points[0].x,points[0].y,points[1].x,points[1].y,points[2].x,points[2].y,6);
        g.fillPolygon(G.poly);
    }

    @Override
    public void mousePressed(MouseEvent me) {
        cPoint = closestPoint(me.getX(),me.getY());
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        points[cPoint].x = me.getX();
        points[cPoint].y = me.getY();
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        repaint();
    }

    public int closestPoint(int x,int y){
        int result = 0;
        int closestDistance = 10000000;
        for (int i = 0; i < points.length; i++) {
            int d = (points[i].x - x)*(points[i].x - x) + (points[i].y - y)*(points[i].y - y);
            if(d < closestDistance){
                closestDistance = d;
                result = i;
            }
        }
        return result;
    }


}
