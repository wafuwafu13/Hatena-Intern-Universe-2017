FROM perl:5.24.0

# 必要なOSパッケージのインストール
RUN apt-get update \
 && apt-get install -y mysql-client python-dev unzip \
 && rm -rf /var/lib/apt/lists/*

# AWS CLIのインストール
RUN curl -s https://s3.amazonaws.com/aws-cli/awscli-bundle.zip -o awscli-bundle.zip \
 && unzip awscli-bundle.zip \
 && ./awscli-bundle/install -i /usr/local/aws -b /usr/local/bin/aws \
 && rm -fr awscli-bundle.zip awscli-bundle

# Cartonのインストール
RUN cpanm Carton

# 必要ディレクトリの設定
ENV PERL_CARTON_PATH="/cpan" \
    APP_PATH="/app"

# アプリケーション関連の設定
ENTRYPOINT ["./script/entrypoint"]
CMD ["./script/appup"]

#############################################################
# ここまでは一度実行すれば十分キャッシュされるはず
#############################################################

# Cartonで依存モジュールのインストール
WORKDIR ${PERL_CARTON_PATH}
COPY cpanfile ${PERL_CARTON_PATH}
RUN carton install

# アプリケーションコードの取り込み
WORKDIR ${APP_PATH}
COPY . ${APP_PATH}
