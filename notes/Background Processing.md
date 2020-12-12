# Background Processing


If you want several things to happen at once then you need to have background processing as Android especially in Java has something called the Main Thread which is started as soon as the App starts. This Main Thread is also called as UI Thread as this is where all the data is assigned or updated to the View elements. If you perform too many operations or too complex operations on this UI Thread then your App will freeze or even crash. So we use various background processing tools to avoid strain on this Main Thread. Let’s start with Threads.

So what is a Thread?

Thread is a single, independent flow/path/route of execution in an Application.

We need these Threads to reduce strain on the Main Thread. The Threads that are not Main are called Child Threads. We use these child Threads to do complex tasks that take a lot of time to avoid freezing the UI. This concept of doing stuff in the background is also called as Multi-Threading where the goal is to do multiple tasks at the same time parallelly than sequentially. A good example is the Browser which we use for surfing the Internet. When you search for a keyword in the search box there are a ton of things happening like checking for spelling mistakes and then hitting the Google servers or whatever search provider servers for getting the response of the query for suggesting relevant keywords and then the autocomplete service that predicts and suggests the type of query you are looking for, then the UI that pops down to the bottom and shows a list of suggestions as you type and if its Bing then there is the dimming down of the whole screen just a bit to let you focus on the search query and in some other browsers they pre-load the top 3 queries to load them fast as you type. This is a fine example of Multi-Threading. So many things happening in parallel to make it easy to search and give us a good experience of browsing the Internet.

Fundamentally there are 2 types of Multi-Tasking

Multi Processing: Think of Multi Processing as an Operating System level thing. So every Application is a process. When you open a Browser it’s a separate process. Word Document is another process.
Multi Threading: Think of Multi Threading as App level thing. In one App we can perform multiple jobs where each job is executed by a separate Thread. All these Threads can run parallelly or technically called concurrently within an App to do many things at once. So checking for spelling mistakes is done on 1 Thread, API call for autocomplete text is another Thread, etc.
There are many different ways to off load tasks to the background process.

Thread
Async Task
Service
Let’s look at Threads and Async Task.

Async Task is used to perform heavy operations like heavy computation, downloads, networking etc. Below are the methods of an Async Task.

Method	Description
onPreExecute()	Happens before background process like a progress bar
doInBackground(Params)	Performs in the background thread for heavy operations like downloading, network requests etc.
onProgressUpdate(Progress…)	While background operation is being performed we can do some UI updates
onPostExecute(Result)	Update UI with the result of background process