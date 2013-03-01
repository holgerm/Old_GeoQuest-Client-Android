package edu.bonn.mobilegaming.geoquest.mission;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import edu.bonn.mobilegaming.geoquest.Globals;
import edu.bonn.mobilegaming.geoquest.Mission;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.Variables;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionOrToolUI;
import edu.ubonn.gq.extmissionhelper.ExternalMissionHelper;

/**
 * External Mission, i.e. this Mission Activity mainly starts another Activity
 * which implements the external Mission.
 * 
 * The external Mission (which is basically an application on its own) must
 * declare the START action in its manifest. It gets delivered the parameters as
 * specified in the game.xml as extras. These parameters can be specified either
 * at the mission xml element or at the triggering action xml element. The
 * action defined parameters overwrite parameters specified in the mission if
 * they have the same name.
 * 
 * All parameters delivered show up in the extras of the starting intent of the
 * external application using the specified name in the game.xml plus the
 * preceding string "arg:".
 * 
 * Further on, the game.xml might specify result parameters (aka result
 * declarations). These are provided also as one extra within the starting
 * intent which has the key "result_declarations". The external application may
 * or may not send these parameters together with concrete string values back to
 * this mission activity.
 * 
 * At the time of writing, there is no option to specify that certain result
 * parameters are required. Instead they are all optional. Hence, you as a game
 * author should not rely on their values.
 * 
 * @author Holger Muegge
 */

public class ExternalMission extends MissionActivity {
	@SuppressWarnings("unused")
	private static final String TAG = "ExternalMission";

	/**
	 * The request code used with the Intent starting the external Mission.
	 */
	private static final int GQ_EXTERNAL_MISSION_REQUEST_CODE = 0;

	private TextView textView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// init UI (e.g. text display):
		setContentView(R.layout.external_mission);
		textView = (TextView) findViewById(R.id.externalMissionTextView);
		textView.setText(getMissionAttribute("description",
				R.string.external_mission_description_default));

		initAndStartExternalIntent();
	}

	/**
	 * The starting Intent which open the external application, is configured by
	 * parameters. These can be given either in the triggering action or the
	 * mission in game.xml. Parameters given in the Action override those given
	 * within the Mission. Hence, we first collect the key value pairs from
	 * Mission and then add those from Action potentially overwriting the
	 * others.
	 * 
	 * @see Mission#startMission(java.util.Map)
	 */
	private void initAndStartExternalIntent() {
		String intentAction = mission.xmlMissionNode.attributeValue("app_package")
				+ ".START";
		if (isIntentAvailable(this, intentAction)) {
			Intent intentForExternalMission = new Intent(intentAction);
			intentForExternalMission.addCategory(Intent.CATEGORY_DEFAULT);
			intentForExternalMission.putExtras(mission
					.getBundleForExternalMission());

			this.startActivityForResult(intentForExternalMission,
					GQ_EXTERNAL_MISSION_REQUEST_CODE);
		} else {
			textView.setText("Intent "
					+ mission.xmlMissionNode.attributeValue("app_package")
					+ ".START" + " not available on your device.");
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == GQ_EXTERNAL_MISSION_REQUEST_CODE
				&& resultCode == Activity.RESULT_OK) {
			// Store all declared and given results as GQ Variables:
			Bundle resultBundle = intent.getExtras();
			for (Iterator<String> iterator = resultBundle.keySet().iterator(); iterator
					.hasNext();) {
				String currentKey = iterator.next();
				if (currentKey.startsWith(ExternalMissionHelper.RES_PREFIX)) {
					Object currentResult = resultBundle.get(currentKey);
					if (currentResult != null
							&& currentResult instanceof String)
						Variables.setValue(ExternalMissionHelper
								.extractResultName(currentKey),
								(String) currentResult);
					else
						Log.e(this.getClass().getName(), "Result with key \""
								+ currentKey + "\" is not a String. Ignored.");
				}
			}
		}

		textView.setText(getMissionAttribute("result",
				R.string.external_mission_result_default));
		finish(Globals.STATUS_SUCCEEDED);
	}

	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * 
	 * @param context
	 *            The application's environment.
	 * @param action
	 *            The Intent action to check for availability.
	 * 
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	public void onBlockingStateUpdated(boolean blocking) {
		// TODO Auto-generated method stub
		
	}

	public MissionOrToolUI getUI() {
	    // TODO Auto-generated method stub
	    return null;
	}
}
