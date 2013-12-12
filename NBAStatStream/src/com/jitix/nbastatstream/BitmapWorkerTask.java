package com.jitix.nbastatstream;

import java.lang.ref.WeakReference;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {

	private final WeakReference<Context> contextReference;
	private final WeakReference<ImageView> imageViewReference;
	private static final String TAG = "NBAStatStream";
	private int data = 0;
	private final boolean last;

	public BitmapWorkerTask(Context context, ImageView imageView, final boolean last) {
		// Use WeakReference to ensure the ImageView can be garbage collected
		imageViewReference = new WeakReference<ImageView>(imageView);
		contextReference = new WeakReference<Context>(context);
		this.last = last;
	}

	// Decode the image in the background
	@Override
	protected Bitmap doInBackground(Integer... params) {
		data = params[0];
		int width = params[1];
		int height = params[2];
		if(contextReference != null) {
			return decodeSampledBitmap(contextReference.get().getResources(), data, width, height);
		} else {
			return null;
		}
	}

	// Once complete, see if ImageView is still around and set bitmap
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if(imageViewReference != null && bitmap != null) {
			final ImageView imageView = imageViewReference.get();
			if(imageView != null) {
				imageView.setImageBitmap(bitmap);
				// If this is the last image that is loaded, clear the progress bar
				if(last == true) {
					Log.d(TAG, "Calling hideProgress");
					TaskListener listener = (TaskListener) contextReference.get();
					listener.hideProgress();
				}
			} else {
				Log.d(TAG, "ImageView == null!");
			}
		} else {
			Log.d(TAG, "ImageViewReference or bitmap == null");
		}
	}

	private static Bitmap decodeSampledBitmap(Resources res, int resId, int width, int height) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPreferredConfig = Config.ARGB_8888;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, width, height);
		//Log.d(TAG, "inSampleSize = " + options.inSampleSize);
		//Log.d(TAG, "Decode : outHeight = " + options.outHeight + ", outWidth = " + options.outWidth);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeResource(res, resId, options);
		return Bitmap.createScaledBitmap(bitmap, options.outWidth/options.inSampleSize, options.outHeight/options.inSampleSize, false);
	}

	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}
}
