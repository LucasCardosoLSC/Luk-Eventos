#!/bin/bash

# Gerar hashes usando BCrypt
ADMIN_HASH=$(echo -n "admin123" | bcrypt)
DEBUG_HASH=$(echo -n "debug123" | bcrypt)
TEST_HASH=$(echo -n "123456" | bcrypt)

# Configurar variáveis de ambiente
export ADMIN_PASSWORD_HASH="$ADMIN_HASH"
export DEBUG_PASSWORD_HASH="$DEBUG_HASH"
export TEST_PASSWORD_HASH="$TEST_HASH"

# Executar migração
mvn flyway:clean flyway:migrate

# Limpar variáveis de ambiente
unset ADMIN_PASSWORD_HASH
unset DEBUG_PASSWORD_HASH
unset TEST_PASSWORD_HASH 