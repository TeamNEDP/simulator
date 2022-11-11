package simulator;

import com.google.gson.Gson;
import simulator.game.*;
import websocket.*;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Arrays;
import java.util.Random;

public class GameStateMachine {
	GameStartData data;
	GameSetting game;
	GameMap currentGameState;
	ScheduledExecutorService service;
	GameResult result;
	WebsocketHandler handler;
	GameTick tick;
	GameStat r_stat;
	GameStat b_stat;
    ScriptEngine engine_r;
    ScriptEngine engine_b;
    ScriptEngineManager engineManager;
	int num;
	int time;
	int first_to_play;
	// private ...

	// current player

	// script

	public GameStateMachine(GameStartData data,ScheduledExecutorService service,WebsocketHandler handler) throws ScriptException {
		// gamesettings initialize
		time=0;
		result=new GameResult();
		this.data = data;
		game=data.setting;
		this.handler=handler;
		this.service = service;
        engineManager = new ScriptEngineManager();
		engine_r = engineManager.getEngineByName("nashorn_r");
        engine_b = engineManager.getEngineByName("nashorn_b");
        engine_r.eval(game.r.script);
        engine_b.eval(game.b.script);
	}

	/**
	 *
	 * @return whether the game ends
	 */
	public boolean tick() throws ScriptException {
		// TODO
		//初始化
		init();
		//每刻城堡/皇冠增加
		increase();
		//每50刻空地统一增加
		if(time%50==0) increase_all();
		//维护当前玩家状态
		update();
		//调用户脚本
        tick.judge_operator(time%2+first_to_play);

		//传给用户脚本的参数
		final var thread = Thread.currentThread();
		final var done = new AtomicBoolean(false);
		service.schedule(() -> {
			if (!done.get()) {
				thread.interrupt();
			}
		}, 500, TimeUnit.MILLISECONDS);


		try {
            if(tick.operator)
			tick.action = (GameAction) engine.eval("Tick(" + new Gson().toJson(new GameStat()) + ")");
		} catch (ScriptException ex) {
			tick.action = null;
		}
		done.set(true);

		if(tick.action==null)
		{
			tick.action_valid =false;
			handler.sendGameUpdateData(new GameUpdateData(data.id,tick));
			return false;
		}
		else
		{
			if(tick.action.moveaction.x<0||tick.action.moveaction.y<0||tick.action.moveaction.x>=game.map.width||tick.action.moveaction.y>=game.map.height)
			{
				tick.action_valid =false;
				handler.sendGameUpdateData(new GameUpdateData(data.id,tick));
				return false;
			}
			MapGrid temp=game.map.grid[tick.action.moveaction.x*game.map.height+tick.action.moveaction.y];
			//判断是否是自己的格子
			if(tick.operator.equals("R"))
			{
				if(!temp.type.equals("R") && !temp.type.equals("CR") && !temp.type.equals("LR")) {
					tick.action_valid =false;
					handler.sendGameUpdateData(new GameUpdateData(data.id,tick));
					return false;
				}
			}
			else
			{
				if(!temp.type.equals("B") && !temp.type.equals("CB") && !temp.type.equals("LB")) {
					tick.action_valid =false;
					handler.sendGameUpdateData(new GameUpdateData(data.id,tick));
					return false;
				}
			}
			//判断格子上的士兵数是否大于操作数
			if(temp.soldiers<=tick.action.moveaction.amount)
			{
				tick.action_valid =false;
				handler.sendGameUpdateData(new GameUpdateData(data.id,tick));
				return false;
			}
			//判断要去的格子
			int to_x=tick.action.moveaction.x;
			int to_y=tick.action.moveaction.y;
			if(tick.action.moveaction.movement=='L')
			{
				to_x--;
			}
			else if(tick.action.moveaction.movement=='R')
			{
				to_x++;
			}
			else if(tick.action.moveaction.movement=='U')
			{
				to_y++;
			}
			else
			{
				to_y--;
			}
			if(to_x<0||to_y<0||to_x>=game.map.width||to_y>=game.map.height)
			{
				tick.action_valid =false;
				handler.sendGameUpdateData(new GameUpdateData(data.id,tick));
				return false;
			}
			MapGrid to_grid=game.map.grid[to_x*game.map.height+to_y];
			//要去的格子是山地
			if(to_grid.type.equals("M") || to_grid.type.equals("MF"))
			{
				tick.action_valid =false;
				handler.sendGameUpdateData(new GameUpdateData(data.id,tick));
				return false;
			}
			tick.action_valid=true;
			if(tick.operator.equals("R"))
			{
				result.r_stat.moves++;
			}
			else
			{
				result.b_stat.moves++;
			}
			game.map.grid[tick.action.moveaction.x*game.map.height+tick.action.moveaction.y].soldiers-=tick.action.moveaction.amount;
			tick.changes=Arrays.copyOf(tick.changes,num);
			tick.changes[num-1]= game.map.grid[tick.action.moveaction.x*game.map.height+tick.action.moveaction.y];
			num++;
			//要去的格子属于红色方
			if(to_grid.type.equals("R") || to_grid.type.equals("CR") || to_grid.type.equals("LR"))
			{
				//行动的是红色方
				if(tick.operator.equals("R"))
				{
					game.map.grid[to_x*game.map.height+to_y].soldiers+=tick.action.moveaction.amount;

				}
				//行动的是蓝色方
				else
				{
					//不足占领
					if(to_grid.soldiers>=tick.action.moveaction.amount)
					{
						game.map.grid[to_x*game.map.height+to_y].soldiers-=tick.action.moveaction.amount;
						result.b_stat.soldiers_killed+=tick.action.moveaction.amount;
					}
					//可以占领
					else
					{
						game.map.grid[to_x*game.map.height+to_y].soldiers=tick.action.moveaction.amount-game.map.grid[to_x*game.map.height+to_y].soldiers;
						result.b_stat.soldiers_killed+=game.map.grid[to_x*game.map.height+to_y].soldiers;
						//占领皇冠
						if(to_grid.type.equals("R"))
						{
							result.winner='B';
							for(MapGrid a:game.map.grid)
							{
								if(a.type.equals("R") || a.type.equals("CR") || a.type.equals("LR"))
									result.r_stat.grids_taken++;
								else if(a.type.equals("B") || a.type.equals("CB") || a.type.equals("LB"))
									result.b_stat.grids_taken++;
							}
							handler.sendGameEndData(new GameEndData(data.id, result));
							return true;
						}
						//占领城堡
						else if(to_grid.type.equals("CR"))
						{
							game.map.grid[to_x*game.map.height+to_y].type="CB";
						}
						//占领空地
						else
						{
							game.map.grid[to_x*game.map.height+to_y].type="LB";
						}
					}
				}
				tick.changes=Arrays.copyOf(tick.changes,num);
				tick.changes[num-1]= game.map.grid[to_x*game.map.height+to_y];
				num++;
			}
			//要去的格子属于蓝色方
			else if(to_grid.type.equals("B") || to_grid.type.equals("CB") || to_grid.type.equals("LB"))
			{
				//行动的是蓝色方
				if(tick.operator.equals("B")) {
					game.map.grid[to_x * game.map.height + to_y].soldiers += tick.action.moveaction.amount;
				}
				//行动的是红色方
				else
				{

					//不足占领
					if(to_grid.soldiers>=tick.action.moveaction.amount)
					{
						game.map.grid[to_x*game.map.height+to_y].soldiers-=tick.action.moveaction.amount;
						result.r_stat.soldiers_killed+=tick.action.moveaction.amount;
					}
					//可以占领
					else
					{
						game.map.grid[to_x*game.map.height+to_y].soldiers=tick.action.moveaction.amount-game.map.grid[to_x*game.map.height+to_y].soldiers;
						result.r_stat.soldiers_killed+=game.map.grid[to_x*game.map.height+to_y].soldiers;
						//占领皇冠
						if(to_grid.type.equals("B"))
						{
							result.winner='R';
							for(MapGrid a:game.map.grid)
							{
								if(a.type.equals("R") || a.type.equals("CR") || a.type.equals("LR"))
									result.r_stat.grids_taken++;
								else if(a.type.equals("B") || a.type.equals("CB") || a.type.equals("LB"))
									result.b_stat.grids_taken++;
							}
							handler.sendGameEndData(new GameEndData(data.id, result));
							return true;
						}
						//占领城堡
						else if(to_grid.type.equals("CB"))
						{
							game.map.grid[to_x*game.map.height+to_y].type="CR";
						}
						//占领空地
						else
						{
							game.map.grid[to_x*game.map.height+to_y].type="LR";
						}
					}
				}
				tick.changes=Arrays.copyOf(tick.changes,num);
				tick.changes[num-1]= game.map.grid[to_x*game.map.height+to_y];
				num++;
			}
			//要去无人占领的城堡
			else if(to_grid.type.equals("C"))
			{
				//不足占领
				if(to_grid.soldiers>=tick.action.moveaction.amount)
				{
					game.map.grid[to_x*game.map.height+to_y].soldiers-=tick.action.moveaction.amount;
				}
				//可以占领
				else
				{
					game.map.grid[to_x*game.map.height+to_y].soldiers=tick.action.moveaction.amount-game.map.grid[to_x*game.map.height+to_y].soldiers;
					//占领者为红色方
					if(tick.operator.equals("R"))
					{
						game.map.grid[to_x*game.map.height+to_y].type="CR";
					}
					//占领者为蓝色方
					else
					{
						game.map.grid[to_x*game.map.height+to_y].type="CB";
					}
				}
				tick.changes=Arrays.copyOf(tick.changes,num);
				tick.changes[num-1]= game.map.grid[to_x*game.map.height+to_y];
				num++;
			}
			//要去无人占领的空地
			else if(to_grid.type.equals("F") || to_grid.type.equals("V"))
			{
				//不足占领
				if(to_grid.soldiers>=tick.action.moveaction.amount)
				{
					game.map.grid[to_x*game.map.height+to_y].soldiers-=tick.action.moveaction.amount;
				}
				//可以占领
				else
				{
					game.map.grid[to_x*game.map.height+to_y].soldiers=tick.action.moveaction.amount-game.map.grid[to_x*game.map.height+to_y].soldiers;
					//占领者为红色方
					if(tick.operator.equals("R"))
					{
						game.map.grid[to_x*game.map.height+to_y].type="LR";
					}
					//占领者为蓝色方
					else
					{
						game.map.grid[to_x*game.map.height+to_y].type="LB";
					}
				}
				tick.changes=Arrays.copyOf(tick.changes,num);
				tick.changes[num-1]= game.map.grid[to_x*game.map.height+to_y];
				num++;
			}
		}
		handler.sendGameUpdateData(new GameUpdateData(data.id,tick));
		return false;
	}
	void init()
	{
        //随机先手
        Random random = new Random();
        if(time%2==0) first_to_play=random.nextInt(2);
		//改动的格子数
		num=1;
		//新建游戏刻
		tick=new GameTick();
		//新建游戏状态
		r_stat=new GameStat();
		b_stat=new GameStat();
		//时间增加
		time++;
		result.b_stat.rounds=time;
		result.r_stat.rounds=time;
	}
	void increase()
	{
		for(MapGrid a:game.map.grid)
		{
			if(a.type.equals("R") || a.type.equals("B") || a.type.equals("CR") || a.type.equals("CB"))
			{
				a.soldiers++;
				tick.changes=Arrays.copyOf(tick.changes,num);
				tick.changes[num-1]=a;
				num++;
			}
		}
		for(MapGrid a:game.map.grid)
		{
			if(a.type.equals("B") || a.type.equals("CB"))
			{
				result.b_stat.soldiers_total++;
			}
			else if(a.type.equals("R") || a.type.equals("CR"))
			{
				result.r_stat.soldiers_total++;
			}
		}
	}

	void increase_all()
	{

		for(MapGrid a:game.map.grid)
		{
			if(a.type.equals("LB") || a.type.equals("LR"))
			{
				a.soldiers++;
				tick.changes= Arrays.copyOf(tick.changes,num);
				tick.changes[num-1]=a;
				num++;
			}
		}
		for(MapGrid a:game.map.grid)
		{
			if(a.type.equals("LB"))
			{
				result.b_stat.soldiers_total++;
			}
			else if(a.type.equals("LR"))
			{
				result.r_stat.soldiers_total++;
			}
		}
	}

	void update()
	{
		r_stat.map=game.map;
		b_stat.map=game.map;
		for(MapGrid a:game.map.grid)
		{
			if(a.type.equals("LB") || a.type.equals("B") || a.type.equals("CB"))
			{
				r_stat.enemy_lands++;
				r_stat.enemy_soldiers+=a.soldiers;
			}
			else if(a.type.equals("LR") || a.type.equals("R") || a.type.equals("CR"))
			{
				b_stat.enemy_lands++;
				b_stat.enemy_soldiers+=a.soldiers;
			}
		}
	}
}
