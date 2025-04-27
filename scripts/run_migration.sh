#!/bin/bash

# Compilar e executar o gerador de hashes
mvn compile exec:java -Dexec.mainClass=com.lukeventos.utils.PasswordHashGenerator

# Carregar hashes do arquivo de propriedades
source password_hashes.properties

# Executar migração
mvn flyway:clean flyway:migrate 