import java.awt.event.*;
import java.awt.Color;
import javax.swing.*;
import javax.swing.table.*;
import java.io.*;
import java.lang.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class ThemeInstallerGUI extends JFrame implements ActionListener{

	private File themeArchive;
	private File IconFolder;
	private File ThemeFolder;
	
	private String FileToString;
	private String IconUnpackDir;
	private String ThemeUnpackDir;
	private String CurrentUnpackDir;
	private String[] Options;
	
	private JButton SelectFileButton;
	private JButton ExitButton;
	private JComboBox ComboBox;
	
	private ImageIcon FolderIcon;
	
	private JFileChooser FileChooser;
	
	private JPanel MainPanel;
	
	private TarExtractor tarExtract;
	private ZipExtractor zipExtract;

	public ThemeInstallerGUI(){
		
		this.CreateGUI();
		this.InitActions();
		
	}
	
	private void CreateGUI(){
	
		this.setTitle("Theme Installer");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		FolderIcon = new ImageIcon("resources/folder.png");
		this.setIconImage(FolderIcon.getImage());
		
		this.setLayout(null);
		this.setResizable(false);
		this.setSize(400, 400);
		
		MainPanel = new JPanel();
		MainPanel.setBackground(new Color(10, 35, 90));
		MainPanel.setBounds(0, 0, 400, 400);
		MainPanel.setLayout(null);
		
		SelectFileButton = new JButton("Select File");
		SelectFileButton.addActionListener(this);
		SelectFileButton.setBounds(115, 155, 100, 30);
		
		ExitButton = new JButton("Exit");
		ExitButton.addActionListener(this);
		ExitButton.setBounds(130, 200, 70, 30);
		
		FileChooser = new JFileChooser();
													//technically goes into .icon folder but the option will stay
		Options = new String[]{ "Select type", "Icon", "Theme", "Cursor" };
		
		ComboBox = new JComboBox(Options);
		ComboBox.setBounds(220, 155, 100, 30);
		
		this.add(MainPanel);
		
		MainPanel.add(SelectFileButton);
		MainPanel.add(ExitButton);
		MainPanel.add(ComboBox);
		
		this.setVisible(true);
	
	}
	
	private void InitActions(){
	
		//Get path to .icons folder
		IconUnpackDir = System.getProperty("user.home") + "/.icons/";
		ThemeUnpackDir = System.getProperty("user.home") + "/.themes/";
		
		IconFolder = new File(IconUnpackDir);
		ThemeFolder = new File(ThemeUnpackDir);
		
		CreateFolders();
		
		tarExtract = new TarExtractor();
		zipExtract = new ZipExtractor();
	}
	
	private void CreateFolders(){
	
		if(!IconFolder.exists()){
			System.out.println("Creating .icons folder.");
			IconFolder.mkdirs();
		}else{
			System.out.println(".icons folder exists.");
		}
		
		if(!ThemeFolder.exists()){
			System.out.println("Creating .themes folder.");
			ThemeFolder.mkdirs();
		}else{
			System.out.println(".themes folder exists.");
		}
		
	}
	
	private String GetExtension(File file){
	
		// convert the file name into string
    	String fileName = file.toString();
    	String extension = "";

    	int index = fileName.lastIndexOf('.');
    	if(index > 0) {
      		extension = fileName.substring(index + 1);
      	}
		
		return extension;
	}
	
	private boolean isValidOption(){
	
		String value = String.valueOf(ComboBox.getSelectedItem());
	
		if(value.equals("Icon") || value.equals("Cursor")){
		
			CurrentUnpackDir = IconUnpackDir;
			return true;
		}
		else if(value.equals("Theme")){
		
			CurrentUnpackDir = ThemeUnpackDir;
			return true;
		}
		else{
		
			try {
			
				throw new IOException("Invalid option selected.");
				
			}catch(IOException e){
			
				e.printStackTrace();
				return false;
			}
		}
		
	}
	
	private void RunCommand(String filename, String extension){
		
		
		if(extension.equals("xz") || extension.equals("gz") || extension.equals("tar")){
			
			tarExtract.Extract(filename, CurrentUnpackDir);
		}
		
		if(extension.equals("zip")){
			
			zipExtract.Unzip(filename, CurrentUnpackDir);
		}
	
	}	
	
	@Override
	public void actionPerformed(ActionEvent e){
		
		if(e.getSource() == SelectFileButton){
			
			int response = FileChooser.showOpenDialog(null);
			
			if(response == JFileChooser.APPROVE_OPTION && isValidOption()){ //Will not unpack archive unless a valid option is selected
			
				//Get path to archive
				themeArchive = new File(FileChooser.getSelectedFile().getAbsolutePath());
				
				FileToString = themeArchive.toString();
				
				//System.out.println(FileToString);
				RunCommand(FileToString, GetExtension(themeArchive));
				
			}
		
		} else if (e.getSource() == ExitButton){
		
			System.exit(0);
		}
		
	}
	
}
