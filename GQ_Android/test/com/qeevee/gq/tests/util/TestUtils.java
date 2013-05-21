package com.qeevee.gq.tests.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import android.content.Intent;
import android.os.Bundle;

import com.qeevee.gq.history.History;
import com.qeevee.gq.history.HistoryItem;
import com.qeevee.gq.history.HistoryItemModifier;
import com.qeevee.gq.tests.ui.mock.MockUIFactory;
import com.xtremelabs.robolectric.Robolectric;

import edu.bonn.mobilegaming.geoquest.GameLoader;
import edu.bonn.mobilegaming.geoquest.GeoQuestActivity;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.GeoQuestMapActivity;
import edu.bonn.mobilegaming.geoquest.Mission;
import edu.bonn.mobilegaming.geoquest.Start;
import edu.bonn.mobilegaming.geoquest.mission.MissionActivity;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.UIFactory;

public class TestUtils {

	/**
	 * @param gameName
	 *            the name of the directory containing the game specification.
	 * @return the game spec as xml document
	 */
	public static Document loadTestGame(String gameName) {
		Document document = null;
		SAXReader reader = new SAXReader();
		try {
			File gameFile = getGameFile(gameName);
			document = reader.read(gameFile);
			GeoQuestApp.setRunningGameDir(new File(gameFile.getParent()));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return document;
	}

	/**
	 * @param gameName
	 *            the name of the directory containing the game specification.
	 * @return the File object representing the game spec xml file.
	 */
	public static File getGameFile(String gameName) {
		URL xmlFileURL = TestUtils.class.getResource("/testgames/" + gameName
				+ "/game.xml");
		if (xmlFileURL == null)
			fail("Resource file not found for game: " + gameName);
		return new File(xmlFileURL.getFile());
	}

	/**
	 * Prepares a mission activity which can then be started by calling its
	 * onCreate() method.
	 * 
	 * @param missionType
	 *            must be a valid mission type for which a class exists in the
	 *            mission implementation package.
	 * @param missionID
	 * @return a new Activity object of the according type for the given mission
	 *         type name. You can for example directly call onCreate() upon it
	 *         to emulate the android framework behavior.
	 * @throws ClassNotFoundException
	 */
	public static GeoQuestActivity prepareMission(String missionType,
			String missionID, Start start) {
		Class<?> missionClass = null;
		GeoQuestActivity missionActivity = null;

		try {
			missionClass = Class.forName(MissionActivity.getPackageBaseName()
					+ missionType);
			missionActivity = (GeoQuestActivity) missionClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		Intent startMissionIntent = new Intent(start, missionClass);
		startMissionIntent.putExtra("missionID", missionID);
		Robolectric.shadowOf(missionActivity).setIntent(startMissionIntent);

		return missionActivity;
	}

	// Does the same as prepareMission() but handles GeoQuestMapActivity instead
	// of GeoAuestActivity
	public static GeoQuestMapActivity prepareMapMission(String missionType,
			String missionID, Start start) {
		Class<?> missionClass = null;
		GeoQuestMapActivity missionActivity = null;

		try {
			missionClass = Class.forName(MissionActivity.getPackageBaseName()
					+ missionType);
			missionActivity = (GeoQuestMapActivity) missionClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		Intent startMissionIntent = new Intent(start, missionClass);
		startMissionIntent.putExtra("missionID", missionID);
		Robolectric.shadowOf(missionActivity).setIntent(startMissionIntent);

		return missionActivity;
	}

	/**
	 * Uses {@link MockUIFactory} and overrides settings in game xml
	 * specification.
	 * 
	 * @param gameFileName
	 * @param uistyles
	 *            optional; if given this {@link UIFactory} is used instead of
	 *            the {@link MockUIFactory}.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Start startGameForTest(String gameFileName,
			Class<? extends UIFactory>... uistyles) {
		Start start = startApp();
		Class<? extends UIFactory> uiFactory = (uistyles.length == 0) ? MockUIFactory.class
				: uistyles[0];
		GameLoader.startGame(null, TestUtils.getGameFile(gameFileName),
				uiFactory);
		return start;
	} 

	public static Start startApp() {
		Start start = new Start();
		GeoQuestApp app = (GeoQuestApp) start.getApplication();
		app.onCreate();
		Mission.setMainActivity(start);
		start.onCreate(null);
		callMethod(start, "onResume", new Class[] {}, new Object[] {});
		return start;
	}

	/**
	 * Lets you access the values of private or protected fields in your tests.
	 * You will have to cast the resulting object down to the real type.
	 * 
	 * @param obj
	 * @param fieldName
	 * @return
	 */
	public static Object getFieldValue(Object obj, String fieldName) {
		Object value = null;
		try {
			Field f = getNearestFieldInHierarchy(obj.getClass(), fieldName);
			f.setAccessible(true);
			value = f.get(obj);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			fail("Implementation of type \"" + obj.getClass().getSimpleName()
					+ "\" misses a field named \"" + fieldName + "\"");
		}
		return value;
	}

	@SuppressWarnings("rawtypes")
	private static Field getNearestFieldInHierarchy(Class clazz,
			String fieldName) throws NoSuchFieldException {
		Field foundField = null;
		try {
			foundField = clazz.getDeclaredField(fieldName);
			return foundField;
		} catch (NoSuchFieldException nsfe) {
			Class superClass = clazz.getSuperclass();
			if (superClass == null)
				throw nsfe;
			else
				return getNearestFieldInHierarchy(superClass, fieldName);

		}
	}

	/**
	 * @param obj
	 * @param methodName
	 * @param parameterTypes
	 *            can be null if no arguments given
	 * @param arguments
	 *            can be null if no arguments given
	 * @return
	 */
	public static Object callMethod(Object obj, String methodName,
			Class<?>[] parameterTypes, Object[] arguments) {
		Object returnValue = null;
		try {
			Method m = obj.getClass().getDeclaredMethod(methodName,
					parameterTypes);
			m.setAccessible(true);
			returnValue = m.invoke(obj, arguments);
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			StringBuffer signature = new StringBuffer();
			signature.append(methodName + "(");
			for (int i = 0; i < parameterTypes.length; i++) {
				if (i > 0)
					signature.append(", ");
				signature.append(parameterTypes[i].getName());
			}
			signature.append(")");
			fail("Implementation of type \"" + obj.getClass().getSimpleName()
					+ "\" misses a method \"" + signature + "\"");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return returnValue;
	}

	public static Object getStaticFieldValue(
			@SuppressWarnings("rawtypes") Class clazz, String fieldName) {
		Object value = null;
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			value = field.get(null);
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			fail("Implementation of type \"" + clazz.getSimpleName()
					+ "\" misses a field named \"" + fieldName + "\"");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return value;
	}

	public static String getResString(int id) {
		return GeoQuestApp.getContext().getResources().getString(id);
	}

	/**
	 * Checks that the last n items in the history are correctly characterized
	 * by the given item class and modifiers.
	 * 
	 * @param n
	 * @param expectedItemClass
	 * @param expectedItemModifier
	 */
	public static void nthLastItemInHistoryShouldBe(int n,
			Class<? extends HistoryItem> expectedItemClass,
			HistoryItemModifier... expectedItemModifier) {
		HistoryItem lastItem = History.getInstance().getNthLastItem(n);
		assertEquals(expectedItemClass, lastItem.getClass());
		for (int i = 0; i < expectedItemModifier.length; i++) {
			assertEquals(expectedItemModifier[i],
					lastItem.getModifier(expectedItemModifier[i].getClass()));
		}
	}

	public static void historyListShouldHaveLength(int i) {
		assertEquals(i, History.getInstance().numberOfItems());
	}

	public static GeoQuestActivity startMissionInGame(String game,
			String missionType, String missionID,
			Class<? extends UIFactory>... uiFactoryClass) {
		Start start = TestUtils.startGameForTest(game, uiFactoryClass);
		GeoQuestActivity mission = TestUtils.prepareMission(missionType,
				missionID, start);
		TestUtils.callMethod(mission, "onCreate",
				new Class<?>[] { Bundle.class }, new Object[] { null });
		return mission;
	}

	public static GeoQuestMapActivity startMapMissionInGame(String game,
			String missionType, String missionID,
			Class<? extends UIFactory>... uiFactoryClass) {
		Start start = TestUtils.startGameForTest(game, uiFactoryClass);
		GeoQuestMapActivity mission = TestUtils.prepareMapMission(missionType,
				missionID, start);
		TestUtils.callMethod(mission, "onCreate",
				new Class<?>[] { Bundle.class }, new Object[] { null });
		return mission;
	}

	public static void setMockUIFactory() {
		try {
			Field field = UIFactory.class.getDeclaredField("instance");
			field.setAccessible(true);
			field.set(null, new MockUIFactory());
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			fail("Implementation of type \"" + UIFactory.class.getSimpleName()
					+ "\" misses a field named \"" + "instance" + "\"");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
