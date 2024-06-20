import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

public class PathFinder{
   int HV_COST = 10;
   int D_COST = 14;
   boolean ALLOW_DIAGONAL = false;
   boolean ALLOW_DIAGONAL_CORNERING = true;

   TileObject[][] mapStatus;
   int[][] openList;
   int[][] Map;
 
   public PathFinder(){
   }
   
   public PathFinder(int[][] mp){
      Map = mp;
      mapStatus = new TileObject[Map.length][Map[0].length];
      //openList = new int[1][2];
      setMapStatus();
   }
   
   public PathFinder(TileMap.Tile[][] map){
      Map = new int[map.length][map[0].length];
      for(int y=0;y<map.length;y++){
         for(int x=0;x<map[0].length;x++){
            TileMap.Tile tile = map[y][x];
            if(tile.walkable == false || tile.occupied == true){
               Map[y][x] = 1;
            }
            else{
               Map[y][x] = 0;
            }
         }
      }
      mapStatus = new TileObject[Map.length][Map[0].length];
      //openList = new int[1][2];
      setMapStatus();
   }
   
   private void setMapStatus(){
      for(int m=0;m<mapStatus.length;m++){
         for(int n=0;n<mapStatus[0].length;n++){
            mapStatus[m][n] = new TileObject();
         }
      }
   }
   
   public int[][] findPath(int startX, int startY, int endX, int endY){
      int mapH = Map.length;
      int mapW = Map[0].length;
      int[] temp = {};
      openTileObject(startX, startY, temp, 0, 0, false);
      while(openList.length>0 && !isClosed(endX, endY)){
         //System.out.println("before: "+openList.length);
         int i = proximateTileObject();
         int nowY = openList[i][0];
         int nowX = openList[i][1];
         //System.out.println("nowY: "+nowY+"nowX: "+nowX);
         closeTileObject(nowX, nowY);
         for(int j=nowY-1;j<=nowY+1;j++){
            for(int k=nowX-1;k<=nowX+1;k++){
               //System.out.println("during -4: "+openList.length);
               if(j>=0 && j<mapH && k>=0 && !(j == nowY && k == nowX) && (ALLOW_DIAGONAL || j == nowY || k == nowX)){
                  //System.out.println("Map["+j+"]["+k+"]: "+Map[j][k]);
                  //System.out.println("before: "+k+"_"+j);
                  //System.out.println("during -3: "+openList.length);
                  if(Map[j][k] != 1){
                     //System.out.println("CHECKING:_x "+k+"_y "+j);
                     //System.out.println("during -2: "+openList.length);
                     if(!isClosed(k, j)){
                        //System.out.println("during -1: "+openList.length);
                        int movementCost = mapStatus[nowY][nowX].movementCost+((j == nowY || k == nowX ? HV_COST : D_COST)*1);
                        //System.out.println("during 0: "+openList.length);
                        if(isOpen(k, j)){
                           //System.out.println("OPEN: "+k+", "+j+" movementCost: "+movementCost);
                           //System.out.println("during 1: "+openList.length);
                           if(movementCost<=mapStatus[j][k].movementCost){
                              int[] tp = {nowY, nowX};
                              //System.out.println("PARENT:Y_"+nowY+"_X_"+nowX);
                              openTileObject(k, j, tp, movementCost, 0, true);
                           }
                           //System.out.println("during 2: "+openList.length);
                        }
                        else{
                           //System.out.println("NOT OPEN: x_"+k+", y_"+j);
                           int heuristic = (Math.abs(j-endY)+Math.abs(k-endX))*30;
                           int[] tp = {nowY, nowX};
                           openTileObject (k, j, tp, movementCost, heuristic, false);
                           //System.out.println("during 3: "+openList.length);
                        
                        }
                     }
                  }
               }
            }
         }
         //System.out.println("after: "+openList.length);
      }
      boolean pFound = isClosed(endX, endY);
      //System.out.println("pathFound: "+pFound);
      if(pFound){
         int[][] returnedPath = null;
         int nowY = endY;
         int nowX = endX;
         //System.out.println("BEFORE LOOP");
         while((nowX != startX || nowY != startY)){
            //System.out.println("ABOVE:NOW_X: "+nowX+"_NOW_Y: "+nowY);
            int[] pF = {nowY, nowX};
            returnedPath = push(returnedPath, pF);
            int newY = mapStatus[nowY][nowX].parent[0];
            int newX = mapStatus[nowY][nowX].parent[1];
            nowY = newY;
            nowX = newX;
            //System.out.println("BELOW:NOW_X: "+nowX+"_NOW_Y: "+nowY);
         
         }
         //System.out.println("AFTER LOOP");
         /**returnedPath = new int[pathArrayList.size()][2];
         for(int q=0;q<returnedPath.length;q++){
            int[] tp = new int[2];
            Integer[] tempIntegerArray = pathArrayList.get(q);
            for(int f=0;f<returnedPath[0].length;f++){
               Integer tempInteger = tempIntegerArray[f];
               tp[f] = tempInteger.intValue();
            }
            returnedPath[q] = tp;
         }
         //path = null;**/
         //System.out.println("Returned Path: "+returnedPath);
         return returnedPath;
        
      }
      else{
         return null;
      }
   }
   
