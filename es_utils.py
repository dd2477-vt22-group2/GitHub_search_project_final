from elasticsearch import Elasticsearch
# from elasticsearch_dsl
from typing import List

import handler
import os

def get_mapping() -> dict:
    doc_mapping = {
        "mappings": {
            "properties": {
                "name": {"type": "text"},
                "is_class": {"type": "boolean"},
                "super_types": {"type": "text"},
                "parameters": {"type": "text"},
                "return_type": {"type": "text"},
                "comments_and_fields": {"type": "text"},
                "file_name": {"type": "text"},
                "file_start_line": {"type": "integer"},
                "file_end_line": {"type": "integer"}
            }
        }
    }
    return doc_mapping


class Doc:
    def __init__(self, name: str, is_class: bool, super_types: str, parameters: str, return_type: str,
                 comments_and_fields: str, file_name: str, file_start_line: int, file_end_line: int):
        self.name = str(name)
        self.is_class = bool(is_class)
        self.super_types = str(super_types)
        self.parameters = str(parameters)
        self.return_type = str(return_type)
        self.comments_and_fields = str(comments_and_fields)
        self.file_name = str(file_name)
        self.file_start_line = int(file_start_line)
        self.file_end_line = int(file_end_line)


def check_or_create_index(name: str = "test", mapping=None):
    if mapping is None:
        mapping = get_mapping()
    if not es.indices.exists(index=name):
        print(es.indices.create(index=name, body=mapping))
        print("index " + str(name) + " created")
        return True
    else:
        print("index " + str(name) + " already exists")
        return False


def delete_index(name: str):
    if es.indices.exists(index=name):
        es.indices.delete(index=name)
        print("index " + str(name) + " deleted")
    else:
        print("index " + str(name) + " does not exist")


def bulk_add(add_index_name: str, add_data: List[dict]):
    actions = []
    for i in range(len(add_data)):
        actions.append({"index": {}})
        actions.append(add_data[i])
    add_res = es.bulk(body=actions, index=add_index_name, refresh=True)
    print(add_res)


def split(a, n):
    k, m = divmod(len(a), n)
    return (a[i * k + min(i, m):(i + 1) * k + min(i + 1, m)] for i in range(n))


data = list(handler.parse_all(os.getcwd()))
print("Finished parsing all " + str(len(data)) + " classes or methods")
# start elasticsearch after parsing, otherwise might es client might disconnect
print("starting elasticsearch")
es = Elasticsearch(
        "http://localhost:9200", request_timeout=300000  # if still timeout, try to increase the timeout
    )

if __name__ == '__main__':

    print(es.info())
    index_name = "github_search"
    # # Uncomment the following lines to add data to the index
    delete_index(index_name)
    check_or_create_index(index_name)
    bulk_add(index_name, data)
    # res = es.search(index=index_name, rest_total_hits_as_int=True)
    # print(res)
