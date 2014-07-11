package com.qeevee.gq.tests.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Scanner;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import android.content.Intent;
import android.os.Bundle;

import com.qeevee.gq.GameLoader;
import com.qeevee.gq.GeoQuestActivity;
import com.qeevee.gq.GeoQuestApp;
import com.qeevee.gq.GeoQuestMapActivity;
import com.qeevee.gq.Mission;
import com.qeevee.gq.history.History;
import com.qeevee.gq.history.HistoryItem;
import com.qeevee.gq.history.HistoryItemModifier;
import com.qeevee.gq.mission.MissionActivity;
import com.qeevee.gq.start.LandingScreen;
import com.qeevee.gq.tests.ui.mock.MockUIFactory;
import com.qeevee.gq.tests.ui.mock.UseGameSpecUIFactory;
import com.qeevee.gq.ui.UIFactory;
import com.xtremelabs.robolectric.Robolectric;

public class TestUtils {

	public static final float DELTA_4_FLOAT_COMPARISON = 0.0001f;

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

	public static File getFile(String path) {
		URL fileURL = TestUtils.class.getResource(path);
		if (fileURL == null) {
			fail("Test failed. File not found: " + path);
			return null;
		} else
			return new File(fileURL.getFile());
	}

	/**
	 * @param fileName
	 *            the relative path to the file containing the text.
	 * @return the text as String.
	 */
	public static String getRessourceFileContent(String fileName) {
		URL fileURL = TestUtils.class.getResource(fileName);
		if (fileURL == null)
			fail("Resource file not found: " + fileName);
		File f = new File(fileURL.getFile());
		if (!f.exists()) 
			fail("File " + fileName + " not found.");
		if (!f.canRead())
			fail("File " + fileName + " cann not be read.");
		try {
			return readFile(fileName);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Problems reading fomr file " + fileName);
		}
		return null;
	}

	public static String readFile(String pathname) throws IOException {
		URL fileURL = TestUtils.class.getResource(pathname);
		if (fileURL == null)
			fail("Resource file not found: " + pathname);
		File file = new File(fileURL.getFile());
		StringBuilder fileContents = new StringBuilder((int) file.length());
		Scanner scanner = new Scanner(file);
		String lineSeparator = System.getProperty("line.separator");

		try {
			while (scanner.hasNextLine()) {
				fileContents.append(scanner.nextLine() + lineSeparator);
			}
			return fileContents.toString();
		} finally {
			scanner.close();
		}
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
			String missionID, LandingScreen start) {
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
			String missionID, LandingScreen start) {
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
	 *            offers three options:
	 *            <ol>
	 *            <li>Use the game specific ui style as given in the attribute
	 *            "uistyle". If you want this, you must specify
	 *            {@link UseGameSpecUIFactory} as class parameter here.</li>
	 *            <li>Use a specific UI for your test independent from (and
	 *            overwriting) the UI style specified in the game. If you want
	 *            this, you must give the class object of you favorite
	 *            {@link UIFactory}</li>
	 *            <li>Use the standard UI factory for test which is the
	 *            {@link MockUIFactory}. If you want this, you can simply omit
	 *            this parameter. Thus this is the default behavior.</li>
	 *            </ol>
	 *            optional; if given this {@link UIFactory} is used instead of
	 *            the {@link MockUIFactory}.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static LandingScreen startGameForTest(String gameFileName,
			Class<? extends UIFactory>... uistyles) {
		LandingScreen start = startApp();
		if (uistyles.length == 0)
			GameLoader.startGame(null, TestUtils.getGameFile(gameFileName),
					MockUIFactory.class);
		else if (uistyles[0] == UseGameSpecUIFactory.class) {
			GameLoader.startGame(null, TestUtils.getGameFile(gameFileName));
		} else
			GameLoader.startGame(null, TestUtils.getGameFile(gameFileName),
					uistyles[0]);
		return start;
	}

	public static LandingScreen startApp() {
		LandingScreen start = new LandingScreen();
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
		LandingScreen start = TestUtils.startGameForTest(game, uiFactoryClass);
		GeoQuestActivity mission = TestUtils.prepareMission(missionType,
				missionID, start);
		TestUtils.callMethod(mission, "onCreate",
				new Class<?>[] { Bundle.class }, new Object[] { null });
		return mission;
	}

	public static GeoQuestMapActivity startMapMissionInGame(String game,
			String missionType, String missionID,
			Class<? extends UIFactory>... uiFactoryClass) {
		LandingScreen start = TestUtils.startGameForTest(game, uiFactoryClass);
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
