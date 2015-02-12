/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tantrixgame;


/**
 *
 * @ original author : Rob Camick. 
 *    http://tips4java.wordpress.com/2009/06/14/moving-windows/
 *    I used some of his interfaces, and I implemented the methods by myself
 */


import java.awt.*;
import java.awt.event.*;

/**
 *  This class allows you to move a Component by using a mouse. 
 */
public class ComponentMover extends MouseAdapter
{
	
	private final Dimension snapSize = new Dimension(1, 1);
	private final Insets edgeInsets = new Insets(0, 0, 0, 0);
	private final boolean changeCursor = true;
	private  boolean autoLayout = false;
	private TantrixPiece targetComponent;
        

	private Point pressed;
	private Point location;

	private boolean potentialDrag;


	/**
	 *  Constructor for moving individual components. The components must be
	 *  regisetered using the registerComponent() method.
	 */
	public ComponentMover()
	{
	}


        
        /**
	 *  Add the required listeners to the specified component
	 *
     * @param components
	 */
	public void registerComponent(Component... components)
	{
		for (Component component : components)
			component.addMouseListener( this );
	}

        
	/**
	 *  Remove listeners from the specified component
	 *
     * @param components
	 */
	public void deregisterComponent(Component... components)
	{
		for (Component component : components)
			component.removeMouseListener( this );
	}

	

	/**
	 *  Setup the variables used to control the moving of the component:
	 *
	 *  source - the source component of the mouse event
	 *  destination - the component that will ultimately be moved
	 *  pressed - the Point where the mouse was pressed in the destination
	 *      component coordinates.
     * @param e
	 */
        
	@Override
	public void mousePressed(MouseEvent e)
	{
		targetComponent = (TantrixPiece) e.getComponent();
		
                if (targetComponent.contains(e.getPoint()))
			setupForDragging(e);
	}

	private void setupForDragging(MouseEvent e)
	{
		targetComponent.addMouseMotionListener( this );
		potentialDrag = true;

		pressed = e.getLocationOnScreen();
                
		location = targetComponent.getLocation();
           
	}

	/**
	 *  Move the component to its new location. The dragged Point must be in
	 *  the destination coordinates.
     * @param e
	 */
	
	/**
	 *  Move the component to its new location. The dragged Point must be in
	 *  the destination coordinates.
     * @param e
	 */
	public void mouseDragged(MouseEvent e)
	{
		Point dragged = e.getLocationOnScreen();
                
                
		int dragX = getDragDistance(dragged.x, pressed.x, snapSize.width);
		int dragY = getDragDistance(dragged.y, pressed.y, snapSize.height);
                

		int locationX = location.x + dragX;
		int locationY = location.y + dragY;
                
             
		//  Mouse dragged events are not generated for every pixel the mouse
		//  is moved. Adjust the location to make sure we are still on a
		//  snap value.

		while (locationX < edgeInsets.left)
			locationX += snapSize.width;

		while (locationY < edgeInsets.top)
			locationY += snapSize.height;

		Dimension d = getBoundingSize( targetComponent );
                
                
		while (locationX + targetComponent.getSize().width + edgeInsets.right > d.width)
			locationX -= snapSize.width;

		while (locationY + targetComponent.getSize().height + edgeInsets.bottom > d.height)
			locationY -= snapSize.height;

		//  Adjustments are finished, move the component
                Dimension piece_dims = targetComponent.getSize();
                double grid_col = locationX/(3.0*piece_dims.width/4.0);
                double grid_row = locationY/piece_dims.height;

                double offsety = 0;
                if ((int)grid_col % 2 != 0) {
                    offsety = piece_dims.height/2;

                }

                targetComponent.setLocation((int)grid_col * 3*piece_dims.width/4, 
                        (int)(grid_row * piece_dims.getSize().height + offsety));
                
              
	}
        
        public boolean isAutoLayout()
	{
		return autoLayout;
	}
        
        
        public void setAutoLayout(boolean autoLayout)
	{
		this.autoLayout = autoLayout;
	}

	/*
	 *  Determine how far the mouse has moved from where dragging started
	 *  (Assume drag direction is down and right for positive drag distance)
	 */
	private int getDragDistance(int larger, int smaller, int snapSize)
	{
		int halfway = snapSize/2;
		int drag = larger - smaller;
		drag += (drag < 0) ? -halfway : halfway;
		drag = (drag / snapSize) * snapSize;

		return drag;
                
                
	}

	/*
	 *  Get the bounds of the parent of the dragged component.
	 */
	private Dimension getBoundingSize(Component source)
	{
		
			return source.getParent().getSize();
		
	}

	/**
	 *  Restore the original state of the Component
     * @param e
	 */
	@Override
	public void mouseReleased(MouseEvent e)
	{
		
            if (!potentialDrag) return;
            else{
               
   
		targetComponent.removeMouseMotionListener( this );
		potentialDrag = false;
            }

	}
}


