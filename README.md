# VirtualForay
This is a tool to help you learn the names of things.  It uses google image search API to retrieve images from a user-defined database, displays the image, and quizzes the user as to the identity of the image.  Because google image search API is limited to 100 images per day, this program will stop working after awhile, in which case, you should simply close it and wait a day.  However, there is also a known bug where an image fails to load and so hangs indefinitely (sometimes this is because too few image results were returned).  In this case, it is best to restart the program.


Note that this project is now static.  I don't plan on modifying it further.  The next step is to make an in-browser version running on a web server.

Historically, I have used this for learning species names of fungi.  You load a database of your choosing (see examples for formatting) and images from the internet are retrieved and displayed.  You then guess what the species is and repeat the process.

Note that this relies heavily on google image search results being correct.  Obviously, occasional misclassifications will occur and certain species names will have to be modified in the data base to get relevant results (e.g. "black jack" vs. "black jack fish").  The accuracy greatly depends on the nature of the database, but I have found it very useful.

### Getting started
Download the archive using the "Download Zip" option at the right of the page. Unpack the zip archive, copying the folder "VirtualForay-master" to a new location (DO NOT SIMPLY OPEN IT AND RUN THE PROGRAM FROM THE ZIP - you will be unable to access the database files).  Run the program "SpeciesSelector1.04.jar" - you may have to play with the java settings on your computer as security tends to be tight for such apps.  Select a database to load - several examples are included in the "DB" folder, configure the options, and press "Start!" to begin!.

The keys "asdf" can be used to select options for multiple choice (the recommended form) and space to confirm your selection.  Occasionally, an image that cannot be shrunk will be displayed and the options will be rendered invisible and cannot be clicked on as they are beyond the bottom of the window.  When this happens, simply press the space bar to move on to the next photo. Hope you find this as useful as I have!
