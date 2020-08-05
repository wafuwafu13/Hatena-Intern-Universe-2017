#!/usr/bin/env bash

PROJECTS=$(find . -maxdepth 1 -type d | grep -E -v '^(./.git|\.$|./tools)' | sed -e 's/^\.\///')
TARGETS=$(find . | grep -E '\.md$')

for PROJECT in $PROJECTS; do
  perl -pi -e "s{(?:\.\./|https://github\.com/).*?hatena/$PROJECT/blob/master/}{/$PROJECT/}g" $TARGETS
  perl -pi -e "s{(?:\.\./|https://github\.com/).*?hatena/$PROJECT/tree/master/}{/$PROJECT/}g" $TARGETS
  perl -pi -e "s{https://github\.com/hatena/$PROJECT}{/$PROJECT}g" $TARGETS
done
