package ca.nicholaspaun.chemlogic.app1;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class BalancerFragment extends ChemlogicFragment {

	private ChemlogicController ctrl;
	
	public BalancerFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_balancer , container,
				false);
		
		
		buttonSetup(rootView);
		populateSpinner(rootView,R.id.balancer_spinner_type_input,R.array.balancer_type_array);
		populateSpinner(rootView,R.id.balancer_spinner_type_output,R.array.balancer_type_array);
		ctrl = ((MainActivity) getActivity()).getController();
		return rootView;
	}
	
	
	public void submit(View v)
	{
    	Spinner inputTypeSpinner =  (Spinner) getActivity().findViewById(R.id.balancer_spinner_type_input);
    	String  inputType = inputTypeSpinner.getSelectedItem().toString().toLowerCase();
    	
    	Spinner outputTypeSpinner =  (Spinner) getActivity().findViewById(R.id.balancer_spinner_type_output);
    	String  outputType = outputTypeSpinner.getSelectedItem().toString().toLowerCase();
    	
    	EditText inputEdit = (EditText) getActivity().findViewById(R.id.balancer_edittext_input);
        String input = inputEdit.getText().toString();
        
        String output = ctrl.command(inputType, input,outputType);

        TextView outputView = (TextView) getActivity().findViewById(R.id.balancer_textview_output);
        outputView.setText(Html.fromHtml(output));
        
        defocus(inputEdit);
 }
	
	
}