import os
import sys
import json
import random

sys.path.append(os.getcwd())
from python_intern_ml.label import POSITIVE_LABEL, NEGATIVE_LABEL
from python_intern_ml.bcomment import Bcomment

from python_intern_ml.example import Example
from python_intern_ml.classification.perceptron import Perceptron
import python_intern_ml.metrics.classification as metrics

def read_json(filename):
    examples = []
    with open(filename, 'r') as fh:
        for line in fh:
            data = json.loads(line)
            label = POSITIVE_LABEL if data["is_malicious"] else NEGATIVE_LABEL
            features = Bcomment().get_features()
            examples.append(Example(features, label))
    return examples

train = read_json(sys.argv[1])

# 試行の度に結果が変わらないように乱数のシードを固定
random.seed(0)

test = read_json(sys.argv[2])

N_DIMENSION = 0
perceptron = Perceptron(N_DIMENSION)

gold_labels = [example.label for example in test]

# パーセプトロンなど識別器の学習に関するコードを書く

pred_labels = [perceptron.predict(example.features) for example in test]
print("{0:.3f}\t{1:.3f}\t{2:.3f}".format(metrics.precision_score(gold_labels, pred_labels),
                                         metrics.recall_score(gold_labels, pred_labels),
                                         metrics.f1_score(gold_labels, pred_labels)))
