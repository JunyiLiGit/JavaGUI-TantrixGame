/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package tantrixgame;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
*
* @author kaitlyn
*/
public final class Tantrix2DArrayModel extends TantrixAbstractModel {

	//
	//                 5(red)
	//              / ----- \
	//   (blue) 4  /         \  0(yellow)
	//            /           \
	//            \           /
	//   (blue) 3  \         /  1(yellow)
	//              \-------/
	//                 2 (red)
	//this 2D array stores the colored line indexes(from 0-5) based on the hexagon edges.
	//The 56 pieces have 14 different patterns, I store the 14 patterns into a 2D array, each row of this 2D array represent a kind of pattern
	//for example, if the user choose Red, Yellow, Blue as the desired color, the first row means 
	// Yellow line: (1,0), Red line : (5,3), Blue line: (4,2) 
	//once I construct this relationship, if the user choose different color, I can just map the three colors, the 14 pattern stay the same
	//this array will not be changed during the game life time
	private final Pair[][] coloredLineIndexes = new Pair[][]{
		{new Pair(1, 0), new Pair(5, 3), new Pair(4, 2)},
		{new Pair(1, 0), new Pair(4, 3), new Pair(5, 2)},
		{new Pair(5, 4), new Pair(1, 0), new Pair(3, 2)},
		{new Pair(4, 0), new Pair(3, 1), new Pair(5, 2)},
		{new Pair(1, 0), new Pair(5, 2), new Pair(4, 3)},
		{new Pair(5, 2), new Pair(4, 0), new Pair(3, 1)},
		{new Pair(4, 2), new Pair(5, 3), new Pair(1, 0)},
		{new Pair(5, 3), new Pair(4, 2), new Pair(1, 0)},
		{new Pair(4, 0), new Pair(5, 2), new Pair(3, 1)},
		{new Pair(1, 0), new Pair(4, 2), new Pair(5, 3)},
		{new Pair(5, 3), new Pair(1, 0), new Pair(4, 2)},
		{new Pair(4, 2), new Pair(1, 0), new Pair(5, 3)},
		{new Pair(5, 2), new Pair(4, 3), new Pair(1, 0)},
		{new Pair(1, 0), new Pair(5, 4), new Pair(3, 2)}
	};

	//a copy of the original coloredLineIndexes array, this array will be change when the user play the game
	//when the user reset the game, recopy the original coloredLineIndexes to this array 
	private final Pair[][] colorEdgeIndexArray = new Pair[14][3];

	//store user selected 3 colors 
	private Color[] colorArray;

	//one  poosible loop color for each level, there are 8 level total
	private Color[] loopColor;

	//map the RGB value to RED,GREEN,BLUE,YELLOW strings
	private final Map<Color, String> coloToStringMap = new HashMap<>();

	//current level, start from the first level
	int currentLevel = 1;

	//userSelectedLoopColor: the loop color that the user currently formed, the user can click the hint button to get the possible color for the current level,
	//the user can also form a loop which is different from the hint color. 
	Color userSelectedLoopColor = Color.BLACK;

	//store the shuffle piece indexes 
	ArrayList<Integer> randomPieceIndexList = new ArrayList<>();

	//store possible loops into this map---for discovery game 
	//key: color
	//value: one possible loop or line for this color 
	HashMap<Color, ArrayList<Pair>> possibleLoopForDiscovery = new HashMap();

	//store all the possible loops or lines into this map---for solitaire game
	//key: color
	//value: all possible loops and lines that have this color
	HashMap<Color, ArrayList<ArrayList<Pair>>> possiblePathForSolitaire = new HashMap();

	Tantrix2DArrayModel() {
		//make a deep copy, use coloredLineIndexes to initialize colorEdgeIndexArray. 
		deepCopy();
		this.colorArray = new Color[3];
		//create the colorToString map
		coloToStringMap.put(Color.RED, "RED");
		coloToStringMap.put(Color.BLUE, "BLUE");
		coloToStringMap.put(Color.GREEN, "GREEN");
		coloToStringMap.put(Color.YELLOW, "YELLOW");
	}

	public void deepCopy() {
		for (int i = 0; i < coloredLineIndexes.length; ++i) {
			for (int j = 0; j < 3; ++j) {
				Pair p = new Pair(coloredLineIndexes[i][j].getFirst(), coloredLineIndexes[i][j].getSecond());
				colorEdgeIndexArray[i][j] = p;
			}
		}
	}

	//set the current level, when the user press "play" or "continue" button, the controller set game level
	@Override
	public void setCurrentLevel(int level) {
		this.currentLevel = level;
	}

