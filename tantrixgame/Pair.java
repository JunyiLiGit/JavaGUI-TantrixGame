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
public class Pair<A,B> {
   private  A first;
   private  B second;
   
   public Pair(A first, B second){
    this.first = first;
    this.second =  second;
   }
   
   public Pair(){
   
   }
   
   public A getFirst(){
     return this.first;
   }
   
   public void setFirst(A first){
     this.first = first;
   }
   
   public void setSecond(B second){
     this.second = second;
   }
   
   public B getSecond(){
    return this.second;
   }
   
   @Override
   public String toString()
    { 
           return "(" + first + ", " + second + ")"; 
    }
   
   public void printPair(){
      System.out.println("("+this.first+","+this.second+")");
   
   }
   
    
}
