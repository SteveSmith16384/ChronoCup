# Chrono Cup
A split-screen multiplayer FPS with time-rewinding game mechanics.

* Gameplay video: https://youtu.be/Xsq16HTg9p8


## Controls
* Press Space at the start to use keyboard/mouse, or press X on your controller to use that.
* Esc to exit back to the start, or to quit out.

* W, A S, D for keyboard & mouse player.
* Tested with PS4 controllers: R2 to shoot
* F1 - Toggle full Screen
* F2 - Toggle full screen but still windowed (required if you want to record the screen using Windows)


## How to Play
Okay, brace yourself: This may sound complicated, but once you know it all, it's quite straightforward:

* The winner is the player who spends most time on the central point.
* There are 3 phases.  During the first phase, it's just you and your opponent shooting at each other.
* During the second phase, you each can move around freely again, but there is a ghost replaying all your moves from the previous phase.
* If you get "killed" (or "de-syncd"), you are still alive, but you (or your echo's) bullets have no effect.  However, movement and shooting are still recorded for playback during the next phase.
* Only avatars that are "alive" can register on the central point.


## Notes for other Developers
* Development branch is the cutting edge but possibly broken branch.  Master is the most stable but out of date.
* Gradle is a real pain to work with.  However, if you have trouble loading this project, I used Gradle v4.10.3.
* The file Settings.java contains various settings that determine what game mode the game starts in.


## Licence
This project uses the MIT licence.  See LICENCE.txt.


## Credits
* Designed and programmed by Stephen Carlyle-Smith https://twitter.com/stephencsmith
* Uses the LibGDX game framework
* Uses BasicECS from https://github.com/SteveSmith16384/BasicECS
* Controller code from https://github.com/electronstudio/sdl2gdx


### Assets Credits
* All humanoid figures by Quaternius http://quaternius.com
* Alien by Quaternius
* Music by  Ville Nousiainen (http://soundcloud.com/mutkanto)
* Replenish Life Force Copyright 2013 Iwan Gabovitch http://freesound.org/people/qubodup/
* Shot sfx by Michael Klier taken from https://opengameart.org/content/futuristic-shotgun
* Explosion sfx by Iwan Gabovitch taken from https://opengameart.org/content/dull-explosion
* De-sync sfx taken from https://opengameart.org/content/energy-drain
* Majestical textures by Robert Yang, based on concepts by Randy Reddig and Adam Foster.
* Airhorn taken from http://soundbible.com/1542-Air-Horn.html


## Become a Patron!
Well, it's worth a try: https://www.patreon.com/bePatron?u=3406199

