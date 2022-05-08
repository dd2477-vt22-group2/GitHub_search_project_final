import os
import requests
import time
import shutil


class Crawler:
    def __init__(self):
        # Path variables
        # Make sure the destination directory exists in the current folder
        self.file_storage = "corpora"
        self.repo_id = 0

        # Github search parameters
        # Max repository size by kilobytes. 30000 means 30MB
        # Use %20 to combine different qualifiers (language, size, number of follows, etc.)
        self.query = "q=language:java%20size:<=30000&sort=stars&order=desc&per_page=10"

        # Personal Access Token
        # Each token grants limited amount of requests per time unit
        # Generate and copy your token here
        # Guide: https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token
        # Select <repo> as permissions
        self.token = "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"

        # 30 calls per minute is allowed
        # Pause for 2 secs before GET
        self.sleep_time = 2.0
        # Number of total repos retrieved
        self.num_repos = 20

    def get_repos(self, max_count):
        url = "https://api.github.com/search/repositories?" + self.query
        count = 0
        page = 1
        while count < max_count:
            curr_url = url + "&page=" + str(page)
            res = self.GET(curr_url)
            page += 1
            if res is not None:
                count += len(res["items"])
                yield res["items"]
            else:
                yield []

    def GET(self, url, download=False):
        time.sleep(self.sleep_time)
        headers = {"Authorization": "token " + self.token}
        res = requests.get(url, headers=headers, stream=download)
        if res.status_code == 403:
            # 403: rate limit reached
            return self.GET(url)
        if res.status_code != 200:
            # other errors
            print("Error code: " + str(res.status_code) + " for url: " + url)
            return None
        if download:
            return res.raw
        else:
            return res.json()

    def download_repo(self, repo_url):
        repo_url = repo_url + "/tarball"
        repo_raw = self.GET(repo_url, download=True)
        if repo_raw is None:
            return
        tarball_name = os.path.join(self.file_storage, str(self.repo_id) + ".tar.gz")
        self.repo_id += 1
        with open(tarball_name, "wb") as f:
            repo_raw.decode_content = True
            shutil.copyfileobj(repo_raw, f)
        print("Saved " + repo_url + " to disk: " + tarball_name)

    def crawl(self):
        for repos in self.get_repos(self.num_repos):
            for repo in repos:
                self.download_repo(repo["url"])


if __name__ == "__main__":
    Crawler().crawl()
