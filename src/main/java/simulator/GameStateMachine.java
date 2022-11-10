package simulator;

import simulator.game;
import simulator;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public final class GameStateMachine {
    GameSetting game;
    GameMap currentGameState;
    GameResult result;
    WebsocketHandler handler;
    int time;
    int first_to_play;
    // private ...

    // current player

    // script

    public GameStateMachine() {
        // gamesettings initialize
        time=0;
        Random random = new Random();
        first_to_play=random.nextInt(2);
        GameResult result=new GameResult();
    }

    /**
     *
     * @return whether the game ends
     */
    boolean tick(ExecutorService service) {
        // TODO
        //改动的格子数
        int num=1;
        //新建游戏刻
        GameTick tick=new GameTick();
        //新建游戏状态
        GameStat r_stat=new GameStat();
        GameStat b_stat=new GameStat();
        //时间增加
        time++;
        result.b_stat.rounds=time;
        result.r_stat.rounds=time;
        //每刻城堡/皇冠增加
        for(MapGrid a:game.map.grid)
        {
            if(a.type=="R"||a.type=="B"||a.type=="CR"||a.type=="CB")
            {
                a.soldiers++;
                game.changes=Arrays.copyOf(game.changes,num);
                game.changes[num-1]=a;
                num++;
            }
        }
        for(MapGrid a:game.map.grid)
        {
            if(a.type=="B"||a.type=="CB")
            {
                result.b_stat.soldiers_total++;
            }
            else if(a.type=="R"||a.type=="CR")
            {
                result.r_stat.soldiers_total++;
            }
        }
        //每50刻空地统一增加
        if(time%50==0)
        {
            for(MapGrid a:game.map.grid)
            {
                if(a.type=="LB"||a.type=="LR")
                {
                    a.soldiers++;
                    game.changes= Arrays.copyOf(game.changes,num);
                    game.changes[num-1]=a;
                    num++;
                }
            }
            for(MapGrid a:game.map.grid)
            {
                if(a.type=="LB")
                {
                    result.b_stat.soldiers_total++;
                }
                else if(a.type=="LR")
                {
                    result.r_stat.soldiers_total++;
                }
            }
        }
        r_stat.map=game.map.grid;
        b_stat.map=game.map.grid;
        for(MapGrid a:game.map.grid)
        {
            if(a.type=="LB"||a.type=="B"||a.type=="CB")
            {
                r_stat.enemy_lands++;
                r_stat.enemy_soldiers+=a.soldiers;
            }
            else if(a.type=="LR"||a.type=="R"||a.type=="CR")
            {
                b_stat.enemy_lands++;
                b_stat.enemy_soldiers+=a.soldiers;
            }
        }
        //调用户脚本
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine engine = engineManager.getEngineByName("nashorn");
        if(time%2==1)
        {

            if(first_to_play==1)
            {
                tick.operator="R";
                engine.eval(game.r.script);
            }
            else
            {
                tick.operator="B";
                engine.eval(game.b.script);
            }
        }
        else
        {
            if(first_to_play==0)
            {
                tick.operator="R";
                engine.eval(game.r.script);
            }
            else
            {
                tick.operator="B";
                engine.eval(game.b.script);
            }
        }
        Invocable invocable = (Invocable) engine;
        //传给用户脚本的参数
                            final var thread = Thread.currentThread();
                            final var done = new AtomicBoolean(false);
                            executor.schedule(() -> {
                                if (!done.get()) {
                                    thread.interrupt();
                                }
                            }, 500, TimeUnit.MILLISECONDS);
                            service.exexecute(thread);
                            var start = System.currentTimeMillis();
                            try {
                                tick.action=engine.eval("Tick(" + new Gson().toJson(gameStat) + ")");
                            } catch (InterruptedException ex) {
                                tick.action=null;
                            }
                            done.set(true);
                            // 固定间隔
                            var took = System.currentTimeMillis() - start;
        if(tick.action==null)
        {
            tick.action_valid =false;
            handler.sendGameTick(tick);
            return false;
        }
        else
        {
            MapGrid temp=game.map.grid[tick.action.moveaction.x*game.map.height+tick.action.moveaction.y];
            //判断是否是自己的格子
            if(tick.operator=="R")
            {
                if(temp.type!="R"&&temp.type!="CR"&&temp.type!="LR") {
                    tick.action_valid =false;
                    handler.sendGameTick(tick);
                    return false;
                }
            }
            else
            {
                if(temp.type!="B"&&temp.type!="CB"&&temp.type!="LB") {
                    tick.action_valid =false;
                    handler.sendGameTick(tick);
                    return false;
                }
            }
            //判断格子上的士兵数是否大于操作数
            if(temp.soldiers<=tick.action.moveaction.amount)
            {
                tick.action_valid =false;
                handler.sendGameTick(tick);
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
                handler.sendGameTick(tick);
                return false;
            }
            MapGrid to_grid=game.map.grid[to_x*game.map.height+to_y];
            //要去的格子是山地
            if(to_grid.type=="M"||to_grid.type=="MF")
            {
                tick.action_valid =false;
                handler.sendGameTick(tick);
                return false;
            }
            tick.action_valid=true;
            if(tick.operator=="R")
            {
                result.r_stat.moves++;
            }
            else
            {
                result.b_stat.moves++;
            }
            game.map.grid[tick.action.moveaction.x*game.map.height+tick.action.moveaction.y].soldiers-=tick.action.moveaction.amount;
            game.changes=Arrays.copyOf(game.changes,num);
            game.changes[num-1]= game.map.grid[tick.action.moveaction.x*game.map.height+tick.action.moveaction.y];
            num++;
            //要去的格子属于红色方
            if(to_grid.type=="R"||to_grid.type=="CR"||to_grid.type=="LR")
            {
                //行动的是红色方
                if(tick.operator=="R")
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
                        if(to_grid.type=="R")
                        {
                            result.winner='B';
                            for(MapGrid a:game.map.grid)
                            {
                                if(a.type=="R"||a.type=="CR"||a.type=="LR")
                                    result.r_stat.grids_taken++;
                                else if(a.type=="B"||a.type=="CB"||a.type=="LB")
                                    result.b_stat.grids_taken++;
                            }
                            handler.sendGameResult(result);

                        }
                        //占领城堡
                        else if(to_grid.type=="CR")
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
                game.changes=Arrays.copyOf(game.changes,num);
                game.changes[num-1]= game.map.grid[to_x*game.map.height+to_y];
                num++;
            }
            //要去的格子属于蓝色方
            else if(to_grid.type=="B"||to_grid.type=="CB"||to_grid.type=="LB")
            {
                //行动的是蓝色方
                if(tick.operator=="B")
                {
                    game.map.grid[to_x*game.map.height+to_y].soldiers+=tick.action.moveaction.amount;
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
                        if(to_grid.type=="B")
                        {
                            result.winner='R';
                            for(MapGrid a:game.map.grid)
                            {
                                if(a.type=="R"||a.type=="CR"||a.type=="LR")
                                    result.r_stat.grids_taken++;
                                else if(a.type=="B"||a.type=="CB"||a.type=="LB")
                                    result.b_stat.grids_taken++;
                            }
                            handler.sendGameResult(result);
                        }
                        //占领城堡
                        else if(to_grid.type=="CB")
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
                game.changes=Arrays.copyOf(game.changes,num);
                game.changes[num-1]= game.map.grid[to_x*game.map.height+to_y];
                num++;
            }
            //要去无人占领的城堡
            else if(to_grid.type=="C")
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
                    if(tick.operator=="R")
                    {
                        game.map.grid[to_x*game.map.height+to_y].type="CR";
                    }
                    //占领者为蓝色方
                    else
                    {
                        game.map.grid[to_x*game.map.height+to_y].type="CB";
                    }
                }
                game.changes=Arrays.copyOf(game.changes,num);
                game.changes[num-1]= game.map.grid[to_x*game.map.height+to_y];
                num++;
            }
            //要去无人占领的空地
            else if(to_grid.type=="F"||to_grid.type=="V")
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
                    if(tick.operator=="R")
                    {
                        game.map.grid[to_x*game.map.height+to_y].type="LR";
                    }
                    //占领者为蓝色方
                    else
                    {
                        game.map.grid[to_x*game.map.height+to_y].type="LB";
                    }
                }
                game.changes=Arrays.copyOf(game.changes,num);
                game.changes[num-1]= game.map.grid[to_x*game.map.height+to_y];
                num++;
            }
        }
        handler.sendGameTick(tick);
        return false;
    }
}
