## 1. Clone the repo, checkout to a branch

```
git clone https://github.com/codenvy/deployment
git checkout puppet_new
cd deployment

```

## 2. Generate Puppet configuration and start Codenvy

When in `/deployment` run

`./run.sh`

## 3. Add a local hosts rule

In your local Mac `/etc/hosts` add:
`192.168.x.x codenvy.onprem`

where 192.168.x.x is your local IP

## 4. In your browser go to:

`http://codenvy.onprem`

Login with the following credentials:

username: `admin`
password: `password`
