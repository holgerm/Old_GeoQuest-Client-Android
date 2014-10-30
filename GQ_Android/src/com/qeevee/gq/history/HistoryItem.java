package com.qeevee.gq.history;

import java.util.HashMap;
import java.util.Map;

import com.qeevee.gq.GeoQuestActivity;

import android.view.View;

/**
 * @author muegge
 * 
 */
public abstract class HistoryItem {

    static public Map<Class<? extends HistoryItemModifier>, HistoryItemModifier> defaultModifiers = new HashMap<Class<? extends HistoryItemModifier>, HistoryItemModifier>();

    static {
	defaultModifiers.put(Actor.class,
			     Actor.DEFAULT);
    }

    private long id;

    protected Map<Class<? extends HistoryItemModifier>, HistoryItemModifier> modifiers;

    protected Class<? extends GeoQuestActivity> activityType;

    /**
     * Main constructor which should directly or indirectly be called by every
     * other constructor of this class.
     * 
     * Each HistoryItem adds itself to the history list.
     */
    public HistoryItem(GeoQuestActivity activity,
		       HistoryItemModifier... modifier) {
	activityType = activity.getClass();
	modifiers = new HashMap<Class<? extends HistoryItemModifier>, HistoryItemModifier>();
	for (int i = 0; i < modifier.length; i++) {
	    if (!modifiers.containsKey(modifier[i].getClass()))
		modifiers.put(modifier[i].getClass(),
			      modifier[i]);
	}
	History.getInstance().add(this);
    }

    public long getId() {
	return id;
    }

    void setId(long id) {
	this.id = id;
    }

    Class<? extends GeoQuestActivity> getActivityType() {
	return activityType;
    }

    public abstract View getView(View convertView);

    /**
     * Each history item can hold modifiers that describe some semantic aspects.
     * For example a TextItem might specify what kind of text it represents with
     * one of the possible modifiers specified in {@link TextType}.
     * 
     * To get an idea of what modifiers exist look at
     * {@link HistoryItemModifier} and its subtypes.
     * 
     * @param modifierClass
     *            e.g. Actor or TextType etc.
     * @return the value for the given sort of modifiers that holds for this
     *         history item.
     */
    public HistoryItemModifier
	    getModifier(Class<? extends HistoryItemModifier> modifierClass) {
	if (modifiers.containsKey(modifierClass))
	    return modifiers.get(modifierClass);
	else
	    return defaultModifiers.get(modifierClass);
    }
}
