package edu.sdsmt.group4.Model;

import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.ArrayList;
import java.util.Random;

public class RectangleCapture extends CaptureObject {
    public RectangleCapture() {
        this.scale = 0.5f;
    }

    private boolean intersects(Collectable obj) {
        float objX = obj.getX();
        float objY = obj.getY();
        float objRadius = obj.getRadius();
        float dx = objX - Math.max(x - width/2, Math.min(objX, x + width/2));
        float dy = objY - Math.max(y - height/2, Math.min(objY, y + height/2));

        return (dx * dx + dy * dy) < (objRadius * objRadius);
    }

    @Override
    public ArrayList<Collectable> getContainedCollectables(ArrayList<Collectable> list) {
        ArrayList<Collectable> contained = new ArrayList<>();

        for (Collectable obj : list)
            if (intersects(obj) && random.nextFloat() <= 0.5 * 0.5f / scale)
                contained.add(obj);

        return contained;
    }

    @Override
    public void draw(Canvas canvas, float canvas_width, float canvas_height, Paint p, Random rand) {
        random = rand;
        this.width = scale * Math.min(canvas_width, canvas_height);
        this.height = scale * Math.max(canvas_width, canvas_height);

        // Draw a square on the screen
        canvas.drawRect(x - width/2,y - height/2,x + width/2,y + height/2, p);
    }
}
