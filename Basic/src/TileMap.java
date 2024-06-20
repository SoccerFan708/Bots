import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class TileMap{
   int[][] Map;
   Tile[][] tiles;
   Tile[][][] Sects;
   int[][] SectorCoordinates;
   int tileWidth; int tileHeight;
   int Sectors;
   int SectorY; int SectorX;
   String[] TILE_FRAME_NAMES ;
   BufferedImage[] TILE_FRAMES;
   BufferedImage tileImage;
  
  
   
   void DEFAULT_FRAMES(){
      TILE_FRAMES = new BufferedImage[3];
      String[] fn =  {"0", "1", "2"};
      TILE_FRAME_NAMES = fn;
      Color[] Colors = {Color.white, Color.black, Color.gray};
      for(int b=0;b<TILE_FRAMES.length;b++){
         BufferedImage bimg = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
         Graphics gb = bimg.getGraphics();
         gb.setColor(Colors[b]);
         gb.fillRect(0, 0, tileWidth, tileHeight);
         gb.setColor(Color.black);
         gb.drawRect(0, 0, tileWidth, tileHeight);
         TILE_FRAMES[b] = bimg;
         tileImage = TILE_FRAMES[b];
      }
      
   }
 
   public TileMap(int[][] passedMap){
      Map = passedMap;
      tiles = new Tile[Map.length][Map[0].length];
      tileWidth = tileHeight = 30;
      DEFAULT_FRAMES();
      buildMap();
   }

	
   public TileMap(int[][] passedMap, int width, int height){
      Map = passedMap;
      tiles = new Tile[Map.length][Map[0].length];
      tileWidth = width;
      tileHeight = height;
      DEFAULT_FRAMES();
      buildMap();
   }
 
   public TileMap(Object[][][] passedMap, int width, int height){
      Map = new int[passedMap.length][passedMap[0].length];
      tiles = new Tile[Map.length][Map[0].length];
      for(int y=0; y<passedMap.length;y++){
         for(int x=0; x<passedMap[0].length;x++){
            String temp = passedMap[y][x][0].toString();
            int TILE_TYPE = Integer.parseInt(temp);
            String TILE_FRAME_NAME = passedMap[y][x][1].toString();
            Map[y][x] = TILE_TYPE;
            tiles[y][x] = new Tile(x, y,TILE_TYPE, TILE_FRAME_NAME);
         }
      }
   }  
   	
   void buildMap(){
      for(int y=0;y<Map.length;y++){
         for(int x=0;x<Map[0].length;x++){
            String frm = String.valueOf(Map[y][x]);
            tiles[y][x] = new Tile(x, y, Map[y][x], frm);
         }
      }
   }
   
   public void makeDoors(int[] doors){
      for(int d=0;d<doors.length;d++){
         int doorInt = doors[d];
         for(int y=0;y<Map.length;y++){
            for(int x=0;x<Map[0].length;x++){
               if(Map[y][x] == doorInt){
                  System.out.println("Door at _x: "+x+"_y: "+y);
                  tiles[y][x].door = true;
               }
            }
         }
      }
   }
    
      
   public void setTileFrames(BufferedImage[] bufferedImages, String[] frameNames){
      TILE_FRAMES = bufferedImages;
      TILE_FRAME_NAMES = frameNames;
      for(int y=0;y<tiles.length;y++){
         for(int x=0;x<tiles[0].length;x++){
            tiles[y][x].makeTile();
         }
      }
   
   }
   
   public void makeSectors(int sectorWidth, int sectorHeight){
      int sctX = Map[0].length/sectorWidth;
      int sctY = Map.length/sectorHeight;
      Sectors = sctX*sctY;
      SectorCoordinates = new int[Sectors][2];
      //Sects = new Tile[Sectors][sectorHeight][sectorWidth];
      int strtX = 0; int strtY = 0;
      int endX = sectorWidth;int endY = sectorHeight;
      for(int s=0;s<Sectors;s++){
         //Tile[][] temp = new Tile[sectorHeight][sectorWidth];
         if((s != 0) && (s+1)%sctX == 1){
            strtX = 0;
            strtY = strtY+sectorHeight;
         }
         SectorCoordinates[s][0] = strtX/sctX;
         SectorCoordinates[s][1] = strtY/sctY;
      
         for(int y=0;y<sectorHeight;y++){
            for(int x=0;x<sectorWidth;x++){
               tiles[strtY+y][strtX+x].sector = s;
               tiles[strtY+y][strtX+x].sector_tiley = y;
               tiles[strtY+y][strtX+x].sector_tilex = x;
               //System.out.println("Sect: "+s+" strty: "+(strtY+y)+" strtx: "+(strtX+x));
               //temp[y][x] = tiles[strtY+y][strtX+x];
            }
         }
         //Sects[s] = temp;
         strtX = strtX+sectorWidth;
      }
   }
	
   public class Tile{
      Sprite sprite;
      BufferedImage bufferedImage;
      Image image;
      int tilex; int X; 
      int tiley; int Y;
      int sector;
      int sector_tiley; int sector_tilex;
      boolean walkable;
      boolean occupied;
      boolean door;
      String frame;
      int type = 0;
      boolean visible = true;
      
      //INTERACTIVE TILE
      boolean interactive;
      int effect_type;
      int effect;
      
     
      public Tile(int x, int y){
         tilex = x; 
         tiley = y;
         X = tilex*tileWidth;
         Y = tiley*tileHeight;
         makeTile();
      }
      
      public Tile(int x, int y, int typ, String frm){
         tilex = x; 
         tiley = y;
         X = tilex*tileWidth;
         Y = tiley*tileHeight;
         type = typ; 
         frame = frm;
         makeTile();
      }
      
      void makeTile(){
         sprite = new Sprite(TILE_FRAMES);
         switch(type){
         //Empty Tile
            case 0: walkable = true;
               break;
               //Wall
            case 1: walkable = false;
               break;
               //Interactive Tile
            case 2: walkable = false;
               interactive = true;
               effect_type = 0;
               effect = 1;
               break;
         }
      
         if(visible){
            if(frame != null){
               goToAndStop(frame);
            }
            else{
               goToAndStop(type);
            }
         }else{
            goToAndStop(2);
         }
         bufferedImage = sprite.spriteBufferedImage;
         image = sprite.spriteImage;
      }
      
      void goToAndStop(int frm){
         if(frm<=sprite.frames){
            sprite.goToAndStop(frm);
         }
         else{
            sprite.goToAndStop(sprite.frames);
         }
      }
      void goToAndStop(String fmn){
         //System.out.println(fmn);
         int frm = sprite.frames-1;
         for(int n=0;n<TILE_FRAME_NAMES.length;n++){
            if(fmn.equals(TILE_FRAME_NAMES[n])){
               frm = n;
            }
         }
         sprite.goToAndStop(frm);
      }
      
      boolean equals(Tile newTile){
         if(newTile.tilex == tilex && newTile.tiley == tiley && newTile.type == type){
         
            return true;
         }else{
         
            return false;
         }
      }
   }
   
   public class Coordinate{
      int X;
      int Y;
   
      public Coordinate(){
         X = Y = 0;
      }
      public Coordinate(int xC, int yC){
         X = xC;
         Y = yC;
      }
      public Coordinate(int[] points){
         X = points[0];
         Y = points[1];
      }
   }
   
}
 
