package ca.nicholaspaun.chemlogic.app1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends ActionBarActivity {
	private ChemlogicController ctrl;
	protected boolean inhibit_spinner = true;
	
	
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
      setup_menu_spinner(menu);
		    

		return true;
	}
	
	public void selectFragment(int id)
	{
		Log.i("chemlogic","Hello, yes?");
		Spinner fragmentSpinner =  (Spinner) findViewById(id);
    	String  fragment = "ca.nicholaspaun.chemlogic.app1." + fragmentSpinner.getSelectedItem().toString() + "Fragment";
    	
    	
    	try {
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.container, (Fragment) Class.forName(fragment).newInstance()).commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("chemlogic","Something really bad happened while switching fragments.");
		}
    	
    	
	}
	
	public void setup_menu_spinner(Menu menu)
	{
		 MenuItem SpinnerItem = menu.findItem(R.id.action_spinner);
		    Spinner spinner = (Spinner) MenuItemCompat.getActionView(SpinnerItem);
		    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.menu_features_array, R.layout.custom_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
			// Apply the adapter to the spinner
			spinner.setAdapter(adapter);
			
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			    @Override
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
			    	
			    	if (inhibit_spinner)
			    		inhibit_spinner = false;
			    	else
			    		selectFragment(R.id.action_spinner);
			    }

			    @Override
			    public void onNothingSelected(AdapterView<?> parentView) {
			        // your code here
			    }

			});
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		Log.i("chemlogic","Somting is happaning.");
		if (id == R.id.action_spinner) {
			//selectFragment(id);
			return true;
		}
		return super.onOptionsItemSelected(item);
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
    
    
    public void kbd_plus(View v)
    {
     EditText editText = (EditText) findViewById(R.id.chemlogic_input);
     editText.append(" + ");
    }

    
    public void kbd_arrow(View v)
    {
     EditText editText = (EditText) findViewById(R.id.chemlogic_input);
     editText.append(" --> ");
    }
    
    }

