package net.niekel.simpledoodle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;


public class MainActivity extends FragmentActivity
						implements ColorPickerDialog.DialogListener, StrokeSizePickerDialog.DialogListener {
	
	private static final String BACKGROUND = "background";
	private static final String COLOR = "color";
	private static final String STROKE = "stroke";
	private static final String IMAGENR = "imagenr";
	
	private static final String TAG = "MainActivity";
	
	private static final int REQUEST_CODE_PICK_IMAGE = 1;
	private static final String BASENAME = "SimpleDoodle";
	
    private DrawView drawView;
    private SharedPreferences prefs;
    private int imagenr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        lock_orientation();
                
        setContentView(R.layout.mainactivity);
        
        prefs = getPreferences(MODE_PRIVATE);
        
        drawView = (DrawView) findViewById(R.id.drawview);
        drawView.init(prefs.getInt(STROKE, getResources().getDimensionPixelSize(R.dimen.dip)),
        		prefs.getInt(COLOR, Color.RED),
        		prefs.getInt(BACKGROUND, Color.WHITE));
        
        imagenr = prefs.getInt(IMAGENR, 0);
    }
    
    private void lock_orientation() {
    	//if activity started in portrait mode, stay in portrait mode
    	//if activity started in landscape mode, stay in landscape mode
    	int rotation = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getRotation();

        switch (rotation) {
         	case Surface.ROTATION_0:
         		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
         		break;
         	case Surface.ROTATION_90:
         		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
         		break;
         	case Surface.ROTATION_180:
         		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
         		break;
         	case Surface.ROTATION_270:
         		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
         		break;
         	default:
         		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
         		break;
         }
    }

    @Override
    public void onPause() {
    	super.onPause();
    	//save current settings
    	prefs.edit().putInt(STROKE, drawView.getStroke()).commit();
    	prefs.edit().putInt(COLOR, drawView.getColor()).commit();
    	prefs.edit().putInt(BACKGROUND, drawView.getBGColor()).commit();
    	prefs.edit().putInt(IMAGENR, imagenr);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.clear:
                drawView.clear();
                return true;
            case R.id.pick:
            	pickColor(drawView.getColor(), COLOR);
                return true;
            case R.id.background:
            	pickColor(drawView.getBGColor(), BACKGROUND);
            	return true;
            case R.id.load:
            	pickImage();
            	return true;
            case R.id.save:
            	save();
            	return true;
            case R.id.stroke:
            	pickStrokeSize();
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void pickStrokeSize() {
    	//start a dialog to set pencil size
    	DialogFragment dialog = new StrokeSizePickerDialog();
    	Bundle args = new Bundle();
    	args.putInt(STROKE, drawView.getStroke());
    	dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), STROKE);
    }
    
    private void pickColor(int color, String tag) {
    	//start a dialog to pick a color
    	DialogFragment dialog = new ColorPickerDialog();
    	Bundle args = new Bundle();
    	args.putInt(COLOR, color);
    	dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), tag);
    }
    
    private void pickImage() {
    	//start an intent to pick an image from the gallery
    	Intent intent = new Intent();
    	intent.setAction(Intent.ACTION_PICK);
    	intent.setDataAndType(Uri.parse(createPath().toString()), "image/*");
    	startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }
    
    private void save() {
    	Bitmap bitmap = drawView.getBitmap();
		File path = createPath();
						
		save(new File(path, BASENAME+String.format("%02d", imagenr)+".png"), bitmap);
	}
    
    private void save(File file, Bitmap bitmap) {
    	try {
			FileOutputStream stream = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 100, stream); //png because I wanted to keep transparency
			stream.close();
			Toast.makeText(this, "Your doodle was saves as " +file.toString(), Toast.LENGTH_LONG).show();
			imagenr = (imagenr + 1) % 100;
			
			Uri uri = Uri.fromFile(file);
						
			Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			intent.setData(uri);
			sendBroadcast(intent);
			
		} catch (Exception e) {
			Log.e(TAG, "Error saving doodle:", e);
			Toast.makeText(this, "Error saving file", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
    }
        
    private File createPath() {
    	String state = Environment.getExternalStorageState();
		
		if (!state.equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(getApplicationContext(),
					"Error accessing external storage, action aborted: state = " +state, Toast.LENGTH_LONG).show();
			return null;
		}	
		
		File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), BASENAME);
		path.mkdirs();
		return path;
    }

    private void load(Uri uri) {
		//load image from uri (retrieved from onActivityResult)
		try {
			InputStream stream = getContentResolver().openInputStream(uri);
			
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;

			BitmapFactory.decodeStream(stream, null, options);
			stream.close();
		
			int outWidth = options.outWidth;
			int outHeight = options.outHeight;
			
			int inWidth = drawView.getBitmap().getWidth(); //width of drawview bitmap
			int inHeight = drawView.getBitmap().getHeight(); //height of drawview bitmap
			
			int sample = 1;

			while (outWidth > inWidth * sample || outHeight > inHeight * sample) {
				sample = sample * 2;
			}
			
			options.inJustDecodeBounds = false;
			options.inSampleSize = sample; //to prevent Out of Memory errors
		
			stream = getContentResolver().openInputStream(uri);

			Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
			stream.close();
			
			float w = (float) bitmap.getWidth(); //width of new bitmap
			float h = (float) bitmap.getHeight(); //height of new bitmap
			float scalefactor = Math.min(((float) inWidth) / w, ((float) inHeight) / h);
			
			w = w * scalefactor;
			h = h * scalefactor;
			//scale bitmap to best fit in draw bitmap
			bitmap = Bitmap.createScaledBitmap(bitmap, (int) w, (int) h, false);
			
			drawView.setBitmap(bitmap);
			bitmap.recycle(); //new bitmap is copied in drawview => recycle
			
		} catch (IOException e) {
			Toast.makeText(this, "Error loading file", Toast.LENGTH_LONG).show();
			Log.e(TAG, "Error loading file:", e);
			e.printStackTrace();
		}
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch (requestCode) {
    		case REQUEST_CODE_PICK_IMAGE:
    			if (resultCode == RESULT_OK) {
    				Uri uri = data.getData();
    				load(uri);
    			}
    			break;
    		default:
    			break;
    	}
    }

    @Override
	public void onColorSelected(boolean positive, int result, String tag) {
    	//implementation from colorpicker interface
    	if (positive) {
    		if (tag.equals(BACKGROUND)) {
    			drawView.setBGColor(result);
    		} else {
    			drawView.setColor(result);
    		}
    	}
	}

	@Override
	public void onStrokeSizeSelected(boolean positive, int result) {
		//implementation from pencil size setting interface
		if (positive) {
    		drawView.setStroke(result);
    	}
	}
}
