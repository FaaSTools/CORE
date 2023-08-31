#!/bin/bash

source_names=( "$@" )

source_path=../deployment-packages/
region=us-east1
entry_point=function.NoOpFunction
project_id=wide-net-377808
bucket_name=deployment-time-bucket-core

# create bucket
# shellcheck disable=SC2129
gcloud storage buckets create gs://$bucket_name --project=$project_id --location=$region 2>>/dev/null

start_upload=$(date +%s)
# shellcheck disable=SC2068
for i in ${!source_names[@]}; do
    source_name=${source_names[$i]}
    gcloud storage cp $source_path"$source_name" gs://$bucket_name/ 2>>/dev/null &
done
wait
end_upload=$(date +%s)

start_deploy=$(date +%s)
# shellcheck disable=SC2068
for i in ${!source_names[@]}; do
    source_name=${source_names[$i]}
    yes | gcloud functions deploy my-function-$i \
    --region=$region \
    --runtime=java11 \
    --source=gs://$bucket_name/"$source_name" \
    --entry-point=$entry_point \
    --trigger-http 2>>/dev/null &
done
wait
end_deploy=$(date +%s)

upload_time=$((end_upload - start_upload))
echo "Upload: $upload_time seconds"
deployment_time=$((end_deploy - start_deploy))
echo "Deployment: $deployment_time seconds"

# shellcheck disable=SC2068
for i in ${!source_names[@]}; do
    source_name=${source_names[$i]}
    # delete deployment package
    # shellcheck disable=SC2129
    gcloud storage rm gs://$bucket_name/"$source_name" 2>>/dev/null &
    # delete function
    yes | gcloud functions delete my-function-$i --region=$region 2>>/dev/null &
done
wait

# delete bucket
gcloud storage rm --recursive gs://$bucket_name/ 2>>/dev/null
