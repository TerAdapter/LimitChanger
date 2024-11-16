### Plugin for changing the maximum number of online players while the server is running.

```yaml
#
#  Commands | Permission:
#   - /limitchanger | limitchanger.command.main
#   - ~ | limitchanger.notify
#

# Plugin settings
main-settings:
  # Should the new online value be saved to the server.properties file?
  # If false, then upon restart the maximum online value will be taken from server.properties before the change
  save-new-max-players: true

# Plugin messages
messages:
  no-permissions: "&8[&6LimitChanger&8] &7» &cYou do not have permission to use this command!"
  changed: "&8[&6LimitChanger&8] &7» &fAdministrator &#00d5fb%executor% &fchanged the value of the maximum online to &#00d5fb%max_online%" # %executor% %max_online%
```
