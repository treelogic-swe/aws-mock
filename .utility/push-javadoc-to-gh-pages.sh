
if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then

  echo -e "Starting to update gh-pages\n"

  cp -R build/docs/javadoc $HOME/javadoc-latest

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/treelogic-swe/aws-mock gh-pages > /dev/null

  cd gh-pages
  cp -Rf $HOME/javadoc-latest ./javadoc

  git add -f .
  git commit -m "Lastest Javadoc by travis build $TRAVIS_BUILD_NUMBER pushed to gh-pages"
  git push -fq origin gh-pages > /dev/null

  echo -e "Done magic with coverage\n"
  
fi
