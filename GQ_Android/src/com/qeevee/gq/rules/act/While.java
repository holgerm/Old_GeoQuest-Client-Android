package com.qeevee.gq.rules.act;

import java.util.ArrayList;
import java.util.List;
import org.dom4j.Element;
import com.qeevee.gq.rules.cond.Condition;
import com.qeevee.gq.rules.cond.ConditionFactory;
import edu.bonn.mobilegaming.geoquest.Variables;


/* Example Game using While Action:
<?xml version="1.0" encoding="UTF-8"?>
<game id="3183" name="ActionDummy" xmlformat="5">
	<mission duration="interactive" id="6485" type="StartAndExitScreen" image="files/bg.jpg">
		<onStart>
			<rule>
				<action type="While">
					<condition>
						<Leq>
							<var>score</var>
							<num>10</num>
						</Leq>
					</condition>
					<then>
						<action type="AddToScore" value="2"/>
					</then>
				</action>
			</rule>
		</onStart>
	</mission>
</game>
 */

public class While extends Action {

	private Condition condition;
	private List<Action> actions;

	@Override
	protected boolean checkInitialization() {
		boolean initOK = true;
		initOK &= elements.containsKey("condition") && elements.containsKey("then");
		return initOK;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute() {
		actions = new ArrayList<Action>();
		Variables.setValue("break_while", Boolean.valueOf("false"));
		
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
				if((Boolean)Variables.getValue("break_while")) break;
			}
			if((Boolean)Variables.getValue("break_while")){
				Variables.setValue("break_while", Boolean.valueOf("false"));
				break;
			}
		}	
	}
}
