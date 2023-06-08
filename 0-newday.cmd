@echo off

set /p orgid= Input ORG-ID to edit: 

set dt=%DATE:~6,4%%DATE:~3,2%%DATE:~0,2%_%TIME:~0,2%%TIME:~3,2%%TIME:~6,2%%TIME:~9,2%
set dt=%dt: =0%

set orgdatafilename=ekb_%orgid%_%dt%.xlsx
echo Data filename: %orgdatafilename%
pause