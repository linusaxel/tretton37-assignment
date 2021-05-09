# tretton37-assignment

***Task***: Create a console program in Java that can recursively traverse and download www.tretton37.com and
save it to disk while keeping the online file structure. Use asynchronicity and parallelism as
much as possible, and show progress in the console. Don’t focus on html link extraction, keep that
part simple (regex or similar). The focus of the assignment is on asynchronicity, parallelism and threading.

***Solution***: This program downloads a wep page and all child directories to disk.
It does this by using multithreading via an ExecutorService to download and parse the HTML from a base URL and all the found sub-directories (i.e. href attributes of hyperlink tags).
A ConcurrentHashMap is used as a common resource for identifying which sub-directories have been or not downloaded and avoids concurrency issues.


***Usage***: To use this product, clone the code locally and run using the Gradle wrapper command and providing the desired base URL as an argument.

`./gradlew run --args='https://tretton37.com'
`

***Disclaimers***: This is *not* a finished product! :) 
