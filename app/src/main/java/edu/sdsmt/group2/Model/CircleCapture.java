package edu.sdsmt.group2.Model;

import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.ArrayList;
import java.util.Random;

public class CircleCapture extends CaptureObject {
    private float radius;

    public CircleCapture() {
        setScale(.2f);
        radius = 0;
    }

    @Override
    public ArrayList<Collectable> getContainedCollectables(ArrayList<Collectable> list) {
        ArrayList<Collectable> contained = new ArrayList<>();
        float dist;

        for (Collectable obj : list) {
            // Compute distance between the center of capture circle and obj circle
            dist = (float) Math.sqrt(Math.pow(x - obj.getX(), 2) + Math.pow(y - obj.getY(), 2));

            // Check if the two circles intersect
            if (obj.getRadius() + radius > dist)
                contained.add(obj);
        }
        return contained;
    }

    @Override
    public void draw(Canvas canvas, float canvas_width, float canvas_height, Paint p, Random rand) {
        random = rand;
        radius = scale * Math.min(canvas_width, canvas_height);
        canvas.drawCircle(x, y, radius, p);
    }
}
