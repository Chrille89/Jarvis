#!/bin/sh

FILE=$1
youtube-dl -f 'bestvideo[ext=mp4]+bestaudio[ext=m4a]/bestvideo+bestaudio' --merge-output-format mp4 "gvsearch1:`cat youtube/song.json`" -o youtube/$FILE
omxplayer -o local youtube/$FILE.mp4
 
 

