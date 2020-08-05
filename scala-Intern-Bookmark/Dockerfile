# ステージ1: sbtを使って依存関係も含めたパッケージ作成
FROM motemen/sbt:0.13.15 AS sbt

COPY .docker-ivy2-cache /root/.ivy2
COPY . /app
RUN cd /app && sbt universal:packageZipTarball

# ステージ2: デプロイ用にJREのみのイメージを元に作成
FROM openjdk:8-jre

# 必要なOSパッケージのインストール
RUN apt-get update \
 && apt-get install -y python-dev unzip \
 && rm -rf /var/lib/apt/lists/*

# AWS CLIのインストール
RUN curl -s https://s3.amazonaws.com/aws-cli/awscli-bundle.zip -o awscli-bundle.zip \
 && unzip awscli-bundle.zip \
 && ./awscli-bundle/install -i /usr/local/aws -b /usr/local/bin/aws \
 && rm -fr awscli-bundle.zip awscli-bundle

# TODO: 同期方法
ENV PACKAGE scala-intern-bookmark-0.0.3

# アプリケーション関連の設定
WORKDIR $PACKAGE
ENTRYPOINT ["./script/entrypoint"]
ENV USER app
CMD exec ./bin/jetty-launcher --port $PORT

# スクリプト系の取り込み
COPY script ./script
COPY db ./db

# 前ステージでのパッケージの取り込みと展開
COPY --from=sbt /app/target/universal/$PACKAGE.tgz /
RUN tar xvzf /$PACKAGE.tgz -C / && rm -f $PACKAGE.tgz
