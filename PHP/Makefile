setup:
	docker-compose run --rm composer install --ignore-platform-reqs --prefer-dist --no-suggest

run:
	docker-compose run --rm php-ci php parser.php

test:
	docker-compose run --rm php-ci ./vendor/bin/phpunit

clean:
	docker-compose down

bench-no-jit:
	docker-compose run --rm php-ci php -dopcache.enable=1 -dopcache.enable_cli=1 -dopcache.jit=0 -dopcache.jit_buffer_size=0 benchmark/bench.php

bench-jit:
	docker-compose run --rm php-ci php -dopcache.enable=1 -dopcache.enable_cli=1 -dopcache.jit=1 -dopcache.jit_buffer_size=100M benchmark/bench.php
