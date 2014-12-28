package ca.nicholaspaun.chemlogic.app1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import  ca.nicholaspaun.chemlogic.app1.ChemlogicController;

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
					.add(R.id.container, new PlaceholderFragment()).commit();
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
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
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


    

    }

