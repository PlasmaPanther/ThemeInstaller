import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.lang.InterruptedException;
import java.util.concurrent.TimeUnit;

public class ZipExtractor {

	private String rootDir = "";
	private long backSlashCount = 0;
	
	private ProcessBuilder pB = new ProcessBuilder();
    
    private void CheckFileStructure(String archiveName){
		
		//List contents of zip
		pB.command("bash", "-c", "unzip -l " + archiveName);
		
		if(this.ProcessCheck(false)){
		
			System.out.println("File structure check in zip successful.");
		}else{
			System.out.println("File structure check in zip failed.");
		}
    }
    
    public void Unzip(String archiveName, String destFolder){
    	
    	this.CheckFileStructure(archiveName);
		
		if(backSlashCount > 1){
			
			//This idiotic solution is used in case the zip archive has a root dir with a bunch of sub-folders for the themes.
			//It un-zips in specified directory then get's all sub-folders from inside the root dir moves them outside and removes the root.
			//Overwrites if necessary and suppress printing of messages.
			String command = "unzip -o -q " + archiveName + " -d " + destFolder + ";mv " + destFolder + rootDir + "* " + destFolder + "; rm -r " + destFolder + rootDir;
			pB.command("bash", "-c", command);
			
		}else{
			
			//Unzip normally in specified dir, overwrite if needed and suppress messages.
			String command = "unzip -o -q " + archiveName + " -d " + destFolder;
			pB.command("bash", "-c", command);
		}
		
		if(this.ProcessCheck(true)){
		
			System.out.println("Unzip was successful.");
		}
		else{
			System.out.println("Unzip failed.");
		}
    }
    
    private boolean ProcessCheck(boolean readOnly){
    	
    	try {

			Process process = pB.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
			
				if(line.endsWith("theme") && !readOnly){

					//Limit output to the path of index.theme file.
					//and get root folder
					int emptySpace = line.lastIndexOf(" ") + 1;
					int firstBackSlash = line.indexOf("/") + 1;
					
					rootDir = line.substring(emptySpace, firstBackSlash);
					line = line.substring(emptySpace, line.length());
					
					backSlashCount = line.chars().filter(ch -> ch == '/').count();
				}
			}
			
			reader.close();
			
			if (process.waitFor(5, TimeUnit.SECONDS)) {
			
				process.destroyForcibly();
				return true;
			}else{
				process.destroyForcibly();
				return false;
			}
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
    	
    	return false;
    }
    
}