	//set color for model, when the user choose the desired color, the controller call this function to set the tantrix piece colors and the 
	//possible loop colors which will be used for "hint"
	@Override
	public void setColor(Color[] color, Color[] loopColor) {
		//set the user selected color array
		this.colorArray = color;
		//set the one possible loop color of each level
		this.loopColor = loopColor;
	}

	//this function check whether every two touched edge have the same color, if they have, store them into possibleLoop hashMap
	@Override
	public void createPossiblePathForDiscovery(ArrayList<Point2D.Double> locations, int length) {
		//every level in the discovery game is not related, so clear the possibleLoopForDiscovery map first
		possibleLoopForDiscovery.clear();
		//after the controller get the locations information(the center locations of the piece), pass the locations into this function, 
		//caculate distance and angle between each two pieces
		for (int i = 0; i < locations.size(); ++i) {
			for (int j = i + 1; j < locations.size(); ++j) {
				//currently my snap to grid does not work well, so I give the player 5 unit flexibility, if the pieces are put "close enough"
				//I will treat them touched
				if (Math.abs(locations.get(i).distance(locations.get(j)) - length * Math.sqrt(3)) < 2) {

					double xDiff = locations.get(j).x - locations.get(i).x;
					double yDiff = locations.get(j).y - locations.get(i).y;
					double degree = Math.toDegrees(Math.atan2(yDiff, xDiff));
					Color colorKey = Color.BLACK;
					//based on the angle that the two center created, decide which two edges are touched
					if (Math.abs(degree + 30) <= 3) {
						//check whether the two touched edges have the same color, if they have, assignment the color to
						//colorKey, if not the colorKey will be black
						colorKey = checkForSameColor(i, 0, j, 3);
					}
					if (Math.abs(degree - 30) <= 3) {
						colorKey = checkForSameColor(i, 1, j, 4);
					}
					if (Math.abs(degree + 90) <= 3) {
						colorKey = checkForSameColor(i, 5, j, 2);
					}
					if (Math.abs(degree - 90) <= 3) {
						colorKey = checkForSameColor(i, 2, j, 5);
					}
					if (Math.abs(degree + 150) <= 3) {
						colorKey = checkForSameColor(i, 4, j, 1);
					}
					if (Math.abs(degree - 150) <= 3) {
						colorKey = checkForSameColor(i, 3, j, 0);
					}
					//after the color check, if the two edge have same color, add the corresponding tantrix piece index into the possibleLoopForDiscovery map
					if (colorKey != Color.BLACK) {
						//if the map already have this color entry, add the edge into that entry
						if (possibleLoopForDiscovery.containsKey(colorKey)) {
							possibleLoopForDiscovery.get(colorKey).add(new Pair(i, j));
						} else {
							//if the map does not have that color entry, create an entry and add that entry into the map
							ArrayList<Pair> tmp = new ArrayList<>();
							tmp.add(new Pair(i, j));
							possibleLoopForDiscovery.put(colorKey, tmp);
						}
					}
				}
			}
		}
	}

	//check whether the two touched edges have the same color, if they have the same color return that color otherwise return black
	public Color checkForSameColor(int pieceIndex1, int edgeIndex1, int pieceIndex2, int edgeIndex2) {
		//initialize these two edge color using different color
		Color edge1Color = Color.BLACK;
		Color edge2Color = Color.RED;
		for (int i = 0; i < 3; ++i) {
			if ((int) colorEdgeIndexArray[pieceIndex1][i].getFirst() == edgeIndex1 || (int) colorEdgeIndexArray[pieceIndex1][i].getSecond() == edgeIndex1) {
				edge1Color = colorArray[i];
			}
			if ((int) colorEdgeIndexArray[pieceIndex2][i].getFirst() == edgeIndex2 || (int) colorEdgeIndexArray[pieceIndex2][i].getSecond() == edgeIndex2) {
				edge2Color = colorArray[i];
			}
		}
		if (edge1Color == edge2Color) {
			return edge1Color;

		} else {
			return Color.BLACK;
		}
	}

	// check whether the user formed a colored loop------for discovery game
	@Override
	public Boolean checkLoopForDiscovery() {
		//for each color entry in the map, find the color entry that the corresponding value have length currentLevel+2
		//the currentLevel start from 1, currentLevel+2 is the pieces number that the user already have
		// for discovery game, if the user form a loop, the number of colored line (value.size())that form the loop equals to the number of total pieces(currentLevel)
		ArrayList<Pair> sameColorPieceIndex = new ArrayList<>();
		for (Map.Entry<Color, ArrayList<Pair>> entry : possibleLoopForDiscovery.entrySet()) {
			Color key = entry.getKey();
			ArrayList<Pair> value = entry.getValue();
			//store the piece Indexs into sameColorPieceIndex arrayList(the two tantrix piece are touched and the touched edge color is the same)
			if (value.size() == (currentLevel + 2)) {
				userSelectedLoopColor = key;
				sameColorPieceIndex = value;
			}
		}

		//check whether the colored lines in each piece form a loop
		//return true if they form a loop, otherwise return false
		return sameColorPieceIndex.size() > 0 && findLoophelper(sameColorPieceIndex);
	}

