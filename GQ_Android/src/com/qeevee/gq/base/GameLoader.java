package com.qeevee.gq.base;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import com.qeevee.gqdefault.R;
import com.qeevee.gqdefault.R.string;
import com.qeevee.gq.loc.Hotspot;
import com.qeevee.gq.ui.UIFactory;

import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import edu.bonn.mobilegaming.geoquest.contextmanager.xmlTagsContext;
import edu.bonn.mobilegaming.geoquest.gameaccess.GameDataManager;

public class GameLoader {

	private static final String ASSET_DIR_FOR_INCLUDED_QUESTS = "included";

	static final String TAG = "GameLoader";

	/**
	 * Points to the currently selected game the user is playing.
	 */
	public static String zipfile;
	public static ExecutorService executor = Executors.newCachedThreadPool();

	/**
	 * Unzips all files from the locally stored archive ({@code newGameZipFile}
	 * and stores it in a new directory named after the zip archive name at the
	 * same place.
	 * 
	 * @param gameZipFile
	 * @return the directory where the game files have been stored
	 */
	static void unzipGameArchive(File gameZipFile) {
		String newGameDirName = gameZipFile.getAbsolutePath().replace(".zip",
				"");
		File newGameDir = new File(newGameDirName);
		if (!newGameDir.exists() || newGameDir.isFile()) {
			if (newGameDir.isFile())
				// just in the awkward case that there is a file with the same
				// name ...
				newGameDir.delete();
			newGameDir.mkdir();
		} else {
			// clean directory:
			deleteDir(newGameDir);
		}

		try {
			ZipFile zipFile = new ZipFile(gameZipFile);
			ZipEntry zipEntry;
			File entryFile;
			FileOutputStream fos;
			InputStream entryStream;

			for (Enumeration<? extends ZipEntry> enumeration = zipFile
					.entries(); enumeration.hasMoreElements();) {
				zipEntry = enumeration.nextElement();

				// skip files starting with ".":
				String zipEntryName = zipEntry.getName();
				String[] zipEntryNameParts = zipEntryName.split("/");
				if (zipEntryNameParts[zipEntryNameParts.length - 1]
						.startsWith("."))
					continue;

				entryFile = new File(newGameDirName + "/" + zipEntry.getName());

				// in case the entry is a directory:
				if (zipEntryName.endsWith("/")) {
					if (!entryFile.exists() || !entryFile.isDirectory())
						entryFile.mkdir();
					continue; // now it exists that's enough for directories ...
				}

				File parentDir = getGameDirectory(entryFile);
				if (!parentDir.exists()) {
					parentDir.mkdir();
				}

				fos = new FileOutputStream(entryFile);
				entryStream = zipFile.getInputStream(zipEntry);
				byte content[] = new byte[1024];
				int bytesRead;

				do {
					bytesRead = entryStream.read(content);
					if (bytesRead > 0)
						fos.write(content, 0, bytesRead);
				} while (bytesRead > 0);

				fos.flush();
				fos.close();

				// set timestamp of new loaded gamefile to serverside timestamp:
				if (entryFile.getName().equals("game.xml")) {
					boolean timeStampOK = entryFile.setLastModified(gameZipFile
							.lastModified());
					if (!timeStampOK)
						Log.e(TAG, "Time stamp of game file for \""
								+ gameZipFile.getName()
								+ "\" could not be set.");
				}
			}
		} catch (ZipException e) {
			Log.d(TAG, "ZipException creating zipfile from " + gameZipFile);
			e.printStackTrace();
		} catch (IOException e) {
			Log.d(TAG, "IOException creating zipfile from " + gameZipFile);
			e.printStackTrace();
		}
	}

	/**
	 * @param dir
	 * @return {@code true} if the give directory an all contained files or
	 *         subdirectories have been deleted. Otherwise {@code false}.
	 */
	private static boolean deleteDir(File dir) {
		if (!dir.exists() || !dir.isDirectory())
			return false;
		boolean deleted = true;
		File[] filesToDelete = dir.listFiles();
		for (int i = 0; i < filesToDelete.length; i++) {
			if (filesToDelete[i].isDirectory())
				deleted &= deleteDir(filesToDelete[i]);
			else
				deleted &= filesToDelete[i].delete();
		}
		return deleted;
	}

