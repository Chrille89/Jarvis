# Authorization
echo "Actualize Token for GoogleSpeech API"
TOKEN=$(gcloud auth print-access-token)
echo "New GoogleToken: "$TOKEN

INDEX=0

# Recording
while [ "$INDEX" -lt 10 ] ; # this is loop1, the outer loop
do

echo "Start Recording..."
arecord -D plughw:1,0 -f cd -t wav -d 5 -r 16000 -c 1 | flac - -f --sample-rate 16000 -o out.flac
echo "Stop Recording!"

# Encoding Audio-File
echo "Encoding Audio-File..."
base64 out.flac -w 0 > audio.json
JSON_BODY2=',"audio": {"content": "'"$(cat audio.json)"'"}}"'
JSON_BODY1='{"config": {"encoding":"FLAC","sampleRate": 16000,"languageCode": "de-DE"}'

# Concat Big Body
rm jsonBody.json
destdir=jsonBody.json
echo "$JSON_BODY1" >> "$destdir"
echo "$JSON_BODY2" >> "$destdir"

# Start Request
echo "Start Request to JarvisBackend..." 
curl -H "Content-Type:application/json" -H "Authorization:"$TOKEN -X POST -d @jsonBody.json http://gondor.selfhost.eu:8080/JarvisBackend/api/question/post -o test.json

# Ausgabe
echo "Answer: "$(cat test.json)
pico2wave --lang de-DE --wave /tmp/Test.wav "`cat test.json`" ; play /tmp/Test.wav; rm /tmp/Test.wav; 
INDEX=`expr $INDEX + 1`
done
