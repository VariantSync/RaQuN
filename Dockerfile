# syntax=docker/dockerfile:1
FROM ubuntu:20.04

# Create a user
ARG USER_ID
ARG GROUP_ID
RUN addgroup --gid $GROUP_ID user
RUN adduser --disabled-password  --home /home/user --gecos '' --uid $USER_ID --ingroup user user

# Prepare the environment
RUN apt-get update \
    && apt-get install -y --no-install-recommends tzdata
RUN apt-get install -y --no-install-recommends build-essential git

WORKDIR /home/user
RUN mkdir -p ./experimental_subjects/argouml
COPY experimental_subjects/full_subjects.zip  ./experimental_subjects
COPY experimental_subjects/argouml/*.zip  ./experimental_subjects/argouml
#COPY experimental_subjects/argouml/argouml_p6.zip  ./experimental_subjects/argouml
#COPY experimental_subjects/argouml/argouml_p7.zip  ./experimental_subjects/argouml
#COPY experimental_subjects/argouml/argouml_p8.zip  ./experimental_subjects/argouml
#COPY experimental_subjects/argouml/argouml_p9.zip  ./experimental_subjects/argouml
COPY local-maven-repo .
COPY src .
COPY pom.xml .
COPY docker-resources/* .

RUN mkdir -p /home/user/results
RUN chown user:user /home/user -R
WORKDIR /home/user
RUN chmod +x extract.sh

ENTRYPOINT ["./run-experiments.sh"]