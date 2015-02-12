/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tantrixgame;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.event.MouseInputListener;

/**
 *
 * @author kaitlyn
 */
public class TantrixController implements java.awt.event.ActionListener, MouseInputListener {

    private TantrixAbstractModel model;
    private TantrixView view;
    //the default game is discovery game
    Boolean discovery = true, solitaire = false;
    //total piece number is 14
    int totalPieceNumber = 14;
    //user can only choose 3 different colors
    int colorCount = 3;
    //the first level is 1
    int level = 1;
    //default game status is not complete
    boolean completeCurrentLevel = false;

    //number of pieces that are shown on the screen
    int pieceCount = 0;
    //default color choice: red, green, blue. The user can change the selected colors before the player press play button
    boolean[] colorChoice = {true, true, true, false};
    //store the three colors that the player selected into an array
    Color[] userSelectedColor = new Color[3];

    //for solitaire: the random piece index that is shown on the screen, get from the model   
    int currentRandomPieceIndex;

    TantrixController() {

    }

    //add model to the controller, the controller can talk to model
    public void addModel(TantrixAbstractModel m) {
        this.model = m;
    }

    //add view to the controller
    public void addView(TantrixView v) {
        this.view = v;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (null != e.getActionCommand()) {
            switch (e.getActionCommand()) {
                case "Play":
                   //When the user selects three colors and begins the game, disable the color selection function
                    if (colorCount == 3) {
                        view.disableCheckBox();
                        //set color for model
                        setColorForModel();
                    }

                    
                    if (discovery) {
                        //initialize the view for discovery game
                        view.changeBeginButtonStatus();
                        //in the first level of discovery game, the player will see the first three pieces at the same time
                       //after the player completes the first level, only one extra piece will be show to the player  
                        while (pieceCount != 3) {
                            view.showTantrixPiece(true, pieceCount, createCurrentPieceHashMap(pieceCount), this);
                            pieceCount++;
                        }
                        
                        model.setCurrentLevel(level);
                    }
                    if (solitaire) {
                        //initialize the solitaire view
                        view.changeBeginButtonStatus();
                        //shuffle the pieces
                        model.shufflePieceIndex();
                        //get the current rangom piece index
                        currentRandomPieceIndex = model.getRandomPieceIndex(pieceCount);
                        //show the current tantrix piece on the screen
                        view.showTantrixPiece(false, currentRandomPieceIndex, createCurrentPieceHashMap(currentRandomPieceIndex), this);
                        pieceCount++;
                        model.setCurrentLevel(level);

                    }

                    break;

                case "Continue":

                    //press continue to enter the next level, if and only if the player finish the previous level, the 
                    //player can enter to the next level
                    if (discovery && completeCurrentLevel && pieceCount < 10) {
                        level++;
                        model.setCurrentLevel(level);
                        view.showTantrixPiece(false, pieceCount, createCurrentPieceHashMap(pieceCount), this);
                        pieceCount++;
                        completeCurrentLevel = false;
                        break;

                    }
                    if (solitaire && completeCurrentLevel && pieceCount < totalPieceNumber) {
                        level++;
                        model.setCurrentLevel(level);
                        currentRandomPieceIndex = model.getRandomPieceIndex(pieceCount);
                        view.showTantrixPiece(false, currentRandomPieceIndex, createCurrentPieceHashMap(currentRandomPieceIndex), this);
                        pieceCount++;
                        completeCurrentLevel = false;
                        break;

                    } else {

                        view.showMessage(model.getMessage("default"), "Good Luck!");
                        break;

                    }

                case "Submit":

                    //when the player press submit button, the controller call the model to caculate the result
                    //and let the view to show the result
                    if (discovery) {

                        model.createPossiblePathForDiscovery(view.getTantrixPiecesCenterLocation(), 40);
                        if (model.checkLoopForDiscovery() && level < 8) {

                            completeCurrentLevel = true;
                            view.showMessage(model.getMessage("Success"), "Congratulations");
                            break;

                        }
                        if (model.checkLoopForDiscovery() && level >= 8) {
                            completeCurrentLevel = true;
                            view.changeEndButtonStatus();

                            view.showMessage(model.getMessage("Complete"), "Congratulations");
                            break;

                        } else {
                            view.showMessage(model.getMessage("Fail"), "Sorry");
                            break;
                        }
                    }

                    if (solitaire) {

                        view.disableRotateAndDrag(pieceCount - 1, this);

                        if (pieceCount == 1 && !completeCurrentLevel) {

                            view.updateLocationArrayList(pieceCount - 1);

                            view.setScore(1);
                            completeCurrentLevel = true;
                            break;

                        }
                        if (pieceCount <= totalPieceNumber && !completeCurrentLevel) {

                            view.updateLocationArrayList(pieceCount - 1);
                            model.createPossiblePathForSolitaire(40,view.getTantrixPiecesCenterLocationForSoliitaire());
                            int score = model.caculateScore(currentRandomPieceIndex);
                            view.setScore(score);
                            completeCurrentLevel = true;
                            break;
                        }

                        if (pieceCount < totalPieceNumber && completeCurrentLevel) {

                            view.showMessage(model.getMessage("Default"), "Continue");
                            break;

                        }
                        if (pieceCount == totalPieceNumber && completeCurrentLevel) {
                            view.changeEndButtonStatus();
                            view.showMessage(model.getMessage("Complete"), "Congratulations");
                            break;

                        }

                    }

                case "Instructions":

                    //Show game instructions
                    if(discovery){
                        view.showMessage(model.getMessage("InstructionsForDiscovery"), "Instructions");
                    }
                    else {
                        view.showMessage(model.getMessage("InstructionsForSolitaire"), "Instructions");
                    }
                    break;

                case "Hint":

                    //show the player the game hint
                    view.showMessage(model.getMessage("Hint"), "Hint");
                    break;

                case "Reset":

                    //reset the game, the player can ga back to the beginning of the game
                    restGame();

                    break;

            }
        }

        //check which game the use choose to play
        if (e.getSource() instanceof JComboBox) {
            JComboBox src = (JComboBox) e.getSource();
            JComboBox combo = (JComboBox) src;
            String sequenceName = (String) combo.getSelectedItem();
            switch (sequenceName) {
                case "Discovery":
                    discovery = true;
                    solitaire = false;
                    break;
                case "Solitaire":
                    solitaire = true;
                    discovery = false;
                    view.initSolitaireView();

                    break;

            }
        }

        //checkBox controll--if three checkBox are already selected(default is rgb), the unselected one will be disabled, since the user can only
        //choose three colors. If the user unselecte one of the three selected colors, the disabled checkBox will be enabled
        if (e.getSource() instanceof JCheckBox) {

            JCheckBox src = (JCheckBox) e.getSource();
            if (src.isSelected()) {
                colorCount++;
                if (colorCount == 3) {
                    for (JCheckBox box : view.getCheckBoxes()) {
                        if (!box.isSelected()) {
                            box.setEnabled(false);
                        }
                    }
                }

                if (colorCount <= 3) {
                    Color color = src.getBackground();
                    switch (color.toString()) {
                        case "java.awt.Color[r=255,g=0,b=0]":
                            colorChoice[0] = true;
                            break;
                        case "java.awt.Color[r=0,g=255,b=0]":
                            colorChoice[1] = true;
                            break;
                        case "java.awt.Color[r=0,g=0,b=255]":
                            colorChoice[2] = true;
                            break;
                        case "java.awt.Color[r=255,g=255,b=0]":
                            colorChoice[3] = true;
                            break;
                    }
                }
            } else {
                colorCount--;
                Color color = src.getBackground();
                switch (color.toString()) {
                    case "java.awt.Color[r=255,g=0,b=0]":
                        colorChoice[0] = false;
                        break;
                    case "java.awt.Color[r=0,g=255,b=0]":
                        colorChoice[1] = false;
                        break;
                    case "java.awt.Color[r=0,g=0,b=255]":
                        colorChoice[2] = false;
                        break;
                    case "java.awt.Color[r=255,g=255,b=0]":
                        colorChoice[3] = false;
                        break;
                }
                // check for less than max selections:
                if (colorCount < 3) {
                    for (JCheckBox box : view.getCheckBoxes()) {
                        box.setEnabled(true);
                    }
                }
            }

        }

    }

