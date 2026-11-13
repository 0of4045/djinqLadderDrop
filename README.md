# djinqLadderDrop

A small and lightweight ladder drop mechanic for Paper servers.

Break a ladder and every connected ladder above it drops naturally. Use `/ladderdrop` to enable or disable the mechanic globally.

Made by djinq ~ [djinq.dev](https://djinq.dev)

## Installation

1. Drop `djinqLadderDrop.jar` into your server's `plugins` folder.
2. Start the server once.
3. Edit `plugins/djinqLadderDrop/config.yml` and `messages.yml` however you want.
4. Run `/ladderdrop reload` or restart the server.

## Commands

| Command | Description |
| --- | --- |
| `/ladderdrop` | Toggles ladder drop globally. |
| `/ladderdrop reload` | Reloads the configuration and messages. |

## Permissions

| Permission | Description |
| --- | --- |
| `djinqladderdrop.toggle` | Allows `/ladderdrop`. |
| `djinqladderdrop.reload` | Allows `/ladderdrop reload` without requiring `djinqladderdrop.toggle`. |

## Configuration

In `config.yml`, you can edit:

- `enabled` ~ enables or disables ladder drop globally.

`messages.yml` contains one `prefix` setting. It is added automatically to every chat message.

Existing customized settings and messages are preserved. Missing defaults are added on startup, with a backup before changes.

Built for Paper 1.21.11 through 26.3. Use Java 21 for Paper 1.21.11, or Java 25 for Paper 26.1+.
