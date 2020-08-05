FROM perl:5.24

RUN apt-get update -yq \
    && apt-get install -yq \
        libssl1.0-dev \
        mysql-client \
        python-dev \
        unzip \
    && apt-get clean \
    && rm -rf /var/cache/apt/archives/* /var/lib/apt/lists/*

RUN curl -s https://s3.amazonaws.com/aws-cli/awscli-bundle.zip -o awscli-bundle.zip \
    && unzip awscli-bundle.zip \
    && ./awscli-bundle/install -i /usr/local/aws -b /usr/local/bin/aws \
    && rm -fr awscli-bundle.zip awscli-bundle

# App::cpm / Cartonのインストール
RUN cpanm App::cpm
RUN cpm install -g Carton

# ディレクトリの設定
ENV PERL_CARTON_PATH="/cpan" \
    APP_PATH="/app"

# アプリケーション関連の設定
ENTRYPOINT ["./script/entrypoint"]
CMD ["./script/appup"]

# --- ここまでの処理はキャッシュされるはず ---

# App::cpmで依存モジュールのインストール
WORKDIR ${PERL_CARTON_PATH}
COPY cpanfile ${PERL_CARTON_PATH}
RUN cpm install -L ${PERL_CARTON_PATH}

# cpanfile.snapshotを作る
RUN carton install

# アプリケーションコードの取り込み
WORKDIR ${APP_PATH}
COPY . ${APP_PATH}
