Deploy
======

```
  ;; Clone the repo
  $ git clone https://github.com/HeinrichHartmann/LiveGovWP1.git
  
  ;; Checkout the right branch
  $ git checkout -b nodeInspection
  
  ;; Install dependencies
  $ cd server/NodeInspectionFrontEnd && npm install

  ;; Edit the config.js to use your database information.
  $ $EDITOR config.js

  ;; Run the server
  $ node app.js

  ;; Or use pm2
  $ pm2 stopAll
  $ pm2 start app.js -i max

```
