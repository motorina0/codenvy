- [Architecture](#Architecture)
- [Installation Types](#Installation_types)
- [System Requirements](#S3-system-requirements)
- [New Installation](#4-new_installation)
- [Logs and User Data](#5-logs-and-user-data)
- [Configuration](#New_Installation)
- [Updates](#Updates)
- [Scaling](#Scaling)
- [Data Backup and Recovery](#Backups)
- [Migration](#migration)
- [Monitoring](#Monitoring)
- [CLI Reference](#cli_reference)

## Introduction
Packaging to configure and run [Codenvy](https://codenvy.com) as a clustered set of Docker containers using Docker compose. 

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

This will let you install Codenvy and run a single workspace. Codenvy's Docker images consume about 800MB of disk and the Docker images for your workspace templates can each range from 5MB up to 1.5GB. 

Docker in a VM:
Boot2Docker, docker-machine, Docker for Windows, and Docker for Mac are all variations that launch virtual machines that contain a Docker daemon that allows you to run Docker. We recommend increasing your default VM size to at least 4GB. Each virtualization solution has different requirements around mounting VM folders to your host machine - please enable this for your OS so that Codenvy data is persisted on your host disk.

## New Installation

### Install Codenvy CLI

Create an empty directory and download Codenvy installer script:

wget https://github.com/codenvy/codenvy/

### Install Codenvy

Mac Users only!

Docker VM is unavailable when reached by a direct IP, thus an alias should be created:

```
# Grabs the IP address of the Xhyve VM
export DOCKER_VM_IP=$(docker run --rm --net host alpine sh -c "ip a show eth0" | \
                    grep 'inet ' | cut -d/ -f1 | awk '{ print $2}')

# Sets the loopback alias for the DOCKER IP
# You will be asked for your root password
sudo ifconfig lo0 alias $DOCKER_VM_IP
```

Boot Codenvy:

`codenvy start`

This command will initialize generation of Puppet configuration files, download all required Docker images, run pre-flight checks, boot Codenvy, and run post-flish checks. If all containers and underlying service have started correctly, the CLI will print out information on where Codenvy is available. It's VM IP by default.

## Logs and User Data

All Codenvy containers are started with host:container volume bindings to provide an easy access to logs and data. By default, Codenvy creates `/instance` folder in a directory where the start script has been run. This directory has 2 subdirectories - logs and data:

```
/logs/codenvy/2016                 // server logs
/logs/codenvy/che-machine-logs     // workspace agent logs
/logs/nginx/                       // nginx access and error logs
/logs/haproxy                      // haproxy logs
```

User data is stored in:

```
/data/codenvy                      // project backups
/data/postgres                     // Postgres data folder (users, workspaces, stacks etc)
/data/registry                     // workspace snapshots
```

!Windows Users!

Due to differences in files system types between Windows host and Docker VM, Posgres data is stored in a named volume inside the VM and it not persisted into the host. Data is lost only after the VM is destroyed.

If you need to backup your Postgres data, run the following command:

`TODO - postgres recovery commands`
