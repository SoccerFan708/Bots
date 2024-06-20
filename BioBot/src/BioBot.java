import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class BioBot extends JPanel implements Runnable{
/*****Bio Bot*****/
   int[][] Map;    
                 
   int[][] GENERATE_MAP(int width, int height){
   
      int[][] GENERATED_MAP = new int[height][width];
      
      for(int h=0;h<height;h++)
         for(int w=0;w<width;w++){
            if(h==0 || w==0 || h==(height-1) || w==(width-1)){
               GENERATED_MAP[w][h] = 1;
            }else{
               GENERATED_MAP[w][h] = 0;
            }
         }
         
      return GENERATED_MAP;
   }


   int tileW; int tileH;
   int mapX; int mapY;
   TileMap mapTiles;
   int SectorWidth; int SectorHeight;
   int currentSector;
   
   Image[][] Images = new Image[4][4];
   BufferedImage displayBoard;
   Image up; Image down; Image left; Image right;
   boolean imagesDone = false;
   int currentImage;
   String message = "Loading...";
   MediaTracker tracker = new MediaTracker(this);
   Thread loadThread = null;   
   
   
   Thread scrnRefresher;
   Bot[] Bots  = new Bot[5];
   Food[][] mapFoods;
   
   int botWidth; int botHeight;
   long time;

   boolean[][] OccupiedTiles;
   


   void load(){  
   
   //Create and Put Bots in an array  
      for(int b=0;b<Bots.length;b++){
         if(b == 0){
            Bots[b] = new Search_Bot(1);
         }else{
            Bots[b] = new Search_Bot(2);
         }
         int[] pos = randomSpot();
      
         placeBot(Bots[b], pos[0], pos[1]);
         //Bots[b].NEST();
      }
      
      //createSearchTeam(Bots, x, y);
      
   }
   
   int[] randomSpot(){
      int[] spots = new int[2];
   
      int x = (int)(Math.random()*Map[0].length);
      int y = (int)(Math.random()*Map.length);
      
      if(Map[y][x] == 0){   
         spots[0] = x;
         spots[1] = y;
      }else{
         spots = randomSpot();
      }
      return spots;
   }


   public void init(){
      setSize(1200, 1200);
      tileW = tileH = 30;
      SectorWidth = 30;
      SectorHeight = 30;
     // botWidth = botHeight = 20;
      ///System.out.println("Before ");
      Map = GENERATE_MAP(30, 30);
      //System.out.println("Width "+Map[0].length+" Height "+Map.length);
      //System.out.println("After ");
      mapTiles = new TileMap(Map, tileW, tileH);
      mapTiles.makeSectors(SectorWidth, SectorHeight);
      currentSector = 1;
      load();
      displayBoard = new BufferedImage(tileW*SectorWidth, tileH*SectorHeight, BufferedImage.TYPE_INT_ARGB);  
     
      OccupiedTiles = new boolean[mapTiles.tiles.length][mapTiles.tiles[0].length];
   }

   void worldCentral(){
      for(int b=0;b<Bots.length;b++){
         Bots[b].botBrain();
         updateWorldWithBot(Bots[b]);
      }
      updateWorld();
   }
      
      
      
   void placeBot(Bot bot, int xtile, int ytile){
      updateBot(bot);
      bot.placeBot(xtile, ytile);      
   }
   
   void updateBot(Bot bot){  
      //bot.orientBot(mapTiles.tiles, Bots, tileW, tileH);  
      //bot.updateBot();
   }
   
   void updateWorldWithBot(Bot bot){
      mapFoods = bot.Foods;
      OccupiedTiles[bot._ytile][bot._xtile] = true;
   }
   
   void updateWorld(){
      for(int s=0;s<mapTiles.tiles.length;s++){
         for(int t=0;t<mapTiles.tiles[0].length;t++){
            mapTiles.tiles[s][t].occupied = OccupiedTiles[s][t];
            OccupiedTiles[s][t] = false;
         }
      }
   }



/***************/








   private Thread animator;
   int x=0, y=0;
   private final int DELAY = 50;
   public BioBot(){
      JFrame jf = new JFrame();
      jf.setSize(1200,1200);
      jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      init();
      jf.add(this);
      jf.setVisible(true);
      
   }

   @Override
   public void addNotify() {
      super.addNotify();
      animator = new Thread(this);
      animator.start();
   }

   @Override
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      //if(imagesDone){
      Graphics2D g2 = (Graphics2D)g;
      Graphics2D dG = (Graphics2D)displayBoard.getGraphics();
         
      int depthDisplay = SectorHeight;
      mapX = (mapTiles.SectorCoordinates[currentSector-1][0]*(SectorWidth*tileW))*-1;
      mapY = (mapTiles.SectorCoordinates[currentSector-1][1]*(SectorHeight*tileH))*-1;
      	
      for(int i=0;i<mapTiles.tiles.length;i++){
         for(int j=0;j<mapTiles.tiles[0].length;j++){
            //System.out.println("sector: "+mapTiles.tiles[i][j].sector+"_currentSector"+currentSector);
            if(mapTiles.tiles[i][j].sector == (currentSector-1)){// && mapTiles.tiles[i][j].walkable){
               dG.drawImage(mapTiles.tiles[i][j].bufferedImage, null, mapX+(j*tileW), mapY+(i*tileH));
            }
         }
      }
      
         
      for(int d=0; d<depthDisplay;d++){
         BufferedImage temp = new BufferedImage((tileW*SectorWidth), (tileH*SectorHeight), BufferedImage.TYPE_INT_ARGB);
         Graphics2D tempG = (Graphics2D)temp.getGraphics();
         for(int i=0;i<mapTiles.tiles.length;i++){
            for(int j=0;j<mapTiles.tiles[0].length;j++){
               if(mapTiles.tiles[i][j].sector == (currentSector-1) && !mapTiles.tiles[i][j].walkable){
                  if(mapTiles.tiles[i][j].sector_tiley == d){
                     tempG.drawImage(mapTiles.tiles[i][j].bufferedImage, null, mapX+(j*tileW), mapY+(i*tileH));
                  }
               }
            }
         }
            
            
         for(int b=0;b<Bots.length;b++){
            Bot bot = Bots[b];
            if(bot._ytile == d){
               tempG.drawImage(bot.sprite.spriteBufferedImage, null, mapX+(bot._x), mapY+(bot._y));
               //tempG.drawImage(bot.botImage, null, 5, 5);
            }
         }
          
         dG.drawImage(temp, null, 0, 0);
      }
         
      g2.drawImage(displayBoard, null, 0, 0);
      g.dispose();
   }
   public void cycle() {
   
      x += 1;
      y += 1;
   }

   public static void main(String[] args) {
      new BioBot();
   }
/**
   @Override
   public void run() {
      long beforeTime, timeDiff, sleep;
   
      beforeTime = System.currentTimeMillis();
   
      while (true) {
      
         cycle();
         repaint();
      
         timeDiff = System.currentTimeMillis() - beforeTime;
         sleep = DELAY - timeDiff;
      
         if (sleep < 0)
            sleep = 2;
         try {
            Thread.sleep(sleep);
         } catch (InterruptedException e) {
            System.out.println("interrupted");
         }
      
         beforeTime = System.currentTimeMillis();
      }
   }
   **/
   
   public void run(){
   
      //SCREEN REFRESHER
      long tm = System.currentTimeMillis();
      while (Thread.currentThread() == scrnRefresher){
         if(imagesDone){
            worldCentral();
            repaint(0, 0, 1200, 1200);
         }
         else{
            repaint();
            try{
               tracker.waitForAll();
               imagesDone = true;
            }
            catch(InterruptedException ie){}
         }
         try{
            tm += 1000/30;
            Thread.sleep(Math.max(0, tm-System.currentTimeMillis()));
         }
         catch(InterruptedException ie){
            break;
         } 
      } 
      
   }
   
   
   public void update(Graphics g){
      paint(g);
   }


}