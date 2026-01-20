FROM eclipse-temurin:21-jdk
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]


##Gemini sugeriu esse:
##FROM ubuntu:latest
##LABEL authors="luizr"

# 1. Atualiza o sistema e instala o JDK e Maven
##RUN apt-get update && apt-get install -y \
##    openjdk-17-jdk \
##    maven \
##    && rm -rf /var/lib/apt/lists/*

# 2. Define o diretório de trabalho
##WORKDIR /app

# 3. Copia os arquivos do seu projeto para dentro da imagem
##COPY . .

# 4. Remove o ENTRYPOINT antigo e define o comando padrão
# Agora o Docker aceitará o "command" do docker-compose ou rodará este por padrão
##CMD ["mvn", "spring-boot:run"]

