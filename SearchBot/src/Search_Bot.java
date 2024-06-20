import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
 
public class Search_Bot extends Bot{
 //Image Array[Direction Bot is Facing][Corresponding Animation Frames]
   BufferedImage[][] Animation_Images_buffered;
   Image[][] Animation_Images;
   BufferedImage botImage;
   
   
   
   int speed = 1;
   int dirx = 0;int diry = 1;
   int currentDirection;int previousDirection;
   int _x; int _y;
   int _xtile; int _ytile;
   int _width; int _height;
   int searchX = 0;int searchY = 0;
   int home_x = -1;int home_y = -1;
   boolean[][] Keys;
   TileMap.Tile[][] World; int wrldTileW; int wrldTileH;
   TileMap.Tile[][] InternalWorldMap;
   ArrayList<TileMap.Tile> FamiliarTiles;
   boolean[][] tilesSearched;
   int[] Current_Frame_Cycle = {0};
   int[] Stand_Cycle = {0};
   int[] Move_Cycle = {0} ;
   int[] Sleep_Cycle = {0};
   int[] Eat_Cycle = {0};
   Sprite sprite;
   PathFinder pathFinder;
   int PathTargetX;
   int PathTargetY;
   int[][] Path;
   boolean buffered = true;
   //Bot Character Traits
   int type;
   int LOS;
   double power = 100;
   //int[] home;
   //Needs

   Thread timeKeeping;

   long time;
   boolean moving;
   boolean home;
   String name;
   
   //WORKER
   boolean homeFound = false;
   boolean goingHome = false;
   boolean onTask = false;
   boolean goingToTask = false;
   boolean stuck = false;
  
 
   private void DEFAULT(){
      type = 1;
      LOS = 3;
      _width = _height = 30;
      wrldTileW = wrldTileH = 30;
      CREATE_BOT_IMAGE();
   }
   
   private void CREATE_BOT_IMAGE(){
      BufferedImage[][] botImages = new BufferedImage[5][6];
      //BufferedImage temp = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
      //Graphics tempG = temp.createGraphics();
      
      int[][] xPoints;
      int[][] yPoints;
      switch(type){
         case 1:
            //tempG.setColor(Color.black);
            //tempG.fillOval(0, 0, 30, 30);
            //tempG.drawOval(0, 0, 30, 30);
            //tempG.setColor(Color.gray);
            //tempG.fillOval(5, 5, 20, 20);
            //tempG.drawRect(0, 10, 30, 10);
            //tempG.setColor(Color.black);
            //tempG.fillRect(0, 10, 30, 10);
            //tempG.setColor(Color.yellow);
            //tempG.fillRect(10, 10, 10, 10);
            
            
         
                  //[[Up][Left][][Right][Down]]
                  /*
            int[][] MxPs = {{0, 15, 30}, {0, 30, 30}, {0, 0, 0}, {0, 0, 30}, {0, 15, 30}};
            xPoints = MxPs;
            int[][] MyPs = {{30, 0, 30}, {15, 0, 30}, {0, 0, 0}, {0, 30, 15}, {0, 30, 0}};
            yPoints = MyPs;
            
            */
            
            int[][] MxPs = {{5, 15, 25}, {5, 25, 25}, {0, 0, 0}, {5, 5, 25}, {5, 15, 25}};
            xPoints = MxPs;
            int[][] MyPs = {{25, 5, 25}, {15, 5, 25}, {0, 0, 0}, {5, 25, 15}, {5, 25, 5}};
            yPoints = MyPs;
            
           /* */
            
            for(int b=0;b<botImages.length;b++){
               BufferedImage[] temp = new BufferedImage[6];
               for(int i=0;i<botImages[0].length;i++){
                  BufferedImage bot = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_ARGB);
                  Graphics gbot = bot.createGraphics();
                  gbot.setColor(Color.black);
                  gbot.fillPolygon(xPoints[b], yPoints[b], 3);
                  gbot.setColor(Color.red);
                  gbot.drawPolygon(xPoints[b], yPoints[b], 3);
               
                  temp[i] = bot;
               }
               botImages[b] = temp;
            }
           
                        
            break;
            
         case 2:
            //tempG.setColor(Color.red);
            //tempG.fillOval(5, 5, 20, 20);
            //tempG.setColor(Color.black);
            //tempG.drawOval(5, 5, 20, 20);
            
         
            //[[Up][Left][][Right][Down]]
            int[][] WxPs = {{5, 15, 25}, {5, 25, 25}, {0, 0, 0}, {5, 5, 25}, {5, 15, 25}};
            xPoints = WxPs;
            int[][] WyPs = {{25, 5, 25}, {15, 5, 25}, {0, 0, 0}, {5, 25, 15}, {5, 25, 5}};
            yPoints = WyPs;
            
           /* /////////////////////////////
            int[][] WxPs = {{0, 15, 30}, {0, 30, 30}, {0, 0, 0}, {0, 0, 30}, {0, 15, 30}};
            xPoints = WxPs;
            int[][] WyPs = {{30, 0, 30}, {15, 0, 30}, {0, 0, 0}, {0, 30, 15}, {0, 30, 0}};
            yPoints = WyPs;
            ////////////////////////////*/
            for(int b=0;b<botImages.length;b++){
               BufferedImage[] temp = new BufferedImage[6];
               for(int i=0;i<botImages[0].length;i++){
                  BufferedImage bot = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_ARGB);
                  Graphics gbot = bot.createGraphics();
                  gbot.setColor(Color.red);
                  gbot.fillPolygon(xPoints[b], yPoints[b], 3);
                  gbot.setColor(Color.black);
                  gbot.drawPolygon(xPoints[b], yPoints[b], 3);
               
                  temp[i] = bot;
               }
               botImages[b] = temp;
            }
         
            
            