	public static String loadTextFile(File gameDir, String relativeResourcePath) {
		String resourcePath = gameDir.getAbsolutePath() + "/"
				+ relativeResourcePath;
		Log.d(TAG + ".loadTextFile()", "Loading text file from " + resourcePath);
		StringBuffer result = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(
					resourcePath)));
			String line;
			while ((line = br.readLine()) != null) {
				result.append(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			Log.w(TAG + ".loadTextFile()", e.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	final static int BYTE_SIZE = 1024;

	private static FileOutputStream createFileWriter(File newGameZipFile) {
		FileOutputStream fOutLocal = null;
		try {
			fOutLocal = new FileOutputStream(newGameZipFile);
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.toString());
		}
		return fOutLocal;
	}

	/**
	 * Starts the game from the given local file.
	 * 
	 * TODO: move to GameStarter
	 * 
	 * @param handler
	 * @param gameXMLFile
	 * @param predefinedUIFactories
	 *            if not empty, the first string in array is taken as name of
	 *            UIFactory and overrides settings in game xml specification.
	 */
	public static void startGame(Handler handler, File gameXMLFile,
			Class<? extends UIFactory>... predefinedUIFactories) {
		endLastGame();
		GeoQuestApp.resetAdaptionEngine();
		resetContextManager();
		// expand missions
		try {
			Mission.documentRoot = getDocument(gameXMLFile).getRootElement();
			setGlobalMissionLayout(predefinedUIFactories);
			makeImprint(getDocument(gameXMLFile).getRootElement());
			setGameDuration();

			sendMsgExpandingGame(handler);

			GeoQuestApp.setRunningGameDir(getGameDirectory(gameXMLFile));
			// Only from now on we can access game ressources.

			Mission firstMission = createMissions(handler);
			createHotspots(Mission.documentRoot);

			GeoQuestApp.setImprint(new Imprint(Mission.documentRoot
					.element("imprint")));

			if (handler != null)
				handler.sendEmptyMessage(GeoQuestProgressHandler.MSG_FINISHED);

			if (firstMission != null) {
				// TODO ReportingService muss jetzt schon gestartet sein!
				GameSessionManager.setSessionID(Mission.documentRoot
						.attributeValue("name"));
				firstMission.startMission();
			}

			// readXML(Mission.documentRoot);

		} catch (Exception e) {
			Log.e(TAG, "DocumentException while parsing game: " + gameXMLFile);
			if (handler != null) {
				Message msg = handler.obtainMessage();
				msg.what = GeoQuestProgressHandler.MSG_ABORT_BY_ERROR;
				msg.arg1 = R.string.start_gameFileCouldNotBeParsed;
				e.printStackTrace();
				handler.sendMessage(msg);
			}
			return;
		}
	}

	/**
	 * Gets the Hotspots data from the XML file.
	 * 
	 * TODO move into a new HotspotFactory class.
	 * 
	 * TODO Copied from StartLocalGame class. Fix this!
	 */
	@SuppressWarnings("unchecked")
	static private void createHotspots(Element document)
			throws DocumentException {
		List<Element> list = document.selectNodes("//hotspot");

		for (Iterator<Element> i = list.iterator(); i.hasNext();) {
			Element hotspot = i.next();
			try {
				new Hotspot(hotspot);
			} catch (Hotspot.IllegalHotspotNodeException exception) {
				Log.e(TAG, exception.toString());
			}
		}
	}

	private static void makeImprint(Element root) {
		String imprintMissionID = root.attributeValue("imprint");
		if (imprintMissionID == null) {
			// TODO ... make standard GQ imprint 
		} else {
			// TODO make specific imprint
		}
	}

	private static File getGameDirectory(File gameXMLFile) {
		return gameXMLFile.getParentFile();
	}

	private static void resetContextManager() {
		if (GeoQuestActivity.contextManager != null) {
			GeoQuestActivity.contextManager.resetHistory();
		}
	}

	private static void setGameDuration() {
		String maxDurationStr = Mission.documentRoot
				.attributeValue(xmlTagsContext.MAX_DURATION.getString());
		if (maxDurationStr != null) {
			GeoQuestActivity.contextManager.setMaximalGameDuration(Long
					.parseLong(maxDurationStr) * 60 * 1000);
		}
	}

	/**
	 * TODO: perhaps move to GameStarter (?)
	 * 
	 * @param gameXMLFile
	 * @return
	 * @throws DocumentException
	 */
	private static Document getDocument(File gameXMLFile)
			throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(gameXMLFile);
		return document;
	}

	/**
	 * TODO move to GameStarter
	 * 
	 * @param handler
	 * @return
	 */
	private static Mission createMissions(Handler handler) {
		@SuppressWarnings("rawtypes")
		List missionNodes = Mission.documentRoot.selectNodes("child::mission");
		boolean first = true;
		Mission firstMission = null;
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = missionNodes.iterator(); iterator.hasNext();) {
			Element missionNode = (Element) iterator.next();
			String idOfMission = missionNode.attributeValue("id");
			Mission curMission = Mission.create(idOfMission, null, missionNode);
			if (first) {
				firstMission = curMission;
				first = false;
			}
		}
		return firstMission;
	}

	private static void sendMsgExpandingGame(Handler handler) {
		int num_missions = countMissions();
		if (handler != null) {
			Message msg = handler.obtainMessage();
			msg.what = GeoQuestProgressHandler.MSG_TELL_MAX_AND_TITLE;
			msg.arg1 = num_missions;
			msg.arg2 = R.string.start_initializeGame;
			handler.sendMessage(msg);
		}
	}

	private static void endLastGame() {
		GeoQuestApp.getInstance().endGame();
		GeoQuestApp.getInstance().setInGame(true);
	}

	private static int countMissions() {
		XPath xpath1 = Mission.documentRoot.createXPath("count(//mission)");
		int num_missions = xpath1.numberValueOf(Mission.documentRoot)
				.intValue();
		return num_missions;
	}

	/**
	 * @param predefinedUIFactoryArray
	 *            if not empty, the first string in array is taken as name of
	 *            UIFactory and overrides settings in game xml specification.
	 */
	private static void setGlobalMissionLayout(
			Class<? extends UIFactory>... predefinedUIFactoryArray) {
		if (predefinedUIFactoryArray.length == 0)
			UIFactory.selectUIStyle(Mission.documentRoot
					.attributeValue("uistyle"));
		else
			UIFactory.setFactory(predefinedUIFactoryArray[0]);

		// TODO get rid of the rest, i.e. the old html layout mechanism:
		String layoutAttr = Mission.documentRoot.attributeValue("layout");
		if (layoutAttr != null && layoutAttr.equals("html")) {
			Mission.setUseWebLayoutGlobally(true);
		}
	}

	public static boolean existsGameOnClient(String repoName, String gameName) {
		// TODO: extend to check whether the game is complete (all referred
		// resources available).
		File repoDir = new File(GameDataManager.getLocalRepoDir(null) + "/"
				+ repoName);
		if (!repoDir.exists())
			return false;
		File gameDir = new File(repoDir, gameName);
		File gameXMLFile = new File(gameDir, "game.xml");
		if (!gameDir.exists() || !gameXMLFile.exists())
			return false;
		return true;
	}

	private static AssetManager assetManager;

	public static boolean loadIncludedQuests() {
		assetManager = GeoQuestApp.getContext().getAssets();
		String[] assetFiles = null;
		try {
			assetFiles = assetManager.list(ASSET_DIR_FOR_INCLUDED_QUESTS);
			if (assetFiles == null || assetFiles.length < 1)
				return false;
			else {
				for (int i = 0; i < assetFiles.length; i++) {
					loadGameFromAssets(ASSET_DIR_FOR_INCLUDED_QUESTS,
							assetFiles[i]);
				}
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
		return true;
	}

	private static void loadGameFromAssets(String dirName, String fileName) {
		InputStream is;
		try {
			// AssetFileDescriptor afd =
			// assetManager.openFd(assetFiles[0]);
			// TODO get length and show progress bar
			is = new BufferedInputStream(assetManager.open(dirName
					+ File.separator + fileName, AssetManager.ACCESS_BUFFER),
					BYTE_SIZE);
			File newRepoFile = GameDataManager.getLocalRepoDir(GeoQuestApp
					.getContext().getText(R.string.predefinedRepoName));
			if (!newRepoFile.exists()) {
				newRepoFile.mkdirs();
			}
			File newGameZipFile = new File(newRepoFile, fileName);
			FileOutputStream fOutLocal = createFileWriter(newGameZipFile);
			if (fOutLocal == null)
				return;

			// TODO: care about lenght == -1, i.e. if info not
			// available,
			// send
			// other msg to handler.
			// Message msg = handler.obtainMessage();
			// msg.what =
			// GeoQuestProgressHandler.MSG_TELL_MAX_AND_TITLE;
			// msg.arg1 = lenght / BYTE_SIZE;
			// msg.arg2 = R.string.start_downloadGame;
			// handler.sendMessage(msg);

			byte by[] = new byte[BYTE_SIZE];
			int c;

			try {
				is.available();
			} catch (IOException e) {
				fOutLocal.close();
				Log.w(TAG, "could not read from assets file)");
				return;

			}

			while ((c = is.read(by, 0, BYTE_SIZE)) != -1) {
				// TODO check access to SDCard!
				fOutLocal.write(by, 0, c);
				// trigger progress bar to proceed:
				// handler.sendEmptyMessage(GeoQuestProgressHandler.MSG_PROGRESS);
			}

			is.close();
			fOutLocal.close();
			GameLoader.unzipGameArchive(newGameZipFile);

			Log.d(TAG, "completed extraction: '" + dirName + File.separator
					+ fileName);
			// handler.sendEmptyMessage(GeoQuestProgressHandler.MSG_FINISHED);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
