package se.goagubbar.twee;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageService {

	public String getBitmapFromURL(String q, Context ctx) {
		try {
			String uri = String.format("http://www.thetvdb.com/banners/%s",q);

			String name = java.util.UUID.randomUUID().toString();

			URL url = new URL(uri);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			//Bitmap myBitmap = BitmapFactory.decodeStream(input,null,new Options().);
			Bitmap myBitmap = BitmapFactory.decodeStream(input);

			if((int)myBitmap.getWidth() > 800)
			{
				myBitmap = Bitmap.createScaledBitmap(myBitmap,(int)(myBitmap.getWidth()*0.8), (int)(myBitmap.getHeight()*0.8), true);
			}
			
			
			return SaveImage(myBitmap, name, ctx);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String SaveImage(Bitmap bitmap, String name, Context context) throws IOException
	{
		String filename = name + ".png";

		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			bitmap.compress(Bitmap.CompressFormat.PNG , 90, fos);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.d("Fel vid sparning",e.getMessage());
			return "error";
		} finally{
			fos.flush();
			fos.close();
		}
		return filename;
	}

	
	public Bitmap GetImage(String name, Context ctx)
	{
		Bitmap bm = null;
		
		try {
			FileInputStream fos = ctx.openFileInput(name);
			bm = BitmapFactory.decodeStream(fos);
			fos.close();
			
			return bm;	
		} catch (Exception e) {
			return bm;
		}
		
	}


	
}