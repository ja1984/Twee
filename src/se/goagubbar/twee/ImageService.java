package se.goagubbar.twee;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;



public class ImageService {
	Boolean tryAgain = true;
	
	public String getBitmapFromURL(String q, String name, Context ctx) throws Exception {
		try {

			String filename = name + ".jpg";

			if(ImageExists(name))
				return filename;

			String uri = String.format("http://www.thetvdb.com/banners/%s",q);

			//String name = java.util.UUID.randomUUID().toString();

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
		} catch (OutOfMemoryError e) {
			Log.d("Fel vid sparning",e.getMessage());
			if(tryAgain)
			{
				tryAgain = false;
				System.gc();
				return getBitmapFromURL(q,name,ctx);
			}
			
			e.printStackTrace();
			return null;
		}
	}

	private String SaveImage(Bitmap bitmap, String name, Context context) throws Exception
	{
		String filename = name + ".jpg";

		String externalStoragePath = Environment.getExternalStorageDirectory().getPath();
		File newFolder = new File(externalStoragePath,"/Tvist");

		if(!newFolder.exists())
			newFolder.mkdir();

		FileOutputStream fos = null;
		try {
			//fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			fos = new FileOutputStream(newFolder + "/" + filename);
			bitmap.compress(Bitmap.CompressFormat.JPEG , 80, fos);
			bitmap.recycle();
				
		} catch (OutOfMemoryError e) {
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

			String externalStoragePath = Environment.getExternalStorageDirectory().getPath();
			File newFolder = new File(externalStoragePath,"/Tvist");

			if(!newFolder.exists())
				newFolder.mkdir();

			//FileInputStream fos = ctx.openFileInput(name);
			FileInputStream fis = new FileInputStream(newFolder + "/" + name);

			BitmapFactory.Options options = new BitmapFactory.Options();

			if(name.contains("_big")){
				options.inSampleSize = 2;
			}

			bm = BitmapFactory.decodeStream(fis,null,options);
			fis.close();

			return bm;	
		} catch (Exception e) {
			return bm;
		}

	}


	private Boolean ImageExists(String imageName)
	{
		String filename = imageName + "jpg";

		String externalStoragePath = Environment.getExternalStorageDirectory().getPath();
		File newFolder = new File(externalStoragePath,"/Tvist");

		if(!newFolder.exists())
			newFolder.mkdir();

		File image = new File(newFolder + "/",filename);

		return image.exists();

	}


}
