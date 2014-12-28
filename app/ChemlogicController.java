/**
 * 
 */
package ca.nicholaspaun.chemlogic.app1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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
	public String chemlogic_ident;
	
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
	
	public void write(String line)
	{
		Log.d("chemlogic","Command: " + line);
		try {
			write.write(line + ".\n");
			write.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String read()
	{
		try
    	{
      	String line;
      	String message = "";
    	while ((line = read.readLine()) != null && !line.startsWith("CL ?-")) {
          message += line;
        }
    	return(message);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return("[ChemlogicController.read] INTERNAL ERROR");
    	}

		
	}
	
	public String command(String InputType, String Input,String OutputType)
	{
		write(InputType + " - '" + Input + "' :: " + OutputType + " print");
		return(read());
	}
	
	public String command(String InputType, String Input)
	{
		write(InputType + " - '" + Input + "' :: print");
		return(read());
	}
	
	
	public void acknowledge()
	{
      chemlogic_ident = read();
      Log.i("chemlogic",chemlogic_ident);
	}		
	
	public void halt()
	{
		write("halt");
	}
	
	public void reset()
	{
		halt();
		init();
	}
}
