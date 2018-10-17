package marlin.graphicsLib;

import marlin.reaction.Gesture;

import java.awt.*;

public interface I {
    public interface Show{
        public void show(Graphics g);

    }
    public interface Area{
        public boolean hit(int x,int y);
        public void dn(int x, int y);
        public void drag(int x, int y);
        public void up(int x, int y);

    }

    public interface Act{
        public void act(Gesture gesture);
    }
    public interface React extends Act {
        public int bid(Gesture gesture);
    }
}
