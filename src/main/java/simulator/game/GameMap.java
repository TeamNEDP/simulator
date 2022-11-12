package simulator.game;

public class GameMap {
	public int width;
	public int height;
	public MapGrid[] grid;

	public boolean checkBorder(int x,int y)
	{
		return (width>x&&height>y&&x>=0&&y>=0);
	}

	public int get_pos(int x,int y)
	{
		return x*height+y;
	}

	public void change(String user)
	{
		for(int i=0;i<height * width; i++ )
		{
			int x=i%width;
			int y=i/width;
			int dx[]={-1,-1,-1,1,1,1,0,0,0};
			int dy[]={-1,0,1,-1,0,1,-1,0,1};
			boolean flag=false;
			for(int j=0;j<9;j++)
			{
				if(!checkBorder(x+dx[j], y+dy[j])) continue;
				if(grid[get_pos(x+dx[j],y+dy[j])].isBelongTo(user))
					flag=true;
			}
			grid[get_pos(x,y)].change(user,flag);
		}
	}
}
