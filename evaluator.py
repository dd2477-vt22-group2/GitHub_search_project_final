import os

import matplotlib.pyplot as plt
import numpy as np
import math

def readFile(filename):
    result = np.array([])
    fhand = open(filename)
    for line in fhand:
        line = line.rstrip()
        v = line.split(" ")
        if len(v) != 2:
            continue
        result = np.append(result, int(v[1]))
    return result


def precision(array):
    return float(np.sum(array != 0)) / np.size(array)


def recall(array):
    return float(np.sum(array != 0)) / 100


def get_precision_and_recall(inner_file_name):
    result = readFile(inner_file_name)

    PRE = np.array([])
    REC = np.array([])
    size = result.size + 1
    for i in range(1, size):
        PRE = np.append(PRE, precision(result[:i]))
        REC = np.append(REC, recall(result[:i]))
    print("Precision:", str(np.round(PRE * 100) / 100.))
    print("Recall:   ", str(REC))

    # Plot the precision vs. number of documents
    plt.figure()
    plt.plot(range(1, size) , PRE, label='Precision', linestyle='dashed', linewidth = 3, marker='o', markerfacecolor='blue')
    plt.axis([0, size, 0, 1])
    plt.xlabel('Number of documents')
    plt.ylabel('Precision')
    plt.title('Precision vs. Number of Documents')
    plt.legend()
    file_name_without_extension = os.path.splitext(inner_file_name)[0]
    print("Saving plot to " + file_name_without_extension + "_precision.png")
    plt.savefig(file_name_without_extension + "_precision.png")
    plt.clf()

    # and recall vs. number of documents
    plt.plot(range(1, size), REC, label='Recall', linestyle='dashed', linewidth = 3, marker='o', markerfacecolor='blue')
    plt.axis([0, size, 0, 1])
    plt.xlabel('Number of documents')
    plt.ylabel('Recall')
    plt.title('Recall vs. Number of Documents')
    plt.legend()
    print("Saving plot to " + file_name_without_extension + "_recall.png")
    plt.savefig(file_name_without_extension + "_recall.png")
    plt.clf()

    # Plot the precision-recall curve
    plt.plot(REC, PRE, color='green', linestyle='dashed', linewidth = 3, marker='o', markerfacecolor='blue')
    plt.axis([0 if np.min(REC) == np.max(REC) else np.min(REC), np.max(REC), 0 if np.min(PRE) == np.max(PRE) else np.min(PRE), np.max(PRE)])
    plt.xlabel('Recall')
    plt.ylabel('Precision')
    plt.title("Precision-Recall curve")
    print("Saving plot to " + file_name_without_extension + "_precision_recall" +".png")
    plt.savefig(file_name_without_extension + "_precision_recall.png")
    plt.clf()


def DCG(result):
    dcg = []
    for i, rel in enumerate(result):
        score = rel / math.log2(i + 1 + 1)
        dcg.append(score)
    return sum(dcg)


def NDCG(result):
    dcg = DCG(result)
    # ideal
    idcg = DCG(sorted(result, reverse=True))
    ndcg = dcg / idcg
    return ndcg


def get_DCG_and_NDCG(file_name):
    results = readFile(file_name)

    print(f"nDCG = {NDCG(results)}\nDCG = {DCG(results)}")
    return NDCG(results), DCG(results)


def get_average_precision_and_recall(folder_name):
    # Loop over all text files in the folder
    # and average their relevance scores by the number of files
    # up until the line number of the file with fewest lines
    # and calculate the precision and recall
    results = []
    for file in os.listdir(folder_name):
        if file.endswith(".txt"):
            file_name = os.path.join(folder_name, file)
            results.append(readFile(file_name))

    # Initialize the arrays to be 50 elements long
    # and fill them with zeros
    PRE = np.zeros(50)
    REC = np.zeros(50)

    for i in range(0, len(results)):
        for j in range(1, 51):
            if j > results[i].size:
                break
            PRE[j - 1] += precision(results[i][:j])/len(results)
            REC[j - 1] += recall(results[i][:j])/len(results)

    print("Precision:", str(np.round(PRE * 100) / 100.))
    print("Recall:   ", str(REC))

    # Plot the precision vs. number of documents
    plt.figure()
    plt.plot(range(1, 51) , PRE, label='Precision', linestyle='dashed', linewidth = 3, marker='o', markerfacecolor='blue')
    plt.axis([0, 50, 0, 1])
    plt.xlabel('Number of documents')
    plt.ylabel('Precision')
    plt.title('Precision vs. Number of Documents')
    plt.legend()
    print("Saving plot to average_precision.png")
    plt.savefig(folder_name + "\\boosting_average_precision.png")
    plt.clf()

    # and recall vs. number of documents
    plt.plot(range(1, 51), REC, label='Recall', linestyle='dashed', linewidth = 3, marker='o', markerfacecolor='blue')
    plt.axis([0, 50, 0, 1])
    plt.xlabel('Number of documents')
    plt.ylabel('Recall')
    plt.title('Recall vs. Number of Documents')
    plt.legend()
    print("Saving plot to average_recall.png")
    plt.savefig(folder_name + "\\boosting_average_recall.png")
    plt.clf()

    # Plot the precision-recall curve
    plt.plot(REC, PRE, color='green', linestyle='dashed', linewidth = 3, marker='o', markerfacecolor='blue')
    plt.axis([0, 1, 0, 1])
    plt.xlabel('Recall')
    plt.ylabel('Precision')
    plt.title("Precision-Recall curve")
    print("Saving plot to average_precision_recall.png")
    plt.savefig(folder_name + "\\boosting_average_precision_recall.png")
    plt.clf()


if __name__ == "__main__":
    # Loop over the folder to_evaluate for txt files
    total_DCG = 0
    total_NDCG = 0
    count = 0
    for file in os.listdir(".\\to_evaluate"):
        if file.endswith(".txt"):
            count += 1
            # Get the name of the file
            file_name = os.path.join(".\\to_evaluate", file)
            print(f"Evaluating {file_name}")
            get_precision_and_recall(file_name)
            temp_NDCG, temp_DCG = get_DCG_and_NDCG(file_name)
            total_DCG += temp_DCG
            total_NDCG += temp_NDCG
            print("\n")

    get_average_precision_and_recall(".\\to_evaluate")
    print(f"Average DCG = {total_DCG/count}\nAverage nDCG = {total_NDCG/count}")