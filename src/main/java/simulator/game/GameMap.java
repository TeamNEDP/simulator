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
}
