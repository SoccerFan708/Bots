import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
 
public class Bot implements Runnable{
 //Image Array[Direction Bot is Facing][Corresponding Animation Frames]
   BufferedImage[][] Animation_Images_buffered;
   Image[][] Animation_Images;
   static int speed = 5;
   int dirx = 0;int diry = 1;
   int currentDirection;int previousDirection;
   int _x; int _y;
   int _xtile; int _ytile;
   int _width; int _height;
   int searchX = 0;int searchY = 0;
   int home_x = -1;int home_y = -1;
   boolean[][] Keys;
   TileMap.Tile[][] World; int wrldTileW; int wrldTileH;
   boolean[][] tilesSearched;
   Food[][] Foods;
   int[] Current_Frame_Cycle = {0, 1, 2, 3, 4, 5};
   int[] Stand_Cycle = {0};
   int[] Move_Cycle = {1, 2, 3, 4, 5} ;
   int[] Sleep_Cycle = {2, 3};
   int[] Eat_Cycle = {4, 5};
   Sprite sprite;
   PathFinder pathFinder;
   int PathTargetX;
   int PathTargetY;
   int[][] Path;
   boolean buffered;
   //Bot Character Traits
   int Age = 0; 
   int Gender = 1; 
   int Type = 1; 
   int Health = 100;
   int Capacity = 1; 
   int Intelligence = 100; 
   int LOS = 3;
   int Fitness = 100;
   //Needs
   int Hunger = 100;
   int Fatigue = 100;
   Thread timeKeeping;
   Thread needTimer;
   long time;
   int fatigueTime = 101;
   int hungerTime = 30;
   int fatigueTimer = 0;
   int hungerTimer = 0;
   int eatTimer = 0;
   int sleepPeriod = 10;int sleepTimer = 0;
   boolean foodFound;
   boolean eating;
   boolean sleeping;
   boolean botSleep;
   boolean moving;
   boolean home;
   String name;
	
 
   private void SET_DEFAULTS(){
   }
   
   private void makeGenuine(){
      String[] colors = {"blue", "red", "yellow", "green", "pink", "cyan"};
      int[] Frames = new int[6];
      boolean alreadyPicked = false;
      boolean pickAgain = true;
      for(int c=0;c<Frames.length;c++){
         Frames[c] = c;
      }
      Frames = randomnise(Frames);
      name = colors[Frames[0]]+"_bot";
      Current_Frame_Cycle = Frames;
      int[] sc = {Frames[0]};
      Stand_Cycle = sc; 
      int[] mc = {Frames[1], Frames[2], Frames[3], Frames[4], Frames[5]};
      Move_Cycle = mc;
      int[] sc2 = {Frames[2], Frames[3]};
      Sleep_Cycle = sc2;
      int[] ec = {Frames[4], Frames[5]};
      Eat_Cycle = ec;
      sc = null;
      mc = null;
      sc2 = null;
      ec = null;
   }
   
   private int[] randomnise(int[] array){
      int[] temp = new int[array.length];
      for(int i=0;i<temp.length;i++){
         temp[i] = -1;
      }
      for(int q=0;q<array.length;q++){
         int p = (int)(Math.random()*array.length);
         while(temp[p] != -1){
            p = (int)(Math.random()*array.length);
         }
         temp[p] = array[q];
      }
      return temp;
   }
 
   public Bot(){
   }
   
   public Bot(Image[][] passed_Images){
      Animation_Images = passed_Images;
      buffered = false;
   }
   

   
   public Bot(BufferedImage[][] passed_Buffered_Images){
      Animation_Images_buffered = passed_Buffered_Images;
      buffered = true;
      makeGenuine();
   }
   public Bot(BufferedImage[][] passed_Buffered_Images, int width, int height){
      Animation_Images_buffered = passed_Buffered_Images;
      buffered = true;
      _width = width;
      _height = height;
      makeGenuine();
   }
   
   public void addKey(int[] key){
      Keys[key[1]][key[0]] = true;
   }
 
