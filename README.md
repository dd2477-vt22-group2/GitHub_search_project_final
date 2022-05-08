# DD2477 GitHub search project

Install elasticsearch 8.1.2 locally, 

For Windows, follow the guide on https://www.elastic.co/guide/en/elasticsearch/reference/current/zip-windows.html

and run it for once (with administrator privileges)

Shut down elasticsearch, open config/elasticsearch.yml and change the following lines:

1. **modify line 92 and line94 to turn security checks off**:

    xpack.security.enabled: false
    
    xpack.security.enrollment.enabled: false

2. **add to the file**:
```
# Set a custom allowed content length:
# 
http.max_content_length: 500mb
```
    
To index into elasticsearch, open the Indexer folder (pycharm recommended)

put the crawled java files (https://drive.google.com/file/d/1y142CsI5SsYZGy3T7nffUlf6kVrjPx8c/view?usp=sharing) to index into the directory,

keep elasticsearch running,

install the necessary python dependencies in requirements.txt and run in command line

`python3 es_utils.py`


3. **compile and run the interface**
To run the user interface, open the interface folder (intellij required, for iml-styled java dependencies).

Before running the interface, please make sure that the whole Java code dataset is in a local folder named "java_files", and keep elasticsearch running.

The dataset we crawled can be downloaded here: https://drive.google.com/file/d/1y142CsI5SsYZGy3T7nffUlf6kVrjPx8c/view?usp=sharing

Please download and unzip it

The java interface of the search engine should be compiled by (on Windows):

`compile_all.bat`

and then run by:

`run_search_engine.bat`

4. others

To run the evaluator, put the manually rating txt files in ./to_evaluate, with a format of each line containing one file and its score, seperated by a space, like:

```
file1.java 0
file2.java 3
file3.java 2
```

and run in command line

`python3 evaluator.py`

To run the crawler, simply:

`python3 clean_crawler.py`

But you need to change the number of crawled files and its corresponding page/id on your own

And the token used in the crawler has expired, please follow the comments in the crawler file to get your own token

Without a valid token, some requests might be refused
