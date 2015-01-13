package ca.nicholaspaun.chemlogic.app1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	private ChemlogicController ctrl;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		installer_upgrade();
		ctrl = new ChemlogicController();
		
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new BalancerFragment()).commit();
		}
		ctrl.acknowledge();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class BalancerFragment extends Fragment {

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
		
		
		public void populateSpinner(View v, int Spinner, int Choices)
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
		
		
		public void buttonSetup(View v)
		{
			  final Button button = (Button) v.findViewById(R.id.balancer_button_submit);
		         button.setOnClickListener(new View.OnClickListener() {

	    public void onClick(View v)
	    {


	    	Spinner inputTypeSpinner =  (Spinner) getActivity().findViewById(R.id.balancer_spinner_type_input);
	    	String  inputType = inputTypeSpinner.getSelectedItem().toString().toLowerCase();
	    	
	    	Spinner outputTypeSpinner =  (Spinner) getActivity().findViewById(R.id.balancer_spinner_type_output);
	    	String  outputType = outputTypeSpinner.getSelectedItem().toString().toLowerCase();
	    	
	    	EditText editText = (EditText) getActivity().findViewById(R.id.balancer_edittext_input);
	        String input = editText.getText().toString();
	        
	        String output = ctrl.command(inputType, input,outputType);
	        
	        editText.clearFocus();
	        TextView textView = (TextView) getActivity().findViewById(R.id.balancer_textview_output);
	        textView.setText(Html.fromHtml(output));
	        
	        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
	        	      Context.INPUT_METHOD_SERVICE);
	        	imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);


	    }
			});
		}
		
		
	}
	
	public boolean installer_checkVersion()
	{
	String versionName = "unknown";
		
		try
		{
		versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
	} catch (PackageManager.NameNotFoundException e) {}

		Log.i("chemlogic","App version: " + versionName);
		return(new File("/data/data/ca.nicholaspaun.chemlogic.app1/files/system/VERSION-" + versionName).exists());
	}
	
	public void installer_upgrade ()  {

		if (!installer_checkVersion())
		{
			Log.i("chemlogic","Upgrade required.");
			installer_install();
		}
		else
		{
			Log.i("chemlogic","Up to date.");
		}
		}
 
	
	public void installer_install() {
		try{
			Runtime.getRuntime().exec("rm -r /data/data/ca.nicholaspaun.chemlogic.app1/files/");
				}
				catch(Exception e)
				{
				 e.printStackTrace();
				}

	            copyAssetFolder(getAssets(), "files", 
	                    "/data/data/ca.nicholaspaun.chemlogic.app1/files");
	}
	

    private static boolean copyAssetFolder(AssetManager assetManager,
            String fromAssetPath, String toPath) {
        try {
            String[] files = assetManager.list(fromAssetPath);
            new File(toPath).mkdirs();
            boolean res = true;
            for (String file : files)
            {
          
            	String[] contents = assetManager.list(fromAssetPath + "/" + file);
            	
            	
            	
            	
                if (contents.length == 0)
                {
                    res &= copyAsset(assetManager, 
                            fromAssetPath + "/" + file,

                            toPath + "/" + file);
                    
                    Log.d("chemlogic",toPath);
                    Log.d("chemlogic",file);
                                      
                    	Runtime.getRuntime().exec("chmod 700 " + toPath + "/" + file);
                }
                else 
                    res &= copyAssetFolder(assetManager, 
                            fromAssetPath + "/" + file,
                            toPath + "/" + file);
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean copyAsset(AssetManager assetManager,
            String fromAssetPath, String toPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
          in = assetManager.open(fromAssetPath);
          new File(toPath).createNewFile();
          out = new FileOutputStream(toPath);
          copyFile(in, out);
          in.close();
          in = null;
          out.flush();
          out.close();
          out = null;
          return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[2 * 1024 * 1024];
        int read;
        while((read = in.read(buffer)) != -1){ 
          out.write(buffer, 0, read);
        }
    }

    public ChemlogicController getController()
    {
    	return(ctrl);
    }
    
    
    public void chemkbd_plus(View v)
    {
     EditText editText = (EditText) findViewById(R.id.balancer_edittext_input);
     editText.append(" + ");
    }

    
    public void chemkbd_arrow(View v)
    {
     EditText editText = (EditText) findViewById(R.id.balancer_edittext_input);
     editText.append(" --> ");
    }
    
    }

