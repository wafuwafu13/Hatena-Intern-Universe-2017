#!/usr/bin/env bash
set -xe

REPOS="
hatena/Hatena-Textbook
hatena/Hatena-Textbook-JavaScript
hatena/Hatena-Intern-Exercise2016
hatena/perl-Intern-Bookmark
hatena/scala-Intern-Bookmark
hatena/perl-Intern-Diary
hatena/scala-Intern-Diary
hatena/perl-Intern-Machine-Learning
hatena/scala-Intern-Machine-Learning
hatena/swift-Sample-GitHubSearch-2016
hatena/swift-Intern-Diary-2016-private
"

for repo in $REPOS; do
  repo_target=$( echo $repo | sed -e 's/^hatena\///' -e 's/2016//g' -e 's/private//g' -e 's/--/-/g' -e 's/-*$//g')
  rm -rf ./$repo_target;
  git clone --depth=1 --branch=master "git@github.com:$repo" $repo_target
  rm -rf ./$repo_target/.git

  git add $repo_target;
  git commit -m "Add $repo_target"
done
