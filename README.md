# tretton37-assignment

***Task***: Create a console program in Java that can recursively traverse and download www.tretton37.com and
save it to disk while keeping the online file structure. Use asynchronicity and parallelism as
much as possible, and show progress in the console. Don’t focus on html link extraction, keep that
part simple (regex or similar). The focus of the assignment is on asynchronicity, parallelism and threading.

***Solution***: This program downloads a wep page and child directories to disk, up to a specified depth.
It does this by using multithreading via an ExecutorService to download and parse the HTML 
from a base URL and all the found sub-directories (i.e. href attributes of hyperlink tags). The parsing is done using the JSOUP library.
A HashMap<String, Boolean> is used to identify which sub-directories have been found.
The key of the HashMap contains the directory, and the value indicates whether it has been downloaded.

***Usage***: To use this program, download it and run using the Gradle wrapper command and providing the desired base URL and depth as an argument.

`./gradlew run --args='https://tretton37.com 3' 
`
