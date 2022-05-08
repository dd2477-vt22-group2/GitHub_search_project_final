# github-search

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
    
To index into elasticsearch, 

put the java files to index into the directory

keep elasticsearch running,

install the necessary python dependencies and run in command line

`python3 es_utils.py`

To run the evaluation, 

put the text files in ./to_evaluate,

and run in command line

`python3 evaluator.py`
