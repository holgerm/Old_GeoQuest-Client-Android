package edu.bonn.mobilegaming.geoquest.mission;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.qeevee.gq.items.Item;
import com.qeevee.gq.items.ItemFactory;
import com.qeevee.gq.items.ItemFlipperManager;
import com.qeevee.ui.BitmapUtil;

import edu.bonn.mobilegaming.geoquest.Globals;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionOrToolUI;

/**
 * Similar to an NPC Talk, but with new Layout containing multiple virtual
 * screens in a flip view.
 * 
 * @author Holger Muegge
 * 
 * @deprecated We are developing a new way to specify your own layouts. Do not
 *             use this approach!
 */
@Deprecated
public class Info extends MissionActivity {
	@SuppressWarnings("unused")
	private static final String TAG = "Info";

	private Button proceedButton;

	/**
	 * Called by the android framework when the mission is created. Setups the
	 * View and calls the readXML method to get the dialogItems. The dialog
	 * starts with the first dialogItem.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);

		makeBackground();
		makeImage();
		makeProceedButton();
		makeFlipper();
	}

	private void makeFlipper() {
		@SuppressWarnings("unchecked")
		List<Element> nodes = mission.xmlMissionNode.selectNodes("item");
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ArrayList<Item> items = new ArrayList(nodes.size());
		for (Iterator<Element> iterator = nodes.iterator(); iterator.hasNext();) {
			Element xmlItem = (Element) iterator.next();
			items.add(ItemFactory.create(xmlItem));
		}
		new ItemFlipperManager(items, this, proceedButton);

	}

	private void makeImage() {
		ImageView imageView = (ImageView) findViewById(R.id.item_image_view_id);
		String imagePath = mission.xmlMissionNode.attributeValue("image");
		Bitmap imageBitmap = BitmapUtil.getRoundedCornerBitmap(
				BitmapUtil.loadBitmap(imagePath, false), 7);
		imageView.setImageBitmap(imageBitmap);
	}

	private void makeBackground() {
		ViewGroup layout = (ViewGroup) findViewById(R.id.infoLayout);
		String backgroundImagePath = mission.xmlMissionNode
				.attributeValue("background");
		Bitmap backgoundBitmap = BitmapUtil.loadBitmap(backgroundImagePath,
				false);
		layout.setBackgroundDrawable(new BitmapDrawable(backgoundBitmap));
	}

	private void makeProceedButton() {
		this.proceedButton = (Button) findViewById(R.id.proceed_to_next_mission_button_id);
		proceedButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish(Globals.STATUS_SUCCEEDED);
			}

		});
	}

	public void onBlockingStateUpdated(boolean blocking) {
		// TODO Auto-generated method stub

	}

	public MissionOrToolUI getUI() {
		// TODO Auto-generated method stub
		return null;
	}
}
