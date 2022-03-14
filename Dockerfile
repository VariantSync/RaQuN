# syntax=docker/dockerfile:1
FROM alpine:3.15

# Prepare the environment
RUN apk add maven

# Build the jar files
WORKDIR /home/user
COPY local-maven-repo ./local-maven-repo
COPY src ./src
COPY pom.xml .
RUN mvn package || exit


FROM alpine:3.15

# Create a user
RUN adduser --disabled-password  --home /home/user --gecos '' user

RUN apk add --no-cache --upgrade bash
RUN apk add --update openjdk17 unzip
RUN apk add --no-cache msttcorefonts-installer fontconfig
RUN update-ms-fonts
RUN apk add --no-cache tesseract-ocr python3 py3-pip py3-numpy && \
    pip3 install --upgrade pip setuptools wheel && \
    apk add --no-cache --virtual .build-deps gcc g++ zlib-dev make python3-dev py3-numpy-dev jpeg-dev && \
    pip3 install matplotlib && \
    apk del .build-deps
    
# Copy all relevant files
WORKDIR /home/user
RUN mkdir -p ./experimental_subjects/argouml
COPY --from=0 /home/user/target ./target
COPY experimental_subjects/* ./experimental_subjects/
COPY experimental_subjects/argouml/* ./experimental_subjects/argouml/
COPY result_analysis_python ./result_analysis_python

# Unpack the experimental subjects
WORKDIR experimental_subjects
RUN unzip -o ./\*.zip
RUN mv argouml_p*.csv argouml/

# Copy the docker resources
WORKDIR /home/user
COPY docker-resources/* ./

# Adjust permissions
RUN chown user:user /home/user -R
RUN chmod +x run-experiments.sh
RUN chmod +x entrypoint.sh

RUN ls -l
RUN ls -l experimental_subjects

ENTRYPOINT ["./entrypoint.sh", "./run-experiments.sh"]
USER user