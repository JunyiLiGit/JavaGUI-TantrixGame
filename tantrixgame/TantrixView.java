/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tantrixgame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;

/**
 *
 * @author kaitlyn
 */
//create tantrix view class
public class TantrixView extends JPanel {

    //store the tantrix pieces that are already draw on the screen
    private final ArrayList<TantrixPiece> tantrixPiecesArrayList;
    //store piece locations
    private final ArrayList<Point2D.Double> locationsForSolitaire;
    private final JFrame f;
    private final JPanel p;
    private final JComboBox gameNameList;
    private final JCheckBox[] checkBoxes;
    private final JToggleButton playAndContinueButton;
    private final JButton resetButton, showInstructions, showResult, showHint;
    private final JLabel2D scoreLabel;
    private final ComponentMover cm;

    //create a toolBar, group buttons
    private final JPanel toolbar;

    public TantrixView() {

        locationsForSolitaire = new ArrayList<>();
        tantrixPiecesArrayList = new ArrayList<>(10);
        f = new JFrame("Tantrix Game");
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        p = new JPanel(new DragLayout());

        Font buttonFont = new Font("bigFont", Font.BOLD, 13);

        toolbar = new JPanel();
        toolbar.setOpaque(true);

        //create game choice combobox
        String[] gameNameStrings = {"Discovery", "Solitaire"};
        gameNameList = new JComboBox(gameNameStrings);
        gameNameList.setToolTipText("Click this button to choice one of the two games.");
        gameNameList.setFont(buttonFont);
        gameNameList.setForeground(Color.BLACK);

        //create color checkBoxes 
        Color[] colorArray = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
        checkBoxes = new JCheckBox[4];

        for (int i = 0; i < checkBoxes.length; ++i) {
            checkBoxes[i] = new JCheckBox();
            checkBoxes[i].setOpaque(true);
            checkBoxes[i].setBackground(colorArray[i]);
        }

        //create play button
        playAndContinueButton = new JToggleButton("Play");
        playAndContinueButton.setFont(buttonFont);
        playAndContinueButton.setToolTipText("Click this button to start or pause the game.");

        //create reset button, reset the game
        resetButton = new JButton("Reset");
        resetButton.setFont(buttonFont);
        resetButton.setToolTipText("Click this button to reset the game.");

        showHint = new JButton("Hint");
        showHint.setFont(buttonFont);
        resetButton.setToolTipText("Click this button to show possible loop color.");

        //create result button
        showResult = new JButton("Submit");
        showResult.setFont(buttonFont);
        showResult.setToolTipText("Click this button to show current result.");

        //create instruction button, show user the instructions
        showInstructions = new JButton("Instructions");
        showInstructions.setFont(buttonFont);
        showInstructions.setToolTipText("Click this button to show the instructions for the game.");

        //create score lable and timer lable
        scoreLabel = new JLabel2D(" ", JLabel.CENTER);
        Font scorefont = new Font("bigFont", Font.BOLD, 20);
        scoreLabel.setFont(scorefont);
        scoreLabel.setOutlineColor(Color.black);
        scoreLabel.setEffectIndex(JLabel2D.EFFECT_GRADIENT);
        GradientPaint gp = new GradientPaint(0, 0, Color.red, 100, 50,
                Color.blue, true);
        scoreLabel.setGradient(gp);
        Dimension s = new Dimension(100, 50);
        scoreLabel.setPreferredSize(s);

        //add buttons to toolbar
        toolbar.add(gameNameList);

        for (int i = 0; i < checkBoxes.length; ++i) {
            toolbar.add(checkBoxes[i]);
        }

        toolbar.add(playAndContinueButton);
        toolbar.add(showHint);
        toolbar.add(showResult);
        toolbar.add(resetButton);
        toolbar.add(showInstructions);
        toolbar.add(scoreLabel);

        cm = new ComponentMover();

        //init the view 
        initView();

        f.getContentPane().add(toolbar, BorderLayout.NORTH);

        f.getContentPane().add(p, BorderLayout.CENTER);  // Add panel P to JFrame f

        f.pack();
        f.setVisible(true);
        
        String gameInstructions =
                "Two versions: Discovery and Solitaire"+"\n"+
 
                "Before you begin the game choose game type and desired three colors first"+"\n"+
                "During the game, you can still press Instructions to see game instructions without loosing points"+"\n"+
                "After you press play, you can't change your game type and game color unless reset or restart the game"+"\n"+
                "In every level, you have to press submit to submit your solution"+"\n"+
                "You can rotate or move the piece using mouse click and mouse drag"+"\n"+
                "You have to start the game from the first level if you restart or reset the game"+"\n"+ 
                "You are not allow to enter to next level without finishing the current level"+"\n"+
                "You can press Hint to see game hint, but you can also choose to ignore the hint"+"\n"+
                "The computer will help you to snap the piece to the closest grid"+"\n"+
                "Make sure you didn't overlap the pieces when you move, otherwise drag it back"+"\n"+
                
                "\n"+
                "Discovery:   Total 10 pieces for 8 levels."+"\n"+
                "             Your goal is to form a same color loop using all the pieces you have"+"\n"+
                "             You will get first three pieces in the first level, one extra piece in the other levels"+"\n"+
                "             When you enter the next level, break up your previous solution and form another loop"+"\n"+
                "             Game results are Complete or Not Complete"+"\n"+"\n"+
                "Solitaire:   Total 14 pieces. Your goal is to get a higher score"+"\n"+
                "             One extra piece will be provided for each level"+"\n"+
                "             Once you press submit, you can't drag or rotate these pieces"+"\n"+
                "             Please put the first piece close to the center of the screen"+"\n"+
                "             In case you need more room for the following pieces"+"\n"+
                "    Score:   1 point for each piece in the longest line or"+"\n"+
                "             2 points for each piece in the longest loop. Maximum is 28"+"\n";
        
        JOptionPane.showMessageDialog(null,gameInstructions, "Tantrix Game Instructions", JOptionPane.INFORMATION_MESSAGE);

    }

