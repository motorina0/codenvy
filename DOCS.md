# Codenvy Installation and Operation
- [Introduction](#Introduction)
- [Team](#Team)
- [Issues](#Issues)
- [Architecture](#architecture)
- [System Requirements](#system-requirements)
- [Installation](#installation)
  - [Hosting]()
- [Quick Start](#quick-start)
- [Offline Installation]()
- [Configuration](#configuration)
- [Logs and User Data](#logs-and-user-data)
- [Updates](#Updates)
- [Scaling](#Scaling)
- [Backup and Recovery](#Backups)
- [Development Mode](#development-mode)
- [Migration](#migration)
- [Monitoring](#Monitoring)
- [CLI Reference](#cli_reference)

## Introduction
Packaging to configure and run [Codenvy](https://codenvy.com) as a clustered set of Docker containers using Docker compose. 

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

Software:
* Docker 11.1+
* Docker Compose 1.8+. 
* Bash

Docker for Mac and Windows have compose pre-installed. See: [Install Docker Compose on Linux](https://docs.docker.com/compose/install/). The Docker Toolbox for Windows installs [Git Bash for Windows](https://git-for-windows.github.io/), which is needed to run the CLI, a cross-platform set of bash scripts.

System:
* 2 cores
* 3GB RAM
* 3GB disk space

This will let you install Codenvy and run a single workspace. Codenvy's Docker images consume about 800MB of disk and the Docker images for your workspace templates can each range from 5MB up to 1.5GB. Codenvy and its dependent core containers will consume about 500MB of RAM, and your running workspaces will each require at least 250MB RAM, depending upon user requirements and complexity of the workspace code and intellisense.

Docker in a VM:
Boot2Docker, docker-machine, Docker for Windows, and Docker for Mac are all variations that launch virtual machines that contain a Docker daemon that allows you to run Docker. We recommend increasing your default VM size to at least 4GB. Each virtualization solution has different requirements around mounting VM folders to your host machine - please enable this for your OS so that Codenvy data is persisted on your host disk.

Workspace Requirements:
Currently, Codenvy's workspaces launch a tiny rsync-agent that allows the centralized Codenvy server to backup project source code from within each workspace to the central servers. When workspaces are shut off or restarted, the project files are automatically rsync'd back into the workspace. rsync runs at workspace start, stop, and on a scheduler. This allows us to preserve the integrity of your source code if the workspace's runtime containers were to have a failure during operation.

We install rsync into each user's workspace to run as a background service. In this version of Codenvy, your user workspaces requires SSH and rsync to be installed in the base image. Some base images, like ubuntu, support this, but others like alpine, do not. If you create custom workspace recipes from Composefiles or Dockerfiles to run within Codenvy, these images must inherit from a base image that has rsync and SSH or you must ensure that these services are installed. If you do not have these services installed, the workspace will not start and provide an error to the user that may cause them to scratch their head.

In the non-container installation version of Codenvy, this requirement does not exist since we install these dependencies onto each host node that is added into the Codenvy cluster. We will be working to package up the rsync agent as a container that is deployed outside of your workspace's runtime. The container will have the dependencies and then this requirement will be removed.

## Installation
Get the Codenvy CLI. The Codenvy images and supporting utilities are downloaded and maintained by the CLI. The CLI also provides utilities for downloading an offline bundle to run Codenvy while disconnected from the network.

### LINUX
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

### WINDOWS
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
We support installation and operation behind a proxy. You will be operating a clustered system that is managed by Docker, and itself is managing a cluster of workspaces each with their own runtime(s). This requires two level of settings for proxy operation:
1. Configuring Docker's daemon for proxy access so that Codenvy can download our images.
2. Configuring Codenvy's system settings to determine how user workspaces will proxy (or not) to the Internet.

## Quick Start
`codenvy start`
This installs a Codenvy configuration, downloads Codenvy's Docker images, run pre-flight port checks, boot Codenvy's services, and run post-flight checks. A successful start will display:
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

## Offline Installation
We support the ability to install and run Codenvy while disconnected from the Internet. This is helpful for certain restricted environments, regulated datacenters, or offshore installations. 

1. Get Docker Images
While connected to the Internet and with access to DockerHub, download Codenvy's Docker images 

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

## Development Mode
If you are developing in the `http://github.com/codenvy/codenvy` repository, you can turn on development mode to allow puppet configuration files and your local Codenvy assembly to be mounted into the appropriate containers. Dev mode is activated by setting environment variables and restarting (if Codenvy is running) or starting Codenvy (if this is the first run):
```
CODENVY_DEVELOPMENT_MODE="on"
CODENVY_DEVELOPMENT_REPO=<path-codenvy-repo>
```
You must run Codenvy from the root of the Codenvy repository. By running in the repository, the local `codenvy.sh` and `cli.sh` scripts will override any installed CLI packages. Additionally, two containers will have host mounted files from the local repository. During the `codenvy config` phase, the repository's `/modules` and `/manifests` will be mounted into the puppet configurator.  During the `codenvy start` phase, a local assembly from `assembly/onpremises-ide-packaging-tomcat-codenvy-allinone/target/onpremises-ide-packaging-tomcat-codenvy-allinone` is mounted into the `codenvy/codenvy` runtime container.