	//check whether a the colored lines inside the pieces form a loop
	// for example: (1,2)(2,5)(5,3)(3,1) will form a loop
	public boolean findLoophelper(ArrayList<Pair> l) {
		//keep track of whether the edge is visited or not
		Boolean[] visited = new Boolean[l.size()];
		Arrays.fill(visited, Boolean.FALSE);
		//the first node of the first edge
		int start = (int) l.get(0).getFirst();
		//the second node of the edge
		int next = (int) l.get(0).getSecond();
        
		//store the nodes which has been visited, use this arrayList as a stack
		ArrayList<Integer> tmp = new ArrayList<>();
		tmp.add(start);
		tmp.add(next);
		visited[0] = true;

		for (int j = 1; j < l.size(); ++j) {
			for (int i = 1; i < l.size(); ++i) {
				if (!visited[i]) {
					if ((int) l.get(i).getFirst() == next) {
						int nextElement = (int) l.get(i).getSecond();
						next = nextElement;
						tmp.add(nextElement);
						visited[i] = true;
						break;
					}
					if ((int) l.get(i).getSecond() == next) {
						int nextElement = (int) l.get(i).getFirst();

						next = nextElement;
						tmp.add(nextElement);
						visited[i] = true;
						break;
					}
				}
			}
		}
		//if the bottom node of the stack equals to the last visited node, it has a loop in this graph
		return Objects.equals(tmp.get(0), tmp.get(tmp.size() - 1));
	}

	//similiar to function createPossiblePathForDiscovery(...), the difference is : the discovery game have to update the possibleLoopForDiscovery map
	//every time when the player enter a new level. But for the solitaire game, the user can not move or rotate the previous pieces once the user enter the next level,
	// so every time when the user enter the next level, only update the information that is related to that new piece
	// another difference is the solitaire game need to caculate both the longest line and longest loop
	//so I need to keep track of all touched pieces during the game
	//I used a arrayList in arrayList to represent different path
	@Override
	public void createPossiblePathForSolitaire(int length, ArrayList<Point2D.Double> pieceLocations) {

		// get the current piece location
		Point2D.Double currentPieceLocation = pieceLocations.get(pieceLocations.size() - 1);

		//caculate the distance between the current piece and the other pieces, based on the angle between these two pieces
		//decide which edge is touched
		for (int i = 0; i < pieceLocations.size() - 1; ++i) {
			if (Math.abs(currentPieceLocation.distance(pieceLocations.get(i)) - length * Math.sqrt(3)) < 5) {

				int oldPiece = randomPieceIndexList.get(i);
				int newPiece = randomPieceIndexList.get(pieceLocations.size() - 1);

				double xDiff = currentPieceLocation.x - pieceLocations.get(i).x;
				double yDiff = currentPieceLocation.y - pieceLocations.get(i).y;

				//based on the angle that the two center created, decide which two edge are touched
				Color colorKey = Color.BLACK;

				double degree = Math.toDegrees(Math.atan2(yDiff, xDiff));
				if (Math.abs(degree + 30) <= 5) {

					colorKey = checkForSameColor(oldPiece, 0, newPiece, 3);
				}
				if (Math.abs(degree - 30) <= 5) {

					colorKey = checkForSameColor(oldPiece, 1, newPiece, 4);
				}
				if (Math.abs(degree + 90) <= 5) {

					colorKey = checkForSameColor(oldPiece, 5, newPiece, 2);
				}
				if (Math.abs(degree - 90) <= 5) {

					colorKey = checkForSameColor(oldPiece, 2, newPiece, 5);
				}
				if (Math.abs(degree + 150) <= 5) {

					colorKey = checkForSameColor(oldPiece, 4, newPiece, 1);
				}
				if (Math.abs(degree - 150) <= 5) {

					colorKey = checkForSameColor(oldPiece, 3, newPiece, 0);
				}

				if (colorKey != Color.BLACK) {
					boolean find = false;
					if (possiblePathForSolitaire.containsKey(colorKey)) {

						for (int k = 0; k < possiblePathForSolitaire.get(colorKey).size(); ++k) {
							//if the  the two piece indexs are added to the coresponding color entry, end the loop
							if (find == true) {
								break;

							} else {
								for (int j = 0; j < possiblePathForSolitaire.get(colorKey).get(k).size(); ++j) {
									int firstPieceIndex = (int) possiblePathForSolitaire.get(colorKey).get(k).get(j).getFirst();
									int secondPieceIndex = (int) possiblePathForSolitaire.get(colorKey).get(k).get(j).getSecond();
									// If the current touched two piece already have a entry in the map, add them into that entry
									if (firstPieceIndex == oldPiece || secondPieceIndex == oldPiece
									|| firstPieceIndex == newPiece || secondPieceIndex == newPiece) {
										possiblePathForSolitaire.get(colorKey).get(k).add(new Pair(oldPiece, newPiece));
										//if the current piece connect two paths which is not connected before, call the connectLinks function
										connectLinks(colorKey, newPiece, oldPiece, k);
										find = true;
										break;
									}
								}
							}
						}
						if (!find) {
							//if the two connected two piece are not connected to the other path which have the same color in the map, create a new path
							//and add them into that color entry
							ArrayList<Pair> newList = new ArrayList<>();
							newList.add(new Pair(oldPiece, newPiece));
							possiblePathForSolitaire.get(colorKey).add(newList);
						}
					} else {
						//if the color entry is not exist in the map, create the entry
						ArrayList<ArrayList<Pair>> sameColorArrayList = new ArrayList<>();
						ArrayList<Pair> tmp = new ArrayList<>();
						tmp.add(new Pair(oldPiece, newPiece));
						sameColorArrayList.add(tmp);
						possiblePathForSolitaire.put(colorKey, sameColorArrayList);
					}
				}
			}
		}
	}