            break;
      }
      
      //botImage = temp;
      //temp = null;
      buffered = true;
      Animation_Images_buffered = botImages;
      
   
   }
   
   //SEARCH BOT
 
   public Search_Bot(){
      DEFAULT();
   }
   
   public Search_Bot(int typ){
      buffered = true;
      type = typ;
      //LOS = 3;
      _width = _height = 30;
      wrldTileW = wrldTileH = 30;
      CREATE_BOT_IMAGE();
      
      switch(typ){
         case 1: speed = 1;
            LOS = 5;
            break;
         case 2: speed = 5;
            LOS = 1;
            break;
      }
   }
   
   
/**
   
   public void addKey(int[] key){
      Keys[key[1]][key[0]] = true;
   }
 **/
   public void orientBot(TileMap.Tile[][] wrld, Search_Bot[] wrldBots, int tw, int th){
      World = wrld;
      WorldBots = wrldBots;
      if(tilesSearched == null){
         //System.out.println("CREATE TILE SEARCH");
         tilesSearched = new boolean[World.length][World[0].length];
      }
      
      if(Keys == null){
         Keys = new boolean[World.length][World[0].length];
      }
      
           	//System.out.println(Foods);
      wrldTileW = tw;
      wrldTileH = th;
      pathFinder = new PathFinder(World);
   }
   
 ///*
   public void placeBot(int xtile, int ytile){
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
   //*/


	
	
	
        
// /*       
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
         System.out.println("Path from x: "+startX+" y: "+startY+" to==> x: "+endX+" y: "+endY);
         Path = pathFinder.findPath(startX, startY, endX, endY);
         //GOING HOME
         getNextTarget();  
      }
      //}
      //else{
         //System.out.println(name+"the same");
         //getNextTarget(); 
      //}
   }
  // */
   
   
   boolean checkPath(int startX, int startY, int endX, int endY){
      boolean check = false;
      pathFinder = new PathFinder(getMap());
      int[][] p = pathFinder.findPath(startX, startY, endX, endY);
      if(p != null){
         check = true;
      }
      
      return check;
   }
   
   public void scanLOS(int xt, int yt, TileMap.Tile[][] WORLD){
      int LOSsize = (LOS*2)+1;
      int SIGHT_SQUARE = (LOSsize)*(LOSsize);
      int[][] inLOS = new int[SIGHT_SQUARE][2];
      TileMap.Tile[][] newSection = new TileMap.Tile[LOSsize][LOSsize];
      int tx = 0;
      int ty = 0;
      //System.out.println("LOS "+inLOS.length);
      for(int i=(-LOS);i<=LOS;i++){
         ty = yt+i;
         //inLOS[i+LOS][1] = ty;
         for(int j=(-LOS);j<=LOS;j++){
            tx = xt+j;
            inLOS[i+LOS][0] = tx;
            inLOS[i+LOS][1] = ty;
            if(ty >= 0 && ty < WORLD.length && tx >= 0 && tx < WORLD[0].length){
               if((i+LOS)<newSection.length && (i+LOS)>= 0 && (j+LOS)<newSection[i+LOS].length && (j+LOS) >= 0){
                  newSection[i+LOS][j+LOS] = WORLD[ty][tx];
               }
            }
         }
      }
      updateFamiliarTiles(newSection);
      updateInternalMap(newSection);
   }
   
   public void updateInternalMap(TileMap.Tile[][] newSection){
      if(InternalWorldMap == null){
         InternalWorldMap = newSection;
      }else{
         ArrayList<TileMap.Tile[]> newInternalMapList = new ArrayList<TileMap.Tile[]>();
         for(int i=0;i<InternalWorldMap.length;i++){
            ArrayList<TileMap.Tile> tempList = new ArrayList<TileMap.Tile> (Arrays.asList(InternalWorldMap[i]));
            for(int k=0;k<newSection.length;k++){
               for(int w=0;w<newSection[k].length;w++){
                  boolean found = false;
                  for(int j=0;j<InternalWorldMap[i].length;j++){
                     if((newSection[k][w] != null) && (InternalWorldMap[i][j] != null) && newSection[k][w].equals(InternalWorldMap[i][j])){
                        found = true;
                     }
                  }
                  if(!found){
                     tempList.add(newSection[k][w]);
                  }
               }
            }
            TileMap.Tile[] tempListArray = getArray(tempList);
            newInternalMapList.add(tempListArray);
         }
         InternalWorldMap = get2DArray(newInternalMapList);
      }
   }
   
   public void updateFamiliarTiles(TileMap.Tile[][] newSection){
      //if(FamiliarTiles == null){
         
      for(int f=0;f<newSection.length;f++){
         for(int q=0;q<newSection[f].length;q++){
            if(FamiliarTiles == null){
               FamiliarTiles = new ArrayList<TileMap.Tile>();
               FamiliarTiles.add(newSection[q][f]);
            }else{
               boolean found = false;
               for(int i=0;i<FamiliarTiles.size();i++){
                  if((newSection[q][f] != null) && (FamiliarTiles.get(i) != null) && newSection[q][f].equals(FamiliarTiles.get(i))){
                     found = true;
                  }
               }
               if(!found){
                  FamiliarTiles.add(newSection[q][f]);
               }
            }
         }
      }
      
   }
   
   TileMap.Tile[] getArray(ArrayList<TileMap.Tile> list){
      TileMap.Tile[] array = new TileMap.Tile[list.size()];
      for(int i=0;i<list.size();i++){
         array[i] = list.get(i);
      }
      return array;
   }
   
   TileMap.Tile[][] get2DArray(ArrayList<TileMap.Tile[]> list){
      TileMap.Tile[][] array = new TileMap.Tile[list.size()][list.get(0).length];
      for(int i=0;i<list.size();i++){
         array[i] = list.get(i);
      }
      return array;
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
  /** 
           
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
   **/
   
   
   Search_Bot[] WorldBots;
   Search_Bot Master;
   Search_Bot[] Workers = new Search_Bot[1];
 
 //CENTRAL BOT DECISION MAKING
   
   
   boolean scanned = false;
   void botBrain(){
      switch(type){
         case 1: 
            MASTER_BRAIN();
            MOVE();
            break;
         case 2: 
            WORKER_BRAIN();
            MOVE();
            //DEFAULT_BRAIN();
            break;
      } 
      
   }
   
   //MASTER FUNCTIONS
   
   void MASTER_BRAIN(){
      scanLOS(_xtile, _ytile, World);
      if(!scanned){
         SCAN_FOR_WORKERS();
         scanned = true;
      }
      MASTER_WORK();
      //MOVE();
      
      if(docked){
         NEST();
         CALL_ALL_WORKERS();
      }
   }
   
   void WORKER_BRAIN(){
      if(onTask || stuck){
         onTask = false;
         stuck = false;
         FIND_HOME();
      
      }
   }
   
   
   int[] randomSpot(){
      int[] spots = new int[2];
   /*
      System.out.println("internal map x: "+InternalWorldMap[0].length+" _y "+InternalWorldMap.length);
      int x = ((int)(Math.random()*InternalWorldMap[0].length));
      int y = ((int)(Math.random()*InternalWorldMap.length));
      
      if((InternalWorldMap[y][x] != null) && (InternalWorldMap[y][x].walkable)){   
         System.out.println("from "+_xtile+"_"+_ytile+" random spot "+InternalWorldMap[y][x].tilex+" _ "+InternalWorldMap[y][x].tiley);
         spots[0] = InternalWorldMap[y][x].tilex;
         spots[1] = InternalWorldMap[y][x].tiley;
      }else{
         System.out.println("random spot recursive");
         spots = randomSpot();
      }
      */
      
      int randomTile = (int)(Math.random()*FamiliarTiles.size());
      if(FamiliarTiles.get(randomTile) != null){
         int x = FamiliarTiles.get(randomTile).tilex;
         int y = FamiliarTiles.get(randomTile).tiley;
         if(((x != _xtile)||(y != _ytile)) && FamiliarTiles.get(randomTile).walkable){
            spots[0] = x;
            spots[1] = y;
         
            System.out.println("from "+_xtile+"_"+_ytile+" random spot "+spots[0]+" _ "+spots[1]);  
         }
      }else{
         System.out.println("random spot recursive");
         spots = randomSpot();
      }
      
      
      
      
      return spots;
   }
   
   int[][] Docks = new int[4][2];
   void NEST(){
      home_x = _xtile;
      home_y = _ytile;
      if(type == 1){
         int i = -1;
         int j = -1;
         for(int d=0;d<Docks.length;d++){
            if(j != 0 ){
               Docks[d][0] = _xtile+j;
               j += 1;
            }else{
               j += 1;
               Docks[d][0] = _xtile+j; 
            }
         
            if( i != 0){
               Docks[d][1] = _ytile+i;
               if(j == 1){
                  i += 1;
                  j = -1;
               }
            }else{
               i += 1;
               Docks[d][1] = _ytile+i;
            }
         //System.out.println("Dock "+d+":"+Docks[d][0]+"_"+Docks[d][1]+" @ "+_xtile+"_"+_ytile);
         
         }
      }
   }
   
   
   
   void SCAN_FOR_WORKERS(){
      for(int w=0;w<InternalWorldMap.length;w++){
         for(int v=0;v<InternalWorldMap[0].length;v++){
            for(int b=0;b<WorldBots.length;b++){
               if((WorldBots[b] != null) && (InternalWorldMap[v][w] != null) && WorldBots[b]._xtile == InternalWorldMap[v][w].tilex && WorldBots[b]._ytile == InternalWorldMap[v][w].tiley){
                     
                  if(WorldBots[b].type == 2 && WorldBots[b].Master == null){
                     HIRE(WorldBots[b]);
                     WorldBots[b].FIND_HOME();
                  }
               }
            }
         }
      }
   
   }
   
   boolean ARE_WORKERS_DOCKED(){
      int workersDocked = 0;
      for(int w=0;w<Workers.length;w++){
         if(Workers[w] != null && Workers[w].docked){
            workersDocked++;
         }
      }
         
      if(workersDocked == Workers.length){
         return true;
      }else{
         return false;
      }
      
   }
   
   int WORKERS_DOCKED(){
      int workersDocked = 0;
      for(int w=0;w<Workers.length;w++){
         if(Workers[w] != null && Workers[w].docked){
            workersDocked++;
         }
      }
      return workersDocked;
      
   }
   
   public void UNDOCK_WORKERS(){
      for(int w=0;w<Workers.length;w++){
         Workers[w].docked = false;   
      }
   }
   
   void MASTER_WORK(){
      boolean workersDocked = ARE_WORKERS_DOCKED();
      
      for(int w=0;w<Workers.length;w++){
         if((Workers[w] != null)){
         
            if(Workers[w].power < 100 && Workers[w].docked){
            //UNDOCK_WORKERS();
               Workers[w].power += speed*0.1;
               if(Workers[w].power > 100){
                  Workers[w].power = 100;
               }
               System.out.println(w+" power: "+Workers[w].power);
            }
         }
      }
      
      if(WORKERS_DOCKED() > 0){
         //UNDOCK_WORKERS();
         int[] newPos = randomSpot();
         for(int w=0;w<Workers.length;w++){
            if((Workers[w] != null)){
               if(Workers[w].docked && Workers[w].power >= 90){
               //Workers[w].docked = false;
                  System.out.println("TASK to "+newPos[0]+"_"+newPos[1]);
                  TASK(Workers[w], newPos[0], newPos[1]);
                  break;
               }
            } 
         }
      }
   }
   
   
   
   void CALL_ALL_WORKERS(){
      for(int w=0;w<Workers.length;w++){
         CALL_WORKER(Workers[w]);
      }
   }
   
   void CALL_WORKER(Search_Bot worker){
      worker.FIND_HOME();
      //System.out.println("Call me home");
   }
   
   /**MARK FOR REVISION AND OPTIMIZATION**/
   
   void HIRE(Search_Bot worker){
      Search_Bot[] tempWorkers;
      if(Workers[0] != null){
         tempWorkers = new Search_Bot[Workers.length+1];
         for(int s=0;s<Workers.length;s++ ){
            tempWorkers[s] = Workers[s];
         }
      }else{
         tempWorkers = Workers;
      }
      for(int t=0;t<tempWorkers.length;t++){
         if(tempWorkers[t] == null){
            worker.Master = this;
            if(t<Docks.length){
               worker.Dock = Docks[t];
               //worker.FIND_HOME();
            }
            tempWorkers[t] = worker;
         }
      }
      Workers = tempWorkers;
   }
   
   void TASK(Search_Bot worker, int targX, int targY){
      
      if((worker.docked || worker.onTask)){
         worker.docked = false;
         worker.onTask = false;
         worker.goingToTask = true;
         worker.goTo(targX, targY);
      }
   }
   
   //GENERAL FUNCTIONS
   
   void MOVE(){
      if(moving){
         if(!onTile(_targetX, _targetY)){
            move(dirx, diry);
         } 
         else{
            getNextTarget();
         }
         //fatigueTime = 50;
      }
   
   }
   
   
   void RELATIONSHIP(){
   }
   
   
   //SERVANT FUNCTIONS
   int[] Dock;
   boolean docked;
   void FIND_HOME(){
      if(Master != null && Dock != null){
         //System.out.println("Dock @ "+Dock[0]+"_"+Dock[1]);
         if(!homeFound){
            home_x = Dock[0];
            home_y = Dock[1];
            homeFound = true;
         }
         goingHome = true;
            //moving = true; 
         findPath(_xtile, _ytile, home_x, home_y);
            
         
      }
         
   }
   
   public void goTo(int nextXTile, int nextYTile){
      
      findPath(_xtile, _ytile, nextXTile, nextYTile);
      
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
         //docked = false;
         moving = true;
      }
      else{
         _targetY = _ytile;
         _targetX = _xtile;  
         moving = false;
         //scanLOS(_xtile, _ytile, World);
         if(goingHome){
            docked = true;
            goingHome = false;
         }
         
         if(goingToTask){
            onTask = true;
            goingToTask = false;
            //docked = true;
         }
      }
      scanLOS(_xtile, _ytile, World);
      //System.out.println("DIRX: "+dirx+"_DIRY: "+diry);
   }
   
   private void checkDirection(){
      if((diry != 0 && dirx != 0) || diry > 1 || dirx > 1 || diry < -1 || dirx < -1 || (diry == 0 && dirx == 0)){
      
         placeBot(_xtile, _ytile);            
         findPath(_xtile, _ytile, PathTargetX, PathTargetY);
         dirx = prevDirx;
         diry = prevDiry;
         
      }
   }

