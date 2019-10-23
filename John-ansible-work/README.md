# GDPR - JPD Applications Automated Production Deployment

This project contains scripts for deploying JPD application on their production servers via Jenkins envionment (bog.saunalahti.fi)

### Prerequisites

* Access to bog.saunalahti.fi

### On Production and development environment

Production deployments are done from Jenkins `https://bog.saunalahti.fi` using `torni` API.

See: https://atlas.elisa.fi/confluence/pages/viewpage.action?spaceKey=SW&title=GDPR+-+JPD+Applications+Automated+Production+Deployment

* code-checkout.yml
    * Checkout required tag of repositories git@github.devcloud.elisa.fi:jpd/devel.git
* code-build.yml
    * Compile and build the code and generate app folder under the applications build folder
* artifact-generate.yml
    * Generate the artifact (war/tar)    
* artifact-publish.yml
    * Publish the artifact (war/tar) into the artifactory (https://artifactory.saunalahti.fi/cno-release-local) 
* artifact-prop-update.yml
    * Pull the artifact (war/tar) into the artifactory (https://artifactory.saunalahti.fi/cno-release-local), extract it, update the svn properties and rebundle it.
* artifact-deploy.yml
    * Deploy the artifact on the production server
* artifact-install.yml
    * Execute Pre-Install, Create Links and Post-Install steps on production server as specified in the install-spec properties.
* server-start.yml
    * Start the application production server
* server-stop.yml
    * Stop application production server.
    
    
    
    
