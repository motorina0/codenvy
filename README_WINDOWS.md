## 1. Clone the repo, checkout to a branch, run dos2unix  

```
git clone https://github.com/codenvy/deployment
git checkout puppet_new
cd deployment
```
## 2. Convert all the files to have Linux ending style

```
find . -type f -exec dos2unix {} \;
```
## 3. Modify configs to specify location of all Codenvy and Puppet stuff


In `/deployment/manifests/codenvy.pp`:

`$codenvy_folder = "/C/Users/UserName/onprem"`

This is where all the puppet configs and compose file will be generated and the mounted into containers. Use your own path here

This location will be then used in all paths in compose file.

## 4. Auto detect VM IP:

`docker run --net host --rm codenvy/che-ip`

Copy VM IP that will be used in puppet conf generation.

## 5. Generate Puppet configuration

`docker run -e "VM_IP=${IP}" -v /c/Users/Codenvy/onprem:/opt/codenvy:rw -v /c/Users/Codenvy/deployment/manifests:/etc/puppet/manifests:ro -v /c/Users/Codenvy/deployment/modules:/etc/puppet/modules:ro -ti puppet/puppet-agent-alpine apply --modulepath /etc/puppet/modules/ /etc/puppet/manifests/codenvy.pp`

where `${IP}` is the IP you have got at step 4 (puppet will grab this env and insert it into generated configuration), `/c/Users/Codenvy/onprem` is the location you have specified in codenvy.pp and `/c/Users/Codenvy/deployment/` is the actual location of pupper manifests and modules

## 6. Edit compose.yml to fix path format for env_file

In generated `/c/Users/Codenvy/onprem/docker-compose.yml` replace paths for env_files to have the following format:

`C:\Users\Codenvy\onprem\config\registry\registry.env`

`env_files` are used for the following containers:

`registry`
`potgres`
`codenvy`

## 7. Boot Codenvy

Navigate to `$codenvy_folder` with the generated composefile and run docker compose:

`docker-compose -p=codenvy up -d`

## 8. Add a local hosts rule

In your local Windows `/etc/hosts` add:
`192.168.x.x codenvy.onprem`

where 192.168.x.x is the IP you used in extra_hosts on step 3. You need Admin provileges to edit that file.

## 9. In your browser go to:

`http://codenvy.onprem`

Login with the following credentials:

username: `admin`
password: `password`