   private boolean isOpen(int x, int y){
      return mapStatus[y][x].open;
   }
   
   private boolean isClosed(int x, int y){
      return mapStatus[y][x].closed;
   }
   
   private int proximateTileObject(){
      int minimum = 999999;
      int indexFound = 0;
      int thisF;
      TileObject thisTileObject;
      int i = openList.length;
      //System.out.println("i: "+i);
      while(i-- >0){
         thisTileObject = mapStatus[openList[i][0]][openList[i][1]];
         thisF = thisTileObject.heuristic+thisTileObject.movementCost;
         if(thisF <= minimum){
            minimum = thisF;
            indexFound = i;
         }
      }
      
      return indexFound;
   }
   
   private void closeTileObject(int x, int y){
      int len = openList.length;
      for(int i=0;i<len;i++){
         if((openList[i][0] == y) && (openList[i][1] == x)){
            openList = splice(openList, i, 1);
            break;           
         }
      }
      mapStatus[y][x].open = false;
      mapStatus[y][x].closed = true;
   }
   
   private int[][] splice( int[][] array, int index, int l){
      int[][] temp = new int[array.length-1][array[0].length];
      int len = index+l;
      for(int a=0;a<array.length;a++){
         if(a>=index && a < len){
            array[a] = null;
            //temp[a] = array[a];
         }
      }
      int t = 0;
      for(int a=0;a<array.length;a++){
         if(array[a] != null){
            temp[t] = array[a];
            t++;
         }
      }
   	
      return temp;
   }
   
   private void openTileObject(int x, int y, int[] parent, int movementCost, int heuristic, boolean replacing){
      //System.out.println("before: "+openList.length);
      if(!replacing){
         int[] temp = {y, x};
         openList = push(openList, temp);
         mapStatus[y][x].heuristic = heuristic;
         mapStatus[y][x].open = true;
         mapStatus[y][x].closed = false;
      }
      mapStatus[y][x].parent = parent;
      mapStatus[y][x].movementCost = movementCost;
      try{
         //System.out.println("parent: "+parent[0]+"_"+parent[1]);
      }
      catch(Exception e){
      }
   }
	
   private int[][] push(int[][] array, int[] item){
      int len;
      int[][] temp;
      //System.out.println("push_0: "+len);
      if(array != null && array.length != 0){
         len = array.length;
         //System.out.println("push_0: "+len);
         temp = new int[array.length+1][item.length];
         for(int p=0;p<len;p++){
            temp[p] = array[p];
         }
      }
      else{
         temp = new int[1][item.length];
      }
      //System.out.println("push_1: "+temp.length);
      temp[temp.length-1] = item;  
      return temp;
   }
	
   private class TileObject{
      boolean open = false;
      boolean closed = false;
      int heuristic = 0;
      int movementCost = 0;
      int[] parent = {};
   
      TileObject(){
      }
      TileObject(int[] p, int m, int h, boolean o, boolean c){
         parent = p;
         movementCost = m;
         heuristic = h;
         open = o;
         closed = c;
      }
   }
}