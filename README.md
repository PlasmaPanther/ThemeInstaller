# ThemeInstaller
Icon and theme installer written in java for linux.

# How to use

1. Download an icon pack or theme.
2. Open this program and select from the menu if you are installing an icon or theme.
3. Select the downloaded archive (the program will extract it automatically).
4. Then enable it using desktop enviorment specific tool.

If no other steps are needed this should be all you need to do.

# Dependencies
To use this you need to have these packages on your system:
* Unzip
* Java JRE
* Java JDK

Unzip usually comes pre-installed but to install it and the rest of the dependencies run the following command:
```
sudo apt install default-jre default-jdk unzip
```
# How to run
* Clone the repository with:
  ```
  git clone https://github.com/PlasmaPanther/ThemeInstaller.git
  ```
* Enter the directory:
  ```
  cd ThemeInstaller
  ```
* Compile with:
  ```
  javac src/Main.java src/ThemeInstallerGUI.java src/TarExtractor.java src/ZipExtractor.java
  ```
* And run with the following command:
  ```
  java Main
  ```
# Something to keep in mind

This program uses `tar` and `unzip` console commands to extract archives. I found it easier to do it this way because to extract zip archives in java I need 3rd party libraries and even if I use said libraries it results with a lot of complicated code.

Also when I experimented with these lbraries they wouldn't extract symlinks properly and icon packs looked broken or just wouldn't extract sub-directories.

The program itself is unfinished and pretty barebones when it comes to front-end and installation process.

I have written this on Debian 12 with GNOME DE and I haven't tried it on other distros so I don't know how well it will work.

Lastly this is the only thing a have written in Java like ever and im doing this to improve my skills so use this at your own risk.

# TODO
* Add support for install.sh scripts (some gtk themes use them for installation).
* Add multiple selection of files to extract.
* Add JTable with JComboBox to select options for multiple extractions of icons, themes etc.
* Add a menu to remove already extracted archives.
* Improve the front-end.
* Eventually rewrite the extracting part with java libraries instead of using console commands (if I have the time).
