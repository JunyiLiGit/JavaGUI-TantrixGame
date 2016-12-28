/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package tantrixgame;

import java.awt.Color;
public class Node {
	private Node next;
	private Node previous;
	private Color nodeColor;
	private int nodeIndex;
	Node(Node next, Node previous, Color c, int index) {
		this.next = null;
		this.previous = null;
		this.nodeColor = c;
		this.nodeIndex = index;

	}

	public void setNext(Node next) {
		this.next = next;

	}

	public void setPrevious(Node previous) {
		this.previous = previous;

	}

	public void setColor(Color c) {
		this.nodeColor = c;
	}

	public void setIndex(int index) {
		this.nodeIndex = index;
	}

	public Color getColor() {
		return nodeColor;
	}

	public int getIndex() {
		return nodeIndex;
	}
						
	public Node getNext(){
		return next;
	}

	public Node getPrevious(){
		return previous;
	}

}