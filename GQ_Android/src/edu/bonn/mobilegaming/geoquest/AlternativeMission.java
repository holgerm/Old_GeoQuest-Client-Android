/**
 * Implementation for the diploma thesis "Adaption in digitalen mobilen Lernspielen - Anwendung in GeoQuest"
 * 
 * @author Sabine Polko
 */
package edu.bonn.mobilegaming.geoquest;

import org.dom4j.Document;
import org.dom4j.Element;

import android.os.Handler;
import android.util.Log;

public class AlternativeMission extends Mission {

    private static final long serialVersionUID = 1L;

    private static final String TAG = "AlternativeMission";

    private static Document missionPoolDoc;

    private String placeholderId;

    private AlternativeMission(String missionId, Mission parent, Element missionNode, Handler loadHandler) {
	super(missionId);
    }

    public static void setMissionPoolDocument(Document doc) {
	missionPoolDoc = doc;
    }

    public static AlternativeMission create(String id, Mission parent, Element missionNode, Handler loadHandler) {
	AlternativeMission m;
	if (Mission.existsMission(id)) {
	    Mission existing = get(id);
	    if (existing instanceof AlternativeMission) {
		m = (AlternativeMission) existing;
	    } else {
		throw new ClassCastException("Mission " + id + " allready exists, but has a wrong type.");
	    }
	} else {
	    m = new AlternativeMission(id, parent, missionNode, loadHandler);
	    Mission.append(m);
	}
	initMission(m, parent, missionNode, loadHandler);
	return m;
    }

    public static AlternativeMission create(String id, String placeholderId) {
	Log.d(TAG, "Loading AlternativeMission " + id + " for PlaceholderMission " + placeholderId);
	AlternativeMission m;
	Long start = System.currentTimeMillis();
	Element missionNode = (Element) missionPoolDoc.selectSingleNode("//mission[@id='" + id + "']");
	if (Mission.existsMission(id)) {
	    Mission existing = get(id);
	    if (existing instanceof AlternativeMission) {
		m = (AlternativeMission) existing;
	    } else {
		throw new ClassCastException("Mission " + id + " allready exists, but has a wrong type.");
	    }
	} else {
	    m = new AlternativeMission(id, null, missionNode, null); 
	    Mission.append(m);
	}
	m.placeholderId = placeholderId;
	initMission(m, null, missionNode, null);
	Log.d("RuntimeMeasure", "Mission " + id + " - " + (System.currentTimeMillis() - start) + " ms");
	return m;
    }

    public void setPlaceholderId(String missionId) {
	placeholderId = missionId;
    }

    public String getPlaceholderId() {
	return placeholderId;
    }

    @Override
    public void applyOnStartRules() {
	Mission.get(placeholderId).applyOnStartRules();
    }

    @Override
    public void applyOnEndRules() {
	Mission.get(placeholderId).applyOnEndRules();
    }

    @Override
    public void setStatus(Double status) {
	if (placeholderId != null) {
	    Mission.get(placeholderId).setStatus(status);
	}
    }
}
