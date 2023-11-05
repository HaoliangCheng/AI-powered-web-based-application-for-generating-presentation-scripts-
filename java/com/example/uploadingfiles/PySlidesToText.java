package com.example.uploadingfiles;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class PySlidesToText {
	
//	public static void main(String[] args) {
//		String s = "";
//		runPythonScript(s,s,s);
//	}

	public static String runPythonScript(String scriptPath, String inputPath, String destPath) {
	    try {
	    	ProcessBuilder processBuilder = new ProcessBuilder();
//	    	processBuilder.command("which", "python3");
	    	processBuilder.command("python3", scriptPath, inputPath, destPath);
	    	Process process = processBuilder.start();

	        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        String line1;
	        StringBuilder output = new StringBuilder();
	        while ((line1 = reader.readLine()) != null) {
	            output.append(line1).append("\n");
	            System.out.print(output.toString());	        }
	    	
	    	// Capture the error output of the process
            InputStream errorStream = process.getErrorStream();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
            StringBuilder errorOutput = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }

	        int exitCode = process.waitFor();
	        if (exitCode == 0) {
	            return "Script execution successfully";
	            
	        } else {
	            // Handle script execution error
	        	System.out.println(exitCode);
	        	System.out.println("Error output:\n" + errorOutput.toString());
	            return "Script execution failed";
	        }
	    } catch (IOException | InterruptedException e) {
	        // Handle exception
	        return "Error executing Python script";
	    }
	}

}
