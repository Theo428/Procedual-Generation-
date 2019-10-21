package generator;

import java.util.ArrayList;
import java.util.Arrays;

import main.Map;

public class MapGenerator 
{
	private static final int[] ROOMS = {Map.mapRooms[0].length, 		//Blank Room 	0
										
										//Single Door
										Map.mapRooms[1].length,		//Up		1
										Map.mapRooms[2].length,		//Right		2
										Map.mapRooms[3].length,		//Down		3
										Map.mapRooms[4].length,		//Left		4
										
										//Two Door
										Map.mapRooms[5].length,		//Up, Right			5
										Map.mapRooms[6].length,		//Right, Down		6
										Map.mapRooms[7].length,		//Down, Left		7
										Map.mapRooms[8].length,		//Left, Up			8
										Map.mapRooms[9].length,		//Up, Down			9
										Map.mapRooms[10].length,		//Right, Left 		10
										
										//Three Door
										Map.mapRooms[11].length,		//Up, Right, Down		11
										Map.mapRooms[12].length,		//Right, Down, Left		12
										Map.mapRooms[13].length,		//Down. Left, Up		13
										Map.mapRooms[14].length,		//Left, Up, Right		14
										
										//Four Door
										Map.mapRooms[15].length,	//Up, Right, Down, Left 	15
	
										Map.mapRooms[16].length,	//Up		16
										Map.mapRooms[17].length,	//Right		17
										Map.mapRooms[18].length,	//Down		18
										Map.mapRooms[19].length};	//Left		19
	
	/*possible rooms to start in
	 * 		-can be any room from the list but may spawn single door rooms on all but one side
	 * 		-must be in format "roomType + room version + "S"" with spaces in between
	 */
	private static final String[] StartingRooms = {"1 0 S",
												   "1 1 S",
												   "2 0 S",
												   "2 1 S",
												   "3 0 S",
												   "3 1 S",
												   "4 0 S",
												   "4 1 S"};
	
	//Array containing all rooms with doors on the top (excluding boss rooms)
	private static final Integer[] TOP_DOOR_IDS = {1, 5, 8, 9, 11, 13, 14, 15};

	//Array containing all rooms with doors on the bottom (excluding boss rooms)
	private static final Integer[] BOTTOM_DOOR_IDS = {3, 6, 7, 9, 11, 12, 13, 15};

	//Array containing all rooms with doors on the left (excluding boss rooms)
	private static final Integer[] LEFT_DOOR_IDS = {4, 7, 8, 10, 12, 13, 14, 15};

	//Array containing all rooms with doors on the right (excluding boss rooms)
	private static final Integer[] RIGHT_DOOR_IDS = {2, 5, 6, 10, 11, 12, 14, 15};
	
	//Max before dead ends added
	private static final int MAX_ROOMS = 40;
	
	//will not spawn single door rooms unless necessary until this threshold is reached
	private static final int MIN_ROOMS = 20;
	
	//row and column of the start point
	private int StartRow = 0;
	private int StartColumn = 0;
	
	//Total number of rooms added
	private int numRooms = 0;
	
	//whether or not the boss room has been created
	private boolean hasMadeBossRoom = false;
	
	//The map itself
	private ArrayList<ArrayList<String>> map = new ArrayList<ArrayList<String>>();
	
	public MapGenerator()
	{
		
	}
	
	//builds map
	public ArrayList<ArrayList<String>> GetMap()
	{	
		//choose initial room
		int StartRoom = (int)(Math.random() * StartingRooms.length);
		
		//input starting Room into map
		map.add(new ArrayList<String>());
		map.get(0).add(StartingRooms[StartRoom]); 
		
		//start creating rooms recursively
		AddRoom(0, 0);
		
		return map;
	}
	
