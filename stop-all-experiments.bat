@echo "Stopping all running experiments (and other docker containers)"
FOR /f "tokens=*" %%i IN ('docker ps -a -q --filter "ancestor=match-experiments"') DO docker stop %%i