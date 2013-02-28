package com.qeevee.gq.tests.hotspots;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.dom4j.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.rules.Rule;
import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.util.TestUtils;
import com.qeevee.gq.xml.GQML_DEFAULTS;

import edu.bonn.mobilegaming.geoquest.Hotspot;
import edu.bonn.mobilegaming.geoquest.HotspotManager;
import edu.bonn.mobilegaming.geoquest.Start;

@RunWith(GQTestRunner.class)
public class HotspotTests {
    Document doc;
    HotspotManager hm;
    Start start;
    final double EPSILON = 0.000001d;

    @Before
    public void setUp() throws Exception {
	start = new Start();
	doc = TestUtils.loadTestGame("Spielmarkt_2012");
	hm = HotspotManager.init(doc);
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Rule> getRules(Hotspot hotspotObject,
				     String ruleKind) {
	Field field;
	ArrayList<Rule> rules = null;
	try {
	    field = Hotspot.class.getDeclaredField(ruleKind + "Rules");
	    field.setAccessible(true);
	    rules = (ArrayList<Rule>) field.get(hotspotObject);
	} catch (SecurityException e) {
	    e.printStackTrace();
	    fail(e.getMessage());
	} catch (NoSuchFieldException e) {
	    e.printStackTrace();
	    fail(e.getMessage());
	} catch (IllegalArgumentException e) {
	    e.printStackTrace();
	    fail(e.getMessage());
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	    fail(e.getMessage());
	}
	return rules;
    }

    @Test
    public void testHotspotWithExplicitAttributes() {
	assertEquals(8,
		     hm.getNumberOfHotspots());
	Hotspot h = hm.getHotspot("HS_Station_2");

	assertNotNull(h);
	assertEquals("HS_Station_2",
		     h.id);
	// latitude="51.155867" longitude="7.141565"
	assertEquals(Double.parseDouble("51.159399"),
		     h.location.getLatitude(),
		     EPSILON);
	assertEquals(Double.parseDouble("7.134433"),
		     h.location.getLongitude(),
		     EPSILON);
	assertEquals(15,
		     h.getRadius());
	assertEquals(false,
		     h.getVisibility());
	assertEquals(true,
		     h.getActivation());
	assertNotNull(h.getIconBitmap());
    }

    @Test
    public void testHotspotDefaultAttributes() {
	assertEquals(8,
		     hm.getNumberOfHotspots());
	Hotspot h = hm.getHotspot("HS_Station_3");

	assertNotNull(h);
	assertEquals("HS_Station_3",
		     h.id);
	assertEquals(GQML_DEFAULTS.HOTSPOT_RADIUS,
		     h.getRadius());
	assertEquals(GQML_DEFAULTS.HOTSPOT_INITIAL_VISIBILITY,
		     h.getVisibility());
	assertEquals(GQML_DEFAULTS.HOTSPOT_INITIAL_ACTIVATION,
		     h.getActivation());
	assertNotNull(h.getIconBitmap());
    }

    @Test
    public void testHotspotRules() {
	Hotspot h = hm.getHotspot("HS_Station_1");

	// This hotspot has one rule onEnter declared which should be read into
	// the runtime Hotspot now:
	ArrayList<Rule> onEnterRules = getRules(h,
						"onEnter");
	assertNotNull(onEnterRules);
	assertEquals(1,
		     onEnterRules.size());

	// This hotspot has no onLeave rules declared but the list should be
	// initialized:
	ArrayList<Rule> onLeaveRules = getRules(h,
						"onLeave");
	assertNotNull(onLeaveRules);
	assertEquals(0,
		     onLeaveRules.size());

	h = hm.getHotspot("HS_Final");
	// This hotspot has two rules onEnter declared which should be read into
	// the runtime Hotspot now:
	onEnterRules = getRules(h,
				"onEnter");
	assertNotNull(onEnterRules);
	assertEquals(2,
		     onEnterRules.size());
    }

}
