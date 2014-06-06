BASEDIR = .

ifeq (${NODE_ENV},production)
PORT=3001
ENVIRONMENT = --production
else
PORT=4001
endif

IGNOREFILE = $(BASEDIR)/.gitignore
REMOVEFILES = `cat $(IGNOREFILE)` *bz2

NAME = livegovwp1-nodeangularfrontend-dev
TARBALL = $(NAME)-`date '+%Y%m%d'`.tar.bz2

REMOTE_HOST = rene@141.26.69.238
REMOTE_PATH = local/src/$(NAME)

DEPLOY_CMD = "cd $(REMOTE_PATH); make; gulp sass"
FOREVER_CMD = "PORT=$(PORT) forever start -w -a -o out.log -e err.log server.js"

install: npm bower

bower:
	@echo installing bower components
	@bower install

npm:
	@echo installing node modules
	@npm install $(ENVIRONMENT)

clean:
	@rm -rf $(REMOVEFILES)

tarball:
	@tar cjpf $(TARBALL) --exclude-from=$(IGNOREFILE) --exclude=$(TARBALL) $(BASEDIR)

forever:
	@echo starting server on port $(PORT)
	@$(FOREVER_CMD)

deploy:
	@echo deploying to $(REMOTE_HOST)
	@rsync -av $(BASEDIR) $(REMOTE_HOST):$(REMOTE_PATH) --exclude-from=$(IGNOREFILE)
	@ssh $(REMOTE_HOST) $(DEPLOY_CMD)
