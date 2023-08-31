#!/bin/bash

downup=('down-up-aws-aws.jar.zip' 'down-up-aws-gcp.jar.zip' 'down-up-gcp-aws.jar.zip' 'down-up-gcp-gcp.jar.zip')

synthesis=('speech-synthesis-aws-aws-aws.jar.zip' 'speech-synthesis-aws-aws-gcp.jar.zip' 'speech-synthesis-aws-gcp-aws.jar.zip' 'speech-synthesis-aws-gcp-gcp.jar.zip' 'speech-synthesis-gcp-aws-aws.jar.zip' 'speech-synthesis-gcp-aws-gcp.jar.zip' 'speech-synthesis-gcp-gcp-aws.jar.zip' 'speech-synthesis-gcp-gcp-gcp.jar.zip')

recognition=('speech-recognition-aws-aws-aws.jar.zip' 'speech-recognition-aws-aws-gcp.jar.zip' 'speech-recognition-aws-gcp-aws.jar.zip' 'speech-recognition-aws-gcp-gcp.jar.zip' 'speech-recognition-gcp-aws-aws.jar.zip' 'speech-recognition-gcp-aws-gcp.jar.zip' 'speech-recognition-gcp-gcp-aws.jar.zip' 'speech-recognition-gcp-gcp-gcp.jar.zip')

./deployment-amazon-parallel.sh "${downup[@]}" >> down-up-parallel-aws.txt
./deployment-amazon-parallel.sh "${synthesis[@]}" >> speech-synthesis-parallel-aws.txt
./deployment-amazon-parallel.sh "${recognition[@]}" >> speech-recognition-parallel-aws.txt

./deployment-google-parallel.sh "${downup[@]}" >> down-up-parallel-gcp.txt
./deployment-google-parallel.sh "${synthesis[@]}" >> speech-synthesis-parallel-gcp.txt
./deployment-google-parallel.sh "${recognition[@]}" >> speech-recognition-parallel-gcp.txt


