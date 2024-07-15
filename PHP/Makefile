setup:
	docker compose run --rm php-cli composer install --prefer-dist --no-suggest

run:
	docker compose run --rm php-cli php parser.php

test:
	docker compose run --rm -e XDEBUG_MODE=coverage php-cli ./vendor/bin/phpunit --coverage-text

phpstan:
	docker compose run --rm php-cli ./vendor/bin/phpstan --xdebug analyse

clean:
	docker compose down