    //based on the user's color choice, mapping the color information for the model
    public void setColorForModel() {

        //the tmp array store the possible loop colors for 8 levels
        if (!colorChoice[0]) {
            //yellow, green, blue
            userSelectedColor[0] = Color.YELLOW;
            userSelectedColor[1] = Color.GREEN;
            userSelectedColor[2] = Color.BLUE;
            Color[] tmp2 = {Color.YELLOW, Color.GREEN, Color.GREEN, Color.BLUE, Color.GREEN, Color.BLUE, Color.YELLOW, Color.GREEN};
            model.setColor(userSelectedColor, tmp2);

        }
        if (!colorChoice[1]) {
            //yellow, red, blue---(compare case)
            userSelectedColor[0] = Color.YELLOW;
            userSelectedColor[1] = Color.RED;
            userSelectedColor[2] = Color.BLUE;
            Color[] tmp2 = {Color.YELLOW, Color.RED, Color.RED, Color.BLUE, Color.RED, Color.BLUE, Color.YELLOW, Color.RED};
            model.setColor(userSelectedColor, tmp2);
        }
        if (!colorChoice[2]) {
            //yellow, green, red
            userSelectedColor[0] = Color.YELLOW;
            userSelectedColor[1] = Color.GREEN;
            userSelectedColor[2] = Color.RED;
            Color[] tmp2 = {Color.YELLOW, Color.GREEN, Color.GREEN, Color.RED, Color.GREEN, Color.RED, Color.YELLOW, Color.GREEN};
            model.setColor(userSelectedColor, tmp2);
        }
        if (!colorChoice[3]) {
            //green, red, blue
            userSelectedColor[0] = Color.GREEN;
            userSelectedColor[1] = Color.RED;
            userSelectedColor[2] = Color.BLUE;
            Color[] tmp2 = {Color.GREEN, Color.RED, Color.RED, Color.BLUE, Color.RED, Color.BLUE, Color.GREEN, Color.RED};
            model.setColor(userSelectedColor, tmp2);
        }
    }

