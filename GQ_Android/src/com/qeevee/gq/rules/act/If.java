package com.qeevee.gq.rules.act;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import com.qeevee.gq.rules.cond.Condition;
import com.qeevee.gq.rules.cond.ConditionFactory;

import edu.bonn.mobilegaming.geoquest.Variables;


/* Example Game using IF Action:
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

public class If extends Action {
	
	private Condition condition;
	private List<Action> actions;


	@Override
	protected boolean checkInitialization() {
		boolean initOK = true;
		initOK &= elements.containsKey("condition") && elements.containsKey("then") && elements.containsKey("else");
		return initOK;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute() {
		actions = new ArrayList<Action>();
		
		Element xmlCondition = (Element) elements.get("condition").selectNodes("*").get(0);		
		condition = ConditionFactory.create(xmlCondition);
			
		if (condition.isFulfilled()) {
			
			Element xmlThen = elements.get("then");
			List<Element> xmlActionNodes = xmlThen.selectNodes("action");
			for (Element xmlAction : xmlActionNodes) {
				actions.add(ActionFactory.create(xmlAction));
			}
			for (Action currentAction : actions) {
				currentAction.execute();
				if((Boolean)Variables.getValue("break_while")) break;
			}
			
		} else{
			
			Element xmlElse = elements.get("else");
			List<Element> xmlActionNodes = xmlElse.selectNodes("action");
			for (Element xmlAction : xmlActionNodes) {
				actions.add(ActionFactory.create(xmlAction));
			}
			for (Action currentAction : actions) {
				currentAction.execute();
				if((Boolean)Variables.getValue("break_while")) break;
			}	
		}	
	}
}
