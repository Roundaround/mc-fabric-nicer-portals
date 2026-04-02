![Nicer Portals](https://imgur.com/KRkBCMD.png)

![](https://img.shields.io/badge/Loader-Fabric-313e51?style=for-the-badge)
![](https://img.shields.io/badge/MC-26.1--26.1.1%20|%201.21%20|%201.20%20|%201.19%20|%201.18.2-313e51?style=for-the-badge)
![](https://img.shields.io/badge/Side-Client%20&%20Server-313e51?style=for-the-badge)

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/nicer-portals?style=flat&logo=modrinth&color=00AF5C)](https://modrinth.com/mod/nicer-portals)
[![CurseForge Downloads](https://img.shields.io/curseforge/dt/1501935?style=flat&logo=curseforge&color=F16436)](https://www.curseforge.com/minecraft/mc-mods/nicer-portals)
[![GitHub Repo stars](https://img.shields.io/github/stars/Roundaround/mc-fabric-nicer-portals?style=flat&logo=github)](https://github.com/Roundaround/mc-fabric-nicer-portals)

[![Support me on Ko-fi](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/donate/kofi-singular-alt_vector.svg)](https://ko-fi.com/roundaround)

---

Various small improvements to portals. There are several features included as part of the mod. Some are client-side and
some server-side. See below for the full list!

## Client-side

The following features only work when installed on the client.

### Dedupe portal break sound

When portals get broken, the entire portal will only make one single break noise, instead of one per block that is
broken. No more headache inducing screeching!

## Server-side or single player

The following features only work when installed on the server (or on your client for single player).

### Prevent zombified piglin spawns

Stop zombified piglins from invading your megabase! Simply prevents them from spawning from portals in the overworld.

### Use crying obsidian in portals

Allows using crying obsidian interchangeably with regular obsidian when constructing portals!

### Custom portal shapes

Allows constructing portals of any enclosed shape! For performance reasons, the portals are limited in size based on the
number of actual portal blocks, so check the config file for the limit!

---

## Configuration

You can configure the behavior of the mod from the `nicerportals.toml` files. You will find one in your normal config
folder, and one in a new config folder _inside_ your world's save folder. If you have ModMenu installed, you can also
access the configuration through the UI in ModMenu's mod list!

`dedupeBreakSound`: `true|false` - Whether to makes portals emit only one sound when they break. Client-side only.

`preventPortalSpawns`: `true|false` - Whether to prevent portals from spawning Zombified Piglins in the overworld.
Server-side & single player only.

`anyShape`: `true|false` - Whether to allow portals in any shape and size. Server-side & single player only.

`maxSize`: `Integer` - The maximum allowed portal size to allow. Be warned that setting values that are too large here
could lag or even crash the game. 2304 is the default and seems to cause only a small hiccup in come cases. Note this
value only has any effect if `anyShape` is true. Server-side & single player only.

`enforceMinimum`: `true|false` - Require that portals are at least a 1x2 shape (can walk through them). Set to false to
allow 1x1 portals. Note this value only has any effect if anyShape is true. Server-side & single player only.

### 1.7.0+26.1 and later

`portalFrameTag`: `true|false` - Whether to replace portal frame block checks with the `#nicerportals:portal_frame` tag.
Server-side & single player only.

### 1.6.1+1.21.11 and earlier

`cryingObsidian`: `true|false` - Whether to allow using crying obsidian for portals. Server-side & single player only.
