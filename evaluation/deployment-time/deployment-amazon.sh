#!/bin/bash

source_name=$1  # speech-synthesis-aws-aws-aws.jar
source_path=../deployment-packages/
region=us-east-1
entry_point=function.NoOpFunction
role_arn=arn:aws:iam::178612579491:role/LabRole
bucket_name=deployment-time-bucket-core

# create deployment bucket
aws s3api create-bucket --bucket $bucket_name --region $region >>/dev/null

for i in {1..10}; do
  # shellcheck disable=SC2006
  start_upload=$(date +%s)
  # upload deployment package
  aws s3 cp $source_path$source_name s3://$bucket_name >>/dev/null
  end_upload=$(date +%s)
  start_deploy=$(date +%s)
  # create function
  aws lambda create-function --function-name my-function \
    --code S3Bucket=$bucket_name,S3Key=$source_name \
    --handler $entry_point \
    --runtime java11 \
    --region us-east-1 \
    --role $role_arn >>/dev/null
  end_deploy=$(date +%s)
  upload_time=$((end_upload - start_upload))
  echo "Upload $i: $upload_time seconds"
  deployment_time=$((end_deploy - start_deploy))
  echo "Deployment $i: $deployment_time seconds"
  # delete deployment package
  aws s3 rm --recursive s3://$bucket_name >>/dev/null
  # delete function
  aws lambda delete-function --function-name my-function --region us-east-1 >>/dev/null
done

# delete deployment bucket
aws s3api delete-bucket --bucket $bucket_name --region $region

