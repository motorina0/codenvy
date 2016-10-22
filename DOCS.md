1. [Architecture](#Architecture)
2. [Installation Types](#Installation_types)
3. [System Requirements](#S3-system-requirements)
4. [New Installation](#4-new_installation)
5. [Logs and User Data](#5-logs-and-user-data)
6. [Configuration](#New_Installation)
7. [Updates](#Updates)
8. [Scaling](#Scaling)
9. [Data Backup and Recovery](#Backups)
10. [Migration](#migration)
10. [Monitoring](#Monitoring)
11. [CLI Command Reference](#cli_reference)



## 3. System Requirements

Codenvy installs on Linux, Mac and Windows. 

Software:

* Docker 11.1+
* Docker Compose 1.8+. 

Docker for Mac and Windows have Compose pre-installed. See: [Install Docker Compose on Linux](https://docs.docker.com/compose/install/).

System:

* 2 cores
* 3GB RAM
* 3GB disc space

These are minimum requirements to install Codenvy and run one workspace. The overall size of Codenvy Docker images is ~800MB. You need at least 1GB for a running workspace as well. 

!Linux and Mac users!

It is recommended to increase VM RAM from the default 2GB to 3-4GB.

Disc sharing must be enabled to persist data onto the host filesystem. 


## 4. New_Installation

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

## 5. Logs and User Data

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