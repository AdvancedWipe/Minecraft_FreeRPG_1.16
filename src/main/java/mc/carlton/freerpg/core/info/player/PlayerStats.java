package mc.carlton.freerpg.core.info.player;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public class PlayerStats {

  static Map<UUID, Map<String, ArrayList<Number>>> player_statsMap = new HashMap<UUID, Map<String, ArrayList<Number>>>();
  static Map<UUID, Number> player_LoginTime = new HashMap<>();
  static Map<UUID, Number> player_playTime = new HashMap<>();
  static Map<UUID, String> player_language = new HashMap<>();
  static Map<UUID, Map<String, Integer>> playerSkillToggleEXPBar = new HashMap<>();
  static Map<UUID, Map<String, Integer>> playerSkillToggleAbility = new HashMap<>();
  static Map<UUID, Boolean> playerAreStatsSaved = new HashMap<>();
  //This class is very messy, it would be better if I created a class to store all these stats for each player,
  // And used a hashmap to assign that class to a UUID, but that would take major restructuring
  private UUID uuid;

  public PlayerStats(Player p) {
    this.uuid = p.getUniqueId();
  }

  public PlayerStats(UUID playerUUID) {
    this.uuid = playerUUID;
  }

  //Common boolean questions asked
  public boolean isPlayerRegistered() {
    if (player_statsMap.containsKey(uuid)) {
      return true;
    } else {
      return false;
    }
  }

  public Map<UUID, Map<String, ArrayList<Number>>> getData() {
    if (!player_statsMap.containsKey(uuid)) {
      PlayerStatsLoadIn loadInPlayer = new PlayerStatsLoadIn(uuid);
      Map<String, ArrayList<Number>> playerStats0 = loadInPlayer.getPlayerStatsMapFromFile();
      player_statsMap.put(uuid, playerStats0);
    }
    return player_statsMap;
  }

  //Getters and setters for entire playerbase map
  public void setData(Map<UUID, Map<String, ArrayList<Number>>> playerStatsMap) {
    this.player_statsMap = playerStatsMap;
  }

  //Getters and setters for single player stats
  public Map<String, ArrayList<Number>> getPlayerData() {
    if (!player_statsMap.containsKey(uuid)) {
      PlayerStatsLoadIn loadInPlayer = new PlayerStatsLoadIn(uuid);
      Map<String, ArrayList<Number>> playerStats0 = loadInPlayer.getPlayerStatsMapFromFile();
      player_statsMap.put(uuid, playerStats0);
    }
    return player_statsMap.get(uuid);
  }

  //Getters and setters for if they player's data has been saved
  public void setPlayerAreStatsSaved(boolean areStatsSaved) {
    playerAreStatsSaved.put(uuid, areStatsSaved);
  }

  public boolean arePlayerStatsSaved() {
    if (playerAreStatsSaved.containsKey(uuid)) {
      return playerAreStatsSaved.get(uuid);
    } else {
      return true;
    }
  }

  //Getters and setters for player times
  public void addPlayerTimes(long loginTime, long playTime) {
    player_LoginTime.put(uuid, loginTime);
    player_playTime.put(uuid, playTime);
  }

  public long getPlayerLoginTime() {
    if (!player_LoginTime.containsKey(uuid)) {
      PlayerStatsLoadIn loadInPlayer = new PlayerStatsLoadIn(uuid);
      long loginTime = loadInPlayer.getLoginTime();
      player_LoginTime.put(uuid, loginTime);
    }
    return (long) player_LoginTime.get(uuid);
  }

  public long getPlayerPlayTime() {
    if (!player_playTime.containsKey(uuid)) {
      PlayerStatsLoadIn loadInPlayer = new PlayerStatsLoadIn(uuid);
      long playTime = loadInPlayer.getPlayTime();
      player_playTime.put(uuid, playTime);
    }
    return (long) player_playTime.get(uuid);
  }

  //Turns player play time to a string
  public String getPlayerPlayTimeString() {
    String playTime_string = "";
    long newPlayTime = getNewPlayTime();
    int hours = (int) Math.floor(newPlayTime / 3600.0);
    int minutes = (int) Math.floor((newPlayTime - (hours * 3600)) / 60.0);
    int seconds = (int) Math.floor((newPlayTime - (hours * 3600) - (minutes * 60)));
    String hoursString = Integer.toString(hours);
    if (hoursString.length() < 2) {
      hoursString = "0" + hoursString;
    }
    String minutesString = Integer.toString(minutes);
    if (minutesString.length() < 2) {
      minutesString = "0" + minutesString;
    }
    String secondsString = Integer.toString(seconds);
    if (secondsString.length() < 2) {
      secondsString = "0" + secondsString;
    }
    playTime_string = hoursString + ":" + minutesString + ":" + secondsString;
    return playTime_string;
  }

  public long getNewPlayTime() {
    long loginTime = getPlayerLoginTime();
    long playTime = getPlayerPlayTime();
    long currentTime = Instant.now().getEpochSecond();
    long newPlayTime = playTime + (currentTime - loginTime);
    return newPlayTime;
  }

  public String getPlayerLanguage() {
    if (!player_language.containsKey(uuid)) {
      PlayerStatsLoadIn loadInPlayer = new PlayerStatsLoadIn(uuid);
      String language = loadInPlayer.getPlayerLanguage();
      player_language.put(uuid, language);
    }
    return player_language.get(uuid);
  }

  //Getters and setters for player language
  public void setPlayerLanguage(String language) {
    player_language.put(uuid, language);
  }

  //Getters and setters for toggle ability/exp bar
  public void addPlayerSkillToggleExpBar(Map<String, Integer> skillToggles) {
    playerSkillToggleEXPBar.put(uuid, skillToggles);
  }

  public void addPlayerSkillToggleAbility(Map<String, Integer> skillToggles) {
    playerSkillToggleAbility.put(uuid, skillToggles);
  }

  public Map<String, Integer> getSkillToggleExpBar() {
    if (!playerSkillToggleEXPBar.containsKey(uuid)) {
      PlayerStatsLoadIn loadInPlayer = new PlayerStatsLoadIn(uuid);
      Map<String, Integer> playerStats0 = loadInPlayer.getSkillExpBarToggles();
      playerSkillToggleEXPBar.put(uuid, playerStats0);
    }
    return playerSkillToggleEXPBar.get(uuid);
  }

  public Map<String, Integer> getSkillToggleAbility() {
    if (!playerSkillToggleAbility.containsKey(uuid)) {
      PlayerStatsLoadIn loadInPlayer = new PlayerStatsLoadIn(uuid);
      Map<String, Integer> playerStats0 = loadInPlayer.getSkillAbilityToggles();
      playerSkillToggleAbility.put(uuid, playerStats0);
    }
    return playerSkillToggleAbility.get(uuid);
  }

  //expbar/skillability boolean information
  public boolean isPlayerSkillExpBarOn(String skillName) {
    int expBarOn = getSkillToggleExpBar().get(skillName);
    if (expBarOn == 1) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isPlayerSkillAbilityOn(String skillName) {
    int abilityOn = getSkillToggleAbility().get(skillName);
    if (abilityOn == 1) {
      return true;
    } else {
      return false;
    }
  }

  //Methods to toggle expbar/skillability
  public void togglePlayerSkillExpBar(String skillName) {
    Map<String, Integer> playerSkillEXPBarMap = getSkillToggleExpBar();
    int expBarOn = playerSkillEXPBarMap.get(skillName);
    if (expBarOn == 1) {
      playerSkillEXPBarMap.put(skillName, 0);
    } else {
      playerSkillEXPBarMap.put(skillName, 1);
    }
    playerSkillToggleEXPBar.put(uuid, playerSkillEXPBarMap);
  }

  public void togglePlayerSkillAbility(String skillName) {
    Map<String, Integer> playerSkillAbiliytMap = getSkillToggleAbility();
    int abilityOn = playerSkillAbiliytMap.get(skillName);
    if (abilityOn == 1) {
      playerSkillAbiliytMap.put(skillName, 0);
    } else {
      playerSkillAbiliytMap.put(skillName, 1);
    }
    playerSkillToggleAbility.put(uuid, playerSkillAbiliytMap);
  }


  //Used to delete all player data from memory
  public void removePlayer() {
    player_statsMap.remove(uuid);
    player_LoginTime.remove(uuid);
    player_playTime.remove(uuid);
    player_language.remove(uuid);
    playerSkillToggleEXPBar.remove(uuid);
    playerSkillToggleAbility.remove(uuid);
    playerAreStatsSaved.remove(uuid);
  }

}
