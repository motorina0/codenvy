# Codenvy Installation and Operation
- [Introduction](#Introduction)
- [Team](#Team)
- [Issues](#Issues)
- [Architecture](#Architecture)
- [Installation Types](#Installation_types)
- [System Requirements](#system-requirements)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Logs and User Data](#logs-and-user-data)
- [Configuration](#configuration)
- [Updates](#Updates)
- [Scaling](#Scaling)
- [Backup and Recovery](#Backups)
- [Migration](#migration)
- [Monitoring](#Monitoring)
- [CLI Reference](#cli_reference)

## Introduction
Packaging to configure and run [Codenvy](https://codenvy.com) as a clustered set of Docker containers using Docker compose. 

## Beta
This packaging and deployment approach is relatively new. We do not yet consider this ready for production deployment of Codenvy. We hope to offer this as the primary production configuration by the end of 2016.

## Team
See [Contributors](../../graphs/contributors) for the complete list of developers that have contributed to this project.

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

## Quick Start

### Codenvy as a Remote Server

When running Codenvy on a remote server, export CODENVY_HOST environment variable before running startup script:

`export CODENVY_HOST=$IP`

where `$IP` is an externally accessible IP or a hostname that can be resolved from client machines.

## Starting Codenvy

`codenvy start`

This will install a Codenvy configuration, download Codenvy's Docker images, run pre-flight port checks, boot Codenvy's services, and run post-flight checks. A successful start should result with:

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
INFO: (codenvy start): Use: http://10.0.75.2
INFO: (codenvy start): API: http://10.0.75.2/swagger
```
The default administrative login is:
user: `admin`
pass: `password`

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

## Configuration
All configuration of Codenvy is done through environment variables. You can set environment variables in your terminal or by modifying `/instance/codenvy.env` after it is generated from a `codenvy init`. 

When Codenvy initializes itself, it creates a `/config` folder and populates it with puppet configuration templatees specific to the version of Codenvy that you are planning to run. While named similarly, this folder is different from `/instance/config`, which has instance-specific configuration for a Codenvy installation. You should not need to modify the contents of `/config`.

You can run `codenvy init` to install a new configuration into an empty directory. This command uses the `codenvy/init:<version>` Docker container to deliver a version-specific set of puppet templates into the folder.

If you run `codenvy config`, Codenvy runs puppet to transform your puppet templates into a Codenvy instance configuration, placing the results into `/instance`. Each time you start Codenvy, we automatically rerun `codenvy config`. It's ok and expected to regenerate configurations - it's the nature of microservices.

### Available Configuration Parameters
| Parameter | Description |
|-----------|-------------|
| `DEBUG` | Set this to `true` to enable entrypoint debugging. |
