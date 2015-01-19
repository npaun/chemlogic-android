package ca.nicholaspaun.chemlogic.app1;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Html.TagHandler;
import android.text.Spanned;
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
	
	public static class ChemlogicHtml
	{
		private static String firstLineStyle(String input)
		{
			/*
			 * The first line of Chemlogic's output is the actual result, or the highlighted error string.
			 * We want to format this specially in the app. We do not do this in Chemlogic itself, because this is just a presentation tweak,
			 * The style used is: centered and enlarged.
			 */
			String result = input
					.replaceFirst("(?m)^(.*)$", "<font size='10'><center><bgcolor-ff0000>$1</bgcolor-ff0000></center></font>");
							
			return(result);
		}
		
		private static String fixNewline(String input)
		{
			/*
			 * In HTML, newlines do not have any effect. This replaces them with actual linebreaks
			 */
			return(input.replace("\n", "<br />"));
		}
		
		private static String spanToPseudoElement(String input)
		{
			/*
			 * Background color support is hacked into Html tag handler using <bgcolor-hhhhhh> "pseudo-elements".
			 * This is done because it is difficult to access real attributes from TagHandlers.
			 * Here, we allow a special case of the standard span tag and CSS inline styles to work by converting to this hacky syntax.
			 */
			return(input.replaceAll("<span style=\"background-color: #([a-fA-F0-9]{6}).*?>(.*?)</span>", "<bgcolor-$1>$2</bgcolor-$1>"));
		}
		
		    public static Spanned fromHtml(String text)
		    {
		    	text = firstLineStyle(text);
		    	text = spanToPseudoElement(text);
			    text = fixNewline(text);
		    	TagHandler handler = new HtmlTagHandler();
		    	return(Html.fromHtml(text,null,handler));
		    	
		    }
	}
}
