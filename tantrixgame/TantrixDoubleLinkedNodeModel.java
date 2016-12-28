/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package tantrixgame;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
*
* @author kaitlyn
*/
public class TantrixDoubleLinkedNodeModel extends TantrixAbstractModel {


	// 
	//                                                  B
	//    
	//                                            /-----5------\
	//                                           /              \
	//                                          /4            0  \
	//                      A                  /                  \  
	//                                        /                    \ 
	//                 /-----5-----\          \                    / 
	//                /4 (Y)   (Y) 0\          \3 (Y)        (Y) 1/
	//    <---next-- @<----pre----->@<---next-->@ <----pre------>@---next-->
	//              /                 \          \              /    
	//             /                   \          \------2-----/
	//             \                   /
	//              \                 /
	//               \ 3            1/
	//                \             /
	//                 \-----2-----/      
	//  
       
	//Tantrix piece class, every piece has six edges, using colored nodes to represent these six edges
	//each node has: two pointers-- previous and next; index, and color 
	//the previous pointer points to the node that has the same color inside the piece
	//the next pointer points to the node which is belong to the other piece, if and only if the two pieces are touched and the touched
	//edge have the same color
	//for example: piece A---node 0,4 are yellow colors, set the "previous" pointer of these two node to each other
	//Assuming piece A (edge 0)and piece B(edge 3) are touched, and they have the same color(yellow),
	//set the "next" pointer of node 0 and node 3 to each other
    
	public class TantrixSinglePiece {

		// store the six node into an array, initialize the previous and next pointer to be null, assign the index for each node
		private final Node[] nodesArray = {new Node(null, null, null, 0), new Node(null, null, null, 1), new Node(null, null, null, 2),
			new Node(null, null, null, 3), new Node(null, null, null, 4), new Node(null, null, null, 5)};

			private final int pieceIndex;
			//constructor for the piece
			TantrixSinglePiece(int index, Color[] c) {
				this.pieceIndex = index;
				for (int i = 0; i < nodesArray.length; ++i) {
					nodesArray[i].setColor(c[i]);

				}
			}
			//if the user rotate the piece(60 degre), shift the nodes inside nodesArray to right 
			public void shiftRight() {
				Node last = nodesArray[nodesArray.length - 1];
				if (nodesArray.length > 1) {
					for (int index = nodesArray.length - 2; index >= 0; index--) {
						nodesArray[index + 1] = nodesArray[index];
					}
				}
				nodesArray[0] = last;
				for (int i = 0; i < 6; i++) {
					nodesArray[i].setIndex(i);
				}
			}
			// give the node index, get node color 
			public Color getColor(int nodeIndex) {
				return nodesArray[nodeIndex].getColor();
			}
			//give the node index, get the node
			public Node getNode(int index) {
				return nodesArray[index];
			}
		}
		//this array store the 14 tantrix pieces
		private final TantrixSinglePiece[] pieceArray;
		//the default piece color
		private Color[] pieceColor = {Color.GREEN, Color.RED, Color.BLUE};
		//the possible loop color of each level for discovery game
		private Color[] loopColor;
		//the color for the longest loop that the player formed during the game
		private Color userSelectedLoopColor = Color.BLACK;
		// the color for the longest line
		private Color userSelectedLineColor = Color.BLACK;
		//color to string map, make it easier for the player to read color information 
		private final Map<Color, String> coloToStringMap = new HashMap<>();
		//level start from 1
		int currentLevel = 1;
		//the node color for each piece
		private final Integer[][] nodeColorMap = {
			{0, 0, 2, 1, 2, 1},
			{0, 0, 2, 1, 1, 2},
			{1, 1, 2, 2, 0, 0},
			{0, 1, 2, 1, 0, 2},
			{0, 0, 1, 2, 2, 1},
			{1, 2, 0, 2, 1, 0},
			{2, 2, 0, 1, 0, 1},
			{2, 2, 1, 0, 1, 0},
			{0, 2, 1, 2, 0, 1},
			{0, 0, 1, 2, 1, 2},
			{1, 1, 2, 0, 2, 0},
			{1, 1, 0, 2, 0, 2},
			{2, 2, 0, 1, 1, 0},
			{0, 0, 2, 2, 1, 1}
		};
		ArrayList<Integer> randomPieceIndexList = new ArrayList<>();
		int longestLine = 0;
		int longestLoop = 0;
		TantrixDoubleLinkedNodeModel() {
			pieceArray = new TantrixSinglePiece[14];
			//initulize colors based on the user selected color
			//colorToString mapping
			coloToStringMap.put(Color.RED, "RED");
			coloToStringMap.put(Color.BLUE, "BLUE");
			coloToStringMap.put(Color.GREEN, "GREEN");
			coloToStringMap.put(Color.YELLOW, "YELLOW");
		}