	private void AddRoom(int currentRow, int currentColumn)
	{
		//increment number of rooms 
		numRooms++;
		
		//value of the room in the current position
		String currentRoom = map.get(GetAbsoluteRow(currentRow)).get(GetAbsoluteColumn(currentColumn));
		
		int currentRoomType = Integer.parseInt(currentRoom.split(" ")[0]);
		
		
		//Randomly decide which direction to generate a room for first
		ArrayList<Integer> generationOptions = new ArrayList<Integer>();
		
		generationOptions.add(0);
		generationOptions.add(1);
		generationOptions.add(2);
		generationOptions.add(3);
		
		Integer[] generationOrder = {0, 0, 0, 0}; //array containing the generation order
		
		for(int i = 0; i < generationOrder.length; i++)
		{
			int index = (int)(Math.random() * generationOptions.size());
			generationOrder[i] = generationOptions.get(index);
			generationOptions.remove(index);
		}
		
		
		//start generating rooms
		for(int i = 0; i < generationOrder.length; i++)
		{
		
			if(generationOrder[i] == 0)
			{
				
				//add room Top
				addRoomTop(currentRow, currentColumn, currentRoomType);
			}
			else if(generationOrder[i] == 1)
			{
				
				//add room bottom
				addRoomBottom(currentRow, currentColumn, currentRoomType);
			}
			else if(generationOrder[i] == 2)
			{
				
				//add room Left
				addRoomLeft(currentRow, currentColumn, currentRoomType);
			}
			else if(generationOrder[i] == 3)
			{
				
				//add room right
				addRoomRight(currentRow, currentColumn, currentRoomType);
			}
		}
		
	}
	
	//generate room above coordinates
	private void addRoomTop(int currentRow, int currentColumn, int currentRoomType)
	{
		//add row above if necessary 
		if(GetAbsoluteRow(currentRow) == 0)
		{
			addRowTop();
		}
		
		//only generate room if the room below has a door on top and there is not already a room above it
		if(Arrays.asList(TOP_DOOR_IDS).contains(currentRoomType) && map.get(GetAbsoluteRow(currentRow + 1)).get(GetAbsoluteColumn(currentColumn)).equals(""))
		{
			//array containig the possible rooms that can be generated
			ArrayList<Integer> possibleRooms = new ArrayList<Integer>(Arrays.asList(BOTTOM_DOOR_IDS));
			
			//replaced single door room with boss room in the same orientation
			if(numRooms >= MIN_ROOMS && !hasMadeBossRoom)
			{
				possibleRooms.set(0, 18);
				System.out.println("Boss Room Possible "  + currentRow + " : " + currentColumn);
			}
			
			//removed single door possibility if the minimum room requirment has not been met
			if(numRooms < MIN_ROOMS)
			{
				possibleRooms.remove(0);
			}
			
			//Filter rooms based on if there is a door on top
			possibleRooms = filterRoomsWithTopDoors(possibleRooms, currentRow + 1, currentColumn);

			//Filter rooms based on if there is a door to the right
			possibleRooms = filterRoomsWithRightDoors(possibleRooms, currentRow + 1, currentColumn);

			//Filter rooms based on if there is a door to the left
			possibleRooms = filterRoomsWithLeftDoors(possibleRooms, currentRow + 1, currentColumn);
			
			//determine which room will be placed from possibleRooms
			int roomIndex = 0;
			if(possibleRooms.size() == 0)
			{
				//adds single door room if there are no other options
				possibleRooms.add(BOTTOM_DOOR_IDS[0]);
			}
			else if(numRooms <= MAX_ROOMS)
			{
				//chooses a random index from possibleRooms
				roomIndex = (int)(Math.random() * possibleRooms.size());
			}
			
			//randomly generate which version of the room will be used
			int roomTypeIndex = (int)(Math.random() * ROOMS[possibleRooms.get(roomIndex)]); 
			
			//add room the the map
			map.get(GetAbsoluteRow(currentRow + 1)).set(GetAbsoluteColumn(currentColumn), possibleRooms.get(roomIndex) + " " + roomTypeIndex);

			//check if a boss room has been created
			hasMadeBossRoom = hasMadeBossRoom || possibleRooms.get(roomIndex) == 18;
			
			//recursivley add another room
			AddRoom(currentRow + 1, currentColumn);
		}
	}

