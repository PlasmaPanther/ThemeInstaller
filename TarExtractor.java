import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.lang.InterruptedException;
import java.util.concurrent.TimeUnit;
 
public class TarExtractor {

	private String GetRootDir(String archiveName){
	
		ProcessBuilder pB = new ProcessBuilder();
	
		String dir = "";
		
		//Get top directory
		pB.command("bash", "-c", "tar --exclude='*/*' -tvf " + archiveName);

		try {

			Process process = pB.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			//This prints one line so no need for a while loop.
			dir = reader.readLine();
			reader.close();
			
			int emptySpace = dir.lastIndexOf(" ") + 1;
			int length = dir.length();
			
			dir = dir.substring(emptySpace, length);
			
			if (process.waitFor(5, TimeUnit.SECONDS)) {
				System.out.println("Tar archive root path command successful.");
			}else{
				
				System.out.println("Could not get tar archive root path.");
			}
			
			process.destroyForcibly();
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
		return dir;
    }
	
	private long CheckFileStructure(String archiveName){
	
		ProcessBuilder pB = new ProcessBuilder();
		
		//List contents of tar
		pB.command("bash", "-c", "tar -tvf " + archiveName);
		
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
				System.out.println("Tar archive content read command was successful.");
			}else{
				
				System.out.println("Could not read tar archive contents.");
			}
			
			process.destroyForcibly();
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
		return backSlashCount;
	}
	
	public void Extract(String archiveName, String destFolder){
		
		String RootDirName = GetRootDir(archiveName);
		long SubdirCount = CheckFileStructure(archiveName);
		
		ProcessBuilder processBuilder = new ProcessBuilder();
		
		if(SubdirCount > 1){
			
			//Does the same thing as the command in the ZipExtractor
			String command = "tar -xf --overwrite " + archiveName + " -C " + destFolder + ";mv " + destFolder + RootDirName + "* " + destFolder + "; rm -r " + destFolder + RootDirName;
			processBuilder.command("bash", "-c", command);
		}else{
			//Execute tar unpack command with custom extract location
			processBuilder.command("bash", "-c", "tar -xf --overwrite " + archiveName + " " + "-C " + destFolder);
		}
		
		try {

			Process process = processBuilder.start();

			StringBuilder output = new StringBuilder();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {}
			
			if (process.waitFor(5, TimeUnit.SECONDS)) {
				System.out.println("Tar extracted successfully.");
			} else{
			
				System.out.println("Tar extract command failed.");
			}
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	
	}
 
}