		public void initPieceArray(){
			for (int i = 0; i < 14; ++i) {
				Color[] tmp = {
					pieceColor[nodeColorMap[i][0]], pieceColor[nodeColorMap[i][1]],
					pieceColor[nodeColorMap[i][2]], pieceColor[nodeColorMap[i][3]],
					pieceColor[nodeColorMap[i][4]], pieceColor[nodeColorMap[i][5]]
				};
				//create a tantrix piece
				TantrixSinglePiece p = new TantrixSinglePiece(i, tmp);

				//initialize the previous pointer for the six nodes
				for (int j = 0; j < 6; ++j) {
					int n = nodeColorMap[i][j];
					for (int k = j + 1; k < 6; ++k) {
						if (nodeColorMap[i][k] == n) {
							p.getNode(j).setPrevious(p.getNode(k));
							p.getNode(k).setPrevious(p.getNode(j));
						}
					}
				}
				//store this tantrix piece into the array
				pieceArray[i] = p;
			}
		}

		//set pieceColor array and possible loop color for each level
		@Override
		void setColor(Color[] color, Color[] loopColor) {
			this.pieceColor = color;
			this.loopColor = loopColor;
			initPieceArray();
		}

		//set current Level
		@Override
		void setCurrentLevel(int level) {
			this.currentLevel = level;
		}

		//set message
		@Override
		public String getMessage(String inforType) {
			switch (inforType) {
				//instruction message
				case "InstructionsForDiscovery":
				String instruction1 = "1. choice which game you want to play: Discovery or Solitaire" + "\n"
				+ "2. Choice three colors that you want to use " + "\n"
				+ "3. Press play button to start the game, you will get three pieces in the first level" + "\n" + "   and one piece each time for the following level" + "\n"
				+ "4. Press submit button to show the result(success or fail)" + "\n"
				+ "5. Click the piece to rotate and drag the mouse to move the piece" + "\n"
				+ "6. If and only if you finish the current level you can press continue to play the next level" + "\n"
				+ "7. In each level, you goal is to form a loop which has the same color," + "\n" + "    a piece can be rotated, but not flilpped, pieces which touch by an edge must have matching colors" + "\n"
				+ "8. You can choice the color for the loop you want to form in the current level," + "\n" + "    you can also press hint button to show you the possible color loop you can form" + "\n"
				+ "9. The discovery game has 8 level";
				return instruction1;

				case "InstructionsForSolitaire":
				String instruction2 = "1. choice which game you want to play: Discovery or Solitaire" + "\n"
				+ "2. Choice three colors that you want to use " + "\n"
				+ "3. Press play or continue button to start the game, you will get one piece each time," + "\n" + "    the pieces are randomly shuffled, in order to get extra piece you have to submit the result first!" + "\n"
				+ "4. Press submit button to show the result(success or fail)" + "\n"
				+ "5. Click the piece to rotate and drag the mouse to move the piece" + "\n"
				+ "6. Once you press submit button, you can not move or rotate the previous pieces" + "\n"
				+ "7. In each level, you goal is to form a longest loop or longest line. " + "\n" + "   loop which has the same color, a piece can be rotated, but not flilpped, pieces which touch by an edge must have matching colors" + "\n"
				+ "8. You will receive 1 point in the longested line or 2 points for each loop, maximum score is 28." + "\n"
				+ "9. The scores are accumulated and posted by the computer";
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
				String complete = "Congratulations! You have finished the game." + "\n" + "Press reset to play it again";
				return complete;
				// when the player can not form a loop
				case "Fail":
				String failResult = "Sorry! You did not finish the current level...";
				return failResult;
				// something else happened
				default:
				return "Please press submit!";
			}
		}

		//shift the node after the user rotate the piece
		@Override
		public void colorUpdateAfterRotate(int pieceIndex) {
			pieceArray[pieceIndex].shiftRight();
		}
		//return the colored line edge indexes 
		@Override
		public Pair[] getEdgeIndexesForCurrentPiece(int pieceIndex) {
			ArrayList<Integer> l1 = new ArrayList<>();
			ArrayList<Integer> l2 = new ArrayList<>();
			ArrayList<Integer> l3 = new ArrayList<>();
			for (int i = 0; i < nodeColorMap[pieceIndex].length; ++i) {
				if (nodeColorMap[pieceIndex][i] == 0) {
					l1.add(i);
				}
				if (nodeColorMap[pieceIndex][i] == 1) {
					l2.add(i);
				}
				if (nodeColorMap[pieceIndex][i] == 2) {
					l3.add(i);
				}
			}

			Pair[] p = {new Pair(l1.get(1), l1.get(0)), new Pair(l2.get(1), l2.get(0)), new Pair(l3.get(1), l3.get(0))};
			return p;
		}

