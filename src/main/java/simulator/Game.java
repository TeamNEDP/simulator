package simulator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Game {


    public static void main(String[] args) {
        ExecutorService service= Executors.newFixedThreadPool(10);
        GameSetting game;
        GameTick tick;
        GameResult result=new GameResult();
        //传入游戏设置
        game=game;
        //初始化结果
        result.winner=' ';
        result.b_stat.rounds=0;
        result.b_stat.soldiers_total=0;
        result.b_stat.grids_taken=1;
        result.b_stat.moves=0;
        result.b_stat.soldiers_killed=0;
        result.r_stat.rounds=0;
        result.r_stat.soldiers_total=0;
        result.r_stat.grids_taken=1;
        result.r_stat.moves=0;
        result.r_stat.soldiers_killed=0;
        int Time=0;
        while(true)
        {
            //传入游戏刻
            tick=tick;
            Time++;
            result.b_stat.rounds++;
            result.r_stat.rounds++;
            //玩家操作
            service.execute(new play(game,tick,result));
            //每50刻空地统一增加
            if(Time%50==0)
                service.execute(new increase_all(game,tick,result));
            //每刻城堡/皇冠增加
            service.execute(new increase(game,tick,result));
        }
    }



}

class play implements Runnable{
    //玩家操作
    GameSetting game;
    GameTick tick;
    GameResult result;
    public play(GameSetting game_1,GameTick tick_1,GameResult result_1)
    {
        game=game_1;
        tick=tick_1;
        result=result_1;
    }
    public void run()
    {
        synchronized (game)
        {
            for(GridChange a:tick.changes)//变化
            {
                game.map.grid[a.x*game.map.height+a.y]=a.grid;
            }
            if(tick.action==null) return;
            else
            {
                MapGrid temp=game.map.grid[tick.action.moveaction.x*game.map.height+tick.action.moveaction.y];
                //判断是否是自己的格子
                if(tick.operator=="R")
                {

                    if(temp.type!="R"&&temp.type!="CR"&&temp.type!="LR") {
                        tick.action_valid =false;
                        return;
                    }
                }
                else
                {
                    if(temp.type!="B"&&temp.type!="CB"&&temp.type!="LB") {
                        tick.action_valid =false;
                        return;
                    }
                }
                //判断格子上的士兵数是否大于操作数
                if(temp.soldiers<=tick.action.moveaction.amount)
                {
                    tick.action_valid =false;
                    return;
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
                    return;
                }
                MapGrid to_grid=game.map.grid[to_x*game.map.height+to_y];
                //要去的格子是山地
                if(to_grid.type=="M"||to_grid.type=="MF")
                {
                    tick.action_valid =false;
                    return;
                }
                tick.action_valid=true;
                game.map.grid[tick.action.moveaction.x*game.map.height+tick.action.moveaction.y].soldiers-=tick.action.moveaction.amount;
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
                        }
                        //可以占领
                        else
                        {
                            game.map.grid[to_x*game.map.height+to_y].soldiers=tick.action.moveaction.amount-game.map.grid[to_x*game.map.height+to_y].soldiers;
                            //占领皇冠
                            if(to_grid.type=="R")
                            {
                                win;
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
                        }
                        //可以占领
                        else
                        {
                            game.map.grid[to_x*game.map.height+to_y].soldiers=tick.action.moveaction.amount-game.map.grid[to_x*game.map.height+to_y].soldiers;
                            //占领皇冠
                            if(to_grid.type=="B")
                            {
                                win;
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
                }
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
                }
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
                }
            }
        }

    }
}

class increase implements Runnable{
    //每刻城堡/皇冠增加
    GameSetting game;
    GameTick tick;
    GameResult result;
    public increase(GameSetting game_1,GameTick tick_1,GameResult result_1)
    {
        game=game_1;
        tick=tick_1;
        result=result_1;
    }
    public void run()
    {
        synchronized (game)
        {
            for(MapGrid a:game.map.grid)
            {
                if(a.type=="R"||a.type=="B"||a.type=="CR"||a.type=="CB")
                    a.soldiers++;
            }
        }
        synchronized (result)
        {
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
        }
    }
}

class increase_all implements Runnable{
    //每50刻空地统一增加
    GameSetting game;
    GameTick tick;
    GameResult result;
    public increase_all(GameSetting game_1,GameTick tick_1,GameResult result_1)
    {
        game=game_1;
        tick=tick_1;
        result=result_1;
    }
    public void run()
    {
        synchronized (game)
        {
            for(MapGrid a:game.map.grid)
            {
                if(a.type=="LB"||a.type=="RB")
                {
                    a.soldiers++;
                }
            }
        }
        synchronized (result)
        {
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
    }
}