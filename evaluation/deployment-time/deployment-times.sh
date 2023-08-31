#!/bin/bash

./deployment-amazon.sh speech-synthesis-aws-aws-aws.jar  >> speech-synthesis-aws-aws-aws-aws.txt
./deployment-amazon.sh speech-synthesis-aws-aws-gcp.jar  >> speech-synthesis-aws-aws-aws-gcp.txt
./deployment-amazon.sh speech-synthesis-aws-gcp-aws.jar  >> speech-synthesis-aws-aws-gcp-aws.txt
./deployment-amazon.sh speech-synthesis-aws-gcp-gcp.jar  >> speech-synthesis-aws-aws-gcp-gcp.txt
./deployment-amazon.sh speech-synthesis-gcp-aws-aws.jar  >> speech-synthesis-aws-gcp-aws-aws.txt
./deployment-amazon.sh speech-synthesis-gcp-aws-gcp.jar  >> speech-synthesis-aws-gcp-aws-gcp.txt
./deployment-amazon.sh speech-synthesis-gcp-gcp-aws.jar  >> speech-synthesis-aws-gcp-gcp-aws.txt
./deployment-amazon.sh speech-synthesis-gcp-gcp-gcp.jar  >> speech-synthesis-aws-gcp-gcp-gcp.txt

./deployment-amazon.sh speech-recognition-aws-aws-aws.jar  >> speech-recognition-aws-aws-aws-aws.txt
./deployment-amazon.sh speech-recognition-aws-aws-gcp.jar  >> speech-recognition-aws-aws-aws-gcp.txt
./deployment-amazon.sh speech-recognition-aws-gcp-aws.jar  >> speech-recognition-aws-aws-gcp-aws.txt
./deployment-amazon.sh speech-recognition-aws-gcp-gcp.jar  >> speech-recognition-aws-aws-gcp-gcp.txt
./deployment-amazon.sh speech-recognition-gcp-aws-aws.jar  >> speech-recognition-aws-gcp-aws-aws.txt
./deployment-amazon.sh speech-recognition-gcp-aws-gcp.jar  >> speech-recognition-aws-gcp-aws-gcp.txt
./deployment-amazon.sh speech-recognition-gcp-gcp-aws.jar  >> speech-recognition-aws-gcp-gcp-aws.txt
./deployment-amazon.sh speech-recognition-gcp-gcp-gcp.jar  >> speech-recognition-aws-gcp-gcp-gcp.txt

./deployment-amazon.sh down-up-aws-aws.jar  >> down-up-aws-aws-aws.txt
./deployment-amazon.sh down-up-aws-gcp.jar  >> down-up-aws-aws-gcp.txt
./deployment-amazon.sh down-up-gcp-aws.jar  >> down-up-aws-gcp-aws.txt
./deployment-amazon.sh down-up-gcp-gcp.jar  >> down-up-aws-gcp-gcp.txt
