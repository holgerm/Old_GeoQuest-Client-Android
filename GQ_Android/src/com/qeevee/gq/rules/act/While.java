package com.qeevee.gq.rules.act;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import android.util.Log;

import com.qeevee.gq.rules.cond.Condition;
import com.qeevee.gq.rules.cond.ConditionFactory;


/* Example Game using While Action:
 * <?xml version="1.0" encoding="UTF-8"?>
<game id="3183" name="ActionDummy" xmlformat="5">
	<mission duration="interactive" id="6485" type="StartAndExitScreen" image="files/bg.jpg">
		<onStart>
			<rule>
				<action type="If">
					<condition>
						<Eq>
							<num>1</num>
							<num>2</num>
						</Eq>
					</condition>
					<then>
						<action type="ShowMessage" message="thenAction1"/>
						<action type="ShowMessage" message="thenAction2"/>
					</then>
					<else>
						<action type="ShowMessage" message="elseAction1"/>
						<action type="ShowMessage" message="elseAction2"/>
					</else>
				</action>
			</rule>
		</onStart>
	</mission>
</game>
 * 
 * 
 */

public class While extends Action {
	
	private Condition condition;
	private List<Action> actions = new ArrayList<Action>();


	@Override
	protected boolean checkInitialization() {
		boolean initOK = true;
		initOK &= elements.containsKey("condition");
		return initOK;
	}

	@Override
	public void execute() {
		
		Element xmlCondition = (Element) elements.get("condition").selectNodes("*").get(0);		
		condition = ConditionFactory.create(xmlCondition);
			
		Element xmlThen = elements.get("then");
		List<Element> xmlActionNodes = xmlThen.selectNodes("action");
		for (Element xmlAction : xmlActionNodes) {
			actions.add(ActionFactory.create(xmlAction));
		}
		int len = actions.size();
		
		while (condition.isFulfilled()) {	
			for (int i = 0; i < len; i++) {
				actions.get(i).execute();
				Log.d("myTag", "action performed: " + actions.get(i).params.get("type"));
			}
		}	
	}
}
