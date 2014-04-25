package com.qeevee.gq.rules.act;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import com.qeevee.gq.rules.cond.Condition;
import com.qeevee.gq.rules.cond.ConditionFactory;


/* Example Game using IF Action:
 * <?xml version="1.0" encoding="UTF-8"?>
<game id="3183" name="ActionDummy" xmlformat="5">
	<mission duration="interactive" id="6485" type="StartAndExitScreen" image="files/bg.jpg">
		<onStart>
			<rule>
				<action type="If">
					<Condition>
						<Eq>
							<num>1</num>
							<num>2</num>
						</Eq>
					</Condition>
					<Then>
						<action type="ShowMessage" message="thenAction1"/>
						<action type="ShowMessage" message="thenAction2"/>
					</Then>
					<Else>
						<action type="ShowMessage" message="elseAction1"/>
						<action type="ShowMessage" message="elseAction2"/>
					</Else>
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
	private List<Action> actions = new ArrayList<Action>();


	@Override
	protected boolean checkInitialization() {
		boolean initOK = true;
		initOK &= elements.containsKey("Condition");
		return initOK;
	}

	@Override
	public void execute() {
		
		Element xmlCondition = (Element) elements.get("Condition").selectNodes("*").get(0);		
		condition = ConditionFactory.create(xmlCondition);
			
		if (condition.isFulfilled()) {
			
			Element xmlThen = elements.get("Then");
			List<Element> xmlActionNodes = xmlThen.selectNodes("action");
			for (Element xmlAction : xmlActionNodes) {
				actions.add(ActionFactory.create(xmlAction));
			}
			for (Action currentAction : actions) {
				currentAction.execute();
			}
			
		} else{
			
			Element xmlElse = elements.get("Else");
			List<Element> xmlActionNodes = xmlElse.selectNodes("action");
			for (Element xmlAction : xmlActionNodes) {
				actions.add(ActionFactory.create(xmlAction));
			}
			for (Action currentAction : actions) {
				currentAction.execute();
			}	
		}	
	}
}
