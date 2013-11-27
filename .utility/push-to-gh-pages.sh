if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then

  echo -e "Start to publish lastest Javadoc to gh-pages...\n"

  cp -R build/docs/javadoc $HOME/javadoc-latest
  cp -f README.md $HOME/index-latest.md
  cp -f contributing.md $HOME/contributing-latest.md

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/treelogic-swe/aws-mock gh-pages > /dev/null

  cd gh-pages
  git rm -rf ./javadoc ./index.md
  cp -Rf $HOME/javadoc-latest ./javadoc
  cp -f $HOME/index-latest.md ./index.md
  cp -f $HOME/contributing-latest.md ./contributing.md
  git add -f .
  git commit -m "Auto-publishing on successful travis build $TRAVIS_BUILD_NUMBER"
  git push -fq origin gh-pages > /dev/null

  echo -e "Done magic with auto publishment to gh-pages.\n"
  
fi
