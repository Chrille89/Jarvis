
#sudo rm out.flac
#sudo rm audio.json
sudo arecord -D plughw:1,0 -f cd -t wav -d 3 -r 16000 -c 1 | flac - -f --sample-rate 16000 -o out.flac
base64 out.flac -w 0 > audio.json

