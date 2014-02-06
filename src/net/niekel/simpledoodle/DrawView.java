package net.niekel.simpledoodle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View
{
    private static final String TAG = "DrawView";
	Paint paint = new Paint();
    Point point = new Point();
    
    private Canvas saved = new Canvas();
    
    private int stroke;
    private int color;
    private int background;
    private Bitmap bitmap;

    public DrawView(Context context)
    {
        super(context);
    }
    
    public DrawView(Context context, AttributeSet attrs) {
    	super(context, attrs);
    }
    
    public DrawView(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    }
    
    public void init(int stroke, int color, int bg_color) {
    	point.x = -100;
    	point.y = -100;
    	setStroke(stroke);
    	setColor(color);
    	setBGColor(bg_color);
    }

    public int getColor()
    {
        return color;
    }

    public void setColor(int c)
    {
        color = c;
        paint.setColor(c);
    }
    
    public int getBGColor()
    {
        return background;
    }

    public void setBGColor(int c)
    {
        background = c;
        setBackgroundColor(c);
    }
    
    public int getStroke() {
    	return stroke;
    }
    
    public void setStroke(int s) {
    	stroke = s;
    	paint.setStrokeWidth(stroke);
    }
    
    public Bitmap getBitmap() {
    	return bitmap;
    }
    
    public void setBitmap(Bitmap b) {
    	bitmap.eraseColor(Color.TRANSPARENT);
    	saved.drawBitmap(b, 0, 0, paint);
    	point.x = -100;
    	point.y = -100;
    	invalidate();
    }
    
    public void clear() {
    	bitmap.eraseColor(Color.TRANSPARENT);
    	point.x = -100;
    	point.y = -100;
    	invalidate();
    }

    @Override
    public void onDraw(Canvas canvas)
    {
    	super.onDraw(canvas);
    	
    	if (bitmap == null) {
    		Log.d(TAG, "onDraw(): bitmap == null => creating bitmap");
    		bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Config.ARGB_8888);
    		saved.setBitmap(bitmap);
    	}
    	    	
        saved.drawPoint(point.x, point.y, paint);
        
        canvas.drawBitmap(bitmap, 0, 0, null);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                point.x = (int) event.getX();
                point.y = (int) event.getY();
                invalidate();
                break;
            }
            case MotionEvent.ACTION_MOVE:
            {
                point.x = (int) event.getX();
                point.y = (int) event.getY();
                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                break;
            }
            default:
            	break;
        }
        return true;
    }
}