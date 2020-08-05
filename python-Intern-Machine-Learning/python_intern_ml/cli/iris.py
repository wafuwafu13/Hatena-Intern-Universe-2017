import sys
import random
import matplotlib.pyplot as plt

import os
sys.path.append(os.getcwd())

from python_intern_ml.feature import Feature
from python_intern_ml.example import Example
from python_intern_ml.label import POSITIVE_LABEL, NEGATIVE_LABEL
from python_intern_ml.classification.perceptron import Perceptron

def evaluate(perceptron, data):
    # F値を返す関数に変更しましょう
    return random.random()

TARGET_LABEL = 'Iris-setosa'
# TARGET_LABEL = 'Iris-virginica'

def read_csv(filename):
    examples = []
    with open(filename, 'r') as fh:
        for line in fh:
            tmp = line.rstrip().split(",")
            features = [Feature(id, float(tmp[id])) for id in range(4)]
            examples.append(Example(features, POSITIVE_LABEL if tmp[4] == TARGET_LABEL else NEGATIVE_LABEL))
    return examples

train = read_csv(sys.argv[1])
test = read_csv(sys.argv[2])

num_of_training = len(train)
f1_scores = []
N_DIMENSION = 4

for n in range(num_of_training):
    train_subset = train[:n]
    perceptron = Perceptron(N_DIMENSION)
    # 学習のアルゴリズムに関するコードがここに入る
    f1_scores.append(evaluate(perceptron, test))

plt.plot(range(num_of_training), f1_scores)
plt.savefig(sys.argv[3])
