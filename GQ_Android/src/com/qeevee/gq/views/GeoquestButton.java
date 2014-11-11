package com.qeevee.gq.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;
import com.qeevee.gqdefault.R;

public class GeoquestButton extends Button {

    public GeoquestButton(Context context) {
	super(context);
	init();
    }

    public GeoquestButton(Context context,
			  AttributeSet attrs) {
	super(context, attrs);
	init();
    }

    public GeoquestButton(Context context,
			  AttributeSet attrs,
			  int defStyle) {
	super(context, attrs, defStyle);
	init();
    }

    private void init() {
	this.setBackgroundDrawable(getResources()
		.getDrawable(R.drawable.geoquest_button));
	this.setTextColor(Color.parseColor("#f0f0f2"));
    }

    @Override
    public void setEnabled(boolean enabled) {
	if (enabled) {
	    this.getBackground().clearColorFilter();
	    Drawable[] drawables = this.getCompoundDrawables();
	    for (Drawable draw : drawables) {
		if (draw != null) {
		    draw.setColorFilter(null);
		}
	    }
	} else {
	    this.getBackground().setColorFilter(Color.GRAY,
						PorterDuff.Mode.MULTIPLY);
	    Drawable[] drawables = this.getCompoundDrawables();
	    for (Drawable draw : drawables) {
		if (draw != null) {
		    draw.setColorFilter(Color.GRAY,
					PorterDuff.Mode.MULTIPLY);
		}
	    }
	}
	super.setEnabled(enabled);
    }

}
