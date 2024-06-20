import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

public class Sprite {
   private Image[] Animation_Images;
   Image spriteImage;
   private BufferedImage[] Animation_Images_buffered;
   BufferedImage spriteBufferedImage;
   int frames;
   int[] Frame_Cycle;
   int width; int height;
   int currentFrame;
   int framePosition;
   boolean buffered;
   Thread animator;
   int beginFrame;
   int endFrame;
   boolean loop;
   String direction;
   long time;
   private static int DEFAULT_DELAY = 1000/12;
   int delay;
 
   private void SET_DEFAULTS(){
      delay = DEFAULT_DELAY;
      loop = true;
      direction = "forward";
      time = System.currentTimeMillis();
      currentFrame = 0;
      framePosition = 0;
      if(buffered){
         Frame_Cycle = new int[Animation_Images_buffered.length];
         for(int f=0;f<Animation_Images_buffered.length;f++){
            Frame_Cycle[f] = f;
         }
      }
      else{
         Frame_Cycle = new int[Animation_Images.length];
         for(int f=0;f<Animation_Images.length;f++){
            Frame_Cycle[f] = f;
         }
      }
      beginFrame = 0;
      endFrame = Frame_Cycle.length-1;
   }
 
   public Sprite(Image[] Images){
      Animation_Images = Images;
      frames = Animation_Images.length;
      buffered = false;
      SET_DEFAULTS();
      setImage();
   }
   public Sprite(BufferedImage[] bufferedImages){
      Animation_Images_buffered = bufferedImages;
      frames = Animation_Images_buffered.length;
      buffered = true;
      SET_DEFAULTS();
      setImage();
   }
   
       
   public void nextFrame(){
      if(framePosition+1 < Frame_Cycle.length){
         framePosition++;
      }
      else{
         if(loop){
            framePosition = 0 ;
         }	
      }
      currentFrame = Frame_Cycle[framePosition];
      setImage();
   }
   public void previousFrame(){
      if(framePosition-1 >= 0){
         framePosition--;
      }
      else{
         if(loop){
            framePosition = Frame_Cycle.length-1 ;
         }
      }
      currentFrame = Frame_Cycle[framePosition];
      setImage();
   }
   public void setAnimationParameters(int fps, String dir){
      delay = (fps>0)?(1000/fps):DEFAULT_DELAY;
      direction = dir;
   }
   public void setFrameCycle(int[] frmList){
      boolean check = Arrays.equals(Frame_Cycle, frmList);
      if(check == false){
         Frame_Cycle = frmList;
         framePosition = 0;
      }
      currentFrame = Frame_Cycle[framePosition];
      setImage();
   }
   public void playAnimation(boolean lp){
      loop = lp;
      play();
   }
   public void playAnimation(int startframe, int endframe, boolean lp){
      beginFrame = startframe;
      endFrame = endframe;
      loop = lp;
      play();
   }   
   public void stopAnimation(){
      stop();
   }
   private void play(){
      if(direction.equalsIgnoreCase("forward")){
         nextFrame();
      }
      else{
         previousFrame();  
      }
   }
    
    
   private void stop(){
      currentFrame = beginFrame;
   }
   
   public void goToAndStop(int frm){
      currentFrame = frm;
      setImage();
   }  
	
   private void setImage(){
      if(buffered){
         spriteBufferedImage = Animation_Images_buffered[currentFrame];
         width = spriteBufferedImage.getWidth();
         height = spriteBufferedImage.getHeight();
      }
      else{
         spriteImage = Animation_Images[currentFrame];  
         width = spriteImage.getWidth(null);
         height = spriteImage.getHeight(null);
      
      }
   }  
}