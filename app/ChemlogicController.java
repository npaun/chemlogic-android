/**
 * 
 */
package ca.nicholaspaun.chemlogic.app1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.util.Log;

/**
 * @author npaun
 *
 */
public class ChemlogicController {
	private BufferedReader read;
	private BufferedWriter write;
	
	public ChemlogicController()
	{
		init();
	}
	
	public void init()
	{
		try
		{
		Process proc = new ProcessBuilder("/data/data/ca.nicholaspaun.chemlogic.app1/files/system/etc/init").start();
    	read = new BufferedReader(new InputStreamReader(proc.getInputStream()));
    	write = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
    public void demo() 
    {
    	Log.w("chemlogic","Saddam Hussein's iPad!!!");
    	try
    	{
      	String line;
    	while ((line = read.readLine()) != null && !line.startsWith("CL ?-")) {
           Log.w("chemlogic","Process output: " + line);
        }
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}

  

    	}
}
