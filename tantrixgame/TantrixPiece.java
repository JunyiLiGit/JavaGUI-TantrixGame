/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/


/*
This class tells you hoe to draw tantrix piece on the view
*/
package tantrixgame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;

public class TantrixPiece extends JComponent {

	//piece index
	private int pieceIndex;

	// use polygon to draw hexagon
	private Polygon p = new Polygon();

	//the edge length of the hexagon
	private final int edgeLength;

	//colorEdgeIndexMap :  key:color - value:edge index information
	private final HashMap<Color, Pair<Integer, Integer>> colorEdgeIndexMap;

	//coordinatesList : store six vertices's coordinates of the hexagon  
	private final ArrayList<Pair> coordinatesList;

	//draw tantrix Piece inside an Image
	BufferedImage sprite;
	Boolean rotate;
	int rotateCount;

	public TantrixPiece(int edgeLength, HashMap<Color, Pair<Integer, Integer>> p) {
		this.p = new Polygon();
		this.edgeLength = edgeLength;
		this.colorEdgeIndexMap = p;
		this.coordinatesList = new ArrayList<>();
		createTantrixPieceSprite();
		rotate = false;
		rotateCount = 0;
	}

	public void setPieceIndex(int index) {
		this.pieceIndex = index;
	}

	public int getPieceIndex() {
		return pieceIndex;
	}

	public int getEdgeLength() {
		return edgeLength;
	}

	public void addListenr(MouseListener controller) {
		addMouseListener(controller);
	}

