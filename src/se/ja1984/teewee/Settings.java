package se.ja1984.teewee;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * This class is responsible for storing and loading the last used settings for the application.
 *
 * @author twig
 */
public class Settings {
  public boolean downloadImage = true;
  public boolean downloadHeader = true;
  public String selectedTheme = "Theme_Holo_Light";
  

//  public Settings() {
//    this.loadSettings();
//  }

//  public void loadSettings() {
//    SharedPreferences settings = G.activity.getPreferences(Activity.MODE_PRIVATE);
//    this.downloadImage = settings.getBoolean("downloadImage", this.downloadImage);
//    this.downloadHeader = settings.getBoolean("downloadHeader", this.downloadHeader);
//    this.selectedTheme = settings.getString("selectedTheme", this.selectedTheme);
//  
//  }
//
//  public void saveSettings() {
//    SharedPreferences settings = getClass().getPreferences(Activity.MODE_PRIVATE);
//    SharedPreferences.Editor editor = settings.edit();
//
//    editor.putBoolean("downloadImage", this.downloadImage);
//    editor.putBoolean("downloadHeader", this.downloadHeader);
//    editor.putString("selectedTheme", this.selectedTheme);
// 
//
//    editor.commit();
//  }
}
