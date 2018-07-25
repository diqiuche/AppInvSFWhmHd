@echo off
xcopy L:\Doc\SVN\Work\SFWhm\trunk\src\SFwhmHd\app\src\main L:\Doc\Git\AppInvSFWhmHd\app\src\main\ /S
xcopy L:\Doc\SVN\Work\SFWhm\trunk\src\SFwhmHd\app\libs L:\Doc\Git\AppInvSFWhmHd\app\libs\ /S
copy L:\Doc\SVN\Work\SFWhm\trunk\src\SFwhmHd\app\build.gradle L:\Doc\Git\AppInvSFWhmHd\app
pause