   public void orientBot(TileMap.Tile[][] wrld, Food[][] foods, int tw, int th){
      World = wrld;
      Foods = foods;
      if(tilesSearched == null){
         //System.out.println("CREATE TILE SEARCH");
         tilesSearched = new boolean[World.length][World[0].length];
      }
      
      if(Keys == null){
         Keys = new boolean[World.length][World[0].length];
      }
      
      for(int s=0;s<World.length;s++){
         for(int t=0;t<World[0].length;t++){
            if(Foods[s][t] != null){
               tilesSearched[s][t] = false;
            }
            
         }
      }
   	//System.out.println(Foods);
      wrldTileW = tw;
      wrldTileH = th;
      //pathFinder = new PathFinder(World);
   }
 
   void placeBot(int xtile, int ytile){
      if(home_x == -1 && home_y == -1){
         home_x = xtile;
         home_y = ytile;
      }
      
      _xtile = xtile;
      _ytile = ytile;
      //System.out.println("WIDTH: "+_width);
      _x = ((_xtile*wrldTileW)+(wrldTileW/2))-(_width/2);
      _y = ((_ytile*wrldTileH)+(wrldTileH/2))-(_height/2);
      updateBot(Stand_Cycle, dirx, diry);
      //findPath(_xtile, _ytile, 8, 8);
   }
   
   public void run(){
   }  
	
   void taxNeed(){
      if(!eating){
         hungerTimer++;
         if(hungerTimer == hungerTime){
            if(Hunger-1 >= 0){
               Hunger--;
            }            
            hungerTimer = 0;
            //System.out.println("HUNGER: "+Hunger);
         
         }
      }
      if(!sleeping){
         fatigueTimer++;
         if(fatigueTimer == fatigueTime){
            if(Fatigue-1 >= 0){
               Fatigue--;
            }
            
            if(Fatigue <= 90){
               botSleep = true;
               //System.out.println(name+" sleep");
            }
            fatigueTimer = 0;
            //System.out.println("FATIGUE: "+Fatigue);
         }
      }
      else{
         fatigueTimer = 0;  
      }
   }
	
	
        
         
   int prevStartX= 0; int prevStartY = 0; int prevEndX = 0; int prevEndY = 0;
   void findPath(int startX, int startY, int endX, int endY){
      //if((startX != prevStartX || startY != prevStartY) || (endX != prevEndX || endY != prevEndY) ){
         //prevStartX = startX;
         //prevStartY = startY;
         //prevEndX = endX;
         //prevEndY = endY;
      Path = null;
      pathFinder = new PathFinder(getMap());
      PathTargetX = endX;
      PathTargetY = endY; 
      if(Path == null || Path.length == 0){
         //System.out.println(name+":::Path to==> x: "+endX+" y: "+endY);
         Path = pathFinder.findPath(startX, startY, endX, endY);
         getNextTarget();  
      }
      //}
      //else{
         //System.out.println(name+"the same");
         //getNextTarget(); 
      //}
   }
   
   boolean checkPath(int startX, int startY, int endX, int endY){
      boolean check = false;
      pathFinder = new PathFinder(getMap());
      int[][] p = pathFinder.findPath(startX, startY, endX, endY);
      if(p != null){
         check = true;
      }
      
      return check;
   }
   

