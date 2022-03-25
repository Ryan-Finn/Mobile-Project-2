package edu.sdsmt.group4.Model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.ArrayList;
import java.util.Random;

public class CaptureObject {
    protected float x = 0;
    protected float y = 0;
    protected float width;
    protected float height;
    protected float scale;
    protected Random random;

    /**
     * Gets collectables that are contained within a capture object from the GameBoardActivity's
     * list of displayed collectables. This is called when the player lifts all of their fingers,
     * thus ending their turn.
     *
     * @param list - List of collectables in the main class
     * @return Collectables contained within the capture object.
     */
    public ArrayList<Collectable> getContainedCollectables(ArrayList<Collectable> list) {
        return new ArrayList<>();
    }

    public void draw(Canvas canvas, float width, float height, Paint p, Random random) {}

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getScale() {
        return scale;
    }

    public float getAngle() {
        return 0;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setAngle(float angle) {
    }

// --Commented out by Inspection START (3/6/2022 1:08 AM):
//    // Debug function that shows hit boxes for all collectable objects
//    public void debug(Canvas canvas, ArrayList<Collectable> list) {
//        Paint p = new Paint();
//        ArrayList<Collectable> collected = getContainedCollectables(list);
//
//        for (Collectable obj : list) {
//            if (!collected.contains(obj))
//                p.setColor(Color.BLUE);
//            else
//                p.setColor(Color.GREEN);
//            p.setAlpha(40);
//            canvas.drawCircle(obj.getX(), obj.getY(), obj.getRadius(), p);
//        }
//    }
// --Commented out by Inspection STOP (3/6/2022 1:08 AM)
}
