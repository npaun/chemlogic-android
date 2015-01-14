package ca.nicholaspaun.chemlogic.app1;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class AboutFragment extends ChemlogicFragment {

	private ChemlogicController ctrl;
	
	public AboutFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_about, container,
				false);
	
		ctrl = ((MainActivity) getActivity()).getController();
		populate_about(rootView);
		populate_identity(rootView);
		
		setup_devtoggle(rootView);
		setup_reset(rootView);
		setup_stop(rootView);
		setup_reinstall(rootView);
		
		return rootView;
	}
	
	public String version_get()
	{
     String versionName = "unknown";
		
		try
		{
		versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
	     return("<p><b>Version</b> "+versionName+"</p>");
	     
	} catch (PackageManager.NameNotFoundException e) {
		return("<p><b>Error!</b> Cannot determine version</p>");
	}
	

	}
	
	public void populate_about(View v)
	{
		TextView aboutText = (TextView) v.findViewById(R.id.about_copyright);
		aboutText.setText(Html.fromHtml(getString(R.string.about_copyright) + version_get()));
		aboutText.setMovementMethod(LinkMovementMethod.getInstance());
		
	}
	
	public String fix_tabs(String input)
	{
		return(input.replace("\t", " "));
	}
	
	public void populate_identity(View v)
	{
		TextView identityText = (TextView) v.findViewById(R.id.about_identity);
		identityText.setText(fix_tabs(ctrl.identify()));
	}
	
	public void setup_devtoggle(View v)
	{
		  final TextView button = (TextView) v.findViewById(R.id.about_dev_toggle);
	         button.setOnClickListener(new View.OnClickListener() {
	        	    public void onClick(View v)
	        	    {
	        	    	v.getRootView().findViewById(R.id.about_dev_panel).setVisibility(View.VISIBLE);
	        	    }
	         });
	}
	
	public void setup_reset(View v)
	{
		  final Button button = (Button) v.findViewById(R.id.about_reset_prolog);
	         button.setOnClickListener(new View.OnClickListener() {
	        	    public void onClick(View v)
	        	    {
	        	    	ctrl.reset();
	        	    }
	         });
	}
	
	public void setup_stop(View v)
	{
		  final Button button = (Button) v.findViewById(R.id.about_stop_prolog);
	         button.setOnClickListener(new View.OnClickListener() {
	        	    public void onClick(View v)
	        	    {
	        	    	ctrl.halt();
	        	    }
	         });
	}
	
	public void setup_reinstall(View v)
	{
		  final Button button = (Button) v.findViewById(R.id.about_reinstall_app);
	         button.setOnClickListener(new View.OnClickListener() {
	        	    public void onClick(View v)
	        	    {
	        	    	((MainActivity) getActivity()).installer_install();
	        	    }
	         });
	}
	
	
}