    //create a hash map for the current piece, the hash map store the color--Edge index mapping
    //when the controller needs the view to show the next tantrix piece, the controller pass this hashMap to the view
    private HashMap<Color, Pair<Integer, Integer>> createCurrentPieceHashMap(int index) {
        //setColorForModel();
        Pair[] tmp = model.getEdgeIndexesForCurrentPiece(index);
        HashMap<Color, Pair<Integer, Integer>> colorEdgeIndexMap = new HashMap();
        for (int i = 0; i < tmp.length; ++i) {

            colorEdgeIndexMap.put(userSelectedColor[i], tmp[i]);

        }

        return colorEdgeIndexMap;

    }

    //reset controller
    public void resetController() {
        colorCount = 3;
        level = 1;
        completeCurrentLevel = false;
        pieceCount = 0;
        boolean[] tmp = {true, true, true, false};
        colorChoice = tmp;
        discovery = true;
        solitaire = false;
    }
    
   //reset the game 
    public void restGame() {
        view.resetView();
        model.resetModel();
        resetController();

    }


    //mouse action listener for rotation
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() instanceof TantrixPiece) {
            TantrixPiece src = (TantrixPiece) e.getSource();
            if (src.getPolygon().contains(e.getPoint())) {
                src.setRotate();
                src.repaint();
                int pieceIndex = src.getPieceIndex();
                model.colorUpdateAfterRotate(pieceIndex);

            }
            

        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    
    
}
