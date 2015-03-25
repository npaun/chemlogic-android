// ChemlogicController.java: A Java API for Chemlogic that communicates over pipes with the Prolog code
// This file is from Chemlogic, a logic programming computer chemistry system  
// <http://icebergsystems.ca/chemlogic>  
// (C) Copyright 2012-2015 Nicholas Paun  

package ca.nicholaspaun.chemlogic.app1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.util.Log;

public class ChemlogicController {
	private BufferedReader read;
	private BufferedWriter write;
	private String chemlogic_ident;
	private String chemlogic_dir;
	
	public ChemlogicController(String directory) {
		chemlogic_dir = directory;
		init();
	}

	public void init() {
		try {
			Process proc = new ProcessBuilder(chemlogic_dir + "/system/etc/init")
					.start();
			read = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			write = new BufferedWriter(new OutputStreamWriter(
					proc.getOutputStream()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void write(String line) {
		Log.d("chemlogic", "Command: " + line);
		try {
			write.write(line + ".\n");
			write.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String read() {
		try {
			String line;
			String message = "";
			while ((line = read.readLine()) != null
					&& !line.startsWith("CL ?-")) {
				message += line + "\n";
			}
			return (message);
		} catch (Exception e) {
			e.printStackTrace();
			return ("[ChemlogicController.read] INTERNAL ERROR");
		}

	}

	private String escape(String str) {
		return (str.replace("\\", "\\\\").replace("'", "\\'"));
	}

	public String command(String InputType, String Input, String OutputType) {
		write(InputType + " - '" + escape(Input) + "' :: " + OutputType
				+ " print");
		return (read());
	}

	public String command(String InputType, String Input) {
		write(InputType + " - '" + escape(Input) + "' :: print");
		return (read());
	}

	public void acknowledge() {
		chemlogic_ident = read();
		Log.i("chemlogic", chemlogic_ident);
	}

	public String identify() {
		return (chemlogic_ident);
	}

	public void halt() {
		write("halt");
	}

	public void reset() {
		halt();
		init();
	}
}
