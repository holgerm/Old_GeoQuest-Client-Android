/**
 * 
 */
package edu.bonn.mobilegaming.geoquest.gameaccess;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * This class is responsible for loading, updating and persisting meta data
 * about the repositories and games. The real game and repository data is
 * managed by the {@link GameDataManager} class.
 * 
 * @author muegge
 * @deprecated we are going to eliminate this class soon.
 * 
 */
public class GameMetadataManager {

	public static Map<String, RepositoryItem> repositories = new HashMap<String, RepositoryItem>();

	/**
	 * Refers either to the file representing the local meta data or to the
	 * downloaded remote meta data. See
	 * {@link GameMetadataManager#loadFileForLocalMetadata()}.
	 */
	protected static File currentMetadataFile;

	private static final String LOCAL_METADATA_FILE_NAME = "repolist.xml";

	/**
	 * How does it happen:
	 * <ol>
	 * <li>Persisted repo data is retrieved from local repolist.xml file and a
	 * runtime object structure is generated, i.e. Repo- and GameItems are
	 * instantiated.
	 * <li>For each Repository- and GameItem it is checked that the according
	 * files exist locally. If not, the items are deleted. (see
	 * {@link RepositoryItem} and {@link GameItem})
	 * <li>The local repo and game files are scanned and those which are not
	 * represented by items are amended.
	 * <li>Now the local information is completely represented by the item
	 * objects. They are newly persisted by overwriting the repolist.xml if
	 * necessary (i.e. if in step 3 a new game or repository has been found).
	 * <li>Server-side information is retrieved (via repolist.php). For each
	 * remote repo and game the runtime items are added or amended.
	 * </ol>
	 */
	public static void createRuntimeMetadata() {
		repositories.clear();
		loadFileForLocalMetadata();
		importMetadataFromFile(currentMetadataFile);
		checkRuntimeMetadataAgainstLocalData();
		amendRuntimeMetadataWithLocalData();
		exportRuntimeMetadataToLocalFile();
		amendRuntimeMetadataWithRemoteData();
	}

	/**
	 * Loads the file representing the locally stored persistent meta data for
	 * repositories and games into the variable {@link #currentMetadataFile}.
	 */
	protected static void loadFileForLocalMetadata() {
		currentMetadataFile = new File(GameDataManager.getLocalRepoDir(null),
				LOCAL_METADATA_FILE_NAME);
	}

	/**
	 * Persisted meta data is retrieved from local repolist.xml file and a
	 * runtime object structure is generated, i.e. Repo- and GameItems are
	 * instantiated.
	 */
	private static void importMetadataFromFile(File metadataFile) {
		if (!metadataFile.exists())
			return;
		SAXReader reader = new SAXReader();
		Document lmdDoc;
		try {
			lmdDoc = reader.read(metadataFile);
			Element lmdDocRoot = lmdDoc.getRootElement();
			@SuppressWarnings("rawtypes")
			List repositoryElements = lmdDocRoot
					.selectNodes("child::repository");
			for (@SuppressWarnings("rawtypes")
			Iterator iterator = repositoryElements.iterator(); iterator
					.hasNext();) {
				Element curRepoElem = (Element) iterator.next();
				importRepoItemFromXML(curRepoElem);
			}
		} catch (DocumentException e) {
			// TODO Implement systematic error handling and use it here.
			e.printStackTrace();
		}
	}

	/**
	 * If the repository represented by the given XML element does not already
	 * exist in the runtime object structure (see
	 * {@link GameMetadataManager#repositories}) a new RepoItem is created and
	 * inserted.
	 * 
	 * @param repoElem
	 *            the DOM ELement corresponding to a {@code <repository>} tag.
	 */
	private static void importRepoItemFromXML(Element repoElem) {
		String repoName = repoElem.attributeValue("name");
		if (!repositories.containsKey(repoName))
			repositories.put(repoName, new RepositoryItem(repoName));
	}

	/**
	 * For each Repo- and GameItem it is checked that the according files exist
	 * locally. If not, the items are deleted.
	 */
	private static void checkRuntimeMetadataAgainstLocalData() {
		// TODO Auto-generated method stub

	}

	/**
	 * The local repo and game files are scanned and those which are not
	 * represented by items are amended.
	 */
	private static void amendRuntimeMetadataWithLocalData() {
		// TODO Auto-generated method stub

	}

	/**
	 * The local information is newly persisted by overwriting the repolist.xml
	 * if necessary (i.e. if in step 3 a new game or repository has been found).
	 */
	private static void exportRuntimeMetadataToLocalFile() {
		// TODO Auto-generated method stub

	}

	/**
	 * Server-side information is retrieved (via repolist.php). For each remote
	 * repo and game the runtime items are added or amended.
	 */
	private static void amendRuntimeMetadataWithRemoteData() {
		// TODO Auto-generated method stub

	}

}
