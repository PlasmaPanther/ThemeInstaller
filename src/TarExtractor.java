import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.lang.InterruptedException;
import java.util.concurrent.TimeUnit;
 
public class TarExtractor {

	private String rootDir = "";
	private long backSlashCount = 0;
	
	private ProcessBuilder pB = new ProcessBuilder();
	
	private void CheckFileStructure(String archiveName){
		
		//List contents of tar
		pB.command("bash", "-c", "tar -tvf " + archiveName);
		
		if(this.ProcessCheck(false)){
		
			System.out.println("Tar file structure check successful.");
		}
		else{
		
			System.out.println("Tar file structure check failed.");
		}
	}
	
	public void Extract(String archiveName, String destFolder){
		
		this.CheckFileStructure(archiveName);
		
		if(backSlashCount > 1){
			
			//Does the same thing as the command in the ZipExtractor
			String command = "tar -xf --overwrite " + archiveName + " -C " + destFolder + ";mv " + destFolder + rootDir + "* " + destFolder + "; rm -r " + destFolder + rootDir;
			pB.command("bash", "-c", command);
		}else{
			//Execute tar unpack command with custom extract location
			pB.command("bash", "-c", "tar -xf " + archiveName + " --overwrite -C " + destFolder);
		}
		
		if(this.ProcessCheck(true)){
		
			System.out.println("Tar extraction successful.");
		} 
		else {
			System.out.println("Tar extraction failed.");
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
					//then get root folder
					int emptySpace = line.lastIndexOf(" ") + 1;
					int firstBackSlash = line.indexOf("/") + 1;
					
					//Throws an out of bound exception if I don't do it this way
					rootDir = line.substring(emptySpace, line.length());
					firstBackSlash = rootDir.indexOf("/") + 1;
					rootDir = rootDir.substring(0, firstBackSlash);
					
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
