import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.lang.InterruptedException;
import java.util.concurrent.TimeUnit;

public class ZipExtractor {
    
    private String GetRootFolder(String archiveName){
    
    	ProcessBuilder pB = new ProcessBuilder();
    	
    	String dir = "";
		
		//Get name of root folder
		pB.command("bash", "-c", "unzip -Z -1" + " " + archiveName + " " + "|" + "head -1");

		try {

			Process process = pB.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			//This prints one line so no need for a while loop.
			dir = reader.readLine();
			reader.close();
			
			if (process.waitFor(5, TimeUnit.SECONDS)) {
				System.out.println("Zip archive root path command successful.");
			}else{
				
				System.out.println("Could not get zip archive root path.");
			}
			
			process.destroyForcibly();
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
		return dir;
    }
    
    private long CheckFileStructure(String archiveName){
    
    	ProcessBuilder pB = new ProcessBuilder();
		
		//List contents of zip
		pB.command("bash", "-c", "unzip -l" + " " + archiveName);
		
		int emptySpace = 0;
		int length = 0;
		long backSlashCount = 0;

		try {

			Process process = pB.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
			
				if(line.endsWith("theme")){
				
					//Limit output to the path of index.theme file.
					emptySpace = line.lastIndexOf(" ") + 1;
					length = line.length();
					line = line.substring(emptySpace, length);
					
					backSlashCount = line.chars().filter(ch -> ch == '/').count();
				}
			}
			
			reader.close();
			
			if (process.waitFor(5, TimeUnit.SECONDS)) {
				System.out.println("Zip archive content read command was successful.");
			}else{
				
				System.out.println("Could not read zip archive contents.");
			}
			
			process.destroyForcibly();
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
		return backSlashCount;
    }
    
    public void Unzip(String archiveName, String destFolder){
    	
    	String RootFolderDir = GetRootFolder(archiveName);
    	long SubdirCount = CheckFileStructure(archiveName);
    	
    	ProcessBuilder processBuilder = new ProcessBuilder();
		
		if(SubdirCount > 1){
			
			//This idiotic solution is used in case the zip archive has a root dir with a bunch of subfolders for the themes.
			//It unzips in specified directory then get's all subfolders from inside the root dir moves them outside and removes the root.
			//Overwrites if necessary and suppress printing of messages.
			String command = "unzip -o -q " + archiveName + " -d " + destFolder + ";mv " + destFolder + RootFolderDir + "* " + destFolder + "; rm -r " + destFolder + RootFolderDir;
			processBuilder.command("bash", "-c", command);
			
		}else{
			
			//Unzip normally in specified dir, overwrite if needed and suppress messages.
			String command = "unzip -o -q " + archiveName + " -d " + destFolder;
			processBuilder.command("bash", "-c", command);
		}
		
		try {

			Process process = processBuilder.start();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			//Read output otherwise it hangs.
			//The -q modifier sometimes supresses only part of the messages and this while loop is here just in case.
			String line;
			while ((line = reader.readLine()) != null) {}
			
			reader.close();
			
			if(process.waitFor(5, TimeUnit.SECONDS)){
				System.out.println("Unzip command was successful.");
			}else{
				
				System.out.println("Unzip command failed.");
			}
			
			process.destroyForcibly();
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
    }
    
}
