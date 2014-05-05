#FIXME use smth like NODE_ENV

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

tarball: clean
	@echo compressing to livegovwp1-nodeangularfrontend-`date '+%Y%m%d'`.tar.bz2
	@tar cjpf livegovwp1-nodeangularfrontend-`date '+%Y%m%d'`.tar.bz2 .. 2>/dev/null

deploy: clean
	rsync -av ../ rene@lg:local/src/livegovwp1-nodeangularfrontend-dev
	ssh rene@lg 'cd local/src/livegovwp1-nodeangularfrontend-dev/app; make; gulp sass'

