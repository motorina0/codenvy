#!/bin/sh

if [ -z "$1" ]; then
	CODENVY_VERSION="latest"
else
	CODENVY_VERSION=$1
fi

if [ ! -d /files/$CODENVY_VERSION ]; then
	echo ""
	echo "We could not find version '$CODENVY_VERSION'. Available versions:"
	cd /files
	for d in */ ; do
      echo " ${d%?}"
  done
  echo ""
  echo "Set CODENVY_VERSION=<version> and rerun."
  echo ""
	return;
fi

for version in /files/* ; do
  cp -rf /$version /copy
done

