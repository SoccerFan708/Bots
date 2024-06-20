   import java.awt.image.*;
  
   
    public class Food{
      int value;
      int _xtile;int _ytile;
      int sector;
      int _x; int _y;
      Sprite sprite;
      BufferedImage bufferedImage;
      boolean beingEaten;
         
       public Food(){
      }
         
       public Food(BufferedImage[] bimgs, int tileX, int tileY){
         _xtile = tileX;
         _ytile = tileY;
         value = 10;
         sprite = new Sprite(bimgs);
         bufferedImage = sprite.spriteBufferedImage;
      }
         
   }