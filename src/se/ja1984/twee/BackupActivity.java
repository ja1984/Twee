package se.ja1984.twee;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;


import se.ja1984.twee.dto.Backup;
import se.ja1984.twee.dto.Episode;
import se.ja1984.twee.dto.Series;

import se.ja1984.twee.models.Profile;
import se.ja1984.twee.utils.DatabaseHandler;
import se.ja1984.twee.utils.Utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class BackupActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_backup);
		getActionBar().setDisplayHomeAsUpEnabled(true);


		Button btnBackup = (Button)findViewById(R.id.btnBackup);
		btnBackup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CreateBackup();
			}
		});

		Button btnRestore = (Button)findViewById(R.id.btnRestore);
		btnRestore.setEnabled(BackupExists());
		btnRestore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RestoreBackup();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_backup, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	private boolean BackupExists(){
		String externalStoragePath = Environment.getExternalStorageDirectory().getPath();
		File backupFolder = new File(externalStoragePath,"/Twee");
		File backupFile = new File(backupFolder + "/" + "backup.txt");
		
		return backupFile.exists();
	}
	
	private void CreateBackup()
	{

		DatabaseHandler db = new DatabaseHandler(BackupActivity.this);

		ArrayList<Profile> profiles = db.GetAllprofiles();
		ArrayList<Backup> backups = new ArrayList<Backup>();
		Gson json = new Gson();

		for (int i = 0; i < profiles.size(); i++) {
			Utils.selectedProfile = profiles.get(i).getId();

			Backup backup = new Backup();

			backup.Profile = profiles.get(i).getName();
			backup.ProfileId = profiles.get(i).getId();

			backup.Shows = db.BackupShows();

			backups.add(backup);

		}


		String result = "";
		try {
			Type backupObject = new TypeToken<ArrayList<Backup>>(){}.getType();
			result =  json.toJson(backups, backupObject);
		} catch (Exception e) {
			// TODO: handle exception
		}


		try {
			String externalStoragePath = Environment.getExternalStorageDirectory().getPath();
			File backupFolder = new File(externalStoragePath,"/Twee");

			if(!backupFolder.exists())
				backupFolder.mkdir();
			File backupFile = new File(backupFolder + "/" + "backup.txt");


			BufferedWriter writer = new BufferedWriter(new FileWriter(backupFile));
			writer.write(result);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			Log.d("Backup", e.getMessage());
			Toast.makeText(BackupActivity.this, R.string.message_backup_error, Toast.LENGTH_SHORT).show();
		}

		Toast.makeText(BackupActivity.this, R.string.message_backup_complete, Toast.LENGTH_SHORT).show();

	}

	private void RestoreBackup()
	{
		Gson json = new Gson();
		ArrayList<Backup> backups = new ArrayList<Backup>();
		String jsonString = "";

		String externalStoragePath = Environment.getExternalStorageDirectory().getPath();
		File backupFolder = new File(externalStoragePath,"/Twee");
		File backupFile = new File(backupFolder + "/" + "backup.txt");

		DatabaseHandler db = new DatabaseHandler(BackupActivity.this);
		
		try {
			
			if(db.KillDb())
			{
				FileInputStream fis = new FileInputStream(backupFile);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader br = new BufferedReader(isr, 8192);    // 2nd arg is buffer size

				jsonString = br.readLine();
				Type backupObject = new TypeToken<ArrayList<Backup>>(){}.getType();
				backups = json.fromJson(jsonString, backupObject);
				isr.close();
				fis.close();
				br.close();
				
				for (int i = 0; i < backups.size(); i++) {
					Backup b = backups.get(i);
					long profileId = db.AddNewProfile(b.Profile);
					Utils.selectedProfile = (int)profileId;		
					for (Series show : b.Shows) {
						se.ja1984.twee.models.Series series = new se.ja1984.twee.models.Series();
						
						series.setActors(show.Actors);
						series.setAirs(show.Airs);
						series.setFirstAired(show.FirstAired);
						series.setGenre(show.Genre);
						series.setHeader(show.Header);
						series.setImage(show.Image);
						series.setImdbId(show.ImdbId);
						series.setLastUpdated(show.LastUpdated);
						series.setName(show.Name);
						series.setRating(show.Rating);
						series.setSeriesId(show.SeriesId);
						series.setStatus(show.Status);
						series.setSummary(show.Summary);
						
						db.AddShow(series);
					
						ArrayList<se.ja1984.twee.models.Episode> episodes = new ArrayList<se.ja1984.twee.models.Episode>();
						for (Episode e : show.Episodes) {
							se.ja1984.twee.models.Episode ep = new se.ja1984.twee.models.Episode();
							
							ep.setAired(e.Aired);
							ep.setEpisode(e.Episode);
							ep.setEpisodeId(e.EpisodeId);
							ep.setLastUpdated(e.LastUpdated);
							ep.setSeason(e.Season);
							ep.setSelected(e.Selected);
							ep.setSeriesId(show.SeriesId);
							ep.setSummary("");
							ep.setTitle(e.Title);
							ep.setWatched(e.Watched);

							episodes.add(ep);

						}
						
						db.AddEpisodes(episodes);
						
					}
					
				}
				
				
			}
			

		} catch (Exception e) {
			Log.d("Error - Restore", "" + e.getMessage());
			Toast.makeText(BackupActivity.this, R.string.message_restore_error, Toast.LENGTH_SHORT).show();
		}

		Toast.makeText(BackupActivity.this, R.string.message_restore_complete, Toast.LENGTH_SHORT).show();
	}

}
