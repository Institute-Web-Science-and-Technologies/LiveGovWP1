all: npm bower

bower:
	@echo installing bower components
	@bower install

npm:
	@echo installing node packages
	@npm install --${NODE_ENV}

clean:
	@echo cleaning up
	@rm -rf bower_components node_modules .sass-cache public/css/*.css public/css/*.map *bz2

# TODO create proper file list

tarball: clean
	@echo compressing to livegovwp1-nodeangularfrontend-`date '+%Y%m%d'`.tar.bz2
	@tar cjpf livegovwp1-nodeangularfrontend-`date '+%Y%m%d'`.tar.bz2 .. 2>/dev/null

# TODO create a rule for deployment via git (push to github -> pull on lg)

deploy:
	rsync -av --exclude-from=.rsync-exclude ../ rene@lg:local/src/livegovwp1-nodeangularfrontend-dev
	ssh rene@lg 'cd local/src/livegovwp1-nodeangularfrontend-dev/app; make; gulp sass'

