package bap.texas.util;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;

public class SizingGallery extends Gallery {
	public SizingGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected boolean getChildStaticTransformation(View child, Transformation t) {
        t.clear();
        View selectedChild = getSelectedView();
        if (child == selectedChild) {
        	Matrix matrix = t.getMatrix();
        	int w2 = child.getWidth() / 4;
        	int h2 = child.getHeight() / 4;
        	matrix.postScale(1.5f, 1.5f, w2, h2);
        }
        return true;
	}
}