    //default choice: Discovery game; red, green, blue
    public void initView() {
        gameNameList.setSelectedIndex(0);
        checkBoxes[0].setSelected(true);
        checkBoxes[1].setSelected(true);
        checkBoxes[2].setSelected(true);
        checkBoxes[3].setEnabled(false);
        showHint.setEnabled(false);
        showResult.setEnabled(false);
        resetButton.setEnabled(false);
        
        

    }

    //init view for solitaire game, add a score label, remove hint button, change instruction description
    public void initSolitaireView() {

        scoreLabel.setText("Score : 0");
        showHint.setVisible(false);

    }

    //when the user press play button, the game begins, the hint, submit buttons will be enabled, the play button will become continue
    public void changeBeginButtonStatus() {
        gameNameList.setEnabled(false);
        showHint.setEnabled(true);
        showResult.setEnabled(true);
        resetButton.setEnabled(true);
        playAndContinueButton.setText("Continue");

    }

    //when the user finish the game, disable hint and continue button
    public void changeEndButtonStatus() {
        showHint.setEnabled(false);
        playAndContinueButton.setEnabled(false);

    }
    
    

    //add controller as the actionListener to the view
    public void addController(ActionListener controller) {

        gameNameList.addActionListener(controller);
        for (int i = 0; i < checkBoxes.length; ++i) {
            checkBoxes[i].addActionListener(controller);

        }

        resetButton.addActionListener(controller);
        playAndContinueButton.addActionListener(controller);
        showResult.addActionListener(controller);
        showHint.addActionListener(controller);

        showInstructions.addActionListener(controller);

    }

    //get the checkBoxes 
    public JCheckBox[] getCheckBoxes() {
        return checkBoxes;

    }

    //after the user select the colors and press the play button, the controller let the view to disable the checkBoxes
    public void disableCheckBox() {

        for (int i = 0; i < checkBoxes.length; ++i) {
            checkBoxes[i].setEnabled(false);
        }
    }

    //get the tantrix piece index  
    public int getPieceIndex(int i) {
        return tantrixPiecesArrayList.get(i).getPieceIndex();

    }

