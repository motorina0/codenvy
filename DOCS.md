# Codenvy Installation and Operation
Codenvy makes cloud workspaces for develoment teams. Codenvy is a multi-user, multi-tenant, and distributed implementation of Eclipse Che with team management and a DevOps workspace platform. This page documents how to install and operate a clustered Codenvy using Docker.

- [Team](#Team)
- [Issues](#Issues)
- [Architecture](#architecture)
- [System Requirements](#system-requirements)
- [Installation](#installation)
- [Hosting](#hosting)
- [Quick Start](#quick-start)
- [Uninstall]()
- [Offline Installation](#offline-installation)
- [Configuration](#configuration)
- [Logs and User Data](#logs-and-user-data)
- [SMTP Configuration](#smpt-configuration)
- [oAuth](#oAuth)
- [Updates](#Updates)
- [Scaling](#Scaling)
- [Backup and Recovery](#Backups)
- [Development Mode](#development-mode)
- [Migration](#migration)
- [Monitoring](#Monitoring)
- [CLI Reference](#cli_reference)

## Beta
This packaging and deployment approach is relatively new. We do not yet consider this ready for production deployment of Codenvy. We hope to offer this as the primary production configuration by the end of 2016.

## Team
See [Contributors](../../graphs/contributors) for the complete list of developers that have contributed to this project.

## Architecture
![Architecture](https://cloud.githubusercontent.com/assets/5337267/19623944/f2366c74-989d-11e6-970b-db0ff41f618a.png)

## Issues
Docker is a relatively new project and is actively tested by a thriving community.

Given the nature of the development and release cycle it is important that you have the latest version of docker installed because any issue that you encounter might have already been fixed with a newer docker release.

Install the most recent version of the Docker Engine for your platform using the [official Docker releases](http://docs.docker.com/engine/installation/), including support for Mac and Windows!  If you are on Linux, you can also innstall using:

```bash
wget -qO- https://get.docker.com/ | sh
```

Sometimes Fedora and RHEL/CentOS users will encounter unexpected issues. Try disabling selinux with `setenforce 0` and check if resolves the issue. 

You may also set `CHE_CLI_DEBUG=true` to enable debugging of the CLI, which could help you pin point any configuration issues.

If using the latest docker version and/or disabling selinux does not fix the issue then please file a issue request on the [issues](https://github.com/codenvy/codenvy/issues) page. If you are a licensed customer of Codenvy, you can get prioritized support with support@codenvy.com.

In your issue report please make sure you provide the following information:
- The host distribution and release version
- Output of the `docker version` command
- Output of the `docker info` command
- The `codenvy <command>` you used to run Codenvy

## System Requirements
Codenvy installs on Linux, Mac and Windows. 

#### Software
* Docker 11.1+
* Docker Compose 1.8+. 
* Bash

Docker for Mac and Windows have compose pre-installed. See: [Install Docker Compose on Linux](https://docs.docker.com/compose/install/). The Docker Toolbox for Windows installs [Git Bash for Windows](https://git-for-windows.github.io/), which is needed to run the CLI, a cross-platform set of bash scripts.

#### Hardware
* 2 cores
* 3GB RAM
* 3GB disk space

This will let you install Codenvy and run a single workspace. Codenvy's Docker images consume about 800MB of disk and the Docker images for your workspace templates can each range from 5MB up to 1.5GB. Codenvy and its dependent core containers will consume about 500MB of RAM, and your running workspaces will each require at least 250MB RAM, depending upon user requirements and complexity of the workspace code and intellisense.

#### Docker in a VM
Boot2Docker, docker-machine, Docker for Windows, and Docker for Mac are all variations that launch virtual machines that contain a Docker daemon that allows you to run Docker. We recommend increasing your default VM size to at least 4GB. Each virtualization solution has different requirements around mounting VM folders to your host machine - please enable this for your OS so that Codenvy data is persisted on your host disk.

#### Workspaces
Currently, Codenvy's workspaces launch a tiny rsync-agent that allows the centralized Codenvy server to backup project source code from within each workspace to the central servers. When workspaces are shut off or restarted, the project files are automatically rsync'd back into the workspace. rsync runs at workspace start, stop, and on a scheduler. This allows us to preserve the integrity of your source code if the workspace's runtime containers were to have a failure during operation.

We install rsync into each user's workspace to run as a background service. In this version of Codenvy, your user workspaces requires SSH and rsync to be installed in the base image. If you are connected to the Internet, we install rsync and SSH automtaically. However, if you are doing an offline installation, then your workspace base images need to have this software included.

Some base images, like ubuntu, support this, but others like alpine, do not. If you create custom workspace recipes from Composefiles or Dockerfiles to run within Codenvy, these images must inherit from a base image that has rsync and SSH or you must ensure that these services are installed. If you do not have these services installed, the workspace will not start and provide an error to the user that may cause them to scratch their head.

In the non-container installation version of Codenvy, this requirement does not exist since we install these dependencies onto each host node that is added into the Codenvy cluster. We will be working to package up the rsync agent as a container that is deployed outside of your workspace's runtime. The container will have the dependencies and then this requirement will be removed.

## Installation
Get the Codenvy CLI. The Codenvy images and supporting utilities are downloaded and maintained by the CLI. The CLI also provides utilities for downloading an offline bundle to run Codenvy while disconnected from the network.

### Linux
```
curl -sL https://raw.githubusercontent.com/codenvy/codenvy/hackathon/codenvy.sh > /usr/local/bin/codenvy
curl -sL https://raw.githubusercontent.com/codenvy/codenvy/hackathon/cli.sh > /usr/local/bin/cli.sh
chmod +x /usr/local/bin/codenvy
chmod +x /usr/local/bin/cli.sh
```

### Mac
```
curl -sL https://raw.githubusercontent.com/codenvy/codenvy/hackathon/codenvy.sh > /usr/local/bin/codenvy
curl -sL https://raw.githubusercontent.com/codenvy/codenvy/hackathon/cli.sh > /usr/local/bin/cli.sh
chmod +x /usr/local/bin/codenvy
chmod +x /usr/local/bin/cli.sh
```
The Moby VM that is provided with Docker for Mac is unavailable over the direct IP address that we auto-detect. You can create a loopback alias which will make communications flow from your host into Codenvy and back:
```
# Grabs the IP address of the Xhyve VM
export DOCKER_VM_IP=$(docker run --rm --net host alpine sh -c "ip a show eth0" | \
                    grep 'inet ' | cut -d/ -f1 | awk '{ print $2}')

# Create a loopback alias for the DOCKER_VM_IP
sudo ifconfig lo0 alias $DOCKER_VM_IP

# Add this to your ~/.bash_profile to have it activated in each shell window
```

### Windows
```
curl -sL https://raw.githubusercontent.com/codenvy/codenvy/hackathon/che.sh > che.sh
curl -sL https://raw.githubusercontent.com/codenvy/codenvy/hackathon/che.bat > che.bat
curl -sL https://raw.githubusercontent.com/codenvy/codenvy/hackathon/cli.sh > cli.sh
set PATH=<path-to-cli>;%PATH%
```

You can verify the CLI is working:
```
codenvy help
```
The CLI is self-updating. If you modify the `cli.sh` companion script or change your `CODENVY_VERSION` then an updated CLI will be downloaded. The CLI installs its core subsystems into `~/.codenvy/cli`.

### Proxies
We support installation and operation behind a proxy. You will be operating a clustered system that is managed by Docker, and itself is managing a cluster of workspaces each with their own runtime(s). There are two separate settings:
1. Configuring Docker's daemon for proxy access so that Codenvy can download our images.
2. Configuring Codenvy's system settings to determine how user workspaces will proxy (or not) to the Internet.

Before starting Codenvy or adding nodes for scaling, configure [Docker's daemon for proxy access](https://docs.docker.com/engine/admin/systemd/#/http-proxy). You must set each physical host node that will run Codenvy with proxy access.

Docker proxy configuration works for Docker daemon itself and running containers. However, Java requires proxy environment variables in `JAVA_OPTS`. This concerns both Codenvy server and workspace agents that run in workspace containers. Proxy settings are configured in `/instance/codenvy.env` file:

```
CODENVY_HTTP_PROXY_FOR_CODENVY=
CODENVY_HTTPS_PROXY_FOR_CODENVY=
CODENVY_NO_PROXY_FOR_CODENVY=

HTTP_PROXY_FOR_CODENVY_WORKSPACES=
HTTPS_PROXY_FOR_CODENVY_WORKSPACES=
NO_PROXY_FOR_CODENVY_WORKSPACES=
```

`NO_PROXY` is required is you use a fake DNS name, so that Java and any system utilities do not attempt to go through proxy when resolving such a DNS name. By default, Codenvy is configured to use IP address as `$CODENVY_HOST`, however, you may reconfigure it in `/instance/codenvy.env`.

## Quick Start
`codenvy start`
This installs a Codenvy configuration, downloads Codenvy's Docker images, run pre-flight port checks, boot Codenvy's services, and run post-flight checks. You do not need root access to start Codenvy, unless your environment requires it for Docker operations. You will need write access to the current directory and to `~/.codenvy` where certain CLI and manifest information is stored.

A successful start will display:
```
INFO: (codenvy cli): Downloading cli-latest
INFO: (codenvy cli): Checking registry for version 'nightly' images
INFO: (codenvy config): Generating codenvy configuration...
INFO: (codenvy config): Customizing docker-compose for Windows
INFO: (codenvy start): Preflight checks
         port 80:  [OK]
         port 443: [OK]

INFO: (codenvy start): Starting containers...
INFO: (codenvy start): Server logs at "docker logs -f codenvy_codenvy_1"
INFO: (codenvy start): Server booting...
INFO: (codenvy start): Booted and reachable
INFO: (codenvy start): Ver: 5.0.0-M6-SNAPSHOT
INFO: (codenvy start): Use: http://10.0.75.2
INFO: (codenvy start): API: http://10.0.75.2/swagger
```
The administrative login is:
user: `admin`
pass: `password`

### Hosting
We use an internal utility, `codenvy/che-ip`, to determine the default value for `CODENVY_HOST`, which is your server's IP address. This works well on desktops, but usually fails on hosted servers. If you are hosting Codenvy at a cloud service like DigitalOcean, set `CODENVY_HOST` to the server's IP address or its DNS.

## Uninstall
```
# Remove your Codevy configuration and destroy user projects and database
codenvy destroy

# Deletes Codenvy's images from your Docker registry
codenvy rmi

# Removes CLI logs, configuration and manifest data
rm -rf ~/.codenvy
```

## Offline Installation
We support the ability to install and run Codenvy while disconnected from the Internet. This is helpful for certain restricted environments, regulated datacenters, or offshore installations. 

1. Get Docker Images
While connected to the Internet and with access to DockerHub, download Codenvy's Docker images as a set of files with `codenvy offline`. Codenvy will download all dependent images and save them to `offline/*.tar` with each image saved as its own file. `CODENVY_VERSION` environment variable is used to determine which images to download unless you already have a Codenvy installation, then the value of `instance/codenvy.ver` will be used. There is about 1GB of data that will be saved.

2. Get Codenvy CLI
Save `codenvy.sh`, `cli.sh`, and if on Windows, `codenvy.bat`.

3. Get Codenvy Stacks
Out of the box, Codenvy has configured a few dozen stacks for popular programming languages and frameworks. These stacks use "recipes" which contain links to Docker images that are needed to create workspaces from these stacks. These workspace runtime images are not saved as part of `codenvy offline`. There are many of these images and they consume a lot of disk space. Most users do not require all of these stacks and most replace default stacks with custom stacks using their own Docker images. If you'd like to get the images that are associated with Codenvy's stacks:
```
docker save <codenvy-stack-image-name> > offline/<base-image-name>.tar
```
The list of images that Codenvy manages is sourced from Eclipse Che's [Dockerfiles repository](https://github.com/eclipse/che-dockerfiles/tree/master/recipes). Each folder is named the same way that our images are stored.  The `alpine_jdk8` folder represents the `codenvy/alpine_jdk8` Docker image, which you would save with `docker save codenvy/alpine_jdk8 > offline/alpine_jdk8.tar`.

4. Start Offline
Extract your files to an offline computer with Docker already configured. Install the CLI files to a directory on your path and ensure that they have execution permissions. Execute the CLI in the directory that has the `offline` sub-folder which contains your tar files. Then start Codenvy in `--offline` mode:
```
codenvy start --offline
```
When invoked with the `offline` parameter, the Codenvy CLI performs a preboot sequence, which loads all saved `offline/*.tar` images including any Codenvy stack images you saved. The preboot sequence takes place before any CLI configuration, which itself depends upon Docker. The `codenvy start`, `codenvy download`, and `codenvy init` commands support `--offline` mode which triggers this preboot seequence.

## Configuration
All configuration is done with environment variables. Environment variables are stored in `/instance/codenvy.env`, a file that is generated during the `codenvy init` phase.

When Codenvy initializes itself, it creates a `/config` folder and populates it with puppet configuration templatees specific to the version of Codenvy that you are planning to run. While similar, this folder is different from `/instance/config`, which has instance-specific configuration for a Codenvy installation. You should not need to modify the contents of `/config`.

You can run `codenvy init` to install a new configuration into an empty directory. This command uses the `codenvy/init:<version>` Docker container to deliver a version-specific set of puppet templates into the folder.

If you run `codenvy config`, Codenvy runs puppet to transform your puppet templates into a Codenvy instance configuration, placing the results into `/instance`. Each time you start Codenvy, we automatically rerun `codenvy config`. It's ok and expected to regenerate configurations - it's the nature of microservices.

### Available Configuration Parameters
| Parameter | Description |
|-----------|-------------|
| `DEBUG` | Set this to `true` to enable entrypoint debugging. |

## Logs and User Data
When Codenvy initializes itself, it creates a `/instance` folder in the directory to store logs, user data, the database, and instance-specific configuration. Codenvy's containers are started with `host:container` volume bindings to mount this information into and out of the containers that require it. You can save the `/instance` folder as a backup for an entire Codenvy instance. 

Codenvy's containers save their logs in the same location:
```
/logs/codenvy/2016                 // Server logs
/logs/codenvy/che-machine-logs     // Workspace logs
/logs/nginx                        // nginx access and error logs
/logs/haproxy                      // HAproxy logs
```

User data is stored in:
```
/data/codenvy                      // Project backups (we synchronize projs from remote ws here)
/data/postgres                     // Postgres data folder (users, workspaces, stacks etc)
/data/registry                     // Workspace snapshots
```

Instance configuration is generated by Codenvy and is updated by our internal configuration utilities. These 'generated' configuration files should not be modified and stored in:
```
/codenvy.var                       // Version of Codenvy installed
/docker-compose.yml                // Docker compose to launch internal services
/config                            // Configuration files which are input mounted into the containers
```

### Microsoft Windows and NTFS
Due to differences in file system types between NTFS and what is commonly used in the Linux world, there is no convenient way to directly host mount Postgres database data from within the container onto your host. We store your database data in a Docker named volume inside your boot2docker or Docker for Windows VM. Your data is persisted permanently. If the underlying VM is destroyed, then the data will be lost.

If you need to backup your Postgres data, run the following command:
`TODO - postgres backup commands`

## SMTP Configuration

By default, Codenvy is configured to use a dummy mail server which makes registration with user email not possible, although admin can still create users or configure oAuth. To configure Codenvy to use SMTP server of choice, provide values for the following environment variables in `codenvy.env` (below is an example for GMAIL):

```
CODENVY_MAIL_HOST=smtp.gmail.com
CODENVY_MAIL_HOST_PORT=465
CODENVY_MAIL_SMTP_AUTH=true
СODENVY_MAIL_TRANSPORT_PROTOCOL=smtp
CODENVY_MAIL_SMTP_AUTH_USERNAME=example@gmail.com
CODENVY_MAIL_SMTP_AUTH_PASSWORD=password
CODENVY_MAIL_SMTP_SOCKETFACTORY_PORT=465
CODENVY_MAIL_SMTP_SOCKETFACTORY_CLASS=javax.net.ssl.SSLSocketFactory
CODENVY_MAIL_SMTP_SOCKETFACTORY_FALLBACK=false
```

## oAuth

Codenvy is shipped with a preconfigured GitHub oAuth application that works for `codenvy.onprem` hostname. To enable GitHub oAuth, add `CODENVY_HOST=codenvy.onprem` to environment file and restart Codenvy.

If you have a custom DNS name, you need to register a GitHub oAuth application with `http://<your_hostname>/api/oauth/callback` as a callback URL, provide Client ID and Secret in `codenvy.env` and restart Codenvy:

```
CODENVY_GITHUB_CLIENT_ID=yourID
CODENVY_GITHUB_SECRET=yourSecret
```

Google oAuth is configured in the exact same way:

```
CODENVY_GOOGLE_CLIENT_ID=yourID
CODENVY_GOOGLE_SECRET=yourSecret
```

## Development Mode
For Codenvy developers that are building and customizing Codenvy from its source repository, there is a development that maps the runtime containers to your source repository. If you are developing in the `http://github.com/codenvy/codenvy` repository, you can turn on development mode to allow puppet configuration files and your local Codenvy assembly to be mounted into the appropriate containers. Dev mode is activated by setting environment variables and restarting (if Codenvy is running) or starting Codenvy (if this is the first run):
```
CODENVY_DEVELOPMENT_MODE="on"
CODENVY_DEVELOPMENT_REPO=<path-codenvy-repo>
```
You must run Codenvy from the root of the Codenvy repository. By running in the repository, the local `codenvy.sh` and `cli.sh` scripts will override any installed CLI packages. Additionally, two containers will have host mounted files from the local repository. During the `codenvy config` phase, the repository's `/modules` and `/manifests` will be mounted into the puppet configurator.  During the `codenvy start` phase, a local assembly from `assembly/onpremises-ide-packaging-tomcat-codenvy-allinone/target/onpremises-ide-packaging-tomcat-codenvy-allinone` is mounted into the `codenvy/codenvy` runtime container.

## CLI Reference
```
Usage: codenvy [COMMAND] [OPTIONS]
    help                                 This help message
    version                              Installed version and upgrade paths
    init [--pull|--force|--offline]      Initializes a directory with a codenvy configuration 
    start [--pull|--force|--offline]     Starts codenvy services
    stop                                 Stops codenvy services
    restart [--force]                    Restart codenvy services
    destroy                              Stops services, and deletes codenvy instance data
    rmi [--force]                        Removes the Docker images for CODENVY_VERSION, forcing a repull
    config                               Generates a codenvy config from vars; run on any start / restart
    upgrade                              Upgrades Codenvy from one version to another with data migrations and bakcups
    download [--pull|--force|--offline]  Pulls Docker images CODENVY_VERSION, or installed, codenvy.ver
    backup                               Backups codenvy configuration and data to CODENVY_BACKUP_FOLDER
    restore                              Restores codenvy configuration and data from CODENVY_BACKUP_FOLDER
    offline                              Saves codenvy Docker images into TAR files for offline install
    info [ --all                         Run all debugging tests
           --debug                       Displays system information
           --network ]                   Test connectivity between ${CHE_MINI_PRODUCT_NAME} sub-systems
```

### `codenvy init`
Initializes an empty directory with a Codenvy configuration and instance folder where user data and runtime configuration will be stored. This command creates two folders, `config` and `instance`. The `config` folder contains the `codenvy.ver` file which you can use to configure how the product is run. Other files in this folder are used by Codenvy's configuration system to structure the runtime microservices. 

These variables can be set in your local environment shell before running and they will be respected during initialization and inserted as defaults into `config/codenvy.ver`:

| Variable | Description |
|----------|-------------|
| `CODENVY_VERSION` | The version of Codenvy to install. You can get a list available with `codenvy version`. We always have `nightly` and `latest` available. |
| `CODENVY_HOST` | The IP address or DNS name of the Codenvy service. We use `codenvy/che-ip` to attempt discovery if not set. |
| `CODENVY_CONFIG` | The folder where a Codenvy config will be placed. The default is `$pwd/config/`. |
| `CODENVY_INSTANCE` | The folder where your Codenvy instance and user data will be placed. The default is `$pwd/instance`. |
| `CODENVY_DEVELOPMENT_MODE` | If `on`, then will mount `CODENVY_DEVELOPMENT_REPO`, overriding the files in Codenvy config and containers. |
| `CODENVY_DEVELOPMENT_REPO` | The location of the `http://github.com/codenvy/codenvy` local clone. |

Codenvy depends upon Docker images. We use Docker images in three ways:
1. As cross-platform utilites within the CLI. For example, in scenarios where we need to perform a `curl` operation, we use a small Docker image to perform this function. We do this as a precaution as many operating systems (like Windows) do not have curl installed.
2. To look up the master version and upgrade manifest, which is stored as a singleton Docker image called `codenvy/version`. 
3. To perform initialization and configuration of Codenvy such as with `codenvy/init`. This image contains templates that are delivered as a payload and installed onto your computer. These payload images can have different files based upon the image's version.
4. To run Codenvy and its dependent services, which include Codenvy, HAproxy, nginx, Postgres, socat, and Docker Swarm.

You can control the nature of how Codenvy downloads these images with command line options. All image downloads are performed with `docker pull`. 

| Mode>>>> | Description |
|------|-------------|
| `--no-force` | Default behavior. Will download an image if not found locally. A local check of the image will see if an image of a matching name is in your local registry and then skip the pull if it is found. This mode does not check DockerHub for a newer version of the same image. |
| `--pull` | Will always perform a `docker pull` when an image is requested. If there is a newer version of the same tagged image at DockerHub, it will pull it, or use the one in local cache. This keeps your images up to date, but execution is slower. |
| `--force` | Performs a forced removal of the local image using `docker rmi` and then pulls it again (anew) from DockerHub. You can use this as a way to clean your local cache and ensure that all images are new. |
| `--ofline` | Loads Docker images from `offline/*.tar` folder during a pre-boot mode of the CLI. Used if you are performing an installation or start while disconnected from the Internet. |

`codenvy config` 