	private void createTantrixPieceSprite() {
		Graphics2D g2;

		//BufferedImage(int width,int height,int imageType)
		sprite = new BufferedImage(2 * edgeLength, (int) (edgeLength * Math.sqrt(3)), BufferedImage.TYPE_4BYTE_ABGR);
		g2 = sprite.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int imageCenterX = edgeLength;
		int imageCenterY = (int) (edgeLength / 2 * Math.sqrt(3));

		for (int i = 0; i < 6; i++) {
			//caculate position of each vertice
			int coordinateX = (int) (imageCenterX + edgeLength * Math.cos(i * 2 * Math.PI / 6));
			int coordinateY = (int) (imageCenterY + edgeLength * Math.sin(i * 2 * Math.PI / 6));
			//add vertices coordinate to the polygon
			this.p.addPoint(coordinateX, coordinateY);

			//store coordinates information into cordinatesList
			Pair pair = new Pair(coordinateX, coordinateY);
			coordinatesList.add(pair);
            
		}

		//draw hexagon using six vertices coordinates
		g2.drawPolygon(this.p);

		g2.setColor(Color.BLACK);

		//create an area using this hexagon
		Area areaP = new Area(this.p);

		//fill the hexagon using fefault color: black
		g2.fill(areaP);

		//draw colored pieces inside the hexagon
		for (Map.Entry<Color, Pair<Integer, Integer>> entry : colorEdgeIndexMap.entrySet()) {
			//get the color information
			Color color = entry.getKey();

			//get the two edge indexes, each edge connect two vertices, 
			//find these four vertices's coordinates from coordinateList
			//using these four coordinates to draw the colored pieces inside hexagon
			//the first edge index
			int firstEdge = entry.getValue().getFirst();

			//for the first edge, get the two point indexs 
			int firstStartPoint = (firstEdge == 0) ? 5 : firstEdge;
			int firstEndPoint = (firstEdge == 0) ? 0 : firstEdge - 1;

			//get the coordinates of these two point fron coordinateList
			int X1 = (int) coordinatesList.get(firstStartPoint).getFirst();
			int Y1 = (int) coordinatesList.get(firstStartPoint).getSecond();

			int X2 = (int) coordinatesList.get(firstEndPoint).getFirst();
			int Y2 = (int) coordinatesList.get(firstEndPoint).getSecond();

			//the middle point's coordinate 
			int firstEdgeMiddleX = (X1 + X2) / 2;
			int firstEdgeMiddleY = (Y1 + Y2) / 2;

			//the second edge index
			int secondEdge = entry.getValue().getSecond();

			int secondStartPoint = (secondEdge == 0) ? 5 : secondEdge;
			int secondEndPoint = (secondEdge == 0) ? 0 : secondEdge - 1;

			int X3 = (int) coordinatesList.get(secondStartPoint).getFirst();
			int Y3 = (int) coordinatesList.get(secondStartPoint).getSecond();

			int X4 = (int) coordinatesList.get(secondEndPoint).getFirst();
			int Y4 = (int) coordinatesList.get(secondEndPoint).getSecond();

			int secondEdgeMiddleX = (X3 + X4) / 2;
			int secondEdgeMiddleY = (Y3 + Y4) / 2;

			//do substraction between these two edges, based on the difference, we will know the shape of the colored pieces
			int difference = abs(firstEdge - secondEdge);

			//(x1,y1): uper-left coordinates of outer circle 
			//(x2,y2): uper-left coordinates of inner circle
			int x1, y1, x2, y2;
			int radius = 0;
			int curveWidth = 3;

			if (difference == 1) {

				radius = (int) (edgeLength / 2);

				x1 = X2 - (radius + curveWidth);
				y1 = Y2 - (radius + curveWidth);
				int height1 = 2 * (radius + curveWidth);
				Ellipse2D.Double circle1;
				circle1 = new Ellipse2D.Double(x1, y1, height1, height1);
				Area areaOne = new Area(circle1);

				x2 = X2 - (radius - curveWidth);
				y2 = Y2 - (radius - curveWidth);
				int height2 = 2 * (radius - curveWidth);
				Ellipse2D.Double circle2;
				circle2 = new Ellipse2D.Double(x2, y2, height2, height2);
				Area areaTwo = new Area(circle2);

				areaOne.intersect(areaP);
				areaTwo.intersect(areaP);

				areaOne.subtract(areaTwo);

				g2.setColor(color);

				g2.fill(areaOne);

			}
			if (difference == 3) {
				int x = firstEdgeMiddleX - curveWidth;
				int y = firstEdgeMiddleY;
				int width = curveWidth * 2;
				int height = abs(firstEdgeMiddleY - secondEdgeMiddleY);
				Rectangle2D rec;
				rec = new Rectangle2D.Double(x, y, width, height);
				Area areaRec = new Area(rec);
				areaRec.intersect(areaP);
				g2.setColor(color);
				g2.fill(areaRec);

			}
			if (difference == 2 || difference == 4) {
				int centerX = 0;
				int centerY = 0;
				if (firstEdge == 5 && secondEdge == 3) {
					centerX = max(X2, X3) - edgeLength;
					centerY = min(Y2, Y1);
					radius = (int) (1.5 * edgeLength);
				}
				if (firstEdge == 3 && secondEdge == 1) {
					centerX = (X2 + X3) / 2;
					centerY = (int) (Y2 + edgeLength / 2 * Math.sqrt(3));
					radius = (int) (1.5 * edgeLength);
				}
				if (firstEdge == 4 && secondEdge == 2) {
					centerX = min(X2, X3) - edgeLength / 2;
					centerY = max(Y2, Y3);
					radius = (int) (1.5 * edgeLength);
				}
				if (firstEdge == 4 && secondEdge == 0) {
					centerX = (X1 + X3) / 2;
					centerY = (int) (Y1 - edgeLength / 2 * Math.sqrt(3));
					radius = (int) (1.5 * edgeLength);
				}

				x1 = centerX - (radius + curveWidth);
				y1 = centerY - (radius + curveWidth);

				x2 = centerX - (radius - curveWidth);
				y2 = centerY - (radius - curveWidth);
				int height1 = 2 * (radius + curveWidth);
				int height2 = 2 * (radius - curveWidth);

				Ellipse2D.Double circle1;
				circle1 = new Ellipse2D.Double(x1, y1, height1, height1);
				Area areaOne = new Area(circle1);

				Ellipse2D.Double circle2;
				circle2 = new Ellipse2D.Double(x2, y2, height2, height2);
				Area areaTwo = new Area(circle2);

				areaOne.intersect(areaP);
				areaTwo.intersect(areaP);
				areaOne.subtract(areaTwo);

				g2.setColor(color);
				g2.fill(areaOne);
			}
		}
		g2.dispose();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		int yoffset = (int) (edgeLength / 2 * Math.sqrt(3));
		int x = (p.xpoints[0] + p.xpoints[3]) / 2;
		int y = (p.ypoints[0] + p.ypoints[3]) / 2;

		if (rotate) {
			BufferedImage spriteCopy = deepCopy(sprite);
			AffineTransform at = new AffineTransform();
			at.translate(getWidth() / 2, getHeight() / 2);
			at.rotate(rotateCount * Math.PI / 3);
			at.translate(-spriteCopy.getWidth() / 2, -spriteCopy.getHeight() / 2);

			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.drawImage(spriteCopy, at, null);

		} else {

			g.drawImage(sprite, (int) (x - edgeLength), (int) (y - yoffset), this);
		}
	}

	static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);

	}

	@Override
	public boolean contains(Point pt) {
		return p.contains(pt.x, pt.y);
	}

	public Polygon getPolygon() {
		return p;
	}

	public void setRotate() {
		rotate = true;
		rotateCount = rotateCount + 1;
	}
}
