package ca.nicholaspaun.chemlogic.app1;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;



public class ChemlogicFragment extends Fragment {

	public ChemlogicFragment() {
	}

	public void setup_spinner(View v, int Spinner, int Choices)
	{
		Spinner spinner = (Spinner) v.findViewById(Spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
		        Choices, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
	}
	
	public void setup_submit(View v)
	{
		  final Button button = (Button) v.findViewById(R.id.chemlogic_submit);
	         button.setOnClickListener(new View.OnClickListener() {
	        	    public void onClick(View v)
	        	    {
	        	    	submit(v);
	        	    }
	         });
	}

	public void submit(View v)
	{
	}
	
	public void defocus(EditText control)
	{
        control.clearFocus();
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
      	      Context.INPUT_METHOD_SERVICE);
      	imm.hideSoftInputFromWindow(control.getWindowToken(), 0);
	}
	
	public String fixNewline(String text)
	{
		return(text.replace("\n", "<br />"));
	}
	
	public void setup_keyboard(View v)
	{
		  final EditText editText = (EditText) v.findViewById(R.id.chemlogic_input);
	         editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					public void onFocusChange(View v, boolean hasFocus) {
						keyboard_focus(v,hasFocus);	
					}
	         }); 	
	}
	
	public void keyboard_focus(View v, boolean hasFocus)
	{
		TableLayout kbdExt = (TableLayout) v.getRootView().findViewById(R.id.keyboard_extension);
		if (hasFocus)
			kbdExt.setVisibility(View.VISIBLE);
		else
			kbdExt.setVisibility(View.GONE);
	}
}
