/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package tantrixgame;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
*
* @author kaitlyn
*/
//This is an abstract model
public abstract class TantrixAbstractModel {
	//properities:
	//abstract methods:
	//set Color for tantrix pieces
	abstract void setColor(Color[] color, Color[] loopColor);
	//set current level
	abstract void setCurrentLevel(int level);
	//get message
	abstract String getMessage(String inforType);
	//update model after user rotate the piece
	abstract public void colorUpdateAfterRotate(int pieceIndex);
	//get current piece color---edgeIndexes mapping information
	abstract public Pair[] getEdgeIndexesForCurrentPiece(int pieceIndex);
	//reset model
	abstract public void resetModel();
	//shuffle piecees
	abstract void shufflePieceIndex();
	//get a random piece
	abstract int getRandomPieceIndex(int i);

	abstract void createPossiblePathForDiscovery(ArrayList<Point2D.Double> locations, int length);

	abstract public Boolean checkLoopForDiscovery();

	abstract void createPossiblePathForSolitaire(int length, ArrayList<Point2D.Double> pieceLocations);

	abstract int caculateScore(int currentPieceIndex);
}
