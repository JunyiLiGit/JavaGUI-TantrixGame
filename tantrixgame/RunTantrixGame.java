/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tantrixgame;

/**
 *
 * @author kaitlyn
 */
public class RunTantrixGame {

    /**
     * @param args the command line arguments
     */
    private final TantrixAbstractModel model;
    private final TantrixView view;
    private final TantrixController controller;
    
    public RunTantrixGame(){
     // this.model = new Tantrix2DArrayModel();
      this.model = new TantrixDoubleLinkedNodeModel();
      this.view = new TantrixView();
      this.controller = new TantrixController();
      controller.addModel(model);
      controller.addView(view);       
      view.addController(controller);
      
      
      
    }
    
    
    
    
    public static void main(String[] args) {
        // TODO code application logic here
        
       RunTantrixGame runPascalTriangle = new RunTantrixGame();
       
       
        
    }
    
}
