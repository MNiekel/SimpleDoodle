package net.niekel.simpledoodle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ColorPickerDialog extends DialogFragment {

	public interface DialogListener {
		public void onColorSelected(boolean positive, int result, String tag);
    }
		
	private SeekBar redBar;
	private SeekBar greenBar;
	private SeekBar blueBar;
		
	private ImageView currentColor;
	private ImageView newColor;
	
	private int color;
    
    private DialogListener listener;
    private View view;
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (DialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ColorPickerDialogListener");
        }
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.colorpicker, null);
        builder.setView(view);
        
        builder.setTitle(R.string.set_color);
                        
        builder.setPositiveButton(R.string.select, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       listener.onColorSelected(true, getColor(), getTag());
                   }
               })
               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       listener.onColorSelected(false, 0, getTag());
                   }
               });
        
        color = getArguments().getInt("color");
        
        findViews();
        
        init();

        return builder.create();
    }
    
    private void findViews() {
    	redBar = (SeekBar) view.findViewById(R.id.seekBarRed);
        greenBar = (SeekBar) view.findViewById(R.id.seekBarGreen);        
        blueBar = (SeekBar) view.findViewById(R.id.seekBarBlue);
        
        currentColor = (ImageView) view.findViewById(R.id.colorViewCurrent);
        newColor = (ImageView) view.findViewById(R.id.colorViewNew);

    }
    
    private void init() {
    	redBar.setProgress(Color.red(color));
    	greenBar.setProgress(Color.green(color));
    	blueBar.setProgress(Color.blue(color));
    	
    	currentColor.setBackgroundColor(color);
    	newColor.setBackgroundColor(color);
    	
    	setListener(redBar);
    	setListener(greenBar);
    	setListener(blueBar);
    }
    
    private void setListener(SeekBar bar) {
    	bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
    		@Override
    		public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				int color = getColor();
				newColor.setBackgroundColor(color);
			}
		});
    }

    
    private int getColor() {
		int red = redBar.getProgress();
		int green = greenBar.getProgress();
		int blue = blueBar.getProgress();
		
		return Color.rgb(red, green, blue);
	}
}
