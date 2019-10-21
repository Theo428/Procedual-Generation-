package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Map
{

	public static final int IMAGE_WIDTH = 8;
	public static final int IMAGE_HEIGHT = 8;
	
	private static final Image shipImg = Toolkit.getDefaultToolkit().getImage("rooms/ship.png");
	
	public static final Image[][] mapRooms = {
			{getImage("rooms/Blank_Room.png")}, 	//Empty Room
			
			//Single Door Rooms
			{getImage("rooms/Black_Up.png"), getImage("rooms/Blue_Up.png")}, 			//Up	1
			{getImage("rooms/Black_Right.png"), getImage("rooms/Blue_Right.png")},		//Right	2
			{getImage("rooms/Black_Down.png"), getImage("rooms/Blue_Down.png")},		//Down	3
			{getImage("rooms/Black_Left.png"), getImage("rooms/Blue_Left.png")},		//Left 	4
			
			//Double Door Rooms
			{getImage("rooms/Black_Up_Right.png"), getImage("rooms/Blue_Up_Right.png")},			//Up, Right		5
	  		{getImage("rooms/Black_Down_Right.png"), getImage("rooms/Blue_Down_Right.png")},		//Right, Down	6
		    {getImage("rooms/Black_Down_Left.png"), getImage("rooms/Blue_Down_Left.png")},			//Down, Left	7
		    {getImage("rooms/Black_Up_Left.png"), getImage("rooms/Blue_Up_Left.png")},				//Left, Up		8
    		{getImage("rooms/Black_Up_Down.png"), getImage("rooms/Blue_Up_Down.png")},				//Up, Down		9
    		{getImage("rooms/Black_Left_Right.png"), getImage("rooms/Blue_Left_Right.png")},		//Right, Left 	10
	
			//Three Door Rooms
			{getImage("rooms/Black_Up_Down_Right.png"), getImage("rooms/Blue_Up_Down_Right.png")},			//Up, Right, Down	11
			{getImage("rooms/Black_Down_Right_Left.png"), getImage("rooms/Blue_Down_Right_Left.png")},		//Right, Down, Left	12
			{getImage("rooms/Black_Up_Down_Left.png"), getImage("rooms/Blue_Up_Down_Left.png")},			//Down. Left, Up	13
			{getImage("rooms/Black_Up_Left_Right.png"), getImage("rooms/Blue_Up_Left_Right.png")},			//Left, Up, Right	14

			//Four Door Rooms
			{getImage("rooms/Black_Up_Down_Left_Right.png"), getImage("rooms/Blue_Up_Down_Left_Right.png")},		//Up, Right, Down, Left 15

			{getImage("rooms/Boss_Up.png")}, 		//Up		16
			{getImage("rooms/Boss_Right.png")}, 	//Right		17
			{getImage("rooms/Boss_Down.png")}, 		//Down		18
			{getImage("rooms/Boss_Left.png")}};		//Left		19

	private AffineTransform transform = new AffineTransform();;
	
	private ArrayList<ArrayList<String>> map;
	
	public Map(ArrayList<ArrayList<String>> map) 
	{
		this.map = map;
	}

	public void render(Graphics graphics) 
	{
		double roomWidth = Game.WIDTH/(map.size() + 2);
		double roomHeight = Game.HEIGHT/(map.get(0).size() + 2);
		
		for(int i = 0; i < map.size(); i++) //room number
		{
			for(int j = 0; j < map.get(i).size(); j++) //column number
			{
				String roomValue = map.get(i).get(j);
				
				int roomType = 0;
				int roomVersion = 0;
						
				if(!roomValue.equals(""))
				{
					roomType = Integer.parseInt(roomValue.split(" ")[0]);
					roomVersion = Integer.parseInt(roomValue.split(" ")[1]);
				}
				
				Graphics2D graphics2d = (Graphics2D)graphics;
				
				if(roomWidth < roomHeight)
				{
					transform.setToTranslation((roomWidth * (j+ 1)), (roomWidth * (i + 1)));
					transform.scale(roomWidth/IMAGE_WIDTH, roomWidth/IMAGE_WIDTH);

				}
				else
				{
					transform.setToTranslation((roomHeight * (j+ 1)), (roomHeight * (i + 1)));
					transform.scale(roomHeight/IMAGE_HEIGHT, roomHeight/IMAGE_HEIGHT);

				}
				
				graphics2d.drawImage(mapRooms[roomType][roomVersion], transform, null);
				
				if(roomWidth < roomHeight)
				{
					
					if(roomValue.split(" ").length == 3)
					{
						graphics.setColor(Color.RED);
						graphics.drawRect((int)(roomWidth * (j+ 1)), (int)(roomWidth * (i + 1)), (int)(roomWidth) - 2, (int)(roomWidth) - 2);
					}
				}
				else
				{
					if(roomValue.split(" ").length == 3)
					{
						graphics.setColor(Color.RED);
						graphics.drawRect((int)(roomHeight * (j+ 1)), (int)(roomHeight * (i + 1)), (int)(roomHeight) - 2, (int)(roomHeight) - 2);
					}
				}
			}
		}
	}
	
	private static Image getImage(String url)
	{
		return Toolkit.getDefaultToolkit().getImage(url);
	}

}