   int[][] getMap(){
      int[][] Map = new int[World.length][World[0].length];
      for(int y=0;y<Map.length;y++){
         for(int x=0;x<Map[0].length;x++){
            if((!World[y][x].door && World[y][x].walkable == false)||(World[y][x].door && !Keys[y][x]) || World[y][x].occupied == true){
               Map[y][x] = 1;
            }
            else{
               Map[y][x] = 0;
            }
         }
         
      }
      return Map;
   }
   
           
   int[][] splice( int[][] array, int index, int l){
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

 
   void botBrain(){
      DEFAULT_BRAIN();
   }
   
   
   void DEFAULT_BRAIN(){
      if(moving){
         if(!onTile(_targetX, _targetY)){
            move(dirx, diry);
         } 
         else{
            getNextTarget();
         }
         //fatigueTime = 50;
      }
      else{
         //fatigueTime = 101;
         if(!botSleep){
            if(!foodFound){
               if(Hunger <=90){
                  if(!eating && !sleeping){
                     findFood();
                  }
               }
               else{
                  if(!onTile(home_x, home_y)){
                     findPath(_xtile, _ytile, home_x, home_y);  
                  }
                  else{
                     stand();  
                  }
               }
            }
            else{
               System.out.println(name+"x: "+_xtile+" y: "+_ytile+"food Found");
               if(onTile(PathTargetX, PathTargetY)){
               //System.out.println(name+" food found");
                  eat(Foods[PathTargetY][PathTargetX]);
               }
               else{
                  findPath(_xtile, _ytile, PathTargetX, PathTargetX);  
               }
            }
         }   
         else{
            if(!sleeping){
               findPath(_xtile, _ytile, home_x, home_y); 
            }
            if(onTile(home_x, home_y)){
               sleep();
            }    
         }
      }
      taxNeed();
   }
   
   int prevDiry = 0; int prevDirx = 0;
   int _targetX = _xtile; int _targetY = _ytile;
   int prevtargetX; int prevtargetY;
   private void getNextTarget(){
      if(Path != null && Path.length>0){
         int i = Path.length-1;
         //int Y = Path[i][0];
         //int X = Path[i][1];
         
         _targetX = Path[i][1];
         _targetY = Path[i][0];
         Path = splice(Path, i, 1);
         prevtargetX = _targetX;
         prevtargetY = _targetY;
            
         prevDiry = diry;
         prevDirx = dirx;
         
         diry = _targetY - _ytile;
         dirx = _targetX - _xtile;
          
         checkDirection();
                    //System.out.println(name+":::Targetx: "+_targetX+" Targety: "+_targetY);
         moving = true;
      }
      else{
         _targetY = _ytile;
         _targetX = _xtile;  
         moving = false;
      }
       
      //System.out.println("DIRX: "+dirx+"_DIRY: "+diry);
   }
   
   private void checkDirection(){
      if((diry != 0 && dirx != 0) || diry > 1 || dirx > 1 || diry < -1 || dirx < -1 || (diry == 0 && dirx == 0)){
      
         dirx = prevDirx;
         diry = prevDiry;
         placeBot(_xtile, _ytile);            
         findPath(_xtile, _ytile, PathTargetX, PathTargetY);
      }
   }

	
   private boolean onTile(int tx, int ty){
      int des_x = ((tx*wrldTileW)+(wrldTileW/2))-(_width/2);
      int des_y = ((ty*wrldTileH)+(wrldTileH/2))-(_height/2); 
      if(_x == des_x && _y == des_y){
         return true;
      }
      else{
         return false;  
      }
   }
   
   public void move(int xdir, int ydir){
      int up = getYtile(_y+(speed*ydir));
      int down = getYtile(_y+_height+(speed*ydir));
      int left = getXtile(_x+(speed*xdir));
      int right = getXtile(_x+_width+(speed*xdir));
      boolean moveOnX = false;
      boolean moveOnY = false;
      boolean findNewPath = false;
      if(xdir == 1){
         if(right != _xtile){
            if((World[_ytile][right].walkable || (World[_ytile][right].door && Keys[_ytile][right])) && !World[_ytile][right].occupied){
               moveOnX = true;
            }
            else{
               findNewPath = true;
            }
         }
         else{
            moveOnX = true;  
         }
      }
      if(xdir == -1){
         if( left != _xtile){
            if((World[_ytile][left].walkable || (World[_ytile][left].door && Keys[_ytile][left])) && !World[_ytile][left].occupied){
               moveOnX = true;
            }
            else{
               findNewPath = true;
            }
         }
         else{
            moveOnX = true;  
         }
      }
      if(ydir == 1){
         if(down != _ytile){
            if((World[down][_xtile].walkable || (World[down][_xtile].door && Keys[down][_xtile])) && !World[down][_xtile].occupied){
               moveOnY = true;
            }
            else{
               findNewPath = true;
            }
               
         }
         else{
            moveOnY = true;  
         }
      }
      if(ydir == -1){
         if(up != _ytile){
            if((World[up][_xtile].walkable || (World[up][_xtile].door && Keys[up][_xtile])) && !World[up][_xtile].occupied){
               moveOnY = true;
            }
            else{
               findNewPath = true;
            }
         }
         else{
            moveOnY = true;  
         }
      }
      if(moveOnX){
         _x += speed*xdir;
      } 
      if(moveOnY){
         _y += speed*ydir;
      } 
      
               
      if(findNewPath){
               //System.out.println(name+"_x"+_xtile+"_y"+_ytile+" stuck");
         placeBot(_xtile, _ytile);
         moving = false;
               //stand();          
      }
      else{
         updateBot(Move_Cycle, xdir, ydir);
         sprite.nextFrame();
      }
   }
   

   public void stand(){
       //System.out.println(name+" xtile: "+_xtile+" ytile: "+_ytile);
      moving = false;
      updateBot(Stand_Cycle, dirx, diry);
      sprite.nextFrame();
      //System.out.println(sprite.currentFrame);
   }
   

	
   void sleep(){
      sleepTimer++;
      if(Fatigue < 100){
         moving = false;
         sleeping = true;
         if(sleepTimer == sleepPeriod){
            if((Fatigue+1)<=100){
               Fatigue++;
            } 
            else{
               Fatigue = 100;
            }
            sleepTimer = 0;
         }
         updateBot(Sleep_Cycle,  dirx, diry);
         sprite.nextFrame();
         System.out.println(name+ " sleeping so fatigue "+Fatigue);
      } 
      else{
         sleeping = false;
         sleepTimer = 0;
         botSleep = false;  
      }
   }
   
   void findFood(){
      stand();  
      searchLOS();
      if(!foodFound){
         boolean stopSearch = false;
         //int stX = 0; int stY = 0; int ndX = World[0].length; int ndY = World.length;
         int stX = World[0].length-1; int stY = World.length-1; int ndX = 0; int ndY = 0;
         for(int b=stY;b>=ndY;b--){
            for(int a=stX;a>=ndX;a--){
               int X = a;
               int Y = b;
               if(_ytile < (World.length/2)){
                  X = stX - a;
                  Y = stY - b;
               }
               if(World[Y][X].walkable && World[Y][X].occupied == false){
                  if(tilesSearched[Y][X] != true){
                     //System.out.println("unsearched: x:"+a+"_y:"+b);
                     searchX = X;
                     searchY = Y;
                     //if(!onTile(X, Y)){
                     findPath(_xtile, _ytile, X, Y);
                        //System.out.println("PATH FOUND");
                     //}
                     if(Path != null){
                        stopSearch = true;
                        break;
                     }
                     else{
                        findPath(_xtile, _ytile, home_x, home_y);  
                     }
                     
                  }
                  //System.out.println(Path[0][0]+"_"+Path[0][1]);
               }
               if(stopSearch){
                  break;
               }
            }
            //System.out.println("Player==> xtile: "+_xtile+"ytile"+_ytile);  
            if(onTile(searchX, searchY) && tilesSearched[searchY][searchX] != true){
               //System.out.println(tilesSearched[searchY][searchX]);
               //System.out.println("BEGIN SEARCH LOS");
               stand();
               searchLOS();
               //System.out.println("END SEARCH LOS");
               tilesSearched[searchY][searchX] = true;
            }  
         }
         if(!stopSearch){
            for(int s=0;s<tilesSearched.length;s++){
               for(int t=0;t<tilesSearched.length;t++){
                  tilesSearched[s][t] = false;
               }
            }
         }	
      }
   }
   
   void findFoodNew(){
     
      int nextX = (LOS+1)*dirx;
      int nextY = (LOS+1)*diry; 
      if(nextX >= 0 && nextX < World[0].length && nextY >= 0 && nextY < World.length){
         if( dirx != 0){
            if(World[_ytile][nextX].walkable && !World[_ytile][nextX].occupied){
               System.out.println(name+"Outside of LOSx");
               findPath(_xtile, _ytile, nextX, _ytile);
            }
            else{
               turnBack();  
               System.out.println(name+" turn backX");
            }
         }
         else{
            if(World[nextY][_xtile].walkable && !World[nextY][_xtile].occupied){
               System.out.println(name+"Outside of LOSy");
               findPath(_xtile, _ytile, _xtile, nextY);
            }
            else{
               turnBack();  
               System.out.println(name+" turn backY");
            }
         }
      }
      
   }
	
   void turnBack(){
      dirx = dirx*-1;
      diry = diry*-1;
   }
   
   void turnRight(){
      if(diry != 0){
         dirx = diry*-1;
         diry = 0;
      }
      else{
         diry = dirx;
         dirx = 0;  
      }
   }
	
   Food currentFood;
   public void eat(Food food){
      if(food != null){
         Foods[_ytile][_xtile].beingEaten = true;
         currentFood = food;
         hungerTimer = 0;
         //eatTimer = 0;
         eating = true;
      }
      if(currentFood != null && eatTimer < 10){
         updateBot(Eat_Cycle,  dirx, diry);
         sprite.nextFrame();
         currentFood.value--;
         if(Hunger+1 <= 100){
            Hunger++;  
         }
         else{
            Hunger = 100;
         }
         eatTimer++;            
         //System.out.println("EAT: "+eatTimer+":*:*:Hunger: "+Hunger);
      }
      else{
         Foods[_ytile][_xtile] = null;
         eatTimer = 0;  
         currentFood = null;
         eating = false;
         foodFound = false;
      }
      
   }
   
   private void searchLOS(){
      if(Foods[_ytile][_xtile] == null){
         for(int i=-LOS; i<=LOS;i++){
            for(int j=-LOS;j<=LOS;j++){
               int los = Math.abs(j)+Math.abs(i);
               if(los<=LOS){
                  int X = _xtile+j;
                  int Y = _ytile+i;
                  if(((X>=0) && (X< Foods[0].length)) && ((Y>=0)&&(Y<Foods.length))){
                     if(Foods != null && Foods[Y][X] != null){
                        if(!Foods[Y][X].beingEaten){
                           boolean check = checkPath(_xtile, _ytile, X, Y);
                           if(check){
                              foodFound = true;
                              findPath(_xtile, _ytile, X, Y);
                           }
                           else{
                              foodFound = false;
                           }
                           
                        }
                     }
                     else{
                        tilesSearched[Y][X] = true;
                     }
                  
                  }
               }
            }
         }
      }
      else{
         if(foodFound){
            System.out.println("food Found");
         }
         foodFound = true;
         //System.out.println("Food found at: "+_xtile+":x_y:"+_ytile+" by "+name);
         //findPath(_xtile, _ytile, X, Y);
      }
   }
            
   private void updateBot(int[] cycle, int xdir, int ydir){
      //System.out.println(xdir+" :xdir_ydir: "+ydir);
      changeDirection(xdir, ydir);
      changeFrameCycle(cycle);
      updateBotSize();
      updateBotLocationTile();
   }
   private void changeDirection(int xdir, int ydir){
      dirx = xdir;
      diry = ydir;
      previousDirection = currentDirection;
      currentDirection = (dirx+diry*2+3)-1;    
      if(currentDirection != previousDirection){
         updateBotSprite();
      }
      previousDirection = currentDirection;
   }
   private void changeFrameCycle(int[] cycle){
      boolean check = Arrays.equals(Current_Frame_Cycle, cycle);
      if(check == false){
         Current_Frame_Cycle = cycle;
         updateBotSprite();
      }
   
   }  

   
   void updateBotLocationTile(){
      _xtile = getXtile(_x+(_width/2));
      _ytile = getYtile(_y+(_height/2));
   
      //name = name+"_"+_xtile+"_ "+_ytile;
      //System.out.println("XTILE: "+_xtile+"YTILE: "+_ytile);
   }
   
   int getXtile(int x){
      return x/wrldTileW;
   }
   int getYtile(int y){
      return y/wrldTileH;
   }
   
   void updateBotSize(){
      _width = sprite.width;
      _height = sprite.height;
   }
   
   void updateBotSprite(){
      if(buffered){
         //System.out.println("BIMG0: "+sprite.spriteBufferedImage);
         BufferedImage[] cDir = Animation_Images_buffered[currentDirection];
         sprite = new Sprite(cDir);
         //System.out.println("BIMG: "+sprite.spriteBufferedImage);
         sprite.setFrameCycle(Current_Frame_Cycle);
         //updateBotSize();
      }
      else{
         Image[] cDir = Animation_Images[currentDirection];
         sprite = new Sprite(cDir);
         sprite.setFrameCycle(Current_Frame_Cycle);
      }
   }
}