		//reset the model
		@Override
		public void resetModel() {
			randomPieceIndexList.clear();
			longestLine = 0;
			longestLoop = 0;
			initPieceArray();
		}

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

		//if two edges are touched, connect the two node's "next" pointer with each other
		//for the discovery game, each level is independend, so break the previous links before the caculation
		@Override
		void createPossiblePathForDiscovery(ArrayList<Point2D.Double> locations, int length) {
			for (int i = 0; i < locations.size(); ++i) {
				breakConnection(i);
			}

			for (int i = 0; i < locations.size(); ++i) {
				for (int j = i + 1; j < locations.size(); ++j) {
					if (Math.abs(locations.get(i).distance(locations.get(j)) - length * Math.sqrt(3)) < 1) {
						double xDiff = locations.get(j).x - locations.get(i).x;
						double yDiff = locations.get(j).y - locations.get(i).y;
						double degree = Math.toDegrees(Math.atan2(yDiff, xDiff));
						if (Math.abs(degree + 30) <= 3) {
							if (pieceArray[i].getNode(0).getColor() == pieceArray[j].getNode(3).getColor()) {
								pieceArray[i].getNode(0).setNext(pieceArray[j].getNode(3));
								pieceArray[j].getNode(3).setNext(pieceArray[i].getNode(0));
							}
						}
						if (Math.abs(degree - 30) <= 3) {
							if (pieceArray[i].getNode(1).getColor() == pieceArray[j].getNode(4).getColor()) {
								pieceArray[i].getNode(1).setNext(pieceArray[j].getNode(4));
								pieceArray[j].getNode(4).setNext(pieceArray[i].getNode(1));
							} 
						}
						if (Math.abs(degree + 90) <= 3) {
							if (pieceArray[i].getNode(5).getColor() == pieceArray[j].getNode(2).getColor()) {
								pieceArray[i].getNode(5).setNext(pieceArray[j].getNode(2));
								pieceArray[j].getNode(2).setNext(pieceArray[i].getNode(5));
							}
                        
						}
						if (Math.abs(degree - 90) <= 3) {
							if (pieceArray[i].getNode(2).getColor() == pieceArray[j].getNode(5).getColor()) {
								pieceArray[i].getNode(2).setNext(pieceArray[j].getNode(5));
								pieceArray[j].getNode(5).setNext(pieceArray[i].getNode(2));
							}
						}
						if (Math.abs(degree + 150) <= 3) {
							if (pieceArray[i].getNode(4).getColor() == pieceArray[j].getNode(1).getColor()) {
								pieceArray[i].getNode(4).setNext(pieceArray[j].getNode(1));
								pieceArray[j].getNode(1).setNext(pieceArray[i].getNode(4));
							}
						}
						if (Math.abs(degree - 150) <= 3) {
							if (pieceArray[i].getNode(3).getColor() == pieceArray[j].getNode(0).getColor()) {
								pieceArray[i].getNode(3).setNext(pieceArray[j].getNode(0));
								pieceArray[j].getNode(0).setNext(pieceArray[i].getNode(3));
							}
						}
					}
				}
			}
			//  printPieceInfor(0);
		}

		//function to break the node links 
		public void breakConnection(int index) {
			for (int i = 0; i < 6; ++i) {
				pieceArray[index].getNode(i).setNext(null);
			}
		}

		//function to find the loop for discovery, if the player formed a loop, the loop must contain the all pieces, so 
		//Start from any one of these pieces, keep track the every node's "next" pointers, if the pointer finally points to the starting node itself
		//that means, the player formed a loop
		public void findLoopHelperForDiscovery() {
			boolean find = false;
			for (int j = 0; j < 6; ++j) {
				if(find){
					break;
				}
				int count = 0;
				//I arbitrarily choose the stating piece to be piece 0
				Node n = pieceArray[0].getNode(j);
				Node m = pieceArray[0].getNode(j);
				while (n.getNext() != null) {
					count++;
					n = n.getNext().getPrevious();
					//conditions to form a longest loop
					if (n == m && count == currentLevel + 2) {
						find = true;
						for(int i =0; i<currentLevel+2; ++i){
							n = n.getNext().getPrevious();
						}
						longestLoop = count;
						userSelectedLoopColor = m.getColor();
						break;
					} else if(n ==m && count != currentLevel+2){
						break;
					}
				}
			}
		}

