jBeagle
=======

This is a fork of the jBeagle project from https://github.com/schierla/jbeagle by Andreas Schierl.
In addition to the original project it offers a way to preview the rendered pages and adjust them:

- cropping from top, bottom or sides
- horizontal and vertical scaling
- brightness and contrast adjustment

Here's an annotated screenshot for a basic introduction:
![annotated screenshot](https://raw.githubusercontent.com/ligius-/jbeagle/master/docs/screenshot_20150706220528_manual.png)

Tips and limitations
====================
Load the file first using the 'Browse' button and adjust the preview, without connecting the reader. If you connect the reader before adjusting there's a high chance it will time out. This will be improved by issuing a periodic ping.

Try to keep the horizontal and vertical scaling factors equal for maximum sharpness.

There are some cosmetic issues because of the transparent spinners.

If you close the preview frame there is no way to get it back other than restarting the application.

There's no reconnection, you have to restart the application.

If you close the application make sure to memorize the settings first, they are not persisted.

Not tested under Linux - a standard Serial Port connection option could be added which would ensure more compatibility.

Brightness and contrast are tricky to adjust - a better option would be a gamma table.

I could not get antialiasing to work properly - a better option would be a gaussian blur.




Copy&paste description from the main project:
=

Java application for managing the txtr beagle

jBeagle supports

- *Listing* the books present on the beagle
- *Deleting* books
- *Uploading PDF* documents as ebooks
 
jBeagle is based on

- *bluecove* http://bluecove.org/ for bluetooth support
- *jzlib* http://www.jcraft.com/jzlib/ for image compression
- *JPedal* http://sourceforge.net/projects/jpedal/ for PDF rendering
- the txtr beagle analysis of Florian Echtler http://floe.butterbrot.org/matrix/hacking/txtr/ 
- and Ligius http://hackcorellation.blogspot.de/2013/07/txtr-beagle-part-two-software.html

jBeagle is released under the GNU GPL.


How to use
==========

1. Pair the beagle with your PC using the operating system mechanisms
2. Switch the beagle into bluetooth mode (switch on, then hold the power key until the light flashes blue)
3. Start jBeagle

jBeagle then shows a list of books present on the beagle. You can select a book and click Delete to remove the book.
Click Upload and select a PDF file to upload it as a new book (You can use Calibre http://calibre-ebook.com/ to convert EPUB to PDF).


How to run
==========

Option 1:
- Import the project into Eclipse (Java + M2E)
- Run the class de.schierla.jbeagle.ui.JBeagle as a Java application

Option 2:
- compile project using *mvn package*
- Execute the resulting file *target/jbeagle-0.1.1-SNAPSHOT-jar-with-dependencies.jar*