    //show tantix piece on the screen
    public void showTantrixPiece(Boolean discovery, int index, HashMap<Color, Pair<Integer, Integer>> colorEdgeIndexMap, MouseListener controller) {
        TantrixPiece tmp = new TantrixPiece(40, colorEdgeIndexMap);
        tmp.setPieceIndex(index);
        Dimension tanreixSize = new Dimension(80, (int) (40 * Math.sqrt(3)));
        tmp.setPreferredSize(tanreixSize);
        if (discovery && index < 3) {
            tmp.setLocation(new Point(p.getSize().width / 2 - 120 + index * 100, 20));

        } else {
            tmp.setLocation(new Point(p.getSize().width / 2 - 40, 20));

        }
        cm.registerComponent(tmp);
        tmp.addMouseListener(controller);
        tantrixPiecesArrayList.add(tmp);
        p.add(tmp);
        p.validate();
        p.repaint();

    }

    //return the tantrix piece based on the piece index
    public TantrixPiece getTantrixPiece(int index) {
        return tantrixPiecesArrayList.get(index);

    }

    //rotate the tantrix piece
    public void rotateTantrixPiece(int index) {
        tantrixPiecesArrayList.get(index).setRotate();

    }

    //return the piece locations for discovery game, the piece index is not shuffled, which means the piece information is mapped automatically
    public ArrayList<Point2D.Double> getTantrixPiecesCenterLocation() {
        ArrayList<Point2D.Double> locations = new ArrayList<>();
        for (int i = 0; i < tantrixPiecesArrayList.size(); ++i) {
            TantrixPiece tantrixPiece = (TantrixPiece) p.getComponent(i);
            Point uperLeftLocation = tantrixPiece.getLocation();
            double edgeLength = tantrixPiece.getEdgeLength();
            double centerX = uperLeftLocation.x - edgeLength;
            double centerY = uperLeftLocation.y - (int) (edgeLength / 2 * Math.sqrt(3));
            Point2D.Double centerLocation = new Point2D.Double(centerX, centerY);
            locations.add(centerLocation);
        }
        return locations;
    }

    //get location information for solitaire game
    public ArrayList<Point2D.Double> getTantrixPiecesCenterLocationForSoliitaire() {
        return locationsForSolitaire;

    }

    //update current piece location(based on the piece index which is randomly shuffled) when the press submit, because the user can not change the previous
    //piece locations when the user press submit, so just update the current piece location, not the whole location information
    public void updateLocationArrayList(int index) {
        TantrixPiece tantrixPiece = (TantrixPiece) p.getComponent(index);
        Point uperLeftLocation = tantrixPiece.getLocation();
        double edgeLength = tantrixPiece.getEdgeLength();
        double centerX = uperLeftLocation.x - edgeLength;
        double centerY = uperLeftLocation.y - (int) (edgeLength / 2 * Math.sqrt(3));
        locationsForSolitaire.add(new Point2D.Double(centerX, centerY));

    }

    //show player the message 
    public void showMessage(String inforMessage, String titleBar) {

        JOptionPane.showMessageDialog(null, inforMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);

    }

    //reset the view after the player press reset button
    public void resetView() {

        tantrixPiecesArrayList.clear();
        locationsForSolitaire.clear();

        checkBoxes[0].setSelected(true);
        checkBoxes[1].setSelected(true);
        checkBoxes[2].setSelected(true);
        checkBoxes[3].setSelected(false);

        checkBoxes[0].setEnabled(true);
        checkBoxes[1].setEnabled(true);
        checkBoxes[2].setEnabled(true);
        checkBoxes[3].setEnabled(false);
        
        gameNameList.setEnabled(true);
        gameNameList.setSelectedIndex(0);
        showHint.setVisible(true);
        
        showHint.setEnabled(false);
        showResult.setEnabled(false);
        
        scoreLabel.setText(" ");
        playAndContinueButton.setEnabled(true);
        playAndContinueButton.setText("Play");
        p.removeAll();
        p.revalidate();
        f.repaint();

    }

  
    //when the user play solitaire game, after the user press submit, the drag and rotate function will be disabled 
    public void disableRotateAndDrag(int index, MouseListener controller) {
        TantrixPiece tantrixPiece = (TantrixPiece) p.getComponent(index);
        cm.deregisterComponent(tantrixPiece);
        tantrixPiece.removeMouseListener(controller);

    }

    //set score for solitaire game  
    public void setScore(Integer i) {

        scoreLabel.setText("Score: " + i.toString());

        scoreLabel.setOpaque(true);

    }

}
