package com.qeevee.gq.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.qeevee.gq.GeoQuestActivity;
import com.qeevee.gq.GeoQuestApp;
import com.qeevee.gq.R;

/**
 * @author muegge
 * 
 */
public class TextItem extends HistoryItem {

    static {
	defaultModifiers.put(TextType.class,
			     TextType.DEFAULT);
    }

    private CharSequence text;

    /**
     * Main constructor which should directly or indirectly be called by every
     * other constructor of this class.
     * 
     * This constructor should be the only one who calls the super constructor.
     * 
     * @param modifier
     *            as many modifiers as you like, but be aware that only one of
     *            each type will be used. If you do not know what to do just
     *            leave empty: then defaults of each type of modifiers will be
     *            used.
     */
    public TextItem(CharSequence text,
		    GeoQuestActivity activity,
		    HistoryItemModifier... modifier) {
	super(activity, modifier);
	this.text = text;
    }

    public View getView(View convertView) {
	TextView tv;
	if (convertView == null) {
	    LayoutInflater inflater = (LayoutInflater) GeoQuestApp.getContext()
		    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    tv = (TextView) inflater.inflate(R.layout.history_text_item,
					     null);
	} else {
	    tv = (TextView) convertView;
	}
	tv.setText(text);
	return tv;
    }

}
