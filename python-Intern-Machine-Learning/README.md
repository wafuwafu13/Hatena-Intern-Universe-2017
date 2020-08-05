# Intern::ML
Web開発におけるコンピュータサイエンス - 機械学習編の課題雛形。2017年の教科書は[こちら](http://developer.hatenastaff.com/draft/KfI1Q2RSsYWJNr0Iet_ii-M5tG0)。

## セットアップ

```
$ git clone https://github.com/pyenv/pyenv.git ~/.pyenv
$ export PATH="$HOME/.pyenv/bin:$PATH" >> ~/.your_profile
$ eval "$(pyenv init -)" >> ~/.your_profile
$ exec $SHELL -l

$ pyenv install
$ pyenv rehash

$ python3 -m venv venv
$ . venv/bin/activate
$ pip install -r requirements.txt
```

`python --version`で`Python 3.6.0`になっているか確認しましょう。


Macを使っている人で、以下のエラーが出る場合には

```console
RuntimeError: Python is not installed as a framework. The Mac OS X backend will not be able to function correctly if Python is not installed as a framework. See the Python documentation for more information on installing Python as a framework on Mac OS X. Please either reinstall Python as a framework, or try one of the other backends. If you are using (Ana)Conda please install python.app and replace the use of 'python' with 'pythonw'. See 'Working with Matplotlib on OSX' in the Matplotlib FAQ for more information.
```


`~/.matplotlib/matplotlibrc`というファイル(なければ作る)に

```
backend: TkAgg
```

と記述しておきましょう。

## 課題1

```
$ python python_intern_ml/cli/iris.py iris.training.data iris.test.data learning_curve.png
```

## 課題2

```
$ python python_intern_ml/cli/bcomment.py bcomment.training.data bcomment.test.data
```