	//generate room below coordinates
	private void addRoomBottom(int currentRow, int currentColumn, int currentRoomType)
	{
		//add row below if necessary
		if(GetAbsoluteRow(currentRow) == map.size() - 1)
		{
			addRowBottom();
		}
		
		//only generate room if the room above has a door on top and there is not already a room below it
		if(Arrays.asList(BOTTOM_DOOR_IDS).contains(currentRoomType) && map.get(GetAbsoluteRow(currentRow - 1)).get(GetAbsoluteColumn(currentColumn)).equals(""))
		{
			//array containig the possible rooms that can be generated
			ArrayList<Integer> possibleRooms = new ArrayList<Integer>(Arrays.asList(TOP_DOOR_IDS));
			
			//replaced single door room with boss room in the same orientation
			if(numRooms >= MIN_ROOMS && !hasMadeBossRoom)
			{
				possibleRooms.set(0, 16);
				System.out.println("Boss Room Possible " + currentRow + " : " + currentColumn);
			}
			
			//removed single door possibility if the minimum room requirment has not been met
			if(numRooms < MIN_ROOMS)
			{
				possibleRooms.remove(0);
			}
			
			//Filter rooms based on if there is a door on the bottom
			possibleRooms = filterRoomsWithBottomDoors(possibleRooms, currentRow - 1, currentColumn);
			
			//Filter rooms based on if there is a door to the right
			possibleRooms = filterRoomsWithRightDoors(possibleRooms, currentRow - 1, currentColumn);

			//Filter rooms based on if there is a door to the left
			possibleRooms = filterRoomsWithLeftDoors(possibleRooms, currentRow - 1, currentColumn);
			
			//determine which room will be placed from possibleRooms
			int roomIndex = 0;
			if(possibleRooms.size() == 0)
			{
				//adds single door room if there are no other options
				possibleRooms.add(TOP_DOOR_IDS[0]);
			}
			else if(numRooms <= MAX_ROOMS)
			{
				//chooses a random index from possibleRooms
				roomIndex = (int)(Math.random() * possibleRooms.size());
			}
			
			//randomly generate which version of the room will be used
			int roomTypeIndex = (int)(Math.random() * ROOMS[possibleRooms.get(roomIndex)]);
			
			//add room the the map
			map.get(GetAbsoluteRow(currentRow - 1)).set(GetAbsoluteColumn(currentColumn), possibleRooms.get(roomIndex) + " " + roomTypeIndex);

			//check if a boss room has been created
			hasMadeBossRoom = hasMadeBossRoom || possibleRooms.get(roomIndex) == 16;
			
			//recursivley add another room
			AddRoom(currentRow - 1, currentColumn);
		}
	}
	
	//generate room to the left of coordinates
	private void addRoomLeft(int currentRow, int currentColumn, int currentRoomType)
	{
		//add column on the left if necessary 
		if(GetAbsoluteColumn(currentColumn) == 0)
		{
			addColumnLeft();
		}
		
		//only generate room if the room below has a door on top and there is not already a room above it
		if(Arrays.asList(LEFT_DOOR_IDS).contains(currentRoomType) && map.get(GetAbsoluteRow(currentRow)).get(GetAbsoluteColumn(currentColumn - 1)).equals(""))
		{
			//array containig the possible rooms that can be generated
			ArrayList<Integer> possibleRooms = new ArrayList<Integer>(Arrays.asList(RIGHT_DOOR_IDS));
			
			//replaced single door room with boss room in the same orientation
			if(numRooms >= MIN_ROOMS && !hasMadeBossRoom)
			{
				possibleRooms.set(0, 17);
				System.out.println("Boss Room Possible " + currentRow + " : " + currentColumn);
			}
			
			//removed single door possibility if the minimum room requirment has not been met
			if(numRooms < MIN_ROOMS)
			{
				possibleRooms.remove(0);
			}
			
			//Filter rooms based on if there is a door on top
			possibleRooms = filterRoomsWithTopDoors(possibleRooms, currentRow, currentColumn - 1);
			
			//Filter rooms based on if there is a door on the bottom
			possibleRooms = filterRoomsWithBottomDoors(possibleRooms, currentRow, currentColumn - 1);


			//Filter rooms based on if there is a door to the left
			possibleRooms = filterRoomsWithLeftDoors(possibleRooms, currentRow, currentColumn - 1);

			
			//determine which room will be placed from possibleRooms
			int roomIndex = 0;
			if(possibleRooms.size() == 0)
			{
				//adds single door room if there are no other options
				possibleRooms.add(RIGHT_DOOR_IDS[0]);
			}
			else if(numRooms <= MAX_ROOMS)
			{
				//chooses a random index from possibleRooms
				roomIndex = (int)(Math.random() * possibleRooms.size());
			}
			
			//randomly generate which version of the room will be used
			int roomTypeIndex = (int)(Math.random() * ROOMS[possibleRooms.get(roomIndex)]);
			
			//add room the the map
			map.get(GetAbsoluteRow(currentRow)).set(GetAbsoluteColumn(currentColumn - 1), possibleRooms.get(roomIndex) + " " + roomTypeIndex);

			//check if a boss room has been created
			hasMadeBossRoom = hasMadeBossRoom || possibleRooms.get(roomIndex) == 17;
			
			//recursivley add another room
			AddRoom(currentRow, currentColumn - 1);
		}
	}
	
