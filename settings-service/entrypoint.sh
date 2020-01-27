#!/bin/sh
set -euo pipefail

./prod-env-updater.sh

exec "$@"
