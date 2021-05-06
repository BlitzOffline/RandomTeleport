# RandomTeleport
###### Explore the universe!

### Features:
* [PlaceholderAPI](https://www.spigotmc.org/resources/6245/) support.
* [WorldGuard](https://dev.bukkit.org/projects/worldguard/) support.
* [Vault](https://www.spigotmc.org/resources/vault.34315/) support.
* [Lands](https://www.spigotmc.org/resources/lands-land-claim-plugin-grief-prevention-protection-gui-management-nations-wars-1-16-support.53313/) support.
* Use world border or max coordinates.
* Hex support (&#aaFF00).
* World Teleport.
* 99% Customizable.
* Placeholders.
* Cooldowns.
* Warmups.

! This plugin should be used on [PaperSpigot](https://papermc.io/downloads)

### Commands:
Command | Permission | Description
--------|------------|------------
/rtp | randomteleport.self | Teleport yourself in a random location from a random world.
/rtp \<player> | randomteleport.others | Teleport others in a random location from a random world.
/rtp world \<world> | randomteleport.world.\[world] | Teleport yourself in a random location from a specific world.
/rtp world \<world> \<player> | randomteleport.world.others | Teleport others in a random location from a specific world.
/rtp reload | randomteleport.admin | Reload the configuration.

### Permissions:
Permission | Description
-----------|------------
randomteleport.cooldown.bypass | Bypass the cooldown if its enabled.
randomteleport.warmup.bypass | Bypass the warmup if its enabled.
randomteleport.cost.bypass | Bypass the cost for random teleport.

### Placeholders:
Placeholder | Description
------------|------------
%randomteleport_cooldown_left% | Shows how much there is left until a player's cooldown expires
%randomteleport_cooldown_enabled% | Shows if the cooldown is enabled or not in config.yml
%randomteleport_enabled_worlds% | Shows the list of all enabled worlds for /rtp
