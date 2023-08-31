#!/bin/bash

source_name=$1  # speech-synthesis-gcp-gcp-gcp.jar.zip
source_path=../deployment-packages/
region=us-east1
entry_point=function.NoOpFunction
project_id=wide-net-377808
bucket_name=deployment-time-bucket-core

# create bucket
# shellcheck disable=SC2129
gcloud storage buckets create gs://$bucket_name --project=$project_id --location=$region 2>>/dev/null

for i in {1..10}; do
  # shellcheck disable=SC2006
  start_upload=$(date +%s)
  # upload deployment package to cloud storage
  # shellcheck disable=SC2129
  gcloud storage cp $source_path$source_name gs://$bucket_name/ 2>>/dev/null
  end_upload=$(date +%s)
  start_deploy=$(date +%s)
  # create function
  yes | gcloud functions deploy my-function \
    --region=$region \
    --runtime=java11 \
    --source=gs://$bucket_name/$source_name \
    --entry-point=$entry_point \
    --trigger-http 2>>/dev/null
  end_deploy=$(date +%s)
  upload_time=$((end_upload - start_upload))
  echo "Upload $i: $upload_time seconds"
  deployment_time=$((end_deploy - start_deploy))
  echo "Deployment $i: $deployment_time seconds"
  # delete deployment package
  # shellcheck disable=SC2129
  gcloud storage rm gs://$bucket_name/$source_name 2>>/dev/null
  # delete function
  yes | gcloud functions delete my-function --region=$region 2>>/dev/null
done

# delete bucket
gcloud storage rm --recursive gs://$bucket_name/ 2>>/dev/null