	//if the current piece connect to two same color pieces, link these two path together
	public void connectLinks(Color c, int currentPieceIndex, int currentPieceNeighbor, int index) {
		boolean find = false;
		for (int i = 0; i < possiblePathForSolitaire.get(c).size(); ++i) {
			if (find) {
				break;
			}
			if (i != index) {
				for (int j = 0; j < possiblePathForSolitaire.get(c).get(i).size(); ++j) {
					if ((int) possiblePathForSolitaire.get(c).get(i).get(j).getFirst() == currentPieceIndex || (int) possiblePathForSolitaire.get(c).get(i).get(j).getSecond() == currentPieceIndex
					|| (int) possiblePathForSolitaire.get(c).get(i).get(j).getFirst() == currentPieceNeighbor || (int) possiblePathForSolitaire.get(c).get(i).get(j).getSecond() == currentPieceNeighbor) {
						possiblePathForSolitaire.get(c).get(i).addAll(possiblePathForSolitaire.get(c).get(index));
						possiblePathForSolitaire.get(c).get(index).clear();
						find = true;
						break;
					}
				}
			}
		}
	}

	//return the colored line edge indexes for each tantrix piece 
	//the controller will call this function to get this information and call the view to draw the piece on the screen
	@Override
	public Pair[] getEdgeIndexesForCurrentPiece(int pieceIndex) {
		return colorEdgeIndexArray[pieceIndex];

	}

	//when the user rotate the piece, update the model---colorEdgeIndexArray
	//
	//                 5(red)                                      5(blue)
	//              /-------\                                    /-------\
	//   (blue) 4  /         \  0(yellow)   rotate      (blue)4 /         \ 0(red)
	//            /           \             ------->           /           \
	//            \           /             60 degree          \           /
	//   (blue) 3  \         /  1(yellow)               (red)3  \         / 1(yellow)
	//              \-------/                                    \-------/
	//                 2 (red)                                    2(yellow)
	//  Yellow (1,0) --> Yellow (2,1)
	//  Red    (5,2) --> Red    (0,3)
	//  Blue   (4,3) --> Blue   (5,2)
	@Override
	public void colorUpdateAfterRotate(int pieceIndex) {
		for (int i = 0; i < 3; ++i) {
			if ((int) colorEdgeIndexArray[pieceIndex][i].getFirst() == 5) {
				colorEdgeIndexArray[pieceIndex][i].setFirst(0);
			} else {
				int newValue = (int) colorEdgeIndexArray[pieceIndex][i].getFirst() + 1;
				colorEdgeIndexArray[pieceIndex][i].setFirst(newValue);
			}
			if ((int) colorEdgeIndexArray[pieceIndex][i].getSecond() == 5) {
				colorEdgeIndexArray[pieceIndex][i].setSecond(0);
			} else {
				int newValue = (int) colorEdgeIndexArray[pieceIndex][i].getSecond() + 1;
				colorEdgeIndexArray[pieceIndex][i].setSecond(newValue);
			}
		}
	}