	//generate room to the right of coordinates
	private void addRoomRight(int currentRow, int currentColumn, int currentRoomType)
	{
		//add column on the right if necessary 
		if(GetAbsoluteColumn(currentColumn) == map.get(0).size() - 1)
		{
			addColumnRight();
		}
		
		//only generate room if the room to the left has a door on the right and there is not already a room above it
		if(Arrays.asList(RIGHT_DOOR_IDS).contains(currentRoomType) && map.get(GetAbsoluteRow(currentRow)).get(GetAbsoluteColumn(currentColumn + 1)).equals(""))
		{
			//array containig the possible rooms that can be generated
			ArrayList<Integer> possibleRooms = new ArrayList<Integer>(Arrays.asList(LEFT_DOOR_IDS));
			
			//replaced single door room with boss room in the same orientation
			if(numRooms >= MIN_ROOMS && !hasMadeBossRoom)
			{
				possibleRooms.set(0, 19);
				System.out.println("Boss Room Possible " + currentRow + " : " + currentColumn);
			}
			
			//removed single door possibility if the minimum room requirment has not been met
			if(numRooms < MIN_ROOMS)
			{
				possibleRooms.remove(0);
			}
			
			//Filter rooms based on if there is a door on top
			possibleRooms = filterRoomsWithTopDoors(possibleRooms, currentRow, currentColumn + 1);
			
			//Filter rooms based on if there is a door on the bottom
			possibleRooms = filterRoomsWithBottomDoors(possibleRooms, currentRow, currentColumn + 1);


			//Filter rooms based on if there is a door on the right
			possibleRooms = filterRoomsWithRightDoors(possibleRooms, currentRow, currentColumn + 1);

			
			//determine which room will be placed from possibleRooms
			int roomIndex = 0;
			if(possibleRooms.size() == 0)
			{
				//adds single door room if there are no other options
				possibleRooms.add(LEFT_DOOR_IDS[0]);
			}
			else if(numRooms <= MAX_ROOMS)
			{
				//chooses a random index from possibleRooms
				roomIndex = (int)(Math.random() * possibleRooms.size());
			}
			
			//randomly generate which version of the room will be used
			int roomTypeIndex = (int)(Math.random() * ROOMS[possibleRooms.get(roomIndex)]);
			
			//add room the the map
			map.get(GetAbsoluteRow(currentRow)).set(GetAbsoluteColumn(currentColumn + 1), possibleRooms.get(roomIndex) + " " + roomTypeIndex);
			
			//check if a boss room has been created
			hasMadeBossRoom = hasMadeBossRoom || possibleRooms.get(roomIndex) == 19;
			
			//recursivley add another room
			AddRoom(currentRow, currentColumn + 1);
		}
	}
	
	//remove rooms from the arraylist possibleRooms depending of whether there is a room with a door above
	private ArrayList<Integer> filterRoomsWithTopDoors(ArrayList<Integer> possibleRooms, int currentRow, int currentColumn)
	{
		//check for a room with a door facing down, above 
		if(CheckForDoorTop(currentRow, currentColumn))
		{
			//remove rooms without doors facing up
			for(int i = 0; i < possibleRooms.size(); i++)
			{
				if(!Arrays.asList(TOP_DOOR_IDS).contains(possibleRooms.get(i)))
				{
					possibleRooms.remove(i);
					i--;
				}
			}
		}
		//check for room without a door facing, down above
		else if(CheckForRoomTop(currentRow, currentColumn))
		{
			//remove rooms with doors facing up
			for(int i = 0; i < possibleRooms.size(); i++)
			{
				if(Arrays.asList(TOP_DOOR_IDS).contains(possibleRooms.get(i)))
				{
					possibleRooms.remove(i);
					i--;
				}
			} 
		}
		
		return possibleRooms;
	}

