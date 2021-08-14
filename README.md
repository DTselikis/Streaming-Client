<div align="center">
<h1>Streaming Client</h1>
A streaming client powered by JAVA 11 and JavaFX.
<img src="https://i.imgur.com/pujGiGH.png" width="670">
</div>

## Usage
At launch, the app will permorm a speedtest using the JSpeedTest library to calculate the client's bandwidth. By pressing the "Start" button the client will try to connect to the server at **127.0.0.1:5000**. After a successfull connection the client will retrieve from the client the files that corespond to the given format. The user then selects the preferable file which will couse a card at the left of the screen to be opened. By pressing "START STREAMING" the client will start streaming the specified file with the specified resolution protocol.
If no protocol was provided, the app will choose the best fit based on bandwidth.
### Note
GUI based on [Fruits Market](https://github.com/mahmoudhamwi/Fruits-Market).

## Features
- Convert video files to 240p, 360, 480p, 720p and 1080p resolution.
- Stream video **without audio**.
- Multiple client support.
- Independentl ogging for each client.
### Supported video formats
- avi
- mp4
- mkv
### Supported protocols
- TCP/IP
- UDP
- RTP/UDP

## Dependencies
- OpenJDK 11
- JavaFX SDK 15
- FFmpeg (ffplay, ffprobe, ffmpeg)

### Maven dependencies
- log4j 2.14.1 (core & api)
- FFmpeg wrapper 0.6.2
- JSpeedTest

## Companion app
[Streaming Server](https://github.com/DTselikis/Streaming-Server)