///*	
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
   //*/
   
   
   boolean findNewPath = false;
   public void move(int xdir, int ydir){
      int up = getYtile(_y+(speed*ydir));
      int down = getYtile(_y+_height+(speed*ydir));
      int left = getXtile(_x+(speed*xdir));
      int right = getXtile(_x+_width+(speed*xdir));
      boolean moveOnX = false;
      boolean moveOnY = false;
      //boolean findNewPath = false;
      if(xdir == 1){
         if(right != _xtile){
            if((World[_ytile][right].walkable && !World[_ytile][right].occupied)|| (World[_ytile][right].door && Keys[_ytile][right])) {
               moveOnX = true;
               findNewPath = false;
            }
            else{
               findNewPath = true;
            }
         }
         else{
            moveOnX = true; 
            findNewPath = false; 
         }
      }
      if(xdir == -1){
         if( left != _xtile){
            if((World[_ytile][left].walkable && !World[_ytile][left].occupied)|| (World[_ytile][left].door && Keys[_ytile][left])) {
               moveOnX = true;
               findNewPath = false;
            }
            else{
               findNewPath = true;
            }
         }
         else{
            moveOnX = true;  
            findNewPath = false;
         }
      }
      if(ydir == 1){
         if(down != _ytile){
            if((World[down][_xtile].walkable && !World[down][_xtile].occupied) || (World[down][_xtile].door && Keys[down][_xtile])) {
               moveOnY = true;
               findNewPath = false;
            }
            else{
               findNewPath = true;
            }
               
         }
         else{
            moveOnY = true;  
            findNewPath = false;
         }
      }
      if(ydir == -1){
         if(up != _ytile){
            if((World[up][_xtile].walkable && !World[up][_xtile].occupied) || (World[up][_xtile].door && Keys[up][_xtile]) ){
               moveOnY = true;
               findNewPath = false;
            }
            else{
               findNewPath = true;
            }
         }
         else{
            moveOnY = true;  
            findNewPath = false;
         }
      }
      if(moveOnX && power>0){
         _x += speed*xdir;
         power -= speed*0.01;
      } 
      if(moveOnY && power>0){
         _y += speed*ydir;
         power -= speed*0.01;
      } 
      
      if(power<0){
         power = 0;
      }
      
               
      if(findNewPath){
         System.out.println(name+"_x: "+_xtile+" _y: "+_ytile+" stuck going to _x: "+_targetX+" _y: "+_targetY);
         //placeBot(_xtile, _ytile);
         //moving = false;
         stuck = true;
         goingToTask = false;
         //FIND_HOME();
         //stand();   
         findNewPath = false;       
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
	
   
   
   
//SEARCH OF AREA OF VIEW
   
   int[][] searchLOS(int xt, int yt){
      int SIGHT_SQUARE = ((LOS*2)+1)*((LOS*2)+1);
      int[][] inLOS = new int[SIGHT_SQUARE][2];
      int tx = 0;
      int ty = 0;
      //System.out.println("LOS "+inLOS.length);
      for(int i=(-LOS);i<=LOS;i++){
         ty = yt+i;
         //inLOS[i+LOS][1] = ty;
         for(int j=(-LOS);j<=LOS;j++){
            tx = xt+j;
            inLOS[i+LOS][0] = tx;
            inLOS[i+LOS][1] = ty;
         }
      }
      
      return inLOS;
   }
      
   void updateBot(){
      CREATE_BOT_IMAGE();
   } 
   
   
            
   void updateBot(int[] cycle, int xdir, int ydir){
      //System.out.println(xdir+" :xdir_ydir: "+ydir);
      changeDirection(xdir, ydir);
      changeFrameCycle(cycle);
      updateBotSize();
      updateBotLocationTile();
   }
   void changeDirection(int xdir, int ydir){
      dirx = xdir;
      diry = ydir;
      previousDirection = currentDirection;
      currentDirection = (dirx+diry*2+3)-1;    
      if(currentDirection != previousDirection){
         updateBotSprite();
      }
      previousDirection = currentDirection;
   }
   void changeFrameCycle(int[] cycle){
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
