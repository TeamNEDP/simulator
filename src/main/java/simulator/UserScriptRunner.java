package simulator;


import simulator.game.GameStat;
import simulator.game.Movement;
import simulator.game.UserScript;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.concurrent.ScheduledExecutorService;

public class UserScriptRunner {
	private ScriptEngine engine = null;
	private final ScheduledExecutorService service;
	private boolean noop = false;

	public UserScriptRunner(UserScript script, ScheduledExecutorService service) {
		this.service = service;
		if (script.type.equals("javascript")) {
			this.engine = new ScriptEngineManager().getEngineByName("nashorn");
			try {
				engine.eval(script.content);
			} catch (ScriptException e) {
				noop = true;
			}
		} else {
			noop = true;
		}
	}

	public Movement run(GameStat stat) {
		// TODO
		
	}

}
