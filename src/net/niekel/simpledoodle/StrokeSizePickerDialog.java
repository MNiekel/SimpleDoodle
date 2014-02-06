package net.niekel.simpledoodle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

public class StrokeSizePickerDialog extends DialogFragment {

	public interface DialogListener {
		public void onStrokeSizeSelected(boolean positive, int result);
    }
	
	private SeekBar bar;
	private int stroke;
    
    private DialogListener listener;
	private View view;
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (DialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement StrokeSizeDialogListener");
        }
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.strokesizepickerdialog, null);
        builder.setView(view);
        
        builder.setTitle(R.string.stroke);
        
        builder.setPositiveButton(R.string.color_confirm, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       listener.onStrokeSizeSelected(true, bar.getProgress());
                   }
               })
               .setNegativeButton(R.string.color_cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       listener.onStrokeSizeSelected(false, 0);
                   }
               });
        
        stroke = getArguments().getInt("stroke");
        
        bar = (SeekBar) view.findViewById(R.id.seekBarStrokeSize);
        bar.setProgress(stroke);
        
        return builder.create();
    }
}
