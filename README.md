AdminHelp
=========

Very simple plugin for communicating player <-> admin

Process:

1. Player asks a question via /ask. e.g. `/ask What are apple rates?`
2. All online admins + console are sent a message like: `[ID 3] ghowden asks: What are apple rates? DEL`
3. Any admin can reply like this: `/reply 3 Vanilla` or can click on `[ID 3]` to fill out `/adminhelp:reply 3 ` automatically.
4. When an admin replies all admins are notified with the response and the asker will be sent the response. If the asker
is not online then when they rejoin they will get the response. (If the server restarts/reloads then the message will
be lost). The client will get a message like `Admin replies: Vanilla`

Messages can be deleted by clicking on `DEL` (ingame only, not console) or by replying with an empty message `/reply 3`.

Admins can view a list of all unanswered questions via `/ahlist`

# Commands

## `/ask <question>`

Send a question to the admins

Aliases: `helpop`, `ahask`

## `/reply <id> [<response>]`

Sends a reply to the question with the given ID and then removes the question from the list.

If the response is empty it will remove the question without sending a message to the asker.

Aliases: `helpopreply`, `ahreply`

## `/ahlist`

Lists all of the unanswered questions

Aliases: `helpoplist`

# Permissions

`uhc.adminhelp.use` - allows use of the `/ask` command, default true

`uhc.adminhelp.admin` - allows use of `/ahlist` and `/reply` commands and receive admin notifications, default op