#!/bin/bash

# The next line updates PATH for the Google Cloud SDK.
if [ -f /opt/google-cloud-sdk/path.bash.inc ]; then
  source '/opt/google-cloud-sdk/path.bash.inc'
fi

# The next line enables shell command completion for gcloud.
if [ -f /opt/google-cloud-sdk/completion.bash.inc ]; then
  source '/opt/google-cloud-sdk/completion.bash.inc'
fi

sudo rm /home/pi/Jarvis/googleToken.json
gcloud auth print-access-token > /home/pi/Jarvis/googleToken.json
