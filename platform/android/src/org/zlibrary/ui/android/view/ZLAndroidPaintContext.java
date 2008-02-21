package org.zlibrary.ui.android.view;

import java.util.HashMap;

import android.graphics.*;

import org.zlibrary.core.image.ZLImageData;
import org.zlibrary.core.util.ZLColor;
import org.zlibrary.core.view.ZLPaintContext;

import org.zlibrary.ui.android.image.ZLAndroidImageData;

public final class ZLAndroidPaintContext extends ZLPaintContext {
	private Canvas myCanvas;
	private final Paint myPaint;
	private ZLColor myColor = new ZLColor(0, 0, 0);

	private int myWidth;
	private int myHeight;

	private HashMap<String,Typeface[]> myTypefaces = new HashMap<String,Typeface[]>();

	ZLAndroidPaintContext() {
		myPaint = new Paint();
		myPaint.setLinearText(true);
		myPaint.setAntiAlias(true);
	}

	void setSize(int w, int h) {
		myWidth = w;
		myHeight = h;
	}

	void beginPaint(Canvas canvas) {
		myCanvas = canvas;
		resetFont();
	}

	void endPaint() {
		myCanvas = null;
	}

	public void clear(ZLColor color) {
		// TODO: implement
		myColor = color;
		myPaint.setColor(Color.rgb(color.Red, color.Green, color.Blue));
		myCanvas.drawRect(0, 0, myWidth, myHeight, myPaint);
	}

	protected void setFontInternal(String family, int size, boolean bold, boolean italic) {
		final int style = (bold ? Typeface.BOLD : 0) | (italic ? Typeface.ITALIC : 0);
		final Paint paint = myPaint;
		Typeface[] typefaces = myTypefaces.get(family);
		if (typefaces == null) {
			typefaces = new Typeface[4];
			myTypefaces.put(family, typefaces);
		}
		Typeface typeface = typefaces[style];
		if (typeface == null) {
			typeface = Typeface.create(family, style);
			typefaces[style] = typeface;
		}
		paint.setTypeface(typeface);
		paint.setTextSize(size);
	}

	public void setColor(ZLColor color, int style) {
		// TODO: use style
		if (!myColor.equals(color)) {
			myColor = color;
			myPaint.setColor(Color.rgb(color.Red, color.Green, color.Blue));
		}
	}

	public void setFillColor(ZLColor color, int style) {
		// TODO: implement
	}

	public int getWidth() {
		return myWidth;
	}
	public int getHeight() {
		return myHeight;
	}
	
	public int getStringWidth(char[] string, int offset, int length) {
		return (int)(myPaint.measureText(string, offset, length) + 0.5f);
	}
	protected int getSpaceWidthInternal() {
		return (int)(myPaint.measureText(" ", 0, 1) + 0.5f);
	}
	protected int getStringHeightInternal() {
		return (int)(myPaint.getTextSize() + 0.5f);
	}
	protected int getDescentInternal() {
		return (int)(myPaint.descent() + 0.5f);
	}
	public void drawString(int x, int y, char[] string, int offset, int length) {
		myCanvas.drawText(string, offset, length, x, y, myPaint);
	}

	public int imageWidth(ZLImageData imageData) {
		Bitmap bitmap = ((ZLAndroidImageData)imageData).getBitmap();
		return (bitmap != null) ? bitmap.width() : 0;
	}

	public int imageHeight(ZLImageData imageData) {
		Bitmap bitmap = ((ZLAndroidImageData)imageData).getBitmap();
		return (bitmap != null) ? bitmap.height() : 0;
	}

	public void drawImage(int x, int y, ZLImageData imageData) {
		Bitmap bitmap = ((ZLAndroidImageData)imageData).getBitmap();
		if (bitmap != null) {
			myCanvas.drawBitmap(bitmap, x, y - bitmap.height(), myPaint);
		}
	}

	public void drawLine(int x0, int y0, int x1, int y1) {
		final Paint paint = myPaint;
		final Canvas canvas = myCanvas;
		paint.setAntiAlias(false);
		canvas.drawLine(x0, y0, x1, y1, paint);
		canvas.drawPoint(x0, y0, paint);
		canvas.drawPoint(x1, y1, paint);
		paint.setAntiAlias(true);
	}

	public void fillRectangle(int x0, int y0, int x1, int y1) {
		// TODO: implement
	}
	public void drawFilledCircle(int x, int y, int r) {
		// TODO: implement
	}

/*	public List<String> fontFamilies() {
		if (myFamilies.isEmpty()) {
			fillFamiliesList(myFamilies);
		}
		return myFamilies;
	}
	
	public String realFontFamilyName(String fontFamily);
	protected void fillFamiliesList(List<String> families);
*/
}