	//based on the status of the game, when the user click buttons, the controller get the coresponding message from the model and call the to show the message to the player
	@Override
	public String getMessage(String inforType) {
		switch (inforType) {
			//instruction message
			case "InstructionsForDiscovery":
			String instruction1 = 
				"1. Press play to start the game, you will get three pieces in the first level" + "\n" + "   and one extra piece for the following levels" + "\n"
			+ "2. Press submit to show the result(success or fail)" + "\n"
			+ "3. Press continue to enter next level, but submit your solution first"
			+ "4. Click the piece to rotate and drag the mouse to move the piece" + "\n"
			+ "5. If and only if you finish the current level you can enter the next level" + "\n"
			+ "6. In each level, you goal is to form a loop which has the same color," + "\n" + "    a piece can be rotated, but not flilpped, pieces which touch by an edge must have matching colors" + "\n"
			+ "7. You can press hint to show the possible color loop you can form, you can also ignore th hint" + "\n"
			+ "8. You are not allow to go back and forth during the game. Press reset to start the game again"
			+ "9. The discovery game has 8 levels";
			return instruction1;

			case "InstructionsForSolitaire":
			String instruction2 = 
				"1. Press play to start the game, you will get one piece in each level," + "\n" + "    the pieces are randomly shuffled, in order to get extra piece you have to submit the result first!" + "\n"
			+ "2. Press submit button to show the result(success or fail)" + "\n"
			+ "3. Press continue to enter next level, but submit your solution first"
			+ "4. Click the piece to rotate and drag the mouse to move the piece" + "\n"
			+ "5. Once you press submit button, you can not move or rotate the previous pieces" + "\n"
			+ "6. In each level, you goal is to form a same color longest loop or longest line. " + "\n" + "    A piece can be rotated, but not flilpped, pieces which touch by an edge must have matching colors" + "\n"
			+ "7. You will receive 1 point for each piece in the longested line or 2 points in the longest loop"+"\n"
			+ "8. The scores are accumulated and posted by the computer, maximum score is 28.";
			return instruction2;
			//hint message, tell player a possible loop that the player may be able to form
			case "Hint":
			Color hintColor = loopColor[currentLevel - 1];

			String hint = "It's possible to form a " + coloToStringMap.get(hintColor) + " loop!";
			return hint;
			// when the player complete the current level, show this message
			case "Success":
			String resultColor = coloToStringMap.get(userSelectedLoopColor);
			String successResult = "Congratulations! " + "\n" + resultColor + " LOOP! " + "\n" + "Press Continue to enter the " + (currentLevel + 1) + " level";
			return successResult;
			// when the player complete all the levels(8 level), show this message
			case "Complete":
			String complete = "Congratulations! You have finished the game." + "\n" + "Press reset to reset it again";
			return complete;
			// when the player can not form a loop
			case "Fail":
			String failResult = "Sorry! You did not finish the current level...";
			return failResult;
			// something else happened
			default:
			return "Please press sumbit!";
		}
	}

	//reset the model
	@Override
	public void resetModel() {
		currentLevel = 1;
		deepCopy();
		randomPieceIndexList.clear();
		possibleLoopForDiscovery.clear();
		possiblePathForSolitaire.clear();
	}

	//shuffle piece index 
	@Override
	public void shufflePieceIndex() {
		for (int i = 0; i < 14; i++) {
			randomPieceIndexList.add(i);
		}
		Collections.shuffle(randomPieceIndexList);
	}

	// get the random piece index for current level
	@Override
	public int getRandomPieceIndex(int i) {
		return randomPieceIndexList.get(i);
	}

	//caculate score for solitaire game
	@Override
	public int caculateScore(int currentPieceIndex) {
		int score = 0;
		for (Map.Entry<Color, ArrayList<ArrayList<Pair>>> entry : possiblePathForSolitaire.entrySet()) {
			Color key = entry.getKey();
			for (int i = 0; i < possiblePathForSolitaire.get(key).size(); ++i) {
				if (possiblePathForSolitaire.get(key).get(i).size() >= 3 && findLoophelper(possiblePathForSolitaire.get(key).get(i))) {

					int s = 2 * possiblePathForSolitaire.get(key).get(i).size();
					if (s > score) {
						score = s - 1;
					}
				} else if (possiblePathForSolitaire.get(key).get(i).size() > score) {
					score = possiblePathForSolitaire.get(key).get(i).size();
				}

			}
		}
		return score + 1;
	}
}