	//remove rooms from the arraylist possibleRooms depending of whether there is a room with a door below
	private ArrayList<Integer> filterRoomsWithBottomDoors(ArrayList<Integer> possibleRooms, int currentRow, int currentColumn)
	{
		//check for a room with a door facing up, below 
		if(CheckForDoorBottom(currentRow, currentColumn))
		{
			//remove rooms without doors facing down
			for(int i = 0; i < possibleRooms.size(); i++)
			{
				if(!Arrays.asList(BOTTOM_DOOR_IDS).contains(possibleRooms.get(i)))
				{
					possibleRooms.remove(i);
					i--;
				}
			}
		}
		//check for a room without a door facing up, below 
		else if(CheckForRoomBottom(currentRow, currentColumn))
		{
			//remove rooms with doors facing down
			for(int i = 0; i < possibleRooms.size(); i++)
			{
				if(Arrays.asList(BOTTOM_DOOR_IDS).contains(possibleRooms.get(i)))
				{
					possibleRooms.remove(i);
					i--;
				}
			} 
		}
		
		return possibleRooms;
	}

	//remove rooms from the arraylist possibleRooms depending of whether there is a room with a door to the left
	private ArrayList<Integer> filterRoomsWithLeftDoors(ArrayList<Integer> possibleRooms, int currentRow, int currentColumn)
	{
		//check for a room with a door facing right, to the left
		if(CheckForDoorLeft(currentRow, currentColumn))
		{
			//remove rooms without doors facing left
			for(int i = 0; i < possibleRooms.size(); i++)
			{
				if(!Arrays.asList(LEFT_DOOR_IDS).contains(possibleRooms.get(i)))
				{
					possibleRooms.remove(i);
					i--;
				}
			}
		}
		//check for a room without a door facing right, to the left
		else if(CheckForRoomLeft(currentRow, currentColumn))
		{
			//remove rooms with doors facing left
			for(int i = 0; i < possibleRooms.size(); i++)
			{
				if(Arrays.asList(LEFT_DOOR_IDS).contains(possibleRooms.get(i)))
				{
					possibleRooms.remove(i);
					i--;
				}
			} 
		}
		
		return possibleRooms;
	}

	//remove rooms from the arraylist possibleRooms depending of whether there is a room with a door to the right
	private ArrayList<Integer> filterRoomsWithRightDoors(ArrayList<Integer> possibleRooms, int currentRow, int currentColumn)
	{
		//check for a room with a door facing left, to the right
		if(CheckForDoorRight(currentRow, currentColumn))
		{
			//remove rooms without doors facing right
			for(int i = 0; i < possibleRooms.size(); i++)
			{
				if(!Arrays.asList(RIGHT_DOOR_IDS).contains(possibleRooms.get(i)))
				{
					possibleRooms.remove(i);
					i--;
				}
			}
		}
		//check for a room without a door facing left, to the right
		else if(CheckForRoomRight(currentRow, currentColumn + 1))
		{
			//remove rooms with doors facing right
			for(int i = 0; i < possibleRooms.size(); i++)
			{
				if(Arrays.asList(RIGHT_DOOR_IDS).contains(possibleRooms.get(i)))
				{
					possibleRooms.remove(i);
					i--;
				}
			} 
		}
		
		return possibleRooms;
	}
	
	//creates a new row at the top of the map
	private void addRowTop()
	{
		//add a new row
		map.add(0, new ArrayList<String>());
		
		//corrects the starting position
		StartRow++;
		
		//fills in new row with the correct columns
		for(int i = 0; i < map.get(1).size(); i++)
		{
			map.get(0).add(new String());
		}
	}
	
	// creates a new row at the bottom of the map
	private void addRowBottom()
	{
		//add a new row
		map.add(new ArrayList<String>());

		//fills in new row with the correct columns
		for(int i = 0; i < map.get(map.size() - 2).size(); i++)
		{
			map.get(map.size() - 1).add(new String());
		}
	}

	// creates a new column on the left side of the map
	private void addColumnLeft()
	{
		//add a new column to each row
		for(int i = 0; i < map.size(); i++)
		{
			map.get(i).add(0, new String());
		}
		
		//corrects the starting position
		StartColumn++;
	}

	// creates a new column on the right side of the map
	private void addColumnRight()
	{
		//add a new column to each row
		for(int i = 0; i < map.size(); i++)
		{
			map.get(i).add(new String());
		}
	}
	
	//check for a room on top of the coordinates
	private boolean CheckForRoomTop(int relativeRow, int relativeColumn)
	{
		//if there is no row above the room return false
		if(GetAbsoluteRow(relativeRow) <= 0)
		{
			return false;
		}
		else
		{
			//return whether or not the room above is empty
			return (!map.get(GetAbsoluteRow(relativeRow + 1)).get(GetAbsoluteColumn(relativeColumn)).equals(""));
		}
	}

