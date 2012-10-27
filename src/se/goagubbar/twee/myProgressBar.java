package se.goagubbar.twee;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class myProgressBar extends ProgressBar {

	public myProgressBar(Context context) {
		super(context);
	}

	public myProgressBar(Context context,AttributeSet attrs) {
		super(context);
	}

	public myProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context);
	}

	@Override
	public synchronized void setProgress(int progress) {
	  super.setProgress(progress);
	   
	  // the setProgress super will not change the details of the progress bar
	  // anymore so we need to force an update to redraw the progress bar
	  invalidate();
	}

}
