package edu.sdsmt.group2.Model;

import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.ArrayList;
import java.util.Random;

public class LineCapture extends CaptureObject {
    private float angle = 0;

    private float squareDistance(float x1, float y1, float x2, float y2) {
        return (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
    }

    @Override
    public ArrayList<Collectable> getContainedCollectables(ArrayList<Collectable> list) {
        ArrayList<Collectable> contained = new ArrayList<>();

        // Kinda janky. Checking all four corners and farthest midpoints
        // *------------------------------------------------------------*
        // |                                                            |
        // *                                                            *
        // |                                                            |
        // *------------------------------------------------------------*
        // * <- these guys
        double a = angle * Math.PI / 180;
        float dx = width * (float)Math.cos(a) / 2;
        float dy = width * (float)Math.sin(a) / 2;
        float rx = height * (float)Math.sin(a) / 2;
        float ry = height * (float)Math.cos(a) / 2;
        float[] cs = {
                x - dx, y - dy ,
                x + dx, y + dy,
                x - dx - rx, y - dy + ry,
                x - dx + rx, y - dy - ry,
                x + dx - rx, y + dy + ry,
                x + dx + rx, y + dy - ry,
        };

        for (Collectable obj : list) {
            if (random.nextFloat() <= 0.75) {
                float ox = obj.getX();
                float oy = obj.getY();
                dx = ox - x;
                dy = oy - y;
                // perpendicular distance from point to line
                float dist = (float) Math.abs(dy * Math.cos(a) - dx * Math.sin(a));

                if (dist <= obj.getRadius() + height / 2) {
                    dist = (float) Math.sqrt(dx * dx + dy * dy - dist * dist);

                    if (dist <= width / 2) {
                        contained.add(obj);
                    } else if (dist <= width / 2 + obj.getRadius()) {
                        float r2 = obj.getRadius() * obj.getRadius();

                        for (int i = 0; i < 6; i++) {
                            if (squareDistance(cs[2 * i], cs[2 * i + 1], ox, oy) <= r2) {
                                contained.add(obj);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return contained;
    }

    @Override
    public void draw(Canvas canvas, float canvas_width, float canvas_height, Paint p, Random rand) {
        random = rand;
        width = 0.8f * Math.min(canvas_width, canvas_height);
        height = 0.1f * Math.min(canvas_width, canvas_height);
        p.setStrokeWidth(0.1f * canvas.getWidth());
        double a = angle * Math.PI / 180;
        float dx = width * (float)Math.cos(a) / 2;
        float dy = width * (float)Math.sin(a) / 2;
        canvas.drawLine(x - dx,y - dy,x + dx,y + dy, p);
    }

    @Override
    public float getAngle() { return angle; }

    @Override
    public void setAngle(float angle) { this.angle = angle; }
}