		//function for finding the loop and line for solitaire game
		public void findLoopOrLineHelperForSolitaire() {
			boolean findLongestLoop = false;
			for (int i = 0; i < currentLevel; ++i) {
				//if the longestLoop is found, end the process
				if (findLongestLoop) {
					break;
				}
				//if the longest line is found and is bigger than 2*longestLoop, finish the process
				if (longestLine == currentLevel && longestLine > longestLoop * 2) {
					break;
				}
				TantrixSinglePiece p = pieceArray[randomPieceIndexList.get(i)];
				//check all six nodes
				for (int j = 0; j < 6; ++j) {
					if (findLongestLoop) {
						break;
					}
					int count = 1;
					Node n = p.getNode(j);
					Node m = p.getNode(j);
					while (n.getNext() != null) {
						n = n.getNext().getPrevious();
						count++;
						//loop
						if (n == m) {
							if (count > longestLoop) {
								longestLoop = count - 1;
								if (longestLoop == currentLevel) {
									findLongestLoop = true;
									userSelectedLoopColor = m.getColor();
								}
							}
							break;
						}
					}
					//line
					if (count > longestLine) {
						longestLine = count;
						userSelectedLineColor = m.getColor();
					}
				}
			}
		}

		//check whether the user form the longest loop for discovery game
		@Override
		public Boolean checkLoopForDiscovery() {
			longestLoop =0;
			findLoopHelperForDiscovery();
			return longestLoop == currentLevel + 2;
		}

		//similiar to function createPossiblePathForDiscovery(...)
		//difference is only update pointers that are related to the new piece
		@Override
		void createPossiblePathForSolitaire(int length, ArrayList<Point2D.Double> pieceLocations) {

			Point2D.Double currentPieceLocation = pieceLocations.get(pieceLocations.size() - 1);
			for (int i = 0; i < pieceLocations.size(); ++i) {
				if (Math.abs(currentPieceLocation.distance(pieceLocations.get(i)) - length * Math.sqrt(3)) < 5) {
					int newPiece = randomPieceIndexList.get(pieceLocations.size() - 1);
					int oldPiece = randomPieceIndexList.get(i);
					double xDiff = currentPieceLocation.x - pieceLocations.get(i).x;
					double yDiff = currentPieceLocation.y - pieceLocations.get(i).y;
					//based on the angle that the two center created, decide which two edge are touched
					double degree = Math.toDegrees(Math.atan2(yDiff, xDiff));

					if (Math.abs(degree + 30) <= 5) {
						if (pieceArray[oldPiece].getNode(0).getColor() == pieceArray[newPiece].getNode(3).getColor()) {
							pieceArray[oldPiece].getNode(0).setNext(pieceArray[newPiece].getNode(3));
							pieceArray[newPiece].getNode(3).setNext(pieceArray[oldPiece].getNode(0));
						}
					}
					if (Math.abs(degree - 30) <= 5) {
						if (pieceArray[oldPiece].getNode(1).getColor() == pieceArray[newPiece].getNode(4).getColor()) {
							pieceArray[oldPiece].getNode(1).setNext(pieceArray[newPiece].getNode(4));
							pieceArray[newPiece].getNode(4).setNext(pieceArray[oldPiece].getNode(1));
						}
					}
					if (Math.abs(degree + 90) <= 5) {
						if (pieceArray[oldPiece].getNode(5).getColor() == pieceArray[newPiece].getNode(2).getColor()) {
							pieceArray[oldPiece].getNode(5).setNext(pieceArray[newPiece].getNode(2));
							pieceArray[newPiece].getNode(2).setNext(pieceArray[oldPiece].getNode(5));
						}
					}
					if (Math.abs(degree - 90) <= 5) {
						if (pieceArray[oldPiece].getNode(2).getColor() == pieceArray[newPiece].getNode(5).getColor()) {
							pieceArray[oldPiece].getNode(2).setNext(pieceArray[newPiece].getNode(5));
							pieceArray[newPiece].getNode(5).setNext(pieceArray[oldPiece].getNode(2));
						}
					}
					if (Math.abs(degree + 150) <= 5) {
						if (pieceArray[oldPiece].getNode(4).getColor() == pieceArray[newPiece].getNode(1).getColor()) {
							pieceArray[oldPiece].getNode(4).setNext(pieceArray[newPiece].getNode(1));
							pieceArray[newPiece].getNode(1).setNext(pieceArray[oldPiece].getNode(4));
						}
					}
					if (Math.abs(degree - 150) <= 5) {
						if (pieceArray[oldPiece].getNode(3).getColor() == pieceArray[newPiece].getNode(0).getColor()) {
							pieceArray[oldPiece].getNode(3).setNext(pieceArray[newPiece].getNode(0));
							pieceArray[newPiece].getNode(0).setNext(pieceArray[oldPiece].getNode(3));
						}
					}
				}
			}
		}

		//caculate score for solitaire game
		@Override
		int caculateScore(int currentPieceIndex) {
			findLoopOrLineHelperForSolitaire();
			return longestLine > 2 * longestLoop ? longestLine : 2 * longestLoop;
		}
	}