	//check for a room on below the coordinates
	private boolean CheckForRoomBottom(int relativeRow, int relativeColumn)
	{
		//if there is no row below the room return false
		if(GetAbsoluteRow(relativeRow) >= map.size() - 1)
		{
			return false;
		}
		else
		{
			//return whether or not the room below is empty
			return (!map.get(GetAbsoluteRow(relativeRow - 1)).get(GetAbsoluteColumn(relativeColumn)).equals(""));
		}
	}

	//check for a room to the left of the coordinates
	private boolean CheckForRoomLeft(int relativeRow, int relativeColumn)
	{
		//if there is no column to the left of the room return false
		if(GetAbsoluteColumn(relativeColumn) <= 0)
		{
			return false;
		}
		else
		{
			//return whether or not the room to the left is empty
			return (!map.get(GetAbsoluteRow(relativeRow)).get(GetAbsoluteColumn(relativeColumn - 1)).equals(""));
		}
	}

	//check for a room to the right of the coordinates
	private boolean CheckForRoomRight(int relativeRow, int relativeColumn)
	{
		//if there is no column to the right of the room return false
		if(GetAbsoluteColumn(relativeColumn) >= map.get(GetAbsoluteRow(relativeRow)).size() - 1)
		{
			return false;
		}
		else
		{
			//return whether or not the room to the right is empty
			return (!map.get(GetAbsoluteRow(relativeRow)).get(GetAbsoluteColumn(relativeColumn + 1)).equals(""));
		}
	}
	
	//check for a room with a door facing down above the coordinates
	private boolean CheckForDoorTop(int relativeRow, int relativeColumn)
	{
		//check for a room above
		if(CheckForRoomTop(relativeRow, relativeColumn))
		{
			//get the room above's value
			String topRoom = map.get(GetAbsoluteRow(relativeRow + 1)).get(GetAbsoluteColumn(relativeColumn));
			
			//get the type of room that it is
			int topRoomType = Integer.parseInt(topRoom.split(" ")[0]);
			
			//return whether or not it has a door on the bottom
			return Arrays.asList(BOTTOM_DOOR_IDS).contains(topRoomType);
		}
		return false;
	}

	//check for a room with a door facing up below the coordinates
	private boolean CheckForDoorBottom(int relativeRow, int relativeColumn)
	{
		//check for a room below
		if(CheckForRoomBottom(relativeRow, relativeColumn))
		{
			//get the room below's value
			String bottomRoom = map.get(GetAbsoluteRow(relativeRow - 1)).get(GetAbsoluteColumn(relativeColumn));

			//get the type of room that it is
			int bottomRoomType = Integer.parseInt(bottomRoom.split(" ")[0]);

			//return whether or not it has a door on the top
			return Arrays.asList(TOP_DOOR_IDS).contains(bottomRoomType);
		}
		return false;
	}

	//check for a room with a door facing right to the left of the coordinates
	private boolean CheckForDoorLeft(int relativeRow, int relativeColumn)
	{
		//check for a room to the left
		if(CheckForRoomLeft(relativeRow, relativeColumn))
		{
			//get the room to the left's value
			String leftRoom = map.get(GetAbsoluteRow(relativeRow)).get(GetAbsoluteColumn(relativeColumn - 1));

			//get the type of room that it is
			int leftRoomType = Integer.parseInt(leftRoom.split(" ")[0]);

			//return whether or not it has a door on the right
			return Arrays.asList(RIGHT_DOOR_IDS).contains(leftRoomType);
		}
		return false;
	}

	//check for a room with a door facing left to the right of the coordinates
	private boolean CheckForDoorRight(int relativeRow, int relativeColumn)
	{
		//check for a room to the right
		if(CheckForRoomRight(relativeRow, relativeColumn))
		{
			//get the room to the right's value
			String rightRoom = map.get(GetAbsoluteRow(relativeRow)).get(GetAbsoluteColumn(relativeColumn + 1));

			//get the type of room that it is
			int rightRoomType = Integer.parseInt(rightRoom.split(" ")[0]);

			//return whether or not it has a door on the left
			return Arrays.asList(LEFT_DOOR_IDS).contains(rightRoomType);
		}
		return false;
	}
	
	//returns absolute coordinates on the map from coordinate relative to the starting point
	private int GetAbsoluteRow(int relativeRow)
	{
		return StartRow - relativeRow;
	}

	//returns absolute coordinates on the map from coordinate relative to the starting point
	private int GetAbsoluteColumn(int relativeColumn)
	{
		return StartColumn + relativeColumn;
	}
}

