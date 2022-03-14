import java.io.*;
import java.util.*;
import data.structures.*;
import data.searching.*;

public class DBReader{
  public static void extractPlayerData(Hashtable<String,ArrayList<Long>> playersHashtable, Game game){
    if(playersHashtable.containsKey(game.blackPlayer)) {
      playersHashtable.get(game.blackPlayer).add(game.startingByte);
    } else{
      playersHashtable.put(game.blackPlayer,new ArrayList<Long>());
      playersHashtable.get(game.blackPlayer).add(game.startingByte);
    }
    if(playersHashtable.containsKey(game.whitePlayer)) {
      playersHashtable.get(game.whitePlayer).add(game.startingByte);
    } else{
      playersHashtable.put(game.whitePlayer,new ArrayList<Long>());
      playersHashtable.get(game.whitePlayer).add(game.startingByte);
    }
  }

  public static void savePlayerData(File playerDataFile, Hashtable<String,ArrayList<Long>> playersHashtable){
    try{
      if(!playerDataFile.exists())playerDataFile.createNewFile();
      FileOutputStream out = new FileOutputStream(playerDataFile);
      OutputStreamWriter writer = new OutputStreamWriter(out);
      Enumeration<String> keys = playersHashtable.keys();
      int cpt = 0;
      while(keys.hasMoreElements()){
        String key = keys.nextElement();
        writer.write("[Pseudo \""+key+"\"]\n");
        ArrayList<Long> value = playersHashtable.get(key);
        for(int i=0;value != null && i<value.size();i++){
          writer.write(value.get(i)+" ");
        }
        writer.write("\n[NumberOfGame "+value.size()+"]\n");
      }
      writer.close();
      out.close();
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  public static void extractOpeningIteration(Hashtable<String,Integer> openingHashtable, Game game) {
    if(openingHashtable.containsKey(game.opening)){
      openingHashtable.put(game.opening,openingHashtable.get(game.opening)+1);
    }else{
      openingHashtable.put(game.opening,1);
    }
  }

  public static void displayOpeningIteration(Hashtable<String,Integer> openingHashtable) {
    openingHashtable.forEach((key, value) -> {
      System.out.println("Opening: "+key+" & Iterations: "+value);
    });
  }

  public static void displayTopOpening(Hashtable<String,Integer> openingHashtable, int top) {
    //display top opening
    Hashtable<String,Integer> localHashtable = (Hashtable<String,Integer>)openingHashtable.clone();
    ArrayList<String> listKeys = Collections.list(localHashtable.keys());
    String topOpening=listKeys.get((int)Math.random()*listKeys.size());
    Set keys = localHashtable.keySet();
    if(top==1)
    System.out.println("============================\nThe most used opening is:");
    else if(top>1)
    System.out.println("============================\nThe most used openings are:");
    else if(top<1) {
      System.out.println("============================\nYou have entered an invalid number, we will consider as a '1'.");
      top=1;
    }
    for(int i=1;i<=top;i++) {
      for(int j=0;j<listKeys.size();j++) {
        if(localHashtable.get(listKeys.get(j))>localHashtable.get(topOpening)) {
          topOpening=listKeys.get(j);
        }
      }
      System.out.println("Top "+i+" opening :: "+topOpening+" with "+localHashtable.get(topOpening)+" iterations.");
      listKeys.remove(topOpening);
      topOpening=listKeys.get((int)Math.random()*listKeys.size());
    }
  }

  public static void extractActivePlayers(Hashtable<String,ArrayList<Long>> playersHashtable, int top) {
    ArrayList<String> listKeys = Collections.list(playersHashtable.keys());
    String activePlayer=listKeys.get((int)Math.random()*listKeys.size());
    Set keys = playersHashtable.keySet();
    if(top==1)
    System.out.println("============================\nThe most active player is:");
    else if(top>1)
    System.out.println("============================\nThe most active players are:");
    else if(top<1) {
      System.out.println("============================\nYou have entered an invalid number, we will consider as a '1'.");
      top=1;
    }
    for(int i=1;i<=top;i++) {
      for(int j=0;j<listKeys.size();j++) {
        //System.out.println(playersHashtable.get(listKeys.get(j)).size());
        if(playersHashtable.get(listKeys.get(j)).size()>playersHashtable.get(activePlayer).size()) {
          activePlayer=listKeys.get(j);
        }
        //System.out.println(playersHashtable.get(listKeys.get(j)));
      }
      System.out.println(i+". "+activePlayer+" with "+playersHashtable.get(activePlayer).size()+" games.");
      listKeys.remove(activePlayer);
      activePlayer=listKeys.get((int)Math.random()*listKeys.size());
    }
  }

  public static void main(String[] args) {
    int overall_cpt = 0;
    try{
      File folder = new File("Src/");
      ArrayList<String> fileFolder = new ArrayList<>();
      for(int i=0;i<folder.list().length;i++){
        fileFolder.add(folder.list()[i]);
      }
      for(int i=0;i<fileFolder.size();i++){
        String dataFile = fileFolder.get(i);
        if(dataFile.endsWith(".pgn")){
          String playersDataFile = "Src/"+dataFile.substring(0,dataFile.length()-4)+"_player_data.dat";
          FileInputStream in = new FileInputStream("Src/"+dataFile);
          BufferedReader reader = new BufferedReader(new InputStreamReader(in));
          Hashtable<String,Integer> openingHashtable = new Hashtable<String,Integer>();
          Hashtable<String,ArrayList<Long>> playersHashtable = new Hashtable<String,ArrayList<Long>>();
          int cpt = 0;
          int lineCpt = 0;
          int byteCpt = 0;
          System.out.println("Processing "+dataFile);
          do{
            Game tmp = new Game();
            tmp.line = lineCpt;
            tmp.startingByte = byteCpt;
            //System.out.println(tmp.line+" : "+tmp.startingByte);
            String line = "";
            int blankLineCpt = 0;
            do{
              line = reader.readLine();
              if(line != null){
                if(line.startsWith("[Event")){
                  tmp.type = line.substring(8,line.length()-2);
                }
                if(line.startsWith("[Site")){
                  tmp.url = line.substring(7,line.length()-2);
                }
                if(line.startsWith("[White ")){
                  tmp.whitePlayer = line.substring(8,line.length()-2);
                }
                if(line.startsWith("[Black ")){
                  tmp.blackPlayer = line.substring(8,line.length()-2);
                }
                if(line.startsWith("[Result")){
                  tmp.result = line.substring(9,line.length()-2);
                }
                if(line.startsWith("[Opening")){
                  tmp.opening = line.substring(10,line.length()-2);
                }
                if(line.equals("")){
                  blankLineCpt++;
                }
              }
              lineCpt++;
              byteCpt += line.getBytes().length+1;
            }while(blankLineCpt < 2);
            cpt++;
            extractPlayerData(playersHashtable,tmp);
            extractOpeningIteration(openingHashtable,tmp);
          }while(reader.ready());
          System.out.println("Saving data as "+playersDataFile);
          File output = new File(playersDataFile);
          savePlayerData(output,playersHashtable);
          reader.close();
          in.close();
          //================================ Print de debug ================================
          System.out.println("==> "+cpt+" Games read");
          System.out.println("==> "+playersHashtable.size()+" Players saved");
          //  displayOpeningIteration(openingHashtable);
          // displayTopOpening(openingHashtable,0);
          // extractActivePlayers(playersHashtable,0);
          overall_cpt += cpt;
        }
      }
      System.out.println("\n\n==> "+overall_cpt+" Games read across all files");
    }catch (IOException e){
      e.printStackTrace();
    }
  }
}
