#!/usr/bin/env bash
set -euo pipefail

required=(
  DATABASE_URL
  DATABASE_USER
  DATABASE_PASSWORD
  JWT_SECRET
  JWT_ISSUER
  JWT_AUDIENCE
)

optional=(
  DATABASE_MAX_POOL_SIZE
  JWT_EXPIRES_IN_HOURS
  OPEN_FOOD_FACTS_URL
  OPEN_FOOD_FACTS_USER_AGENT
  OPEN_FOOD_FACTS_TIMEOUT_MS
  PORT
)

missing=0

printf "BarcodeBite backend production preflight\n"
printf "======================================\n"

for key in "${required[@]}"; do
  if [[ -z "${!key:-}" ]]; then
    printf "[MISSING] %s\n" "$key"
    missing=1
  else
    printf "[OK] %s\n" "$key"
  fi
done

for key in "${optional[@]}"; do
  if [[ -z "${!key:-}" ]]; then
    printf "[WARN] %s is not set (will use defaults)\n" "$key"
  else
    printf "[OK] %s\n" "$key"
  fi
done

if [[ -n "${JWT_SECRET:-}" && ${#JWT_SECRET} -lt 32 ]]; then
  printf "[MISSING] JWT_SECRET must be at least 32 chars\n"
  missing=1
fi

if [[ "$missing" -ne 0 ]]; then
  printf "\nPreflight FAILED. Set missing variables before deploy.\n"
  exit 1
fi

printf "\nPreflight PASSED. Environment is deploy-ready.\n"
