
# Contains a manifest of Docker images required to run Codenvy for a particular version.
# Also contains an upgrade summary showing which upgrade paths a particular version is allowed.
# This image is only tagged as "latest".  It is a single image that provides starting point of info.
#
# Build:
#   docker build -t eclipse/che-test .
# 
# Use:
#    docker run -v $(pwd):/copy codenvy/version
FROM alpine:3.4
RUN mkdir -p /copy 

COPY test /
COPY docker-entrypoint.sh /

RUN chmod +x docker-entrypoint.sh 
ENTRYPOINT ["/docker-entrypoint.sh"]